package com.moviles.clothingapp.userPostList

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.moviles.clothingapp.post.data.PostRepository
import kotlinx.coroutines.launch

class UserPostListViewModel : ViewModel() {
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val currentUserId = currentUser?.uid
    private val repository = PostRepository()
    private val _posts = MutableStateFlow<List<PostData>>(emptyList())
    val posts: StateFlow<List<PostData>> = _posts


    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return

        val database = getDatabase(context)
        val favoriteDao = database.favoriteDao()

        /* Get favorites from Room */
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = currentUserId?.let { repository.fetchPostsByUser(it) }
                _posts.value = result ?: emptyList()
            } catch (e: Exception) {
                Log.e("UserPostListViewModel", "Error fetching posts from $currentUser: ${e.message}")
            }
        }

        isInitialized = true
    }

}