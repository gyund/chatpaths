package com.gy.chatpaths.builder

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import androidx.annotation.AnyRes
import com.gy.chatpaths.aac.resource.R

class UriHelper {

    companion object {
        val TAG = "UriHelper"

        /**
         * get uri to drawable or any other resource type if u wish
         * @param context - [Context]
         * @param drawableId - drawable res id
         * @return - uri
         */
        fun getUriToDrawable(
            context: Context,
            @AnyRes drawableId: Int,
        ): Uri = getUriToDrawable(context.resources, drawableId)

        /**
         * get uri to drawable or any other resource type if u wish
         * @param resources - [Resources]
         * @param drawableId - drawable res id
         * @return - uri
         */
        private fun getUriToDrawable(
            resources: Resources,
            drawableId: Int,
        ) = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(drawableId))
            .appendPath(resources.getResourceTypeName(drawableId))
            .appendPath(resources.getResourceEntryName(drawableId))
            .build()

        fun getResourceUris(
            context: Context,
            list: MutableList<String>,
            searchText: String? = null,
        ) {
            val imgList = context.resources.obtainTypedArray(R.array.images)

            for (i in 0 until imgList.length()) {
                val id = imgList.getResourceId(i, -1)
                if (!searchText.isNullOrBlank()) {
                    val name = context.resources.getResourceEntryName(id).substringAfterLast("ic_")
                    // Since the name is always english, if the locale is not english, translate it
                    val searchString = getSearchString(context, name)
                    if (!searchString.contains(searchText)) {
                        continue // skip
                    }
                }
                if (id != -1) {
                    getUriToDrawable(context.resources, id)?.apply {
                        list.add(this.toString())
                    }
                }
            }
            imgList.recycle()
        }

        /**
         * Retrieves a translated set of words that can help find an image by using contains
         *
         * @note we need to use getIdentifier so we can search find the resource mapping to the correct image
         */
        @SuppressLint("DiscouragedApi")
        private fun getSearchString(context: Context, name: String): String {
            context.resources.configuration.locales.apply {
                if (!isEmpty) {
                    val defaultLocale = get(0)
                    defaultLocale.language.apply {
                        // Translate - hopefully we have the translation unit
                        val resName = "search_$name"
                        val resId =
                            context.resources.getIdentifier(resName, "string", context.packageName)
                        if (0 != resId) {
                            return context.getString(resId)
                        } else {
                            Log.d(TAG, "missing translation for $name")
                        }
                    }
                }
            }

            return name
        }
    }
}
