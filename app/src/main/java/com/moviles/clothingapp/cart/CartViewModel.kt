package com.moviles.clothingapp.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import com.moviles.clothingapp.cart.data.CartItemEntity
import com.moviles.clothingapp.post.data.PostData
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.moviles.clothingapp.data.RoomDB.Companion.getDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers


class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItemEntity>>(emptyList())
    val cartItems: StateFlow<List<CartItemEntity>> = _cartItems

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return

        val database = getDatabase(context)
        val cartItemDao = database.cartItemDao()

        /* Get items from Room */
        viewModelScope.launch {
            cartItemDao.getAllItems().collect { items ->
                _cartItems.value = items
            }
        }

        isInitialized = true
    }

    fun addToCart(context: Context, product: PostData) {
        initialize(context)
        val database = getDatabase(context)
        val cartItemDao = database.cartItemDao()

        viewModelScope.launch (Dispatchers.Main) {
            Log.d(
                "CART",
                "Adding product to cart: ${product.name}, ID: ${product.id}, Price: ${product.price}"
            )
            product.id?.let {
                CartItemEntity(
                    // id will be automatic
                    postId = it,  // original post id
                    name = product.name,
                    price = product.price,
                    size = product.size,
                    brand = product.brand,
                    imageUrl = product.image,
                    category = product.category,
                    color = product.color,
                    group = product.group,
                    thumbnail = product.thumbnail,
                    quantity = 1
                )
            }?.let {
                cartItemDao.insertItem(
                    it
                )
            }
        }
    }

    suspend fun removeFromCart(context: Context, productId: String) {
        initialize(context)
        val database = getDatabase(context)
        return database.cartItemDao().deleteItemByPostId(productId)
    }

}

