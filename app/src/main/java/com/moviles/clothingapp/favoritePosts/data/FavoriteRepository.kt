import android.util.Log
import com.moviles.clothingapp.favoritePosts.data.BrandCount
import com.moviles.clothingapp.ui.utils.RetrofitInstance

class FavoriteRepository {

    private val apiService = RetrofitInstance.apiService

    private suspend fun sendFavoriteToBackend(brand: String, latitude: Double?, longitude: Double?) {
        try {
            val brandCount = BrandCount(brand, count = 1, latitude, longitude)

            val response = apiService.addFavorite(brandCount)
            if (response.isSuccessful) {
                Log.d("Favorites", "Favorite send to backend")
            } else {
                Log.e("Favorites", "Failed sending favorite to back")
            }
        } catch (e: Exception) {
            Log.e("Favorites", "Error sending favorite", e)
        }
    }

    suspend fun addFavoriteBrand(brand: String, latitude: Double?, longitude: Double?) {
        sendFavoriteToBackend(brand, latitude, longitude)
    }
}