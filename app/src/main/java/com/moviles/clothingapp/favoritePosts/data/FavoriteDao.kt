package com.moviles.clothingapp.favoritePosts.data


import androidx.room.*
import com.squareup.moshi.Json
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE postId = :postId")
    suspend fun deleteFavoriteByPostId(postId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE postId = :postId LIMIT 1)")
    suspend fun isFavoriteByPostId(postId: String): Boolean

    @Query("SELECT brand, COUNT(*) as count FROM favorites WHERE brand IS NOT NULL GROUP BY brand ORDER BY count DESC")
    fun getFavoriteBrands(): Flow<List<BrandCount>>

}

data class BrandCount(

    @Json(name="brand") val brand: String,
    @Json(name="count") val count: Int,
    @Json(name="latitude") val latitude: Double?,
    @Json(name="longitude") val longitude: Double?

)