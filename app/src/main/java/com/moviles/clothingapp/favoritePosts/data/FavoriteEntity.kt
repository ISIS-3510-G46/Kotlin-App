package com.moviles.clothingapp.favoritePosts.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,  // Auto-generated ID

    val postId: String,  // Original post ID
    val name: String,
    val price: String,
    val size: String,
    val brand: String,
    val imageUrl: String,
    val category: String,
    val color: String,
    val group: String,
    val thumbnail: String
)
