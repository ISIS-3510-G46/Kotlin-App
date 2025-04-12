package com.moviles.clothingapp.home

import android.util.Log
import androidx.lifecycle.*
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


    private val _postData = MutableLiveData<List<PostData>>()
    val postData: LiveData<List<PostData>> = _postData

    init {
        getPostData()
    }

    private fun getPostData() {
        viewModelScope.launch(Dispatchers.IO) {
            val postResult = postRepository.fetchRepository()
            withContext(Dispatchers.Main) {
                _postData.postValue(postResult ?: emptyList())
            }

        }
    }
}
