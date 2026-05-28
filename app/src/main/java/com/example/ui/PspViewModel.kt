package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PspDatabase
import com.example.data.PspGameEntity
import com.example.data.PspRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PhoneSpec(val label: String) {
    BASIC("Básico / Entrada"),
    MEDIUM("Intermediário"),
    PREMIUM("Top de Linha / High-End")
}

enum class OptimizationPriority(val label: String) {
    PERFORMANCE("Máximo Desempenho (60 FPS)"),
    BALANCED("Equilibrado (30 FPS Estável / Visual Limpo)"),
    GRAPHICS("Alta Resolução e Gráficos HD")
}

data class OptimizerResult(
    val backend: String,
    val resolution: String,
    val frameSkipping: String,
    val lazyTextureCache: Boolean,
    val softwareSkinning: Boolean,
    val hardwareTransform: Boolean,
    val vertexCache: Boolean,
    val anisotropicFiltering: String,
    val bufferRenderingMode: String,
    val tip: String
)

class PspViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PspRepository

    init {
        val database = PspDatabase.getDatabase(application)
        repository = PspRepository(database.pspGameDao())
        
        // Populate the DB if empty
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    // List of games from DB
    val gamesList: StateFlow<List<PspGameEntity>> = repository.allGames
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Selected Tab in Single View Screen
    private val _selectedTab = MutableStateFlow(0) // 0: Catalog, 1: Optimizer, 2: Spec/Overclock, 3: Compressor
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun selectTab(idx: Int) {
        _selectedTab.value = idx
    }

    // Toggle target state "isOwned"
    fun toggleOwned(game: PspGameEntity) {
        viewModelScope.launch {
            repository.updateGame(game.copy(isOwned = !game.isOwned))
        }
    }

    // Update custom settings for a game
    fun updateGameConfig(game: PspGameEntity, backend: String, resolution: Int, notes: String) {
        viewModelScope.launch {
            repository.updateGame(
                game.copy(
                    preferredBackend = backend,
                    renderingResolutionMultiplier = resolution,
                    customNotes = notes
                )
            )
        }
    }

    // Add a custom game to DB
    fun addCustomGame(title: String, genre: String, targetFps: Int, emulationDifficulty: String, rating: Float, notes: String) {
        viewModelScope.launch {
            val shortId = "CUSTOM-${System.currentTimeMillis() % 10000}"
            val newGame = PspGameEntity(
                id = shortId,
                title = title,
                genre = genre,
                targetFps = targetFps,
                emulationDifficulty = emulationDifficulty,
                rating = rating,
                isOwned = true,
                customNotes = notes,
                preferredBackend = "Vulkan",
                renderingResolutionMultiplier = 2
            )
            repository.insertGame(newGame)
        }
    }

    // Delete a game from DB
    fun deleteGame(id: String) {
        viewModelScope.launch {
            repository.deleteGame(id)
        }
    }

    // --- EMULATOR OPTIMIZER STATE & COMPUTATIONS ---
    private val _phoneSpecState = MutableStateFlow(PhoneSpec.MEDIUM)
    val phoneSpecState: StateFlow<PhoneSpec> = _phoneSpecState.asStateFlow()

    private val _priorityState = MutableStateFlow(OptimizationPriority.BALANCED)
    val priorityState: StateFlow<OptimizationPriority> = _priorityState.asStateFlow()

    fun setPhoneSpec(spec: PhoneSpec) {
        _phoneSpecState.value = spec
    }

    fun setPriority(priority: OptimizationPriority) {
        _priorityState.value = priority
    }

    val optimizerResult: StateFlow<OptimizerResult> = kotlinx.coroutines.flow.combine(
        _phoneSpecState,
        _priorityState
    ) { spec, priority ->
        computeOptimization(spec, priority)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), computeOptimization(PhoneSpec.MEDIUM, OptimizationPriority.BALANCED))

    private fun computeOptimization(spec: PhoneSpec, priority: OptimizationPriority): OptimizerResult {
        return when (spec) {
            PhoneSpec.BASIC -> {
                when (priority) {
                    OptimizationPriority.PERFORMANCE -> OptimizerResult(
                        backend = "Vulkan (Mais veloz em novos chips)",
                        resolution = "1x PSP (Ideal para telas pequenas/básicas)",
                        frameSkipping = "Pular 1 Quadro (Automático)",
                        lazyTextureCache = true,
                        softwareSkinning = true,
                        hardwareTransform = true,
                        vertexCache = true,
                        anisotropicFiltering = "Desativado",
                        bufferRenderingMode = "Pular efeitos de buffer (Não-bufferizado - Extremamente rápido)",
                        tip = "Para celulares mais simples, pular os efeitos de buffer e renderizar em 1x PSP dará o máximo de velocidade, mas pode causar falhas em alguns menus."
                    )
                    OptimizationPriority.BALANCED -> OptimizerResult(
                        backend = "OpenGL (Compatibilidade máxima)",
                        resolution = "1x ou 2x PSP",
                        frameSkipping = "Desativado",
                        lazyTextureCache = true,
                        softwareSkinning = true,
                        hardwareTransform = true,
                        vertexCache = true,
                        anisotropicFiltering = "Desativado",
                        bufferRenderingMode = "Renderização com buffer (Bufferizado)",
                        tip = "Esta configuração resolve os menus pretos mantendo bom desempenho. Caso o áudio estale, ative o pulo de quadros automático."
                    )
                    OptimizationPriority.GRAPHICS -> OptimizerResult(
                        backend = "Vulkan",
                        resolution = "2x PSP (Limite seguro)",
                        frameSkipping = "Pular 1 Quadro",
                        lazyTextureCache = false,
                        softwareSkinning = true,
                        hardwareTransform = true,
                        vertexCache = true,
                        anisotropicFiltering = "2x",
                        bufferRenderingMode = "Renderização com buffer (Bufferizado)",
                        tip = "Forçar 2x PSP de resolução com filtragem anisotrópica pode esquentar o aparelho e necessitar de um pouco de pulo de quadros ativo."
                    )
                }
            }
            PhoneSpec.MEDIUM -> {
                when (priority) {
                    OptimizationPriority.PERFORMANCE -> OptimizerResult(
                        backend = "Vulkan",
                        resolution = "2x PSP",
                        frameSkipping = "Desativado",
                        lazyTextureCache = true,
                        softwareSkinning = true,
                        hardwareTransform = true,
                        vertexCache = true,
                        anisotropicFiltering = "Desativado",
                        bufferRenderingMode = "Renderização com buffer (Bufferizado)",
                        tip = "Equilíbrio excelente! Mantém 60 FPS estáticos na maioria dos jogos como God of War em resolução nítida de duas vezes a original."
                    )
                    OptimizationPriority.BALANCED -> OptimizerResult(
                        backend = "Vulkan",
                        resolution = "2x PSP ou 3x PSP",
                        frameSkipping = "Desativado",
                        lazyTextureCache = false,
                        softwareSkinning = true,
                        hardwareTransform = true,
                        vertexCache = true,
                        anisotropicFiltering = "4x",
                        bufferRenderingMode = "Renderização com buffer (Bufferizado)",
                        tip = "Configuração altamente recomendada. Oferece visual polido e sem borrões com excelente suavidade de 30 a 60 FPS."
                    )
                    OptimizationPriority.GRAPHICS -> OptimizerResult(
                        backend = "Vulkan",
                        resolution = "3x PSP (Alta Definição)",
                        frameSkipping = "Desativado",
                        lazyTextureCache = false,
                        softwareSkinning = false,
                        hardwareTransform = true,
                        vertexCache = true,
                        anisotropicFiltering = "8x",
                        bufferRenderingMode = "Renderização com buffer (Bufferizado)",
                        tip = "Ativa alta definição em 3x PSP. Desativar Software Skinning acelera animações 3D em processadores octa-core intermediários."
                    )
                }
            }
            PhoneSpec.PREMIUM -> {
                when (priority) {
                    OptimizationPriority.PERFORMANCE -> OptimizerResult(
                        backend = "Vulkan",
                        resolution = "3x ou 4x PSP",
                        frameSkipping = "Desativado",
                        lazyTextureCache = false,
                        softwareSkinning = true,
                        hardwareTransform = true,
                        vertexCache = true,
                        anisotropicFiltering = "8x",
                        bufferRenderingMode = "Renderização com buffer (Bufferizado)",
                        tip = "Hardware de ponta mantém 60 FPS facilmente. Menus de jogo e texturas estarão extremamente fluidos sem precisar comprometer efeitos visuais!"
                    )
                    OptimizationPriority.BALANCED -> OptimizerResult(
                        backend = "Vulkan",
                        resolution = "4x PSP (Qualidade Ultra)",
                        frameSkipping = "Desativado",
                        lazyTextureCache = false,
                        softwareSkinning = true,
                        hardwareTransform = true,
                        vertexCache = true,
                        anisotropicFiltering = "16x",
                        bufferRenderingMode = "Renderização com buffer (Bufferizado)",
                        tip = "Imagens tão limpas quanto um console de console moderno de mesa. 4x PSP remove serrilhados completamente nos painéis OLED."
                    )
                    OptimizationPriority.GRAPHICS -> OptimizerResult(
                        backend = "Vulkan",
                        resolution = "5x ou 6x PSP (Resolução de Monitor 4K!)",
                        frameSkipping = "Desativado",
                        lazyTextureCache = false,
                        softwareSkinning = false,
                        hardwareTransform = true,
                        vertexCache = true,
                        anisotropicFiltering = "16x",
                        bufferRenderingMode = "Renderização com buffer (Bufferizado)",
                        tip = "Seu aparelho topo de linha aguenta qualquer desaforo. Pode usar Upscaling de Textura (como xBRZ ou Hybrid 2x) para polimento máximo retrô."
                    )
                }
            }
        }
    }

    // --- ISO COMPRESSOR COMPUTATIONS ---
    private val _isoSizeMb = MutableStateFlow(1200)
    val isoSizeMb: StateFlow<Int> = _isoSizeMb.asStateFlow()

    private val _compressionLevel = MutableStateFlow(5) // Ranges from 1 to 9
    val compressionLevel: StateFlow<Int> = _compressionLevel.asStateFlow()

    fun setIsoSize(size: Int) {
        _isoSizeMb.value = size.coerceIn(100, 2000)
    }

    fun setCompressionLevel(level: Int) {
        _compressionLevel.value = level.coerceIn(1, 9)
    }

    data class CompressionResult(
        val predictedSizeMb: Int,
        val spaceSavedMb: Int,
        val percentageSaved: Int,
        val batteryImpact: String,
        val decompressionCpuOverhead: String,
        val actionRecommendation: String
    )

    val compressionResult: StateFlow<CompressionResult> = kotlinx.coroutines.flow.combine(
        _isoSizeMb,
        _compressionLevel
    ) { size, level ->
        // Calculations based on level
        val multiplier = 1.0 - (level * 0.045) - 0.15 // level 5 gives ~ 62% of original size
        val csoSize = (size * multiplier).toInt().coerceAtLeast(30)
        val saved = size - csoSize
        val pSaved = ((saved.toDouble() / size.toDouble()) * 100).toInt()

        val cpuOverhead = when {
            level <= 3 -> "Mínimo (Recomendado para celulares muito antigos)"
            level <= 6 -> "Moderado (Não afeta celulares modernos)"
            else -> "Alto (Pode causar engasgos em telas de loading se o celular for básico)"
        }

        val battery = when {
            level <= 3 -> "Baixo consumo mecânico"
            level <= 6 -> "Normal"
            else -> "Um pouco maior devido à descompressão em tempo real"
        }

        val recommendation = when {
            level <= 3 -> "Ideal para jogos de RPG com loadings recorrentes e rápidos."
            level <= 7 -> "Nível ideal! Recomendável para economizar espaço e manter loadings ótimos."
            else -> "Somente se o seu espaço de memória for extremamente crítico."
        }

        CompressionResult(
            predictedSizeMb = csoSize,
            spaceSavedMb = saved,
            percentageSaved = pSaved,
            batteryImpact = battery,
            decompressionCpuOverhead = cpuOverhead,
            actionRecommendation = recommendation
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CompressionResult(750, 450, 37, "Normal", "Moderado", ""))

    // --- CONSOLE DIAGNOSTICS & OVERCLOCK STATE ---
    data class PspConsoleModel(
        val name: String,
        val codename: String,
        val releaseYear: String,
        val ramAmount: String,
        val screenSpec: String,
        val hasWifi: Boolean,
        val batteryCapacity: String,
        val storageSolution: String,
        val cfwVersions: String,
        val description: String
    )

    val consoleModels = listOf(
        PspConsoleModel(
            name = "PSP-1000",
            codename = "Fat (Gordo)",
            releaseYear = "2004",
            ramAmount = "32 MB",
            screenSpec = "LCD 4.3\" IPS-like (Ghosting alto, cores quentes)",
            hasWifi = true,
            batteryCapacity = "1800 mAh (Removível)",
            storageSolution = "Memory Stick PRO Duo",
            cfwVersions = "6.61 PRO-C2, LME-2.3 (Desbloqueio definitivo na placa-mãe)",
            description = "O pioneiro lendário. Carcaça robusta e excelente empunhadura metálica, mas possui metade da memória RAM dos sucessores, o que pode atrasar carregamentos no navegador original."
        ),
        PspConsoleModel(
            name = "PSP-2000",
            codename = "Slim & Lite",
            releaseYear = "2007",
            ramAmount = "64 MB (Dobro da RAM)",
            screenSpec = "LCD 4.3\" Brilhante (Ghosting reduzido, cores fortes)",
            hasWifi = true,
            batteryCapacity = "1200 mAh (Mais fina)",
            storageSolution = "Memory Stick PRO Duo",
            cfwVersions = "6.61 PRO/LME com suporte a Infinity 2.0 ou temporário",
            description = "Mais fino e leve. A RAM extra acelerou de forma absurda o carregamento de jogos e adicionou suporte a saída de vídeo na TV via cabo componente."
        ),
        PspConsoleModel(
            name = "PSP-3000",
            codename = "Bright / Vibrant",
            releaseYear = "2008",
            ramAmount = "64 MB",
            screenSpec = "LCD 4.3\" Antirreflexo (Cores vibrantes, sem ghosting)",
            hasWifi = true,
            batteryCapacity = "1200 mAh",
            storageSolution = "Memory Stick PRO Duo",
            cfwVersions = "6.61 PRO/LME com desbloqueio definitivo via Infinity 2.0",
            description = "Considerado a melhor tela portátil original. Sem lag de fósforo (tempo de resposta minúsculo) e microfone embutido para chamadas no Skype nos anos 2000."
        ),
        PspConsoleModel(
            name = "PSP Go (N1000)",
            codename = "Slide Pocket",
            releaseYear = "2009",
            ramAmount = "64 MB",
            screenSpec = "LCD 3.8\" Super Nítido (Pixel pitch excelente)",
            hasWifi = true,
            batteryCapacity = "930 mAh (Interna)",
            storageSolution = "16 GB Memória Interna + Slot Memory Stick Micro (M2)",
            cfwVersions = "6.61 PRO-C Infinity 2.0 (Excelente compatibilidade)",
            description = "Design de slide futurista. Não possui leitor de UMD (discos), dependendo inteiramente de ISOs e do Bluetooth nativo para conectar controle de PS3!"
        ),
        PspConsoleModel(
            name = "PSP Street (E1000)",
            codename = "Economical",
            releaseYear = "2011",
            ramAmount = "64 MB",
            screenSpec = "LCD 4.3\" Opaco (Ajuste de brilho mecânico)",
            hasWifi = false, // Wifi removed!
            batteryCapacity = "925 mAh (Interna e lacrada)",
            storageSolution = "Memory Stick PRO Duo",
            cfwVersions = "6.61 LME-2.3 e PRO-C temporário",
            description = "Modelo de baixo custo lançado apenas na Europa. Alto-falante mono, sem conexão Wi-Fi e tampa traseira inteiriça para os discos UMD. Muito leve."
        )
    )

    private val _selectedModelIndex = MutableStateFlow(2) // Defaults to PSP-3000
    val selectedModelIndex: StateFlow<Int> = _selectedModelIndex.asStateFlow()

    fun selectModel(index: Int) {
        _selectedModelIndex.value = index.coerceIn(0, consoleModels.size - 1)
    }

    // Overclock Simulator Slider State
    private val _cpuFrequencyMhz = MutableStateFlow(222) // Defaults to native speed 222
    val cpuFrequencyMhz: StateFlow<Int> = _cpuFrequencyMhz.asStateFlow()

    fun setCpuFrequency(frequency: Int) {
        _cpuFrequencyMhz.value = frequency.coerceIn(222, 333)
    }

    data class OverclockStat(
        val speedState: String, // "Padrão", "Aceleração Segura", "Overclock Técnico"
        val batteryDrainPercent: Int, // Simulated percent decrease
        val heatingCoefficient: Double, // Simulated °C addition
        val performanceGainPercent: Int, // Simulated speed boost
        val compatibilityWarning: String
    )

    val overclockStatus: StateFlow<OverclockStat> = _cpuFrequencyMhz.combineState(MutableStateFlow(0)) { freq, _ ->
        val pctGain = (((freq - 222).toDouble() / 222.0) * 100).toInt()
        val speed = when {
            freq <= 222 -> "Velocidade Original Conservadora (222 MHz)"
            freq <= 266 -> "Modo Executivo Otimizado (266 MHz)"
            freq <= 300 -> "Aceleração de Jogos Pesados (300 MHz)"
            else -> "Overclock Oficial Máximo (333 MHz)"
        }
        val drain = when {
            freq <= 222 -> 0
            freq <= 266 -> 15
            freq <= 300 -> 30
            else -> 50
        }
        val heat = when {
            freq <= 222 -> 0.0
            freq <= 266 -> 1.5
            freq <= 300 -> 3.2
            else -> 6.5
        }
        val warning = when {
            freq <= 222 -> "Máxima vida útil de bateria. Alguns jogos pesados do final de geração podem soltar frames."
            freq <= 266 -> "Excelente para emulador. Não sobrecarrega nenhum componente de silício."
            freq <= 300 -> "Usado oficialmente em Monster Hunter Portable 3rd e God of War. Boa pedida."
            else -> "Usa a potência total do chipset do PSP de terceira revisão. Drena a bateria de forma agressiva (até 50% mais rápida)."
        }

        OverclockStat(
            speedState = speed,
            batteryDrainPercent = drain,
            heatingCoefficient = heat,
            performanceGainPercent = pctGain,
            compatibilityWarning = warning
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverclockStat("Padrão", 0, 0.0, 0, ""))
}

// Simple Kotlin extension to combine StateFlow
fun <T1, T2, R> StateFlow<T1>.combineState(
    flow2: StateFlow<T2>,
    transform: (T1, T2) -> R
): kotlinx.coroutines.flow.Flow<R> {
    return kotlinx.coroutines.flow.combine(this, flow2) { val1, val2 ->
        transform(val1, val2)
    }
}
