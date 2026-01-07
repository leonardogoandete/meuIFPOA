package br.com.ifrs.meuifpoa.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.ifrs.meuifpoa.ui.MainScreen as MainScreenRoute

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(MainScreenRoute.Home.route, Icons.Default.Home, "Início")
    object Notas : BottomNavItem(MainScreenRoute.Notas.route, Icons.Default.DateRange, "Notas")
    object Noticias : BottomNavItem(MainScreenRoute.Noticias.route, Icons.Default.List, "Notícias")
    object Perfil : BottomNavItem(MainScreenRoute.Perfil.route, Icons.Default.AccountCircle, "Perfil")
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Notas,
        BottomNavItem.Noticias,
        BottomNavItem.Perfil,
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
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
