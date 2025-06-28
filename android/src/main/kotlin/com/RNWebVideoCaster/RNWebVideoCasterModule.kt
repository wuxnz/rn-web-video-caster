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

        // Create intent exactly like the working play() method
        val intent = createWebVideoCasterIntent(videoURL, options.getString("mimeType"))
        
        // Debug logging - log the basic intent first
        Log.d("RNWebVideoCaster", "Basic intent created successfully like play() method")
        Log.d("RNWebVideoCaster", "Package: ${intent.`package`}")
        Log.d("RNWebVideoCaster", "Data: ${intent.data}")
        Log.d("RNWebVideoCaster", "Type: ${intent.type}")

        // Now add extras one by one, with safety checks
        try {
            // Add title if provided (most common and safe extra)
            if (options.hasKey("title")) {
                val title = options.getString("title")
                if (title != null && title.isNotEmpty()) {
                    intent.putExtra("title", title)
                    Log.d("RNWebVideoCaster", "Added title: $title")
                }
            }

            // Add headers using the string array format that Web Video Caster expects
            val headersList = mutableListOf<String>()
            
            if (options.hasKey("headers")) {
                val headersMap = options.getMap("headers")
                if (headersMap != null) {
                    val iterator = headersMap.keySetIterator()
                    while (iterator.hasNextKey()) {
                        val key = iterator.nextKey()
                        val value = headersMap.getString(key)
                        if (value != null && value.isNotEmpty()) {
                            headersList.add(key)
                            headersList.add(value)
                            Log.d("RNWebVideoCaster", "Added header: $key = $value")
                        }
                    }
                }
            }

            // Handle deprecated userAgent property - add to headers array
            if (options.hasKey("userAgent")) {
                val userAgent = options.getString("userAgent")
                if (userAgent != null && userAgent.isNotEmpty()) {
                    // Check if User-Agent is not already in the headers list
                    var userAgentExists = false
                    for (i in 0 until headersList.size step 2) {
                        if (headersList[i] == "User-Agent") {
                            userAgentExists = true
                            break
                        }
                    }
                    if (!userAgentExists) {
                        headersList.add("User-Agent")
                        headersList.add(userAgent)
                        Log.d("RNWebVideoCaster", "Added User-Agent from userAgent property: $userAgent")
                    }
                }
            }
            
            // Add headers array to intent if we have any headers
            if (headersList.isNotEmpty()) {
                val headersArray = headersList.toTypedArray()
                intent.putExtra("headers", headersArray)
                Log.d("RNWebVideoCaster", "Added headers array with ${headersList.size / 2} header pairs")
            }

            // Add poster URL if provided
            if (options.hasKey("posterURL")) {
                val posterURL = options.getString("posterURL")
                if (posterURL != null && posterURL.isNotEmpty()) {
                    intent.putExtra("poster", posterURL)
                    Log.d("RNWebVideoCaster", "Added poster: $posterURL")
                }
            }

            // Add position if provided and valid
            if (options.hasKey("position")) {
                val position = options.getInt("position")
                if (position >= 0) {
                    intent.putExtra("position", position)
                    Log.d("RNWebVideoCaster", "Added position: $position")
                }
            }

            // Add subtitles if provided - use first subtitle only
            if (options.hasKey("subtitles")) {
                val subtitlesArray = options.getArray("subtitles")
                if (subtitlesArray != null && subtitlesArray.size() > 0) {
                    val subtitle = subtitlesArray.getMap(0)
                    if (subtitle != null) {
                        val url = subtitle.getString("url")
                        if (url != null && url.isNotEmpty()) {
                            intent.putExtra("subtitle", url)
                            Log.d("RNWebVideoCaster", "Added subtitle: $url")
                        }
                    }
                }
            }

            // Add filename if provided and clean
            if (options.hasKey("filename")) {
                val filename = options.getString("filename")
                if (filename != null && filename.isNotEmpty()) {
                    intent.putExtra("filename", filename)
                    Log.d("RNWebVideoCaster", "Added filename: $filename")
                }
            }

            // Add boolean options only if explicitly true
            if (options.hasKey("hideVideoAddress") && options.getBoolean("hideVideoAddress")) {
                intent.putExtra("secure_uri", true)
                Log.d("RNWebVideoCaster", "Added secure_uri: true")
            }

            if (options.hasKey("suppressErrorMessage") && options.getBoolean("suppressErrorMessage")) {
                intent.putExtra("suppress_error_message", true)
                Log.d("RNWebVideoCaster", "Added suppress_error_message: true")
            }

            // Add file size if provided and valid
            if (options.hasKey("size")) {
                val size = options.getInt("size")
                if (size > 0) {
                    intent.putExtra("size", size.toLong())
                    Log.d("RNWebVideoCaster", "Added size: $size")
                }
            }

            Log.d("RNWebVideoCaster", "All extras added successfully, launching...")
            
        } catch (ex: Exception) {
            Log.e("RNWebVideoCaster", "Error adding extras to intent", ex)
            // Continue with basic intent like play() method does
        }

        // Launch exactly like the working play() method
        try {
            reactApplicationContext.startActivity(intent)
            Log.d("RNWebVideoCaster", "Successfully launched Web Video Caster")
        } catch (ex: ActivityNotFoundException) {
            Log.e("RNWebVideoCaster", "Web Video Caster not found, opening Play Store", ex)
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