package com.smartphonedoctor

import android.app.AppOpsManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartphonedoctor.presentation.viewmodel.HomeViewModel
import com.smartphonedoctor.presentation.viewmodel.ScanState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartphonedoctor.presentation.navigation.BottomNavItem
import com.smartphonedoctor.presentation.screens.HealthScoreScreen
import com.smartphonedoctor.presentation.screens.HistoryScreen
import com.smartphonedoctor.presentation.screens.HomeScreen
import com.smartphonedoctor.presentation.screens.IssuesScreen
import com.smartphonedoctor.presentation.screens.PermissionOnboardingScreen
import com.smartphonedoctor.presentation.screens.SettingsScreen
import com.smartphonedoctor.presentation.ui.theme.SmartPhoneDoctorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartPhoneDoctorTheme {
                var hasUsageStatsPermission by remember { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    mutableStateOf(checkUsageStatsPermission())
                } else {
                    TODO("VERSION.SDK_INT < Q")
                }
                }
                
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                hasUsageStatsPermission = checkUsageStatsPermission()
                            }
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                if (!hasUsageStatsPermission) {
                    PermissionOnboardingScreen(onGrantClicked = {})
                } else {
                    val navController = rememberNavController()
                    Scaffold(
                        bottomBar = { BottomNavigationBar(navController = navController) }
                    ) { innerPadding ->
                        NavigationGraph(navController = navController, modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkUsageStatsPermission(): Boolean {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Issues,
        BottomNavItem.History,
        BottomNavItem.Settings
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(navController, startDestination = BottomNavItem.Home.route, modifier = modifier) {
        composable(BottomNavItem.Home.route) { 
            HomeScreen(
                uiState = uiState,
                onStartScan = { homeViewModel.startScan() },
                onScanFinish = {
                    navController.navigate("health_score")
                }
            ) 
        }
        composable("health_score") {
            val score = (uiState as? ScanState.Success)?.result?.healthScore
            if (score != null) {
                HealthScoreScreen(
                    healthScore = score,
                    onSeeIssuesClick = {
                        navController.navigate(BottomNavItem.Issues.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    }
                )
            } else {
                // Should not happen, but fallback
                HealthScoreScreen(
                    onSeeIssuesClick = {
                        navController.navigate(BottomNavItem.Issues.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    }
                )
            }
        }
        composable(BottomNavItem.Issues.route) { 
            val issues = (uiState as? ScanState.Success)?.result?.issues ?: emptyList()
            IssuesScreen(issues = issues) 
        }
        composable(BottomNavItem.History.route) { HistoryScreen() }
        composable(BottomNavItem.Settings.route) { SettingsScreen() }
    }
}
