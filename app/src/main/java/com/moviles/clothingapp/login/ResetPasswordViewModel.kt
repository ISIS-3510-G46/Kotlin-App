package com.moviles.clothingapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/* Reset Password ViewModel: sends the request to firebase and updates status for view to update page. */
class ResetPasswordViewModel(private val auth: FirebaseAuth) : ViewModel() {

    private val _resetPasswordResult = MutableLiveData<ResetPasswordResult>()
    val resetPasswordResult: LiveData<ResetPasswordResult> get() = _resetPasswordResult

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                auth.sendPasswordResetEmail(email).await()
                withContext(Dispatchers.Main) {
                    _resetPasswordResult.value = ResetPasswordResult.Success
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _resetPasswordResult.value =
                        ResetPasswordResult.Failure(e.message ?: "error")
                }
            }
        }
    }

    sealed class ResetPasswordResult {
        data object Success : ResetPasswordResult()
        data class Failure(val errorMessage: String) : ResetPasswordResult()
    }


}