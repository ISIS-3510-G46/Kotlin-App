package com.moviles.clothingapp.createPost

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.moviles.clothingapp.createPost.data.ImageStoringRepository
import com.moviles.clothingapp.post.data.PostData
import com.moviles.clothingapp.post.data.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat


/*  Create a new post ViewModel: used to see changes in state of the View
*   Gets the information inserted by the user in the CreatePostScreen and CameraScreen
*   Sends the information gotten by UI to the repository to POST the information into backend and BD
*/
class NewPostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepository()
    private val appwriteRepository = ImageStoringRepository(application.applicationContext)
    private val auth = FirebaseAuth.getInstance()

    /* Get current user id */
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    /* Title field */
    private val _title = mutableStateOf("")
    val title: State<String> = _title

    /* Brand field */
    private val _brand = mutableStateOf("")
    val brand: State<String> = _brand

    /* Dropdown selections */
    /* Size selection */
    private val _selectedSize = mutableStateOf("M")
    val selectedSize: State<String> = _selectedSize

    /* Category selection */
    private val _selectedCategory = mutableStateOf("Calor")
    val selectedCategory: State<String> = _selectedCategory

    /* Group selection */
    private val _selectedGroup = mutableStateOf("Hombre")
    val selectedGroup: State<String> = _selectedGroup

    /* Color selection */
    private val _selectedColor = mutableStateOf("Negro")
    val selectedColor: State<String> = _selectedColor

    /* Price selection */
    private val _price = mutableStateOf("")
    val price: State<String> = _price
    private val _formattedPrice = mutableStateOf("")
    val formattedPrice: State<String> = _formattedPrice


    /* Image taken by user */
    private val _imageUri = mutableStateOf<String?>(null)
    val imageUri: State<String?> = _imageUri


    /* Functions to update the state of fields */
    fun setTitle(newTitle: String) { _title.value = newTitle }
    fun setBrand(brand: String) { _brand.value = brand }
    fun setSize(size: String) { _selectedSize.value = size }
    fun setCategory(category: String) { _selectedCategory.value = category }
    fun setGroup(group: String) { _selectedGroup.value = group }
    fun setColor(color: String) { _selectedColor.value = color }
    fun setPrice(newPrice: String) {
        viewModelScope.launch(Dispatchers.Default) {
        if (newPrice.isEmpty()) {
            _price.value = ""
            _formattedPrice.value = ""
        }
        _price.value = newPrice
        try {
            val digitsOnly = newPrice.replace(",", "")
            val formatted = DecimalFormat("#,###").format(digitsOnly.toLong())
            _formattedPrice.value = formatted
        } catch (e: Exception) {
            _formattedPrice.value = newPrice
        }
        }
    }
    fun setImage(uri: String?) {
        _imageUri.value = uri
    }


    /* Submit Post function to send the data gotten by UI to the repository */
    fun submitPost(onResult: (Boolean) -> Unit) {
        viewModelScope.launch (Dispatchers.IO) {
            try {

                val context = getApplication<Application>().applicationContext

                // Upload Image First
                val imageUrl = withContext(Dispatchers.IO) {
                    _imageUri.value?.let { uriString ->
                        val uri = Uri.parse(uriString)
                        appwriteRepository.uploadImage(context, uri)
                    }
                }



                if (imageUrl == null) {
                    onResult(false)
                    return@launch
                }

                val newPost = PostData(
                    name = title.value,
                    brand = brand.value,
                    size = selectedSize.value,
                    category = selectedCategory.value,
                    group = selectedGroup.value,
                    price = formattedPrice.value,
                    image = imageUrl,
                    color = selectedColor.value,
                    thumbnail = "",
                    userId = currentUserId
                )

                val response = withContext(Dispatchers.IO){
                    repository.createPost(newPost)
                }
                withContext(Dispatchers.Main){
                    onResult(response != null)  // Notify success or failure
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }
}
