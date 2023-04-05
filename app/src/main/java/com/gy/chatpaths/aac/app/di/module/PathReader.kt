package com.gy.chatpaths.aac.app.di.module

import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.AutomaticGainControl
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.model.Path
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.util.*
import javax.inject.Inject

/**
 * PathReader will read a path based on its current position.
 *
 * You must call [release] when the fragment is finished to release internal resources
 */
@Module
@InstallIn(FragmentComponent::class)
class PathReader @Inject constructor(@ApplicationContext val context: Context) :
    TextToSpeech.OnInitListener {

    val TAG = "PathReader"

    /**
     * TTS Language selections, set after init so we know we're ready
     */
    private var language: Int? = null
    private val tts = TextToSpeech(context, this)

    /**
     * Call when the fragment is finished with it to release internal resources
     */
    fun release() {
        tts.shutdown()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale.getDefault()
            language = if (TextToSpeech.LANG_AVAILABLE == tts.isLanguageAvailable(locale)) {
                tts.setLanguage(locale)
            } else {
                tts.setLanguage(Locale.US)
            }
        } else {
            Toast.makeText(context, context.getString(R.string.tts_unavailable), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private var player: MediaPlayer? = null

    private fun say(fileUri: Uri) {
        player?.apply {
            release()
        }
        player = MediaPlayer().apply {
            var agc = if (AutomaticGainControl.isAvailable()) {
                val agc = AutomaticGainControl.create(audioSessionId)
                agc.enabled = true
                agc
            } else {
                null
            }

            fun onFailure(e: Exception) {
                Log.d(TAG, "exception with play: $e")
                FirebaseCrashlytics.getInstance().recordException(e)
                agc?.release()
                agc = null
            }

            try {
                setDataSource(fileUri.path)
                prepare()
                start()
                setOnCompletionListener {
                    it.release()
                    player = null
                    agc?.release()
                    agc = null
                }
            } catch (e: IOException) {
                onFailure(e)
            } catch (e: IllegalStateException) {
                onFailure(e)
            }
        }
    }

    private fun read(paths: List<Path>) {
        val last = paths.lastOrNull()
        last?.apply {
            val hasAudio = !audioPromptUri.isNullOrBlank()

            if (hasAudio) {
                // Just read the audio here
                try {
                    say(Uri.parse(audioPromptUri))
                } catch (e: RuntimeException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            } else {
                val sentence = buildSentence(paths)
                say(sentence, tts)
            }
        }
    }

    /**
     * Read the path after the code specified has been run.
     *
     * This routine will save the parent history prior to executing the code and will
     * tack on the current path for the name to be read.
     *
     * If the last path in the list to be read has it's own audio, the audio will be used instead
     * of combining TTS and Audio, since some languages read differently.
     */
    suspend fun readAfter(
        pathNavigator: PathNavigator,
        code: suspend () -> Unit,
    ) {
        // Make a copy
        val pathToRead = pathNavigator.parentHistory.toMutableList()
        // parentHistory doesn't contain the path just selected, so we want to read this
        // after we select it
        pathNavigator.getPathState().currentPath?.apply {
            pathToRead.add(this)
        }
        // Call the code now that we've preserved the selected path to read
        code()

        // Read what was pressed, whether or not it had a child
        read(pathToRead)
    }

    private fun buildSentence(paths: List<Path>): String {
        val builder = StringBuilder()
        paths.forEach {
            it.name?.apply {
                builder.append(this).append(" ")
            }
        }
        return builder.toString()
    }

    private fun say(string: String, tts: TextToSpeech) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val readOutLoud = preferences.getBoolean("read_out_loud_preference", false)
        if (readOutLoud) {
            tts.speak(string, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
}
