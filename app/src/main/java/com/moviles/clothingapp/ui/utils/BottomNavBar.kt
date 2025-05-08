package com.moviles.clothingapp.ui.utils

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


/* Bottom Navigation Bar:
*   - Uses colors and icons of figma design.
*   - When clicked is supposed to go to each section (different page) - also different package.
*   - Each nav item has a route established here and in the navigation package - very important to name them equally.
 */
@Composable
// TODO: Implementar la funcion de ir a donde se pulsa para cada boton.
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = DarkGreen
    ) {
        val items = listOf(
            BottomNavItem("home", Icons.Rounded.Home, "Home"), // Implemented: YES
            BottomNavItem("discover/", Icons.Rounded.Explore, "Discover"), // Implemented: YES
            BottomNavItem("cart", Icons.Rounded.ShoppingCart, "Cart"), // Implemented: YES
            BottomNavItem("camera", Icons.Rounded.CameraAlt, "Camera"), // Implemented: YES
            BottomNavItem("map/", Icons.Rounded.Map, "Map"), // Implemented: YES
            BottomNavItem("chat", Icons.Rounded.ChatBubble, "Chat") // Implemented: -
        )

        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    if (currentRoute != null) {
                        Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(32.dp),
                            tint = if (currentRoute.startsWith(item.route)) Color.White else Color.Gray)
                    }
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent // background color
                )
            )
        }
    }
}

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)
