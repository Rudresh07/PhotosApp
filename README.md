
# Image Grid App

This is an Android application that displays a scrollable grid of images using a 3-column layout. The images are loaded asynchronously from the Unsplash API. The app features lazy loading, memory and disk caching, and graceful error handling for network and image loading failures.


## Features

3-column grid layout with center-cropped images
Asynchronous image loading from Unsplash API
Lazy loading with cancellation of unnecessary requests
Memory and disk caching for efficient image retrieval
Graceful error handling with placeholders for failed loads


## Implementation detail

Image Grid Layout
Implemented using RecyclerView with a GridLayoutManager for a 3-column layout.

Images are center-cropped to fit within their grid cells.

Image Loading

Images are loaded asynchronously using Kotlin coroutines.

Network requests are made to the Unsplash API to fetch image URLs.

Image loading is managed with a custom ImageLoader class.

Lazy Loading and Cancellation

Implemented using Kotlin coroutines and the Job class for cancellation.

Image loading requests for off-screen images are cancelled to prioritize current viewable items.

Caching

Memory cache is implemented using LruCache.

Disk cache is implemented using the Android DiskLruCache library.

When an image is not found in memory cache, it is fetched from disk cache. If not found in disk cache, it is downloaded from the API and then stored in both caches.

Error Handling

Network and image loading errors are handled gracefully.
A placeholder image is displayed for failed loads.
Error messages are logged for debugging purposes.
## Setup

Setup Instructions
1. Clone the Repository
sh

Copy code

git clone https://github.com/Rudresh07/PhotosApp

cd ImageGridApp

2. Open the Project in Android Studio

Launch Android Studio.

Select Open an existing project.

Navigate to the cloned repository and select the project.
