package com.smartphonedoctor.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Issues : BottomNavItem("issues", "Issues", Icons.Default.Build)
    object History : BottomNavItem("history", "History", Icons.Default.DateRange)
    object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
}
