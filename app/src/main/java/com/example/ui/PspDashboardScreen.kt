package com.example.ui

import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PspGameEntity
import com.example.ui.theme.PspBlue
import com.example.ui.theme.PspCardBg
import com.example.ui.theme.PspCyan
import com.example.ui.theme.PspDarkBg
import com.example.ui.theme.PspGold
import com.example.ui.theme.PspMuted
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PspDashboardScreen(
    viewModel: PspViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val games by viewModel.gamesList.collectAsState()
    val phoneSpec by viewModel.phoneSpecState.collectAsState()
    val priority by viewModel.priorityState.collectAsState()
    val optResult by viewModel.optimizerResult.collectAsState()
    val selectedModelIdx by viewModel.selectedModelIndex.collectAsState()
    val cpuFreq by viewModel.cpuFrequencyMhz.collectAsState()
    val overclockStat by viewModel.overclockStatus.collectAsState()
    val isoSize by viewModel.isoSizeMb.collectAsState()
    val compressionLevel by viewModel.compressionLevel.collectAsState()
    val compressionResult by viewModel.compressionResult.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val timeString = remember {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date())
    }

    var showAddNewGameDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PspDarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .drawBehind {
                    // Draw a subtle retro energy wave background mimicking the XMB wavy effect
                    val wavePath1 = Path().apply {
                        moveTo(0f, size.height * 0.35f)
                        cubicTo(
                            size.width * 0.25f, size.height * 0.2f,
                            size.width * 0.65f, size.height * 0.5f,
                            size.width, size.height * 0.35f
                        )
                    }
                    val wavePath2 = Path().apply {
                        moveTo(0f, size.height * 0.42f)
                        cubicTo(
                            size.width * 0.3f, size.height * 0.52f,
                            size.width * 0.7f, size.height * 0.22f,
                            size.width, size.height * 0.42f
                        )
                    }
                    drawPath(
                        path = wavePath1,
                        color = PspBlue.copy(alpha = 0.08f),
                        style = Stroke(width = 12f)
                    )
                    drawPath(
                        path = wavePath2,
                        color = PspCyan.copy(alpha = 0.05f),
                        style = Stroke(width = 8f)
                    )
                }
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(PspBlue.copy(alpha = 0.2f), CircleShape)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Games,
                                contentDescription = null,
                                tint = PspCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PSP",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HUB",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Light,
                            color = PspCyan,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                    }
                    Text(
                        text = "Biblioteca & Otimizador Retro",
                        fontSize = 12.sp,
                        color = PspMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Time Indicator (Like XMB top-right clock)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(PspCardBg.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Green, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = timeString,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Tab navigation bar
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = PspCyan,
                edgePadding = 12.dp,
                divider = { HorizontalDivider(color = PspMuted.copy(alpha = 0.15f)) },
                tabs = {
                    val tabsList = listOf<Pair<String, androidx.compose.ui.graphics.vector.ImageVector>>(
                        "Catálogo" to Icons.Default.Games,
                        "Otimizador" to Icons.Default.Smartphone,
                        "Console & Overclock" to Icons.Default.DeviceHub,
                        "Compactador" to Icons.Default.Storage
                    )
                    tabsList.forEachIndexed { idx, (title, icon) ->
                        Tab(
                            selected = selectedTab == idx,
                            onClick = { viewModel.selectTab(idx) },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            selectedContentColor = PspCyan,
                            unselectedContentColor = PspMuted
                        )
                    }
                }
            )

            // Content matching active tab
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    0 -> CatalogTabContent(
                        games = games,
                        onAddClick = { showAddNewGameDialog = true },
                        onToggleOwned = { viewModel.toggleOwned(it) },
                        onUpdateGame = { game, backend, res, notes ->
                            viewModel.updateGameConfig(game, backend, res, notes)
                        },
                        onDeleteGame = { viewModel.deleteGame(it) },
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState
                    )
                    1 -> OptimizerTabContent(
                        phoneSpec = phoneSpec,
                        priority = priority,
                        result = optResult,
                        onPhoneSpecSelected = { viewModel.setPhoneSpec(it) },
                        onPrioritySelected = { viewModel.setPriority(it) },
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState
                    )
                    2 -> ConsoleDiagnosticsTabContent(
                        models = viewModel.consoleModels,
                        selectedIndex = selectedModelIdx,
                        cpuFrequency = cpuFreq,
                        overclockStat = overclockStat,
                        onModelSelected = { viewModel.selectModel(it) },
                        onFrequencyChange = { viewModel.setCpuFrequency(it) }
                    )
                    3 -> CompressorTabContent(
                        isoSize = isoSize,
                        level = compressionLevel,
                        result = compressionResult,
                        onSizeChange = { viewModel.setIsoSize(it) },
                        onLevelChange = { viewModel.setCompressionLevel(it) }
                    )
                }
            }
        }

        // Add Custom Game dialog
        if (showAddNewGameDialog) {
            AddGameDialog(
                onDismiss = { showAddNewGameDialog = false },
                onAddGame = { title, genre, fps, diff, rating, notes ->
                    viewModel.addCustomGame(title, genre, fps, diff, rating, notes)
                    showAddNewGameDialog = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Jogo $title adicionado com sucesso!")
                    }
                }
            )
        }
    }
}

