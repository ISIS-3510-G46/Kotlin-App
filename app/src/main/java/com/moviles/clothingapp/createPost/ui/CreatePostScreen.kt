package com.moviles.clothingapp.createPost.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.moviles.clothingapp.createPost.NewPostViewModel
import com.moviles.clothingapp.ui.utils.DarkGreen
import com.moviles.clothingapp.ui.utils.BottomNavigationBar
import com.moviles.clothingapp.ui.utils.NetworkHelper.isInternetAvailable
import com.moviles.clothingapp.ui.utils.figtreeFamily
import com.moviles.clothingapp.ui.utils.smallNoInternetMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/* Create Post Screen:
*   Takes the imageURI gotten in CameraScreen and sends it to the NewPostViewModel
*   Has an auxiliary function at the bottom just for the dropdown items.
*   Here the user submits the information needed to create a post:
*   -Name, Brand, Size, Category, Group, Color, and Price
*/
@Composable
fun CreatePostScreen(navController: NavController, imageUri: String, viewModel: NewPostViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    /* Send image to the ViewModel */
    LaunchedEffect(imageUri) {
        viewModel.setImage(imageUri)
    }

    /* Validation logic for each field */
    val validationInputs = remember {
        derivedStateOf {
            viewModel.title.value.isNotBlank() &&
                    viewModel.brand.value.isNotBlank() &&
                    viewModel.selectedSize.value.isNotBlank() &&
                    viewModel.selectedCategory.value.isNotBlank() &&
                    viewModel.selectedGroup.value.isNotBlank() &&
                    viewModel.selectedColor.value.isNotBlank() &&
                    viewModel.price.value.isNotBlank()
        }
    }


    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                /* Header and go back button */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
                    }
                    Text(
                        text = "Nueva Publicacion",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                if (!isInternetAvailable(context)) {
                    Log.d("Status Internet", isInternetAvailable(context).toString())
                    smallNoInternetMessage()
                }

                /* Show taken image by user */
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                /* Title field */
                Text(
                    text = "Titulo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                OutlinedTextField(
                    value = viewModel.title.value,
                    onValueChange = { newValue ->
                        if (newValue.length <= 70) {  // max character limit of 70 characters
                            viewModel.setTitle(newValue)
                        }
                    },
                    label = { Text("") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                /* Brand field */
                Text(
                    text = "Marca",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                OutlinedTextField(
                    value = viewModel.brand.value,
                    onValueChange = { newValue ->
                        if (newValue.length <= 30) {
                            viewModel.setBrand(newValue)
                        }
                    },
                    label = { Text("") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                /* Size dropdown */
                Text(
                    text = "Talla",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                ExposedDropdownMenuBoxComponent(
                    label = "",
                    options = listOf("XS", "S", "M", "L", "XL"),
                    selectedOption = viewModel.selectedSize.value,
                    onOptionSelected = { viewModel.setSize(it) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                /* Category dropdown */
                Text(
                    text = "Categoria",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                ExposedDropdownMenuBoxComponent(
                    label = "",
                    options = listOf("Nublado", "Frio", "Lluvia", "Calor"),
                    selectedOption = viewModel.selectedCategory.value,
                    onOptionSelected = { viewModel.setCategory(it) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                /* Group dropdown */
                Text(
                    text = "Grupo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                ExposedDropdownMenuBoxComponent(
                    label = "",
                    options = listOf("Hombre", "Mujer"),
                    selectedOption = viewModel.selectedGroup.value,
                    onOptionSelected = { viewModel.setGroup(it) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                /* Color dropdown */
                Text(
                    text = "Color",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                ExposedDropdownMenuBoxComponent(
                    label = "",
                    options = listOf("Rojo", "Azul", "Verde", "Amarillo", "Negro", "Gris"),
                    selectedOption = viewModel.selectedColor.value,
                    onOptionSelected = { viewModel.setColor(it) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                /* Price field */
                Text(
                    text = "Precio",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                    OutlinedTextField(
                        value = viewModel.price.value,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() } && newValue.length <= 15) {
                                viewModel.setPrice(newValue)
                            }
                        },
                        label = { Text("Precio ($ COP)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                /* Display formatted price with currency */
                if (viewModel.price.value.isNotEmpty()) {
                    Text(
                        text = "$ " + viewModel.formattedPrice.value + " COP",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                        fontFamily = figtreeFamily,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                /* Submit button */
                Button(
                    onClick = {
                        if (validationInputs.value) {
                        viewModel.submitPost { success ->
                            scope.launch {
                                val message =
                                    if (success) "Publicación exitosa" else "Error al publicar: Verifica tu conexion a internet e intenta nuevamente."
                                snackbarHostState.showSnackbar(message)
                                delay(100)
                                navController.navigate("home")
                            }
                        }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Por favor, complete todos los campos.")
                            }
                        }
                    },
                    enabled = validationInputs.value,
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Text("Publicar")
                }



            }
        }
    }
    /* Composable kind of a notification for success or failure of posting: */
    SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(60.dp))
}





/*  AUXILIARY FUNCTION -> Dropdown component for options
*   Takes a set of options and displays them to user.
*   Selected option is remembered.
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuBoxComponent(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedOption) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedText = option
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

