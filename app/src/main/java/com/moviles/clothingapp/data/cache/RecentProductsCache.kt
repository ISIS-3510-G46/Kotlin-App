package com.moviles.clothingapp.data.cache

import androidx.collection.LruCache
import com.moviles.clothingapp.post.data.PostData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Cache en memoria RESTful: expone un StateFlow que UI puede recolectar. */
object RecentProductsCache {
    // LRU de 1 entrada, valor = lista de hasta 6 PostData
    private val lru = object : LruCache<String, List<PostData>>(1) {
        override fun sizeOf(key: String, value: List<PostData>) = value.size
    }

    // StateFlow interno / external immutable
    private val _state = MutableStateFlow<List<PostData>>(emptyList())
    val flow: StateFlow<List<PostData>> = _state

    /** Guarda en LRU y dispara el flow */
    fun put(list: List<PostData>) {
        lru.put("featured", list)
        _state.value = list
    }

    /** Carga desde LRU si existe */
    fun load() {
        lru["featured"]?.let { _state.value = it }
    }
}
