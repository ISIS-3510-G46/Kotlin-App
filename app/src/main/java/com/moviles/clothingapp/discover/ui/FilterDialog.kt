package com.moviles.clothingapp.discover.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moviles.clothingapp.ui.utils.DarkGreen
import com.moviles.clothingapp.ui.utils.figtreeFamily



/* Filter dialog pop-up:
*   - Appears after clicking the filter button in section discovery.
*   - This contains all the filter options for the user to adjust.
*   - Features a slider for range filter.
*   - Features a color selection with circles full of the color.
*   - Features the cloth size and category as selectable buttons.
*   - Click outside the dialog to discard the filters.
*   - Click apply changes to confirm the filters.
*
* */
@Composable
fun FilterDialog(
    onDismiss: () -> Unit, // Unit = void (returns void)
    selectedColor: String,
    onColorChange: (String) -> Unit,
    selectedSize: String,
    onSizeChange: (String) -> Unit,
    selectedGroup: String,
    onGroupChange: (String) -> Unit,
    minPrice: String,
    onMinPriceChange: (String) -> Unit,
    maxPrice: String,
    onMaxPriceChange: (String) -> Unit
) {


    /* Convert min/max prices (removing commas) as we store data as "20,000" COP */
    val minPriceValue = minPrice.replace(",", "").toFloatOrNull() ?: 0f
    val maxPriceValue = maxPrice.replace(",", "").toFloatOrNull() ?: 200000f // 200,000 COP

    val stepSize = 1000f // Increments by 1,000 COP
    var sliderPosition by remember { mutableStateOf(minPriceValue..maxPriceValue) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Filtrar Productos", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {


                /* Slider to control the price filter */
                FilterSection(title = "Rango de Precio") {
                    RangeSlider(
                        value = sliderPosition,
                        onValueChange = { newValue ->
                            val newStart = (newValue.start / stepSize).toInt() * stepSize
                            val newEnd = (newValue.endInclusive / stepSize).toInt() * stepSize
                            sliderPosition = newStart..newEnd
                            onMinPriceChange(newStart.toInt().toString())
                            onMaxPriceChange(newEnd.toInt().toString())
                        },
                        valueRange = 0f..200000f,
                        steps = ((200000f - 0f) / stepSize).toInt(), // Steps for 1,000 COP from 0-200k
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = DarkGreen,
                            activeTrackColor = DarkGreen,
                            inactiveTrackColor = Color.LightGray
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("$${sliderPosition.start.toInt()} COP")
                        Text("$${sliderPosition.endInclusive.toInt()} COP")
                    }
                }

                /* Color filters */
                FilterSection(title = "Color") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        /* Mapping of the color variables to system colors to display */
                        val colors = mapOf(
                            "Negro" to Color.Black,
                            "Rojo" to Color.Red,
                            "Gris" to Color.Gray,
                            "Yellow" to Color.Yellow,
                            "Azul" to Color.Blue
                        )

                        colors.forEach { (name, color) ->
                            ColorButton(
                                color = color,
                                isSelected = selectedColor == name,
                                onClick = { onColorChange(name) }
                            )
                        }
                    }
                }

                /* Filter for size */
                FilterSection(title = "Tamaño") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("XS", "S", "M", "L", "XL").forEach { size ->
                            SizeButton(
                                size = size,
                                isSelected = selectedSize == size,
                                onClick = { onSizeChange(size) }
                            )
                        }
                    }
                }

                /* Filter for categories: Men, Women, Kids */
                FilterSection(title = "Categoría") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CategoryButton(
                            text = "Todos",
                            isSelected = selectedGroup == "Todos",
                            onClick = { onGroupChange("Todos") },
                            modifier = Modifier.weight(1f),

                        )
                        CategoryButton(
                            text = "Mujer",
                            isSelected = selectedGroup == "Mujer",
                            onClick = { onGroupChange("Mujer") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CategoryButton(
                            text = "Hombre",
                            isSelected = selectedGroup == "Hombre",
                            onClick = { onGroupChange("Hombre") },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryButton(
                            text = "Niños",
                            isSelected = selectedGroup == "Niños",
                            onClick = { onGroupChange("Niños") },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryButton(
                            text = "Niñas",
                            isSelected = selectedGroup == "Niñas",
                            onClick = { onGroupChange("Niñas") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },

        /* Confirm filters button */
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Aplicar Filtros")
            }
        }
    )
}


/*      Auxiliary composables for better UI:
*
*   - FilterSection: Section for each filter with its title.
*   - ColorButton: Displays the color filter as circles with each color visually.
*   - SizeButton: Displays the size buttons semi-squared.
*   - CategoryButton: Displays the category buttons as ovals.
*
* */


@Composable
fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = TextStyle(fontFamily = figtreeFamily, fontWeight = FontWeight.Normal),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun ColorButton(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) DarkGreen else Color.LightGray,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}

@Composable
fun SizeButton(
    size: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) DarkGreen else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isSelected) DarkGreen else Color.LightGray,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Text(
            text = size,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun CategoryButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) DarkGreen else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isSelected) DarkGreen else Color.LightGray,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

