package com.gearsnap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gearsnap.theme.*

@Composable
fun GSBottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        BottomNavItem("home", "Home", Icons.Default.Home),
        BottomNavItem("map", "Map", Icons.Default.Place),
        BottomNavItem("gear", "Gear", Icons.Default.Hiking),
        BottomNavItem("community", "Community", Icons.Default.People),
        BottomNavItem("profile", "Profile", Icons.Default.Person)
    )

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ),
        containerColor = GS_NavBg,
        contentColor = GS_NavInactive
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (currentRoute == item.route) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GS_NavActive,
                    selectedTextColor = GS_NavActive,
                    unselectedIconColor = GS_NavInactive,
                    unselectedTextColor = GS_NavInactive,
                    indicatorColor = GS_NavActive.copy(alpha = 0.1f)
                )
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)