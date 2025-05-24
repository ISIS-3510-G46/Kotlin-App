package com.moviles.clothingapp.post.data

import android.util.Log
import com.moviles.clothingapp.discover.ui.data.FilterUsageDto
import com.moviles.clothingapp.favoritePosts.data.BrandCount
import com.moviles.clothingapp.ui.utils.RetrofitInstance
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

class PostRepository(/*private val postDao: PostDao*/) {
    private val apiService = RetrofitInstance.apiService

    /* Function to fetch all products from backend */
    suspend fun fetchRepository(): List<PostData>? =
        safeApiCall { apiService.fetchClothes() }

    suspend fun fetchPostsFiltered(): List<PostData>? =
        safeApiCall { apiService.fetchClothesFiltered() }

    suspend fun fetchPostsByCategory(categoryId: String): List<PostData>? =
        safeApiCall { apiService.fetchClothesByCategory(categoryId) }

    suspend fun fetchPostsByUser(userId: String): List<PostData>? =
        safeApiCall { apiService.fetchClothesByCategory(userId) }

    suspend fun createPost(postData: PostData): PostData? =
        safeApiCall { apiService.createPost(postData) }

    suspend fun fetchPostById(id: Int): PostData? =
        safeApiCall { apiService.fetchClothesById(id) }


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

        @GET("clothing")
        suspend fun fetchClothesByUserId(@Query("userId") userId: String): Response<List<PostData>>

        @POST("create-post") // POST a new piece of clothing
        suspend fun createPost(@Body newPost: PostData): Response<PostData>

        @GET("clothing/{id}")
        suspend fun fetchClothesById(@Path("id") id: Int): Response<PostData>

        @POST("favorites/add")
        suspend fun addFavorite(@Body brandCount: BrandCount): Response<Any>

        @POST("filter_usage")
        suspend fun addFilterUsage(@Body body: FilterUsageDto): Response<Unit>
    }
}
