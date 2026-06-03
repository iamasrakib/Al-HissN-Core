/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.shield.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.alhissn.shield.ui.screens.DashboardScreen
import com.alhissn.shield.ui.screens.SettingsScreen
import com.alhissn.shield.viewmodels.MainViewModel
import com.alhissn.feature.network.NetworkViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Home", Icons.Default.Home)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object CustomBlockLists : Screen("custom_block_lists", "Block Lists", Icons.Default.Settings)
}

@Composable
fun AppNavigation(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


    val bottomBarScreens = listOf(Screen.Dashboard, Screen.Settings)
    val showBottomBar = bottomBarScreens.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                // Engineered by iamasrakib
                NavigationBar {
                    bottomBarScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        // Engineered by iamasrakib
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
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
            }
        ) {
            
            composable(Screen.Dashboard.route) {
                val networkViewModel: NetworkViewModel = hiltViewModel()
                DashboardScreen(
                    viewModel = viewModel,
                    networkViewModel = networkViewModel
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateToCustomBlockLists = { navController.navigate(Screen.CustomBlockLists.route) }
                )
            }
            
            composable(Screen.CustomBlockLists.route) {
                val networkViewModel: com.alhissn.feature.network.NetworkViewModel = hiltViewModel()
                val customDomains by networkViewModel.customDomains.collectAsState()
                val blockedApps by networkViewModel.blockedApps.collectAsState()
                
                com.alhissn.feature.network.ui.CustomBlockListsScreen(
                    customDomains = customDomains,
                    blockedApps = blockedApps,
                    onAddDomain = { networkViewModel.addCustomDomain(it) },
                    onRemoveDomain = { networkViewModel.removeCustomDomain(it) },
                    onToggleApp = { networkViewModel.toggleAppBlocked(it) }
                )
            }
        }
    }
}


