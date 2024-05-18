package com.example.photosapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.photosapp.databinding.ActivityMainBinding
import com.example.photosapp.retrofit.APIinterface
import com.example.photosapp.retrofit.RetrofitInstance
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageAdapter: ImageRecyclerViewAdapter

    private var currentPage = 1
    private val perPage = 30
    private var isLoading = false

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Setting layout for RecyclerView with 3 columns
        val spanCount = 3
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing) // 8dp spacing

        val layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        binding.ImageRecyleView.layoutManager = layoutManager

        val itemDecoration = GridSpacingItemDecoration(spanCount, spacing)
        binding.ImageRecyleView.addItemDecoration(itemDecoration)

        imageAdapter = ImageRecyclerViewAdapter(this)
        binding.ImageRecyleView.adapter = imageAdapter

        binding.ImageRecyleView.addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                fetchImages(page)
            }
        })

        fetchImages(currentPage)
    }

    private fun fetchImages(page: Int) {
        if (isLoading) return
        isLoading = true

        // Cancel any ongoing image loading jobs when fetching new images
        imageAdapter.clearJobs()

        scope.launch {
            try {
                val service = RetrofitInstance.retrofitInstance.create(APIinterface::class.java)
                val response = withContext(Dispatchers.IO) {
                    service.getPhotos(page, perPage, "I87G-vk7uuRo6VTTQA-ksoc46ZrRPchGl67XF2F3QgM")
                }
                imageAdapter.submitList(response)
                currentPage = page
            } catch (e: Exception) {
                e.printStackTrace() // Handle the exception appropriately
            } finally {
                isLoading = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel() // Cancel all coroutines when the activity is destroyed
    }
}
