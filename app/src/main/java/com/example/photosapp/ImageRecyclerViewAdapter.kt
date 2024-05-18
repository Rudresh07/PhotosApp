package com.example.photosapp

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.photosapp.Data.ImageResponse
import com.example.photosapp.utils.CachingUtil
import kotlinx.coroutines.*

class ImageRecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<ImageRecyclerViewAdapter.ImageViewHolder>() {

    private var images: MutableList<ImageResponse> = mutableListOf()
    private val jobMap = mutableMapOf<ImageView, Job>()

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.Image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_image_layout, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val photo = images[position]
        val imageUrl = photo.urls.regular

        // Cancel any existing job for this ImageView
        jobMap[holder.imageView]?.cancel()

        // Set a placeholder image while loading (optional)
        // holder.imageView.setImageResource(R.drawable.placeholder_image)

        // Launch a new coroutine to download and cache the image
        val job = CoroutineScope(Dispatchers.Main).launch {
            val bitmap = withContext(Dispatchers.IO) {
                CachingUtil.downloadAndCacheImage(imageUrl, context)
            }
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap)
            } else {
                // Handle download error (optional)
                // holder.imageView.setImageResource(R.drawable.error_image)
            }
        }

        // Store the job in the map
        jobMap[holder.imageView] = job
    }

    override fun getItemCount(): Int = images.size

    fun submitList(newImages: List<ImageResponse>) {
        images.addAll(newImages)
        notifyDataSetChanged()
    }

    fun clearJobs() {
        for (job in jobMap.values) {
            job.cancel()
        }
        jobMap.clear()
    }
}
