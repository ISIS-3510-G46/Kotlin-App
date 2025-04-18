package com.moviles.clothingapp.ui.utils

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache

object CoilProvider {
    fun get(ctx: Context) = ImageLoader.Builder(ctx)
        .memoryCache { MemoryCache.Builder(ctx).maxSizePercent(0.20).build() }
        .diskCache {
            DiskCache.Builder()
                .directory(ctx.cacheDir.resolve("image_cache"))
                .maxSizeBytes(100L * 1024 * 1024)   // 100MB
                .build()
        }
        .crossfade(true)
        .build()
}
