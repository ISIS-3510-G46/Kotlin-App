package com.moviles.clothingapp.favoritePosts

import FavoriteRepository
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.clothingapp.data.RoomDB.Companion.getDatabase
import com.moviles.clothingapp.favoritePosts.data.BrandCount
import com.moviles.clothingapp.favoritePosts.data.FavoriteEntity
import com.moviles.clothingapp.post.data.PostData
import com.moviles.clothingapp.weatherBanner.data.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class FavoritesViewModel : ViewModel() {

    private val favoriteRepository = FavoriteRepository()

    private val _favorites = MutableStateFlow<List<FavoriteEntity>>(emptyList())
    val favorites: StateFlow<List<FavoriteEntity>> = _favorites

    /* For T2 Question */
    private val _favoriteBrands = MutableStateFlow<List<BrandCount>>(emptyList())
    val favoriteBrands: StateFlow<List<BrandCount>> = _favoriteBrands



    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return

        val database = getDatabase(context)
        val favoriteDao = database.favoriteDao()

        /* Get favorites from Room */
        viewModelScope.launch {
            favoriteDao.getAllFavorites().collect { favorites ->
                _favorites.value = favorites
            }
        }

        /* Get favorite brands from Room */
        viewModelScope.launch {
            favoriteDao.getFavoriteBrands().collect { brands ->
                _favoriteBrands.value = brands
            }
        }

        isInitialized = true
    }





    /* Function to add or remove a favorite */
    fun newFavorite(context: Context, post: PostData) {
        initialize(context)
        val database = getDatabase(context)
        val favoriteDao = database.favoriteDao()

        val weatherRepository = WeatherRepository(context)

        viewModelScope.launch(Dispatchers.Main) {
            val location = weatherRepository.getCurrentLocation()
            val latitude = location?.latitude
            val longitude = location?.longitude

            favoriteRepository.addFavoriteBrand(post.brand, latitude, longitude)


            val isFavorite = post.id?.let { favoriteDao.isFavoriteByPostId(it) }
            if (isFavorite == true) {
                favoriteDao.deleteFavoriteByPostId(post.id)
            } else {
                /* Add to favorites - create new entity with postId */
                post.id?.let {
                    FavoriteEntity(
                        // id will be automatic
                        postId = it,  // original post id
                        name = post.name,
                        price = post.price,
                        size = post.size,
                        brand = post.brand,
                        imageUrl = post.image,
                        category = post.category,
                        color = post.color,
                        group = post.group,
                        thumbnail = post.thumbnail
                    )
                }?.let {
                    favoriteDao.insertFavorite(
                        it
                    )
                }
            }
        }
    }

    /* Check if post is favorite */
    suspend fun isFavorite(context: Context, postId: String): Boolean {
        initialize(context)
        val database = getDatabase(context)
        return database.favoriteDao().isFavoriteByPostId(postId)
    }

    /* Check for favorite brand match */
    fun checkForFavoriteBrandMatch(post: PostData): Boolean {
        Log.d("FavoriteNoti", post.toString())
        val brand = post.brand
        return _favoriteBrands.value.any {
            it.brand == brand && it.count >= 1
        }
    }
}