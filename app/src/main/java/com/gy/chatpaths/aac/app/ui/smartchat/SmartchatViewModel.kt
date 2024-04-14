package com.gy.chatpaths.aac.app.ui.smartchat

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.gy.chatpaths.aac.app.di.module.CurrentUser
import com.gy.chatpaths.aac.app.di.module.InAppReview
import com.gy.chatpaths.aac.app.di.module.PathNavigator
import com.gy.chatpaths.aac.app.model.Path
import com.gy.chatpaths.aac.app.model.source.CPRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SmartchatViewModel @Inject constructor(
    val repository: CPRepository,
    val pathNavigator: PathNavigator,
    val currentUser: CurrentUser,
    val inAppReview: InAppReview,
) : ViewModel() {

    /**
     * Sets the current collection and reinitializes the path.
     */
    suspend fun setCurrentCollection(context: Context, collectionId: Int) {
        // Only initialize once
        if (null == pathNavigator.collectionId) {
            pathNavigator.setCollection(collectionId)
        } else {
            // just refresh the state because this is called when we resume, and the data may have changed
            pathNavigator.dataHasChanged()
        }
    }

    /**
     * Move the currently tracked path to the previous path entry
     */
    suspend fun previousPath(): Path? = pathNavigator.previous()

    /**
     * Move the currently tracked path to the next path entry
     */
    suspend fun nextPath(): Path? = pathNavigator.next()

    suspend fun goCurrentPath() {
        withContext(Dispatchers.IO) {
            pathNavigator.currentPath?.let { _ ->
                pathNavigator.select()
            }
        }
    }

    suspend fun goParentPath() {
        pathNavigator.back()
    }

    private val textColorLUT = mutableMapOf<String, Int>()
    fun setTextColorFromDrawable(drawable: Drawable, textView: TextView, defaultColor: Int) {
        // If we've calculated this color already, just reuse it
        val text = textView.text.toString()
        textColorLUT[text]?.let {
            textView.setTextColor(it)
            return // found it
        }

        val bm = drawable.toBitmap(50, 50, Bitmap.Config.RGB_565)
        val palette = Palette.from(bm).addTarget(LIGHT).maximumColorCount(24).generate()

        // The alternate color is derived off a moduli of the seed
        val color = palette.vibrantSwatch?.rgb ?: palette.lightVibrantSwatch?.rgb ?: defaultColor

        if (color != 0) {
            textView.setTextColor(color)
            textColorLUT[text] = color
        }
    }

    /**
     * If we've seen this text before, use the colors
     */
    fun setTextColorFromCachedDrawable(textView: TextView) {
        val text = textView.text.toString()
        textColorLUT[text]?.let {
            textView.setTextColor(it)
        }
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
