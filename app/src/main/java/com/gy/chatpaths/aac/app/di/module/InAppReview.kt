package com.gy.chatpaths.aac.app.di.module

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gy.chatpaths.aac.app.BuildConfig
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Module
@InstallIn(FragmentComponent::class)
class InAppReview @Inject constructor(@ApplicationContext val context: Context) {

    @Inject
    lateinit var firebase: Firebase

    private fun recordDialogRequested() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val key = "usage_count"
        var count = prefs.getInt(key, 0)
        val maxCount = firebase.getReviewCheckFrequency()
        if (count >= maxCount) { // rollover
            count = 0
        }

        prefs.edit().apply {
            putInt(key, count + 1).apply()
        }
    }

    /**
     * Show an inapp review dialog only if the user qualifies
     */
    fun showInAppReviewDialog(
        activity: AppCompatActivity
    ) {
        recordDialogRequested()

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val key = "usage_count"
        val count = prefs.getInt(key, 0)
        val maxCount = firebase.getReviewCheckFrequency()

        try {

            if (count % maxCount == maxCount - 1) {
                val manager = if (BuildConfig.DEBUG) {
                    Toast.makeText(context, "Review Triggered", Toast.LENGTH_SHORT).show()
                    FakeReviewManager(context)
                } else {
                    ReviewManagerFactory.create(context)
                }
                val request = manager.requestReviewFlow()
                request.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val reviewInfo = it.result
                        val flow = manager.launchReviewFlow(activity, reviewInfo)
                        flow.addOnCompleteListener { _ ->
                            // The flow has finished. The API does not indicate whether the user
                            // reviewed or not, or even whether the review dialog was shown. Thus, no
                            // matter the result, we continue our app flow.
                        }
                    } else {
                        // There was some problem, continue regardless of the result.
                    }
                }
            }
        } catch (e: Exception) {
            // exception with the library
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

}