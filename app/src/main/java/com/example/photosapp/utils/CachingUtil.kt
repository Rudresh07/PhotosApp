package com.example.photosapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.LinkedHashMap

object CachingUtil {

    private const val CACHE_DIR = "image_cache"
    private const val MAX_MEMORY_CACHE_SIZE = 40 * 1024 * 1024 // 40 MB

    // Memory cache with a size limit
    private val memoryCache = object : LinkedHashMap<String, Bitmap>(0, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Bitmap>?): Boolean {
            return size > MAX_MEMORY_CACHE_SIZE / (1024 * 1024)
        }
    }

    private fun getCacheDir(context: Context): File {
        val cacheDir = File(context.cacheDir, CACHE_DIR)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        return cacheDir
    }

    private fun generateUniqueFilename(imageUrl: String): String {
        val filename = URL(imageUrl).path.substringAfterLast("/")
        return filename.replace("[^a-zA-Z0-9.-_]+".toRegex(), "") // Remove special characters
    }

    private fun storeBitmap(bitmap: Bitmap, context: Context, filename: String) {
        // Store in memory cache
        memoryCache[filename] = bitmap

        // Store in disk cache
        val cacheDir = getCacheDir(context)
        val file = File(cacheDir, filename)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle IO Exception
        }
    }

    private fun loadBitmap(context: Context, filename: String): Bitmap? {
        // Check memory cache first
        memoryCache[filename]?.let {
            return it
        }

        // Check disk cache
        val cacheDir = getCacheDir(context)
        val file = File(cacheDir, filename)
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)?.also {
                // Add to memory cache
                memoryCache[filename] = it
            }
        } else {
            null
        }
    }

    suspend fun downloadAndCacheImage(imageUrl: String, context: Context): Bitmap? {
        val filename = generateUniqueFilename(imageUrl)

        // Check memory cache first
        memoryCache[filename]?.let {
            return it
        }

        // Check disk cache if not in memory
        loadBitmap(context, filename)?.let {
            return it
        }

        // Download image if not cached
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                storeBitmap(bitmap, context, filename)
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null // Handle download error (optional)
            }
        }
    }
}
