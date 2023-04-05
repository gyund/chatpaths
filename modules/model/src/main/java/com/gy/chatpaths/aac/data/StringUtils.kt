package com.gy.chatpaths.aac.data

import android.content.Context
import androidx.annotation.StringRes

class StringUtils {
    companion object {
        /**
         * Get a drawable from a resource string, like ic_raise_hand, or a URI
         *
         * @param context
         * @param resourceNameOrUri String like ic_rase_hand, or a URI
         * @return NULL or drawable
         */
        fun getStringFromStringResourceName(context: Context, resourceName: String?, @StringRes defaultStringResource: Int?): String? {
            fun getStringFromResource(): String? {
                // Default
                val resId =
                    context.resources.getIdentifier(
                        resourceName,
                        "string",
                        context.packageName,
                    )
                return when {
                    0 != resId -> context.getString(resId) // return
                    null != defaultStringResource -> context.getString(defaultStringResource)
                    else -> null // return
                }
            }
            if (resourceName.isNullOrBlank()) return null
            return getStringFromResource()
        }
    }
}
