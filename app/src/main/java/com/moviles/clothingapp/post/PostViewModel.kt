package com.moviles.clothingapp.post

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.clothingapp.post.data.PostData
import com.moviles.clothingapp.post.data.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostViewModel : ViewModel() {
    private val repository = PostRepository()

    private val _posts = MutableStateFlow(emptyList<PostData>())
    val posts: StateFlow<List<PostData>> get() = _posts

    private val _post = MutableStateFlow<PostData?>(null)
    val post: StateFlow<PostData?> get() = _post

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _imageUrl = MutableStateFlow<String?>(null)

    init {
        fetchPostsFiltered()
    }

    private fun fetchPostsFiltered() {
        viewModelScope.launch(Dispatchers.IO) {  /* Get data with IO thread pool */
            val result = repository.fetchPostsFiltered() // Ensure repository is returning data
            withContext(Dispatchers.Main) {
                _posts.value = result ?: emptyList()
            }
        }
    }


    /* Fetch post by ID */
    fun fetchPostById(id: Int) {
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            try {
                val result = repository.fetchPostById(id)
                withContext(Dispatchers.Main) {
                    _post.value = result
                    result?.image?.let { fetchImageUrl(it) }
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetching post by ID $id: ${e.message}")
                withContext(Dispatchers.Main) {
                    _post.value = null
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }

    fun fetchImageUrl(fileId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val projectId = "moviles"
            val bucketId = "67ddf3860035ee6bd725"
            val url =
                "https://cloud.appwrite.io/v1/storage/buckets/$bucketId/files/$fileId/view?project=$projectId"
            _imageUrl.value = url
        }
    }
}

