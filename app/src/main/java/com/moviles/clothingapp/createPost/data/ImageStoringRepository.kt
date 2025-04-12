package com.moviles.clothingapp.createPost.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.moviles.clothingapp.BuildConfig
import io.appwrite.Client
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ImageStoringRepository(context: Context) {
    private val client = Client(context)
        .setEndpoint("https://cloud.appwrite.io/v1")
        .setProject("moviles")

    private val storage = Storage(client)

    // Convert URI to File
    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)

        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    // Upload image file in bucket in AppWrite bucket
    suspend fun uploadImage(context: Context, imageUri: Uri): String? {
        return try {
            val imageFile = uriToFile(context, imageUri)
            val inputFile = InputFile.fromFile(imageFile)
            val bucketId = BuildConfig.BUCKET_ID

            val response = storage.createFile(bucketId, "unique()", inputFile)
            Log.d("response", response.toString())
            response.id // Return file ID
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
