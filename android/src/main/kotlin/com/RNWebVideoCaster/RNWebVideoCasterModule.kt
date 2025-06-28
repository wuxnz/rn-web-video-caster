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

class RNWebVideoCasterModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val WEB_VIDEO_CASTER_PACKAGE = "com.instantbits.cast.webvideo"
        private const val WEB_VIDEO_CASTER_ACTIVITY = "com.instantbits.cast.webvideo.VideoCasterReceiverActivity"
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
        return Intent().apply {
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            setDataAndType(Uri.parse(videoURL), mimeType ?: "video/*")
            setPackage(WEB_VIDEO_CASTER_PACKAGE)
        }
    }

    private fun createWebVideoCasterIntentWithActivity(videoURL: String, mimeType: String? = null): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            setDataAndType(Uri.parse(videoURL), mimeType ?: "video/*")
            setClassName(WEB_VIDEO_CASTER_PACKAGE, WEB_VIDEO_CASTER_ACTIVITY)
        }
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

        // Check if Web Video Caster is installed
        if (!isWebVideoCasterInstalled()) {
            openPlayStore()
            return
        }

        val intent = createWebVideoCasterIntent(videoURL, options.getString("mimeType"))

        // Add headers if provided
        if (options.hasKey("headers")) {
            val headersMap = options.getMap("headers")
            if (headersMap != null) {
                val headersList = mutableListOf<String>()
                val iterator = headersMap.keySetIterator()
                while (iterator.hasNextKey()) {
                    val key = iterator.nextKey()
                    val value = headersMap.getString(key)
                    if (value != null) {
                        headersList.add(key)
                        headersList.add(value)
                    }
                }
                if (headersList.isNotEmpty()) {
                    intent.putExtra("headers", headersList.toTypedArray())
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

        // Add subtitles if provided
        if (options.hasKey("subtitles")) {
            val subtitlesArray = options.getArray("subtitles")
            if (subtitlesArray != null && subtitlesArray.size() > 0) {
                val subtitleUris = mutableListOf<String>()
                val subtitleNames = mutableListOf<String>()
                val enabledSubtitles = mutableListOf<String>()

                for (i in 0 until subtitlesArray.size()) {
                    val subtitle = subtitlesArray.getMap(i)
                    if (subtitle != null) {
                        val url = subtitle.getString("url")
                        if (url != null) {
                            subtitleUris.add(url)
                            
                            // Add subtitle name
                            val name = subtitle.getString("name") ?: subtitle.getString("language") ?: "Subtitle ${i + 1}"
                            subtitleNames.add(name)
                            
                            // Check if this subtitle should be enabled
                            if (subtitle.hasKey("enabled") && subtitle.getBoolean("enabled")) {
                                enabledSubtitles.add(url)
                            }
                        }
                    }
                }

                if (subtitleUris.isNotEmpty()) {
                    intent.putExtra("subs", subtitleUris.toTypedArray())
                    intent.putExtra("subs.name", subtitleNames.toTypedArray())
                    if (enabledSubtitles.isNotEmpty()) {
                        intent.putExtra("subs.enable", enabledSubtitles.toTypedArray())
                    }
                }
            }
        }

        // Add position if provided
        if (options.hasKey("position")) {
            intent.putExtra("position", options.getInt("position"))
        }

        // Add User-Agent if provided (through headers)
        if (options.hasKey("userAgent")) {
            val userAgent = options.getString("userAgent")
            if (userAgent != null) {
                // Add User-Agent to headers
                val existingHeaders = intent.getStringArrayExtra("headers")?.toMutableList() ?: mutableListOf()
                existingHeaders.add("User-Agent")
                existingHeaders.add(userAgent)
                intent.putExtra("headers", existingHeaders.toTypedArray())
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

        try {
            reactApplicationContext.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            // Try with specific activity class name as fallback
            try {
                val activityIntent = createWebVideoCasterIntentWithActivity(videoURL, options.getString("mimeType"))
                // Copy all the extras from the original intent
                activityIntent.putExtras(intent.extras ?: Bundle())
                reactApplicationContext.startActivity(activityIntent)
            } catch (ex2: ActivityNotFoundException) {
                // If both approaches fail, open Play Store
                openPlayStore()
            }
        }
    }

    @ReactMethod
    fun play(videoURL: String) {
        // Backward compatibility method
        
        // Check if Web Video Caster is installed
        if (!isWebVideoCasterInstalled()) {
            openPlayStore()
            return
        }

        val intent = createWebVideoCasterIntent(videoURL)

        try {
            reactApplicationContext.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            // Try with specific activity class name as fallback
            try {
                val activityIntent = createWebVideoCasterIntentWithActivity(videoURL)
                reactApplicationContext.startActivity(activityIntent)
            } catch (ex2: ActivityNotFoundException) {
                // If both approaches fail, open Play Store
                openPlayStore()
            }
        }
    }

    @ReactMethod
    fun isAppInstalled(callback: com.facebook.react.bridge.Callback) {
        val isInstalled = isWebVideoCasterInstalled()
        callback.invoke(isInstalled)
    }
} 