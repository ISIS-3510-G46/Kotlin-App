package com.moviles.clothingapp.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore // Import the KTX extension
import com.google.firebase.Firebase // Import the main Firebase object
import com.moviles.clothingapp.chat.data.ChatOverview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatListViewModel : ViewModel() {

    private val _chatList = MutableStateFlow<List<ChatOverview>>(emptyList())
    val chatList: StateFlow<List<ChatOverview>> = _chatList

    fun loadChatList(currentUserId: String) {
        val db = Firebase.firestore
        Log.d("FIRESTOREE", "-> loadChatList llamado con currentUserId: $currentUserId")

        val query = db.collection("users")
            .document(currentUserId)
            .collection("chat_overview")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        Log.w("FIRESTORE", query.toString())

        query.addSnapshotListener { snapshot, e ->

            // Manejo básico de errores
            if (e != null) {
                Log.w("FIREBASEE", "Error al escuchar cambios en chat_overview:", e)
                // Considera manejar el estado de error en tu UI
                _chatList.value = emptyList() // Limpiar la lista o mostrar un estado de error
                return@addSnapshotListener
            }

            // Log para indicar que el listener se ha activado (con o sin datos)
            Log.d("FIREBASEE", "Listener de snapshot para chat_overview activado.")


            if (snapshot != null) {
                // Log si se recibieron documentos
                Log.d("FIREBASEE", "Snapshot recibido. Número de documentos: ${snapshot.size()}")

                // Verifica si hay documentos antes de intentar convertirlos
                if (!snapshot.isEmpty) {
                    val chats = snapshot.toObjects(ChatOverview::class.java)
                    // Log para ver cuántos objetos se crearon
                    Log.d("FIREBASEE", "Snapshot convertido a ${chats.size} objetos ChatOverview.")

                    _chatList.value = chats
                    // Log para confirmar que el StateFlow se ha actualizado
                    Log.d("FIREBASEE", "StateFlow _chatList actualizado con ${chats.size} elementos.")

                    // Opcional: Loguea los IDs de los chats recibidos para verificar
                    chats.forEachIndexed { index, chat ->
                        // Asegúrate de que ChatOverview tiene un campo que identifique el chat,
                        // o loguea parte de su contenido.
                        // Ejemplo: Log.d(TAG, "  Chat ${index}: Last Message: ${chat.lastMessageText}")
                        Log.d("FIREBASEE", "  Chat ${index}: Data received.")
                    }

                } else {
                    // Log si el snapshot está vacío (no hay chats para este usuario)
                    Log.d("FIREBASEE", "Snapshot recibido pero está vacío (0 documentos).")
                    _chatList.value = emptyList() // Asegura que la lista esté vacía si el snapshot está vacío
                }

            } else {
                // Este caso no debería ocurrir normalmente con addSnapshotListener,
                // pero es buena práctica manejarlo.
                Log.w("FIREBASEE", "Snapshot recibido es NULL.")
                _chatList.value = emptyList() // Limpiar la lista si el snapshot es nulo
            }
        }

    }
}

