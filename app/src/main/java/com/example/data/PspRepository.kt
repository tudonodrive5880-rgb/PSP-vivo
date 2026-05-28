package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PspRepository(private val pspGameDao: PspGameDao) {

    val allGames: Flow<List<PspGameEntity>> = pspGameDao.getAllGames()

    suspend fun insertGame(game: PspGameEntity) {
        pspGameDao.insertGame(game)
    }

    suspend fun updateGame(game: PspGameEntity) {
        pspGameDao.updateGame(game)
    }

    suspend fun deleteGame(id: String) {
        pspGameDao.deleteGameById(id)
    }

    suspend fun prepopulateIfEmpty() {
        val currentList = pspGameDao.getAllGames().first()
        if (currentList.isEmpty()) {
            val games = listOf(
                PspGameEntity(
                    id = "GOW-GOS",
                    title = "God of War: Ghost of Sparta",
                    genre = "Ação / Hack n Slash",
                    targetFps = 60,
                    emulationDifficulty = "Difícil",
                    rating = 4.9f,
                    isOwned = true,
                    customNotes = "Roda melhor usando backend Vulkan e resolução no máximo em 2x-3x PSP. Mantenha 'Pular quadros' desligado se seu celular for intermediário.",
                    preferredBackend = "Vulkan",
                    renderingResolutionMultiplier = 3
                ),
                PspGameEntity(
                    id = "GTA-VCS",
                    title = "GTA: Vice City Stories",
                    genre = "Mundo Aberto / Ação",
                    targetFps = 30,
                    emulationDifficulty = "Médio",
                    rating = 4.7f,
                    isOwned = true,
                    customNotes = "Para evitar queda de frames nas explosões, ligue o Cache de Texturas Preguiçoso (Lazy Texture Caching). Resolução recomendada: 2x PSP.",
                    preferredBackend = "Vulkan",
                    renderingResolutionMultiplier = 2
                ),
                PspGameEntity(
                    id = "CC-FF7",
                    title = "Crisis Core: Final Fantasy VII",
                    genre = "Action RPG",
                    targetFps = 30,
                    emulationDifficulty = "Fácil",
                    rating = 4.8f,
                    isOwned = false,
                    customNotes = "Excelente compatibilidade. Roda perfeitamente em 3x ou 4x de resolução mesmo em aparelhos básicos com OpenGL.",
                    preferredBackend = "OpenGL",
                    renderingResolutionMultiplier = 3
                ),
                PspGameEntity(
                    id = "T6",
                    title = "Tekken 6",
                    genre = "Luta",
                    targetFps = 60,
                    emulationDifficulty = "Médio",
                    rating = 4.6f,
                    isOwned = false,
                    customNotes = "Luta fluida requer 60 FPS estáveis. Use Vulkan e ligue 'Vertex Cache' para melhor rendering 3D.",
                    preferredBackend = "Vulkan",
                    renderingResolutionMultiplier = 2
                ),
                PspGameEntity(
                    id = "MHP3",
                    title = "Monster Hunter Portable 3rd",
                    genre = "RPG / Multiplayer",
                    targetFps = 30,
                    emulationDifficulty = "Médio",
                    rating = 4.8f,
                    isOwned = true,
                    customNotes = "A tradução em Português-BR fica excelente. Use Vulkan em 2x PSP. Para celulares potentes, ative upscaling 2x xBRZ.",
                    preferredBackend = "Vulkan",
                    renderingResolutionMultiplier = 2
                ),
                PspGameEntity(
                    id = "NFS-MW",
                    title = "Need for Speed: Most Wanted",
                    genre = "Corrida",
                    targetFps = 30,
                    emulationDifficulty = "Fácil",
                    rating = 4.3f,
                    isOwned = false,
                    customNotes = "Roda com tranquilidade. Se houver áudio estalando (audio stuttering), ative a velocidade de clock alternativa em 60Hz.",
                    preferredBackend = "OpenGL",
                    renderingResolutionMultiplier = 2
                ),
                PspGameEntity(
                    id = "DBZ-SB",
                    title = "Dragon Ball Z: Shin Budokai",
                    genre = "Luta",
                    targetFps = 60,
                    emulationDifficulty = "Fácil",
                    rating = 4.5f,
                    isOwned = false,
                    customNotes = "Roda extremamente rápido. Praticamente qualquer celular atinge os 60 FPS cravados em 2x PSP.",
                    preferredBackend = "Vulkan",
                    renderingResolutionMultiplier = 2
                ),
                PspGameEntity(
                    id = "P3P",
                    title = "Persona 3 Portable",
                    genre = "JRPG",
                    targetFps = 30,
                    emulationDifficulty = "Fácil",
                    rating = 4.9f,
                    isOwned = true,
                    customNotes = "Jogo 2.5D super leve. Pode rodar na resolução mais alta suportada pela sua tela para ficar cristalino.",
                    preferredBackend = "OpenGL",
                    renderingResolutionMultiplier = 4
                )
            )
            pspGameDao.insertGames(games)
        }
    }
}
