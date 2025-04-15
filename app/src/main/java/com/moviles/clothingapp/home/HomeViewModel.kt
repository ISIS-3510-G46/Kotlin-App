package com.moviles.clothingapp.home

import android.content.Context
import androidx.lifecycle.*
import com.moviles.clothingapp.favoritePosts.FavoritesViewModel
import com.moviles.clothingapp.post.data.PostData
import com.moviles.clothingapp.post.data.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*  HomeViewModel:
*   - Fetches the information from the post repository (the one that connects with the API),
*     to send the information of the products to the Categories and FeaturedProducts views.
*/
class HomeViewModel : ViewModel() {
    private val postRepository = PostRepository()
    private var appContext: Context? = null
    private val favoritesViewModel = FavoritesViewModel()


    private val _postData = MutableLiveData<List<PostData>>()
    val postData: LiveData<List<PostData>> = _postData

    private val _showNewFavoriteBrandBanner = MutableLiveData<Boolean>(false)
    val showNewFavoriteBrandBanner: LiveData<Boolean> = _showNewFavoriteBrandBanner

    private val _newFavoriteBrandPosts = MutableLiveData<List<PostData>>(emptyList())
    val newFavoriteBrandPosts: LiveData<List<PostData>> = _newFavoriteBrandPosts

    /* Last loaded posts before refreshing */
    private var lastKnownPosts: List<PostData> = emptyList()

    init {
        getPostData()
    }

    fun setContext(context: Context) {
        appContext = context.applicationContext
        favoritesViewModel.initialize(appContext!!)
    }


    private fun getPostData() {
        viewModelScope.launch(Dispatchers.IO) {
            val postResult = postRepository.fetchRepository()
            withContext(Dispatchers.Main) {
                _postData.postValue(postResult ?: emptyList())
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            val postResult = postRepository.fetchRepository() ?: emptyList()

            /* New posts that were not fetched previously */
            val newPosts = if (lastKnownPosts.isNotEmpty()) {
                postResult.filter { newPost ->
                    !lastKnownPosts.any { it.id == newPost.id }
                }
            } else {
                emptyList()
            }

            /* Check if any new posts match user favorite brands */
            val matchingPosts = newPosts.filter { post ->
                favoritesViewModel.checkForFavoriteBrandMatch(post)
            }

            withContext(Dispatchers.Main) {
                _postData.postValue(postResult)
                lastKnownPosts = postResult

                /* If any matching post show banner */
                if (matchingPosts.isNotEmpty()) {
                    _newFavoriteBrandPosts.postValue(matchingPosts)
                    _showNewFavoriteBrandBanner.postValue(true)
                }
            }
        }
    }

    fun closeNewFavoriteBrandBanner() {
        _showNewFavoriteBrandBanner.postValue(false)
    }
}
