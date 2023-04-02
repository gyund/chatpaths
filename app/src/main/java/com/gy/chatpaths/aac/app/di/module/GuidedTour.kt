package com.gy.chatpaths.aac.app.di.module

import android.app.Activity
import android.graphics.Typeface
import android.view.View
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.gy.chatpaths.aac.app.R
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal
import java.util.*
import javax.inject.Inject

@Module
@InstallIn(FragmentComponent::class)
class GuidedTour @Inject constructor() {
    private class Info constructor(
        val target: View?,
        val dialog: Dialog,
        val primaryText: String,
        val secondaryText: String,
        val focalColour: Int?,
        val focalShape: Shape?,
        val target_id: Int? = null,
        val targetIcon: Int? = null,
    )

    private val list: Queue<Info> = LinkedList()
    private var current: MaterialTapTargetPrompt? = null

    /**
     * Add an entry to the guided setup.
     *
     * You can configure the focul color and shape. By default, the shape of the focal poing matches
     * the background because squares inside circles look kinda weird.
     *
     * @param target        Where should the user direct their attention
     * @param dialog        The dialog is an ID used to uniquely reference to skip displaying it
     *                      in the future
     * @param primaryText   This is like the title of the guide popup
     * @param secondaryText This is the extended description of info to provide the user
     * @param focalColour   This is the color that appears behind the target in focus
     * @param focalShape    This is the shape used around the target.
     */
    fun addGuidedEntry(
        target: View,
        dialog: Dialog,
        primaryText: String,
        secondaryText: String,
        focalColour: Int? = null,
        focalShape: Shape? = null,
    ): GuidedTour {
        list.add(
            Info(
                target = target,
                dialog = dialog,
                primaryText = primaryText,
                secondaryText = secondaryText,
                focalColour = focalColour,
                focalShape = focalShape,
            ),
        )
        return this
    }

    fun addGuidedEntry(
        target: Int,
        dialog: Dialog,
        primaryText: String,
        secondaryText: String,
        focalColour: Int? = null,
        focalShape: Shape? = null,
        targetIcon: Int? = null,
    ): GuidedTour {
        list.add(
            Info(
                target = null,
                dialog = dialog,
                primaryText = primaryText,
                secondaryText = secondaryText,
                focalColour = focalColour,
                focalShape = focalShape,
                target_id = target,
                targetIcon = targetIcon,
            ),
        )
        return this
    }

    fun show(fragment: Fragment) {
        show(fragment.activity)
    }

    fun show(activity: Activity?) {
        // The dialog fragment display requires an activity to be attached
        activity?.also {
            val prefs = PreferenceManager.getDefaultSharedPreferences(it)
            list.poll()?.let { info ->
                val tourId = CFG_PREFIX + info.dialog.name
                val lastVersion = prefs.getInt(tourId, 0)

                if (lastVersion < VERSION) {
                    // show this tour
                    val builder = MaterialTapTargetPrompt.Builder(it)
                        .setPrimaryTextColour(activity.resources.getColor(R.color.background, null))
                        .setSecondaryTextColour(
                            activity.resources.getColor(
                                R.color.background,
                                null,
                            ),
                        )
                        .setPrimaryTextTypeface(Typeface.DEFAULT_BOLD)
                        .setBackgroundColour(
                            it.resources.getColor(
                                R.color.secondaryDarkColor,
                                null,
                            ),
                        )
                        .setPrimaryText(info.primaryText)
                        .setSecondaryText(info.secondaryText)
                        .setPromptStateChangeListener { _, state ->
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED ||
                                state == MaterialTapTargetPrompt.STATE_DISMISSED
                            ) {
                                // User has pressed the prompt target
                                prefs.edit().putInt(
                                    tourId,
                                    VERSION,
                                ).apply()
                                current = null
                                // Show the next if it exists
                                show(it)
                            }
                        }
                    info.focalColour?.let { color ->
                        builder.focalColour = color
                    }
                    info.focalShape?.let {
                        when (it) {
                            Shape.Rectangle -> {
                                builder.promptFocal = RectanglePromptFocal()
                                builder.promptBackground = RectanglePromptBackground()
                            }
                            Shape.Round -> null
                        }
                    }
                    info.target?.let { t ->
                        builder.setTarget(t)
                    }
                    info.target_id?.let { t ->
                        builder.setTarget(t)
                    }
                    info.targetIcon?.let { t ->
                        builder.setIcon(t)
                    }
                    current = builder.show()
                } else {
                    // Show the next if it's available
                    show(it)
                }
            }
        }
    }

    fun dismiss() {
        current?.apply { dismiss() }
    }

    /**
     * Call this to reset the help dialogs for the user
     */
    fun resetHelpDialogs(fragment: Fragment) {
        val prefs = fragment.context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        Dialog.values().forEach {
            val tourId = CFG_PREFIX + it.name
            prefs?.edit()?.remove(
                tourId,
            )?.apply()
        }
    }

    companion object {
        const val VERSION = 1
        const val CFG_PREFIX = "gt_id_"
    }

    enum class Dialog {
        PATH_DETAIL_ADD_CHILD,
        PATH_DETAIL_ANCHOR_PATH,
        PATH_ADD,
        EDIT_IMAGES,
        PATH_AUTO_SORT,
        COLLECTION_ADD,
        USER_ADD,
        PATH_DETAIL_GOTO_CHILD,
        PATH_COPY_TO_USER,
        COLLECTION_ADD_TEMPLATE,
        PATH_READ_OUT_LOUD,
    }

    enum class Shape {
        Round,
        Rectangle,
    }
}
