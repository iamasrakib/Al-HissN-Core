/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.core.common.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
// Code crafted by iamasrakib
class HtmlProviderManager @Inject constructor() {
    // Developed by iamasrakib

    private val FILE_NAME = "blocked.html"
    private val DIR_NAME = "pages"

    fun getBlockedPageUri(context: Context): Uri? {
        return try {
            val cachePath = File(context.cacheDir, DIR_NAME)
            if (!cachePath.exists()) {
                cachePath.mkdirs()
            }
            
            val file = File(cachePath, FILE_NAME)
            if (!file.exists()) {
                context.assets.open(FILE_NAME).use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
            }
            
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


