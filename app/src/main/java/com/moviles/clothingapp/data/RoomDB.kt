package com.moviles.clothingapp.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.moviles.clothingapp.cart.data.CartItemDao
import com.moviles.clothingapp.cart.data.CartItemEntity
import com.moviles.clothingapp.favoritePosts.data.FavoriteDao
import com.moviles.clothingapp.favoritePosts.data.FavoriteEntity

@Database(entities = [FavoriteEntity::class, CartItemEntity::class], version = 1, exportSchema = false)
abstract class RoomDB : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun cartItemDao(): CartItemDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDatabase(context: Context): RoomDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}