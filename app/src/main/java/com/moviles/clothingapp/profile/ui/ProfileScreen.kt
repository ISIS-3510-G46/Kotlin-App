package com.moviles.clothingapp.profile.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.moviles.clothingapp.profile.SettingsViewModel
import com.moviles.clothingapp.ui.utils.BottomNavigationBar
import com.moviles.clothingapp.ui.utils.DarkGreen
import com.moviles.clothingapp.ui.utils.dmSansFamily
import com.moviles.clothingapp.ui.utils.figtreeFamily
import java.io.File


@Composable
fun ProfileScreen(
    navController: NavController,
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    viewModel: SettingsViewModel = viewModel()
) {
    val currentUser = auth.currentUser
    val userName by viewModel.userName.observeAsState("Pepito Perez")
    val profileImagePath by viewModel.profileImagePath.observeAsState()
    val userEmail = currentUser?.email ?: ""

    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {

            Column(

                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Perfil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )}
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Picture
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8E1F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImagePath != null) {
                            AsyncImage(
                                model = profileImagePath?.let { File(it) },
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(2.dp, Color.LightGray, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8E1F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Default Profile",
                                    modifier = Modifier.size(60.dp),
                                    tint = Color(0xFF9575CD)
                                )
                            }
                    }}

                    Spacer(modifier = Modifier.height(16.dp))

                    // User Name
                    Text(
                        text = userName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = figtreeFamily
                    )

                    // User Email (if available)
                    if (userEmail.isNotEmpty()) {
                        Text(
                            text = userEmail,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontFamily = figtreeFamily,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Menu Options
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    ProfileMenuItem(
                        icon = Icons.Rounded.Settings,
                        title = "Configuración",
                        onClick = { navController.navigate("settings") }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                    /* TODO: JAIMEEE AQUIIII 3D */
                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Rounded.Article,
                        title = "Mis Artículos",
                        onClick = { navController.navigate("myPosts") }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    ProfileMenuItem(
                        icon = Icons.Rounded.ShoppingCart,
                        title = "Mi Carrito",
                        onClick = { navController.navigate("cart") }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    ProfileMenuItem(
                        icon = Icons.Rounded.CameraAlt,
                        title = "Agregar una prenda",
                        onClick = { navController.navigate("camera") }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cerrar Sesión Button
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DarkGreen
                    ),
                    border = BorderStroke(1.dp, DarkGreen)
                ) {
                    Text(
                        text = "Cerrar Sesion",
                        fontFamily = figtreeFamily,
                        fontWeight = FontWeight.Medium
                    )
                }

            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Cerrar Sesión",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro que deseas cerrar sesión?",
                    fontFamily = figtreeFamily
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        auth.signOut()
                        viewModel.closeSession()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        text = "Cerrar Sesión",
                        color = Color.Red,
                        fontFamily = figtreeFamily,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text(
                        text = "Cancelar",
                        color = DarkGreen,
                        fontFamily = figtreeFamily,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = title,
            fontSize = 16.sp,
            fontFamily = figtreeFamily,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
            contentDescription = "Navigate",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}