package com.example

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class HardikVlogApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        initFirebase()
    }

    private fun initFirebase() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:123456789012:android:9876543210ab")
                    .setApiKey("AIzaSyDummyKeyForDevelopmentPlatform999")
                    .setProjectId("hardik-life-vlog")
                    .setStorageBucket("hardik-life-vlog.appspot.com")
                    .build()
                FirebaseApp.initializeApp(this, options)
                Log.i("HardikVlogApp", "FirebaseApp initialized programmatically with valid options")
            } else {
                Log.i("HardikVlogApp", "FirebaseApp initialized automatically via google-services.json")
            }
        } catch (e: Exception) {
            Log.e("HardikVlogApp", "Error during FirebaseApp initialization: ${e.message}", e)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50L * 1024 * 1024)
                    .build()
            }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .respectCacheHeaders(false)
            .crossfade(true)
            .build()
    }
}