// ==================== CATALOG TAB CONTENT ====================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatalogTabContent(
    games: List<PspGameEntity>,
    onAddClick: () -> Unit,
    onToggleOwned: (PspGameEntity) -> Unit,
    onUpdateGame: (PspGameEntity, String, Int, String) -> Unit,
    onDeleteGame: (String) -> Unit,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    snackbarHostState: androidx.compose.material3.SnackbarHostState
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterOwnedOnly by remember { mutableStateOf(false) }

    val filteredGames = games.filter {
        val matchesSearch = it.title.contains(searchQuery, ignoreCase = true) ||
                it.genre.contains(searchQuery, ignoreCase = true)
        val matchesOwned = !filterOwnedOnly || it.isOwned
        matchesSearch && matchesOwned
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search & Filter controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                placeholder = { Text("Pesquise por título ou gênero...", fontSize = 13.sp, color = PspMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PspCyan) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PspCyan,
                    unfocusedBorderColor = PspMuted.copy(alpha = 0.4f),
                    focusedContainerColor = PspCardBg.copy(alpha = 0.6f),
                    unfocusedContainerColor = PspCardBg.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = PspBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(56.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar jogo")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Novo", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Filters Row (Pills)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !filterOwnedOnly,
                onClick = { filterOwnedOnly = false },
                label = "Todos (${games.size})"
            )
            FilterChip(
                selected = filterOwnedOnly,
                onClick = { filterOwnedOnly = true },
                label = "Minha Biblioteca (${games.count { it.isOwned }})"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Empty State
        if (filteredGames.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Games,
                        contentDescription = null,
                        tint = PspMuted.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Nenhum jogo encontrado",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tente alterar os filtros ou adicione um novo jogo usando o botão acima.",
                        color = PspMuted,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredGames, key = { it.id }) { game ->
                    GameListItem(
                        game = game,
                        onToggleOwned = onToggleOwned,
                        onUpdateGame = onUpdateGame,
                        onDeleteGame = onDeleteGame,
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    val containerBg by animateColorAsState(if (selected) PspBlue else PspCardBg.copy(alpha = 0.4f))
    val textAndBorderColor by animateColorAsState(if (selected) Color.White else PspMuted)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(containerBg)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = textAndBorderColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListItem(
    game: PspGameEntity,
    onToggleOwned: (PspGameEntity) -> Unit,
    onUpdateGame: (PspGameEntity, String, Int, String) -> Unit,
    onDeleteGame: (String) -> Unit,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    snackbarHostState: androidx.compose.material3.SnackbarHostState
) {
    var expanded by remember { mutableStateOf(false) }
    var preferredBackendState by remember { mutableStateOf(game.preferredBackend) }
    var resolutionState by remember { mutableStateOf(game.renderingResolutionMultiplier) }
    var userNotesState by remember { mutableStateOf(game.customNotes) }
    var isEditing by remember { mutableStateOf(false) }

    val elevation = if (expanded) 8.dp else 2.dp
    val borderBrush = if (game.isOwned) {
        Brush.horizontalGradient(listOf(PspBlue.copy(alpha = 0.5f), PspCyan.copy(alpha = 0.5f)))
    } else {
        Brush.linearGradient(listOf(PspMuted.copy(alpha = 0.1f), PspMuted.copy(alpha = 0.1f)))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = PspCardBg.copy(alpha = 0.85f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, borderBrush)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Main quick info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Game Code Avatar Circle
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(PspBlue.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (game.isOwned) "Owned" else "PSP",
                        color = if (game.isOwned) PspCyan else PspMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = game.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = game.genre,
                            color = PspMuted,
                            fontSize = 11.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .background(PspMuted.copy(alpha = 0.5f), CircleShape)
                        )
                        Text(
                            text = game.emulationDifficulty,
                            color = when (game.emulationDifficulty) {
                                "Fácil" -> Color(0xFF4CAF50)
                                "Médio" -> Color(0xFFFF9800)
                                else -> Color(0xFFF44336)
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = {
                        onToggleOwned(game)
                        val text = if (game.isOwned) "Removido da biblioteca" else "Adicionado à biblioteca"
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(text)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (game.isOwned) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = "Posse",
                        tint = if (game.isOwned) PspCyan else PspMuted.copy(alpha = 0.5f),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = PspMuted
                )
            }

            // Expanded settings and custom values area
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(PspDarkBg.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Configuração Recomendada PPSSPP:",
                        fontSize = 12.sp,
                        color = PspCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Processador de Vídeo", fontSize = 10.sp, color = PspMuted)
                            if (isEditing) {
                                var showMenu by remember { mutableStateOf(false) }
                                Box {
                                    Text(
                                        text = preferredBackendState,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable { showMenu = true }
                                            .background(
                                                PspCardBg,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                    ExposedDropdownMenuBox(
                                        expanded = showMenu,
                                        onExpandedChange = { showMenu = !showMenu }
                                    ) {
                                        androidx.compose.material3.DropdownMenu(
                                            expanded = showMenu,
                                            onDismissRequest = { showMenu = false },
                                            modifier = Modifier.background(PspCardBg)
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Vulkan", color = Color.White) },
                                                onClick = {
                                                    preferredBackendState = "Vulkan"
                                                    showMenu = false
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("OpenGL", color = Color.White) },
                                                onClick = {
                                                    preferredBackendState = "OpenGL"
                                                    showMenu = false
                                                }
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = game.preferredBackend,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Column {
                            Text("Resolução Render", fontSize = 10.sp, color = PspMuted)
                            if (isEditing) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { if (resolutionState > 1) resolutionState-- },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text("-", color = PspCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    Text(
                                        text = "${resolutionState}x PSP",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                    IconButton(
                                        onClick = { if (resolutionState < 6) resolutionState++ },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text("+", color = PspCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }
                            } else {
                                Text(
                                    text = "${game.renderingResolutionMultiplier}x PSP",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Column {
                            Text("FPS Alvo", fontSize = 10.sp, color = PspMuted)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = PspGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${game.targetFps} FPS",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Notas & Dicas de Otimização:", fontSize = 10.sp, color = PspMuted)
                    Spacer(modifier = Modifier.height(4.dp))

                    if (isEditing) {
                        OutlinedTextField(
                            value = userNotesState,
                            onValueChange = { userNotesState = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 13.sp, color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PspCyan,
                                unfocusedBorderColor = PspMuted.copy(alpha = 0.3f),
                                focusedContainerColor = PspCardBg,
                                unfocusedContainerColor = PspCardBg.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    } else {
                        Text(
                            text = game.customNotes.ifEmpty { "Nenhuma nota inserida. Toque em Editar para configurar dicas." },
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action tools in details edit/delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (isEditing) {
                            OutlinedButton(
                                onClick = {
                                    isEditing = false
                                    // Reset values to DB defaults
                                    preferredBackendState = game.preferredBackend
                                    resolutionState = game.renderingResolutionMultiplier
                                    userNotesState = game.customNotes
                                },
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PspMuted),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, PspMuted.copy(alpha = 0.3f))
                            ) {
                                Text("Cancelar", fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    onUpdateGame(game, preferredBackendState, resolutionState, userNotesState)
                                    isEditing = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Configurações salvas para ${game.title}!")
                                    }
                                },
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PspCyan),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp), tint = PspDarkBg)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Salvar", fontSize = 11.sp, color = PspDarkBg, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            IconButton(
                                onClick = { isEditing = true },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = PspMuted, modifier = Modifier.size(16.dp))
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = {
                                    onDeleteGame(game.id)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Jogo ${game.title} deletado da base.")
                                    }
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== EMULATOR OPTIMIZER TAB CONTENT ====================
@Composable
fun OptimizerTabContent(
    phoneSpec: PhoneSpec,
    priority: OptimizationPriority,
    result: OptimizerResult,
    onPhoneSpecSelected: (PhoneSpec) -> Unit,
    onPrioritySelected: (OptimizationPriority) -> Unit,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    snackbarHostState: androidx.compose.material3.SnackbarHostState
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step 1 Card: Select Hardware Spec
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PspCardBg.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PspMuted.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PASSO 1: Potência do seu Android",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PspCyan,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PhoneSpec.values().forEach { spec ->
                        val isSelected = phoneSpec == spec
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PspBlue.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { onPhoneSpecSelected(spec) }
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isSelected) PspCyan else PspMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = spec.label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = when (spec) {
                                        PhoneSpec.BASIC -> "Snapdragon série 4/6, Helio P/G, até 4GB RAM"
                                        PhoneSpec.MEDIUM -> "Snapdragon série 7, Dimensity 800/1000, 6-8GB RAM"
                                        PhoneSpec.PREMIUM -> "Snapdragon 8 Gen 1/2/3, Dimensity 9000+, 12GB+ RAM"
                                    },
                                    fontSize = 11.sp,
                                    color = PspMuted
                                )
                            }
                        }
                        if (spec != PhoneSpec.PREMIUM) {
                            HorizontalDivider(color = PspMuted.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }

        // Step 2 Card: Select Priority
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PspCardBg.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PspMuted.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PASSO 2: Prioridade de Emulação",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PspCyan,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OptimizationPriority.values().forEach { prio ->
                        val isSelected = priority == prio
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PspBlue.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { onPrioritySelected(prio) }
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isSelected) PspCyan else PspMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = prio.label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = when (prio) {
                                        OptimizationPriority.PERFORMANCE -> "Foco em bater 60 fps fluidos com menor processamento"
                                        OptimizationPriority.BALANCED -> "Roda bem economizando bateria e evitando crash"
                                        OptimizationPriority.GRAPHICS -> "Aumenta resolução interna ao limite para imagem perfeita"
                                    },
                                    fontSize = 11.sp,
                                    color = PspMuted
                                )
                            }
                        }
                        if (prio != OptimizationPriority.GRAPHICS) {
                            HorizontalDivider(color = PspMuted.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }

        // Results Card (Config Recommendation PPSSPP)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PspCardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, PspCyan.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Configuração Gerada:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PspCyan
                        )
                        IconButton(
                            onClick = {
                                val textToCopy = "Configuração PPSSPP: Backend: ${result.backend}, Resolução: ${result.resolution}, Pulo Quadros: ${result.frameSkipping}, Buffer: ${result.bufferRenderingMode}"
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Configurações compartilhadas!")
                                }
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Compartilhar", tint = PspCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ResultSettingRow(label = "Motor de Gráficos (Backend)", value = result.backend, icon = Icons.Default.Memory)
                    ResultSettingRow(label = "Resolução de Renderização", value = result.resolution, icon = Icons.Default.Smartphone)
                    ResultSettingRow(label = "Pulo de Quadros (Frame Skipping)", value = result.frameSkipping, icon = Icons.Default.Speed)
                    ResultSettingRow(label = "Modo de Buffer", value = result.bufferRenderingMode, icon = Icons.Outlined.Settings)

                    HorizontalDivider(color = PspMuted.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 12.dp))

                    Text(
                        text = "Configurações de Hacks & Performance",
                        fontSize = 11.sp,
                        color = PspMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PerformanceCheckItem(label = "Texture Lazy Cache", active = result.lazyTextureCache)
                        PerformanceCheckItem(label = "Software Skinning", active = result.softwareSkinning)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PerformanceCheckItem(label = "Hardware Transform", active = result.hardwareTransform)
                        PerformanceCheckItem(label = "Vertex Cache", active = result.vertexCache)
                    }

                    HorizontalDivider(color = PspMuted.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 12.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PspGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = result.tip,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultSettingRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PspMuted,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, fontSize = 10.sp, color = PspMuted)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun PerformanceCheckItem(
    label: String,
    active: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (active) Icons.Default.CheckCircle else Icons.Default.Close,
            contentDescription = null,
            tint = if (active) Color.Green else Color.Red.copy(alpha = 0.6f),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

// ==================== CONSOLE DIAGNOSTICS TAB CONTENT ====================
@Composable
fun ConsoleDiagnosticsTabContent(
    models: List<PspViewModel.PspConsoleModel>,
    selectedIndex: Int,
    cpuFrequency: Int,
    overclockStat: PspViewModel.OverclockStat,
    onModelSelected: (Int) -> Unit,
    onFrequencyChange: (Int) -> Unit
) {
    val currentModel = models[selectedIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Selector slide list of Models
        item {
            Column {
                Text(
                    text = "Selecione o Modelo do PSP:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PspCyan,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    models.forEachIndexed { index, model ->
                        val isSelected = index == selectedIndex
                        val btnBg = if (isSelected) PspBlue else PspCardBg.copy(alpha = 0.5f)
                        val txtColor = if (isSelected) Color.White else PspMuted

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(btnBg)
                                .clickable { onModelSelected(index) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = model.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = txtColor
                                )
                                Text(
                                    text = model.codename.substringBefore(" "),
                                    fontSize = 9.sp,
                                    color = txtColor.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Details of selected model
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PspCardBg.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PspMuted.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentModel.name} \"${currentModel.codename}\"",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        BaseLabel(
                            text = "Lançamento: ${currentModel.releaseYear}",
                            containerColor = PspBlue.copy(alpha = 0.2f),
                            textColor = PspCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentModel.description,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 17.sp
                    )

                    HorizontalDivider(color = PspMuted.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 12.dp))

                    SpecValueRow(label = "Memória RAM", value = currentModel.ramAmount)
                    SpecValueRow(label = "Tela Portátil", value = currentModel.screenSpec)
                    SpecValueRow(label = "Bateria", value = currentModel.batteryCapacity)
                    SpecValueRow(label = "Armazenamento", value = currentModel.storageSolution)
                    SpecValueRow(
                        label = "Wi-Fi Nativo",
                        value = if (currentModel.hasWifi) "Sim (802.11b legado)" else "Não integrado"
                    )
                    SpecValueRow(label = "Desbloqueios recomendados", value = currentModel.cfwVersions)
                }
            }
        }

        // Overclock Simulator Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PspCardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, PspGold.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Overclock",
                            tint = PspGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simulador de Overclock da CPU",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Originalmente, os consoles vinham bloqueados em 222MHz para economizar bateria. Mudar para 333MHz era o boost oficial para os maiores jogos.",
                        fontSize = 11.sp,
                        color = PspMuted,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Clock value readout
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Frequência da CPU:",
                            fontSize = 12.sp,
                            color = PspMuted
                        )
                        Text(
                            text = "${cpuFrequency} MHz",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (cpuFrequency >= 300) Color.Red else PspGold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Frequency slider
                    Slider(
                        value = cpuFrequency.toFloat(),
                        onValueChange = { onFrequencyChange(it.toInt()) },
                        valueRange = 222f..333f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = PspGold,
                            activeTrackColor = PspGold,
                            inactiveTrackColor = PspMuted.copy(alpha = 0.2f)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = overclockStat.speedState,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PspCyan,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated metrics columns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SimulatedMetric(
                            label = "Ganho de Performance",
                            value = "+${overclockStat.performanceGainPercent}%",
                            textColor = Color.Green,
                            modifier = Modifier.weight(1f)
                        )
                        SimulatedMetric(
                            label = "Dreno de Bateria",
                            value = "+${overclockStat.batteryDrainPercent}% por hora",
                            textColor = if (overclockStat.batteryDrainPercent > 20) Color.Red else Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        SimulatedMetric(
                            label = "Calor da Carcaça",
                            value = "+${overclockStat.heatingCoefficient}°C",
                            textColor = if (overclockStat.heatingCoefficient > 4) Color.Red else PspGold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = PspMuted.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 12.dp))

                    Text(
                        text = overclockStat.compatibilityWarning,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BaseLabel(
    text: String,
    containerColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Composable
fun SpecValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(text = label, fontSize = 11.sp, color = PspMuted)
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}

@Composable
fun SimulatedMetric(
    label: String,
    value: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 9.sp, color = PspMuted, textAlign = TextAlign.Center, lineHeight = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = textColor, fontFamily = FontFamily.Monospace)
    }
}

// ==================== COMPRESSOR TAB CONTENT ====================
@Composable
fun CompressorTabContent(
    isoSize: Int,
    level: Int,
    result: PspViewModel.CompressionResult,
    onSizeChange: (Int) -> Unit,
    onLevelChange: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PspCardBg.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PspMuted.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = PspCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Compactador ISO para CSO",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Diminua dramaticamente o tamanho das ISOs de jogos usando a compressão CSO (CISO). Perfeito para otimizar espaço no armazenamento do celular.",
                        fontSize = 11.sp,
                        color = PspMuted,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Calculator Controls Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PspCardBg.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PspMuted.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Configuração do Jogo:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PspCyan
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Input ISO Size Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tamanho da ISO original:", fontSize = 11.sp, color = PspMuted)
                        Text(
                            text = "${"%.2f".format(isoSize / 1000.0)} GB ($isoSize MB)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Slider(
                        value = isoSize.toFloat(),
                        onValueChange = { onSizeChange(it.toInt()) },
                        valueRange = 100f..1800f,
                        colors = SliderDefaults.colors(
                            thumbColor = PspCyan,
                            activeTrackColor = PspCyan,
                            inactiveTrackColor = PspMuted.copy(alpha = 0.2f)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Select Level
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Nível de Compressão DAX:", fontSize = 11.sp, color = PspMuted)
                        Text(
                            text = "Nível $level (DAX)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PspGold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Slider(
                        value = level.toFloat(),
                        onValueChange = { onLevelChange(it.toInt()) },
                        valueRange = 1f..9f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = PspGold,
                            activeTrackColor = PspGold,
                            inactiveTrackColor = PspMuted.copy(alpha = 0.2f)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Veloz (Menos compressão)", fontSize = 9.sp, color = PspMuted)
                        Text("Ultra (Economia total de espaço)", fontSize = 9.sp, color = PspMuted)
                    }
                }
            }
        }

        // Projected Results Card with animated bar comparisons
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PspCardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, PspCyan.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Resultados da Simulação:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PspCyan
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated comparison progress bars
                    Column {
                        Text("ISO Original: $isoSize MB (100%)", fontSize = 10.sp, color = PspMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(PspMuted.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .fillMaxHeight()
                                    .background(PspMuted.copy(alpha = 0.6f))
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("CSO Comprimido: ${result.predictedSizeMb} MB (${100 - result.percentageSaved}%)", fontSize = 10.sp, color = PspMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(PspMuted.copy(alpha = 0.2f))
                        ) {
                            val ratio = result.predictedSizeMb.toFloat() / isoSize.toFloat()
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(ratio.coerceIn(0.01f, 1f))
                                    .fillMaxHeight()
                                    .background(PspCyan)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Saved space highlight
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PspDarkBg.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Percent,
                            contentDescription = null,
                            tint = Color.Green,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("ESPAÇO ECONOMIZADO", fontSize = 9.sp, color = PspMuted)
                            Text(
                                text = "${result.spaceSavedMb} MB economizados! (-${result.percentageSaved}%)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Green
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    SpecValueRow(label = "Impacto no Processador Cores", value = result.decompressionCpuOverhead)
                    SpecValueRow(label = "Uso Térmico & Bateria", value = result.batteryImpact)

                    HorizontalDivider(color = PspMuted.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 12.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PspGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = result.actionRecommendation,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// ==================== DIALOG COMPONENT ====================
@Composable
fun AddGameDialog(
    onDismiss: () -> Unit,
    onAddGame: (String, String, Int, String, Float, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Ação / Aventura") }
    var targetFps by remember { mutableStateOf(30) }
    var difficulty by remember { mutableStateOf("Médio") }
    var rating by remember { mutableStateOf(4.5f) }
    var notes by remember { mutableStateOf("") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = PspCardBg),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.5.dp, PspCyan.copy(alpha = 0.5f))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Novo Jogo Favorito",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PspCyan
                    )
                    Text(
                        text = "Complete os dados para registrar o jogo no catálogo.",
                        fontSize = 11.sp,
                        color = PspMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Nome do Jogo", fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PspCyan,
                            unfocusedBorderColor = PspMuted.copy(alpha = 0.4f)
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = genre,
                        onValueChange = { genre = it },
                        label = { Text("Gênero", fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PspCyan,
                            unfocusedBorderColor = PspMuted.copy(alpha = 0.4f)
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Column {
                        Text("FPS Alvo: $targetFps FPS", fontSize = 11.sp, color = PspMuted)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(30, 60).forEach { fps ->
                                val selected = targetFps == fps
                                val btnBg = if (selected) PspBlue else Color.Transparent
                                val txtColor = if (selected) Color.White else PspMuted
                                OutlinedButton(
                                    onClick = { targetFps = fps },
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = btnBg),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (selected) PspCyan else PspMuted.copy(alpha = 0.3f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("${fps} FPS", color = txtColor, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Column {
                        Text("Dificuldade de Emulação: $difficulty", fontSize = 11.sp, color = PspMuted)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Fácil", "Médio", "Difícil").forEach { diff ->
                                val selected = difficulty == diff
                                val btnBg = if (selected) PspBlue else Color.Transparent
                                val txtColor = if (selected) Color.White else PspMuted
                                OutlinedButton(
                                    onClick = { difficulty = diff },
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = btnBg),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (selected) PspCyan else PspMuted.copy(alpha = 0.3f)),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 4.dp)
                                ) {
                                    Text(diff, color = txtColor, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Nota Pessoal:", fontSize = 11.sp, color = PspMuted)
                            Row {
                                Icon(Icons.Default.Star, contentDescription = null, tint = PspGold, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("${"%.1f".format(rating)}/5", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Slider(
                            value = rating,
                            onValueChange = { rating = it },
                            valueRange = 1.0f..5.0f,
                            steps = 38,
                            colors = SliderDefaults.colors(
                                thumbColor = PspGold,
                                activeTrackColor = PspGold,
                                inactiveTrackColor = PspMuted.copy(alpha = 0.2f)
                            )
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notas do Usuário", fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PspCyan,
                            unfocusedBorderColor = PspMuted.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PspMuted)
                        ) {
                            Text("Cancelar", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    onAddGame(title, genre, targetFps, difficulty, rating, notes)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PspCyan),
                            enabled = title.isNotBlank()
                        ) {
                            Text("Adicionar", color = PspDarkBg, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
