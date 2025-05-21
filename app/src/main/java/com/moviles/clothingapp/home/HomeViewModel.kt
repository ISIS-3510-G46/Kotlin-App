package com.moviles.clothingapp.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.moviles.clothingapp.home.data.cache.RecentProductsCache
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
    private val _featured = MutableLiveData<List<PostData>>()
    private val postRepository = PostRepository()
    private var appContext: Context? = null
    private val favoritesViewModel = FavoritesViewModel()


    private val _postData = MutableLiveData<List<PostData>>()
    val postData: LiveData<List<PostData>> = _postData

    private val _showNewFavoriteBrandBanner = MutableLiveData<Boolean>(false)
    val showNewFavoriteBrandBanner: LiveData<Boolean> = _showNewFavoriteBrandBanner

    private val _newFavoriteBrandPosts = MutableLiveData<List<PostData>>()
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
            val postResult: List<PostData> = postRepository.fetchRepository() ?: emptyList()

            withContext(Dispatchers.Main) {
                _postData.value = postResult
                val recent = postResult.takeLast(6)
                if (recent.isNotEmpty()) {
                    RecentProductsCache.put(recent)
                    _featured.value = recent
                }
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


            val recent = postResult.takeLast(6)
            if (recent.isNotEmpty()) {
                Log.d("RecentProductsCache", "putting refresh recent.size=${recent.size}")
                RecentProductsCache.put(recent)
                _featured.postValue(recent)
            } else {
                Log.d("RecentProductsCache", "skip caching empty recent on refresh")
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
