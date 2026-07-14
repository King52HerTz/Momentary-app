package com.eliauk.momentary.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eliauk.momentary.MomentaryViewModel
import com.eliauk.momentary.model.Journal
import com.eliauk.momentary.ui.screens.ChatScreen
import com.eliauk.momentary.ui.screens.FavoritesScreen
import com.eliauk.momentary.ui.screens.JournalDetailScreen
import com.eliauk.momentary.ui.screens.JournalsScreen
import com.eliauk.momentary.ui.screens.ProfileScreen
import com.eliauk.momentary.ui.screens.QuickCaptureSheet
import com.eliauk.momentary.ui.screens.SummaryScreen
import com.eliauk.momentary.ui.screens.TodayScreen

private data class TopDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val icon: ImageVector,
)

private val topDestinations = listOf(
    TopDestination("today", "今天", Icons.Filled.Home, Icons.Outlined.Home),
    TopDestination("journals", "日记", Icons.Filled.Book, Icons.Outlined.Book),
    TopDestination("favorites", "回响", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    TopDestination("profile", "我的", Icons.Filled.Person, Icons.Outlined.PersonOutline),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MomentaryApp(viewModel: MomentaryViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var showCapture by remember { mutableStateOf(false) }
    var selectedJournal by remember { mutableStateOf<Journal?>(null) }
    val showBottomBar = currentRoute in topDestinations.map { it.route }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets.navigationBars,
                    ) {
                        topDestinations.take(2).forEach { destination ->
                            MomentaryNavigationItem(destination, currentRoute == destination.route) {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        FloatingActionButton(
                            onClick = { showCapture = true },
                            modifier = Modifier.height(56.dp),
                            shape = MaterialTheme.shapes.medium,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary,
                            elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(0.dp, 0.dp),
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "记录新片刻")
                        }
                        topDestinations.drop(2).forEach { destination ->
                            MomentaryNavigationItem(destination, currentRoute == destination.route) {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "today") {
            composable("today") {
                TodayScreen(
                    state = state,
                    contentPadding = innerPadding,
                    onAddMoment = viewModel::addMoment,
                    onSelectMood = viewModel::selectMood,
                    onToggleFavorite = viewModel::toggleEntryFavorite,
                    onChat = { navController.navigate("chat") },
                    onSummary = { navController.navigate("summary") },
                )
            }
            composable("journals") {
                JournalsScreen(
                    journals = state.journals,
                    contentPadding = innerPadding,
                    onJournalClick = {
                        selectedJournal = it
                        navController.navigate("journal-detail")
                    },
                )
            }
            composable("favorites") {
                FavoritesScreen(
                    state = state,
                    contentPadding = innerPadding,
                    onToggleEntry = viewModel::toggleEntryFavorite,
                    onToggleChat = viewModel::toggleChatFavorite,
                )
            }
            composable("profile") {
                ProfileScreen(
                    state = state,
                    contentPadding = innerPadding,
                    onSelectPersona = viewModel::selectPersona,
                    onCreatePersona = viewModel::createPersona,
                )
            }
            composable("chat") {
                ChatScreen(
                    messages = state.chatMessages.filter { it.personaId == state.activePersonaId },
                    personas = state.personas,
                    activePersonaId = state.activePersonaId,
                    onBack = navController::popBackStack,
                    onSelectPersona = viewModel::selectPersona,
                    onSend = viewModel::sendChat,
                    onFavorite = viewModel::toggleChatFavorite,
                    onIncludeToday = viewModel::includeChatToday,
                )
            }
            composable("summary") {
                SummaryScreen(
                    state = state,
                    onBack = navController::popBackStack,
                    onSave = {
                        viewModel.saveTodayAsJournal()
                        navController.navigate("journals") {
                            popUpTo("today")
                        }
                    },
                )
            }
            composable("journal-detail") {
                selectedJournal?.let { journal ->
                    JournalDetailScreen(journal = journal, onBack = navController::popBackStack)
                }
            }
        }
    }

    if (showCapture) {
        ModalBottomSheet(onDismissRequest = { showCapture = false }) {
            QuickCaptureSheet(
                onSave = {
                    viewModel.addMoment(it)
                    showCapture = false
                },
                onChat = {
                    if (it.isNotBlank()) viewModel.sendChat(it)
                    showCapture = false
                    navController.navigate("chat")
                },
            )
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.MomentaryNavigationItem(
    destination: TopDestination,
    selected: Boolean,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = if (selected) destination.selectedIcon else destination.icon,
                contentDescription = destination.label,
            )
        },
        label = { androidx.compose.material3.Text(destination.label) },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}
