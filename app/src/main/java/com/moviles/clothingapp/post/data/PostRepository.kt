package com.moviles.clothingapp.post.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.moviles.clothingapp.discover.ui.data.FilterUsageDto
import com.moviles.clothingapp.favoritePosts.data.BrandCount
import com.moviles.clothingapp.ui.utils.RetrofitInstance
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

class PostRepository(/*private val postDao: PostDao*/) {
    private val apiService = RetrofitInstance.apiService

    /* Get all posts from local database TODO: Connect this with ROM or something for cache maybe?
    fun getPosts(): LiveData<List<PostData>> {
        return postDao.getPosts()
    }
    */

    /* Function to fetch all products from backend */
    suspend fun fetchRepository(): List<PostData>? {
        return try {
            val response = apiService.fetchClothes()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("PostRepository", "Response failed: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Error: ${e.message}")
            null
        }
    }


    /* Fetch products by category */
    suspend fun fetchPostsFiltered(): List<PostData>? {
        return try {
            val response = apiService.fetchClothesFiltered()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("PostRepository", "Response failed: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Error: ${e.message}")
            null
        }
    }


    /* Function used to search clothing of a specific category */
    suspend fun fetchPostsByCategory(categoryId: String): List<PostData>? {
        return try {
            val response = apiService.fetchClothesByCategory(categoryId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("PostRepository", "Error fetching category: ${e.message}")
            null
        }
    }

    /* Create Post function to post a new clothing item */
    suspend fun createPost(postData: PostData): PostData? {
        return try {
            val response = apiService.createPost(postData)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("PostRepository", "Error creating post: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception: ${e}")
            null
        }
    }
            
    suspend fun fetchPostById(id: Int): PostData? {
        return safeApiCall { apiService.fetchClothesById(id) }
    }
    
    
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): T? {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("PostRepository", "Error ${response.code()}: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Network error: ${e.message}")
            null
        }
    }





    /* Retrieves information using FAST API, testing with this return format:
    *       Retrofit retrieves the information which has this return format:
    *       [{name: "", price:"", brand:"", image:""}, {...}, ...]
    *       JSON response is parsed to our model class with moshi.
    * */
    interface ApiService {
        @GET("clothing")
        suspend fun fetchClothes(): Response<List<PostData>>

        @GET("clothing") //TODO documentation
        suspend fun fetchClothesFiltered(): Response<List<PostData>>


        @GET("clothing/category/{categoryId}") // Fetch by category
        suspend fun fetchClothesByCategory(@retrofit2.http.Path("categoryId") categoryId: String): Response<List<PostData>>


        @POST("create-post") // POST a new piece of clothing
        suspend fun createPost(@Body newPost: PostData): Response<PostData>



        @GET("clothing/{id}")
        suspend fun fetchClothesById(@retrofit2.http.Path("id") id: Int): Response<PostData>

        @POST("favorites/add")
        suspend fun addFavorite(@Body brandCount: BrandCount): Response<Any>

        @POST("filter_usage")
        suspend fun addFilterUsage(@Body body: FilterUsageDto): Response<Unit>
    }
}
