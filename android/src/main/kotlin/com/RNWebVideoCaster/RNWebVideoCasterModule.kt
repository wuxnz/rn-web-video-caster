package com.RNWebVideoCaster

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableArray
import android.content.Intent
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log

class RNWebVideoCasterModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val WEB_VIDEO_CASTER_PACKAGE = "com.instantbits.cast.webvideo"
    }

    override fun getName(): String {
        return "RNWebVideoCaster"
    }

    private fun isWebVideoCasterInstalled(): Boolean {
        return try {
            reactApplicationContext.packageManager.getPackageInfo(WEB_VIDEO_CASTER_PACKAGE, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun createWebVideoCasterIntent(videoURL: String, mimeType: String? = null): Intent {
        // Create intent exactly as shown in official documentation
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(videoURL), mimeType ?: "video/*")
        intent.setPackage(WEB_VIDEO_CASTER_PACKAGE)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return intent
    }



    private fun openPlayStore() {
        try {
            val marketIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=$WEB_VIDEO_CASTER_PACKAGE")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            reactApplicationContext.startActivity(marketIntent)
        } catch (e: ActivityNotFoundException) {
            // Fallback to web browser if Play Store is not available
            val webIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=$WEB_VIDEO_CASTER_PACKAGE")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            reactApplicationContext.startActivity(webIntent)
        }
    }

    @ReactMethod
    fun playVideo(options: ReadableMap) {
        val videoURL = options.getString("videoURL")
        if (videoURL == null) {
            throw IllegalArgumentException("videoURL is required")
        }

        val intent = createWebVideoCasterIntent(videoURL, options.getString("mimeType"))

        // Add headers if provided - Web Video Caster expects JSON string format
        if (options.hasKey("headers")) {
            val headersMap = options.getMap("headers")
            if (headersMap != null) {
                // Convert to JSON string like the working SendIntentAndroid example
                val headersJson = StringBuilder("{")
                val iterator = headersMap.keySetIterator()
                var first = true
                while (iterator.hasNextKey()) {
                    val key = iterator.nextKey()
                    val value = headersMap.getString(key)
                    if (value != null) {
                        if (!first) headersJson.append(",")
                        headersJson.append("\"$key\":\"$value\"")
                        first = false
                    }
                }
                headersJson.append("}")
                
                if (!first) { // Only add if we have headers
                    intent.putExtra("headers", headersJson.toString())
                }
            }
        }

        // Add title if provided
        if (options.hasKey("title")) {
            intent.putExtra("title", options.getString("title"))
        }

        // Add poster URL if provided
        if (options.hasKey("posterURL")) {
            intent.putExtra("poster", options.getString("posterURL"))
        }

        // Add subtitles if provided - use official format (single subtitle)
        if (options.hasKey("subtitles")) {
            val subtitlesArray = options.getArray("subtitles")
            if (subtitlesArray != null && subtitlesArray.size() > 0) {
                val subtitle = subtitlesArray.getMap(0) // Use first subtitle as per official docs
                if (subtitle != null) {
                    val url = subtitle.getString("url")
                    if (url != null) {
                        intent.putExtra("subtitle", url)
                    }
                }
            }
        }

        // Add position if provided
        if (options.hasKey("position")) {
            intent.putExtra("position", options.getInt("position"))
        }

        // Handle deprecated userAgent property for backward compatibility
        if (options.hasKey("userAgent")) {
            val userAgent = options.getString("userAgent")
            if (userAgent != null) {
                // Check if headers already exist and add User-Agent if not already present
                val existingHeaders = intent.getStringExtra("headers")
                if (existingHeaders != null) {
                    // Parse existing JSON and add User-Agent if not present
                    if (!existingHeaders.contains("User-Agent")) {
                        val updatedHeaders = existingHeaders.dropLast(1) + ",\"User-Agent\":\"$userAgent\"}"
                        intent.putExtra("headers", updatedHeaders)
                    }
                } else {
                    // No existing headers, create new JSON with just User-Agent
                    intent.putExtra("headers", "{\"User-Agent\":\"$userAgent\"}")
                }
            }
        }

        // Add file size if provided
        if (options.hasKey("size")) {
            intent.putExtra("size", options.getInt("size").toLong())
        }

        // Add filename if provided
        if (options.hasKey("filename")) {
            intent.putExtra("filename", options.getString("filename"))
        }

        // Add secure URI option (hide video address)
        if (options.hasKey("hideVideoAddress") && options.getBoolean("hideVideoAddress")) {
            intent.putExtra("secure_uri", true)
        }

        // Add suppress error message option
        if (options.hasKey("suppressErrorMessage") && options.getBoolean("suppressErrorMessage")) {
            intent.putExtra("suppress_error_message", true)
        }

        // Debug logging
        Log.d("RNWebVideoCaster", "Attempting to launch Web Video Caster with intent:")
        Log.d("RNWebVideoCaster", "Package: ${intent.`package`}")
        Log.d("RNWebVideoCaster", "Data: ${intent.data}")
        Log.d("RNWebVideoCaster", "Type: ${intent.type}")
        Log.d("RNWebVideoCaster", "Extras: ${intent.extras}")
        
        try {
            reactApplicationContext.startActivity(intent)
            Log.d("RNWebVideoCaster", "Successfully launched Web Video Caster")
        } catch (ex: ActivityNotFoundException) {
            Log.e("RNWebVideoCaster", "Web Video Caster not found, opening Play Store", ex)
            // Open Play Store if it fails to launch the app because the package doesn't exist
            openPlayStore()
        } catch (ex: Exception) {
            Log.e("RNWebVideoCaster", "Unexpected error launching Web Video Caster", ex)
            openPlayStore()
        }
    }

    @ReactMethod
    fun play(videoURL: String) {
        // Backward compatibility method
        val intent = createWebVideoCasterIntent(videoURL)

        try {
            reactApplicationContext.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            // Open Play Store if it fails to launch the app because the package doesn't exist
            openPlayStore()
        }
    }

    @ReactMethod
    fun isAppInstalled(callback: com.facebook.react.bridge.Callback) {
        val isInstalled = isWebVideoCasterInstalled()
        callback.invoke(isInstalled)
    }
} 