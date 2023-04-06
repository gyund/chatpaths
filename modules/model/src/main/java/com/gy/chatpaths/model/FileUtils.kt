package com.gy.chatpaths.model

import android.net.Uri
import android.util.Log
import java.io.File

class FileUtils {
    companion object {
        fun deleteUriImage(imageUriString: String) {
            if (imageUriString.isBlank()) return
            try {
                val uri = Uri.parse(imageUriString)
                uri.path?.let { path ->
                    val fdelete = File(path)
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            Log.d("SC Common", "file Deleted :" + uri.path)
                        } else {
                            Log.d("SC Common", "file not Deleted :" + uri.path)
                        }
                    }
                }
            } catch (e: Exception) {
                // NOTHING JUST RETURN NULL
            }
        }
    }
}
