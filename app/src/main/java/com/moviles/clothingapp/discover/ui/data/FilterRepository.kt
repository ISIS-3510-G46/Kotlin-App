import com.moviles.clothingapp.ui.utils.RetrofitInstance
import android.util.Log
import com.moviles.clothingapp.discover.ui.data.FilterUsageDto

class FilterRepository {

    private val api = RetrofitInstance.apiService

    suspend fun send(type: String, value: String,
                     lat: Double?, lon: Double?) {
        try {
            val dto = FilterUsageDto(type, value, lat, lon)
            val r = api.addFilterUsage(dto)
            if (r.isSuccessful) Log.d("FilterUsage","sent")
            else Log.e("FilterUsage","resp ${r.code()}")
        } catch (e: Exception) {
            Log.e("FilterUsage","net err", e)
        }
    }
}

