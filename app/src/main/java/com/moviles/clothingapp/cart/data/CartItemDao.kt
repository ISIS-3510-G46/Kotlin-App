package com.moviles.clothingapp.cart.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {
    @Query("SELECT * FROM cart")
    fun getAllItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartItemEntity)

    @Query("DELETE FROM cart WHERE postId = :postId")
    suspend fun deleteItemByPostId(postId: String)

    @Delete
    suspend fun deleteItem(item: CartItemEntity)
}