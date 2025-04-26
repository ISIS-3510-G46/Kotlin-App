package com.moviles.clothingapp.ui.utils


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(searchText: String, onSearchTextChange: (String) -> Unit, onSearchSubmit: () -> Unit, navController: NavController) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        TextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { onSearchSubmit() }
            ),
            placeholder = {
                Text(
                    "Buscar",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Normal
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray
                )
            },
            textStyle = TextStyle(fontSize = 16.sp),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = superLightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = { navController.navigate("favorites") },
            modifier = Modifier
                .size(50.dp)
                .background(color = superLightGray, shape = RoundedCornerShape(8.dp) )
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = "Favorites",
                tint = Color.Red
            )
        }
    }
}

