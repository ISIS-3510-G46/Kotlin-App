package com.moviles.clothingapp.post.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Json


/* Decorators help moshi assign values of the JSON: */
data class PostData(
    @Json(name="id") val id: String?=null, // Autoasigned in postgreSQL
    @Json(name="name") val name: String,
    @Json(name="price") val price: String,
    @Json(name="brand") val brand: String,
    @Json(name="category") val category: String,
    @Json(name="image") val image: String,
    @Json(name="color") val color: String,
    @Json(name="size") val size: String,
    @Json(name="group") val group: String,
    @Json(name="thumbnail") val thumbnail: String,
    @Json(name="userId") val userId: String = ""

)

/* DAO for the PostsData */
class PostDao {
    private val postList = mutableListOf<PostData>()
    private val posts = MutableLiveData<List<PostData>>()

    init{
        posts.value = postList
    }

    fun getPosts() = posts as LiveData<List<PostData>>
}
