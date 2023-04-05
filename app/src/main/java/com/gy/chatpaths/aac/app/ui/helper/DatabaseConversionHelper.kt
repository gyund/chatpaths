package com.gy.chatpaths.aac.app.ui.helper

import android.content.Context
import android.graphics.drawable.Drawable
import com.gy.chatpaths.aac.app.DrawableUtils
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.model.Path
import com.gy.chatpaths.model.PathCollection

class DatabaseConversionHelper {

    companion object {

        private fun getCollectionDrawable(
            context: Context,
            primaryImage: String?,
            secondaryImage: String?,
        ): Drawable? {
            val img = if (!primaryImage.isNullOrBlank()) {
                DrawableUtils.getDrawableImage(context, primaryImage, null)
            } else {
                DrawableUtils.getDrawableImage(
                    context,
                    secondaryImage,
                    R.drawable.ic_baseline_image_24,
                )
            }
            return img
        }

        fun getCollectionDrawable(context: Context, collection: PathCollection): Drawable? {
            return getCollectionDrawable(context, collection.imageUri, null)
        }

//        fun getPathDrawableImage(context: Context, path: Path, iconTheme: String): Drawable? {
//            fun getDrawableFromUri(uri: Uri): Drawable? {
//                val inputStream = context.contentResolver.openInputStream(uri)
//                return Drawable.createFromStream(inputStream, uri.toString())
//            }
//
//            fun getImageResource(imageResource: String?): Drawable? {
//                if(imageResource.isNullOrBlank()) {
//                    return ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_24)
//                }
//                // Default
//                val imgId =
//                    context.resources.getIdentifier(
//                        imageResource + iconTheme,
//                        "drawable",
//                        context.packageName
//                    )
//                return if (0 != imgId) {
//                    ResourcesCompat.getDrawable(context.resources, imgId, null) // return
//                } else {
//                    ResourcesCompat.getDrawable(context.resources, R.drawable.ic_baseline_image_24, null)
//                }
//            }
//
//            return if(!path.userImageUri.isNullOrEmpty()) {
//                // User defined image, display this instead
//                if(true == path.userImageUri?.startsWith(SCHEME_ANDROID_RESOURCE)) {
//                    getImageResource(path.userImageUri?.substringAfterLast("/"))
//                } else {
//                    val uri = Uri.parse(path.userImageUri)
//                    if (uri.toFile().exists()) {
//                        getDrawableFromUri(uri)
//                    } else {
//                        getImageResource(path.userImageUri)
//                    }
//                }
//            } else if (!path.defaultImageUri.isNullOrEmpty()) {
//                // User defined image, display this instead
//                if(true == path.defaultImageUri?.startsWith(SCHEME_ANDROID_RESOURCE)) {
//                    getImageResource(path.defaultImageUri?.substringAfterLast("/"))
//                } else {
//                    val uri = Uri.parse(path.defaultImageUri)
//                    if (uri.toFile().exists()) {
//                        getDrawableFromUri(uri)
//                    } else {
//                        null
//                    }
//                }
//            } else {
//                null
//            }
//        }

        fun getPathTitleString(context: Context, path: Path): String? {
            //            if(null == title) {title = StringUtils.getStringFromStringResourceName(context, path.defaultTitleStringResource, null)}
            return path.name
        }
    }
}
