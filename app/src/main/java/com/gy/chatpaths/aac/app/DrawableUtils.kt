package com.gy.chatpaths.aac.app

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.gy.chatpaths.model.Path

class DrawableUtils {
    companion object {

        private fun getDrawableFromUri(context: Context, resourceNameOrUri: String): Drawable? {
            try {
                val uri = Uri.parse(resourceNameOrUri)
                if (uri.toFile().exists()) {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    return Drawable.createFromStream(inputStream, uri.toString())
                }
            } catch (e: Exception) {
                // NOTHING JUST RETURN NULL
            }
            return null
        }

        private fun getDefaultResource(context: Context, @DrawableRes defaultResource: Int?) =
            if (null != defaultResource) {
                ContextCompat.getDrawable(context, defaultResource)
            } else {
                null
            }

        private fun getDrawableAsResource(context: Context, resourceNameOrUri: String): Drawable? {
            return if (resourceNameOrUri.startsWith(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                val altUri = resourceNameOrUri.substringAfterLast('/')
                // Default
                val imgId =
                    context.resources.getIdentifier(
                        altUri,
                        "drawable",
                        context.packageName,
                    )
                return if (0 != imgId) {
                    ContextCompat.getDrawable(context, imgId) // return
                } else {
                    null
                }
            } else {
                null
            }
        }

        /**
         * Get a drawable from a resource string, like ic_raise_hand, or a URI
         *
         * @param context
         * @param resourceNameOrUri String like ic_rase_hand, or a URI
         * @return NULL or drawable
         */
        fun getDrawableImage(
            context: Context,
            resourceNameOrUri: String?,
            @DrawableRes defaultResource: Int? = null,
        ): Drawable? {
            if (resourceNameOrUri.isNullOrBlank()) {
                return getDefaultResource(
                    context,
                    defaultResource,
                )
            }
            return getDrawableAsResource(context, resourceNameOrUri)
                ?: getDrawableFromUri(context, resourceNameOrUri)
                ?: getDefaultResource(context, defaultResource)
        }

        fun getDrawableImage(
            context: Context,
            path: Path,
            @DrawableRes defaultResource: Int? = null,
        ): Drawable? {
            return getDrawableImage(context, path.imageUri, defaultResource)
        }

        private val LIGHT = androidx.palette.graphics.Target.Builder().setMinimumLightness(0.50f)
            .setTargetLightness(0.74f)
            .setMaximumLightness(1.0f)
            .setMinimumSaturation(0.1f)
            .setTargetSaturation(0.7f)
            .setMaximumSaturation(1f)
            .setPopulationWeight(0.18f)
            .setSaturationWeight(0.22f)
            .setLightnessWeight(0.60f)
            .setExclusive(false)
            .build()
    }
}
