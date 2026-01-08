package br.com.ifrs.meuifpoa.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.ifrs.meuifpoa.R
import br.com.ifrs.meuifpoa.ui.MainScreen as MainScreenRoute

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    companion object {
        val Home = BottomNavItem(MainScreenRoute.Home.route, Icons.Default.Home, "Início")
        val Notas = BottomNavItem(MainScreenRoute.Notas.route, Icons.Default.DateRange, "Notas")
        val Noticias = BottomNavItem(MainScreenRoute.Noticias.route, Icons.AutoMirrored.Filled.List, "Notícias")
        val Perfil = BottomNavItem(MainScreenRoute.Perfil.route, Icons.Default.AccountCircle, "Perfil")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem.Home.copy(label = stringResource(id = R.string.home_label)),
        BottomNavItem.Notas.copy(label = stringResource(id = R.string.notas_bar)),
        BottomNavItem.Noticias.copy(label = stringResource(id = R.string.noticias_bar)),
        BottomNavItem.Perfil.copy(label = stringResource(id = R.string.perfil_bar))
    )

    var showMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreen = bottomNavItems.find { item -> currentDestination?.hierarchy?.any { it.route == item.route } ?: false }
        ?: bottomNavItems[0]

    Scaffold(
        topBar = {
             TopAppBar(
                title = { Text(text = currentScreen.label) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button_description))
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showAboutDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = stringResource(R.string.about_button_description))
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_options_button_description))
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            bottomNavItems.forEach { screen ->
                                if (screen.route != BottomNavItem.Perfil.route) { // Compare routes for safety
                                    DropdownMenuItem(
                                        text = { Text(screen.label) },
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                            showMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.Black,
                            unselectedTextColor = Color.Black,
                            indicatorColor = MaterialTheme.colorScheme.primary // Indicator matches background
                        ),
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        if (showAboutDialog) {
            AboutDialog(onDismiss = { showAboutDialog = false })
        }

        NavHost(
            navController,
            startDestination = MainScreenRoute.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(MainScreenRoute.Home.route) { HomeScreen() }
            composable(MainScreenRoute.Notas.route) { NotasScreen() }
            composable(MainScreenRoute.Noticias.route) { NoticiasScreen() }
            composable(MainScreenRoute.Perfil.route) { PerfilScreen(onLogout = onLogout) }
        }
    }
}

@Composable
private fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.msg_titulo_sobre)) },
        text = { Text(stringResource(id = R.string.msg_sobre)) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ok_button))
            }
        }
    )
}
