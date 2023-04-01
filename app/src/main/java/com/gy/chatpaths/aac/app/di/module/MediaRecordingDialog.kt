package com.gy.chatpaths.aac.app.di.module

import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.audiofx.AutomaticGainControl
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.databinding.DialogRecordAudioBinding
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject

@Module
@InstallIn(FragmentComponent::class)
class MediaRecordingDialog @Inject constructor() {

    val TAG = "MediaRecordingDialog"

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var originalRecordButtonTintList: ColorStateList? = null
    private var originalPlayButtonTintList: ColorStateList? = null

    enum class State {
        IDLE,
        RECORDING,
        PLAYING,
        STOP_PLAYING,
        STOP_RECORDING
    }

    private var state = State.IDLE

    /**
     * Displays a dialog to record audio, when it is finished, it will
     * return the URI to the file that was created.
     *
     * This class assumes that the permissions have already been
     * requested to allow the audio to be recorded.
     *
     * @param fragment
     * @param onFinished Provdes the uri of the recorded file. The callback must return
     *                   true if it saved/recorded the file, otherwise false will make sure the
     *                   file is deleted.
     */
    fun recordAudio(
        fragment: Fragment,
        audioPromptUri: Uri?,
        onDelete: (() -> Unit)?,
        onFinished: (uri: Uri?) -> Boolean
    ) {
        // initialize with the audioPromptUri if it's set
        val uri = fragment.context?.let {
            val path = it.filesDir.path + File.separator + UUID.randomUUID().toString() + ".3gp"
            try {
                Uri.fromFile(File(path))
            } catch (e: RuntimeException) {
                null
            }
        }

        // Don't continue if we don't have a file handle
        if (null == uri) {
            onFinished(uri)
            return
        }

        fun onUserCancel() {
            val f = uri.toFile()
            if (f.exists()) {
                f.delete()
            }

            onFinished(null)
        }

        val layout = DialogRecordAudioBinding.inflate(fragment.layoutInflater)
        var originalPrompt = audioPromptUri
        layout.playButton.setOnClickListener {
            layout.playButtonToggleAction(originalPrompt ?: uri)
        }
        layout.recordButton.setOnClickListener {
            originalPrompt = null
            layout.recordButtonToggleAction(uri)
        }


        // Display Dialog
        val mab = MaterialAlertDialogBuilder(layout.root.context)
            .setTitle(layout.root.context.getString(R.string.record_audio))
            .setView(layout.root)
            .setPositiveButton(
                layout.root.context.getString(R.string.ok)
            ) { _, _ ->
                if (!onFinished(uri)) {
                    onUserCancel() // delete the file
                }
            }
            .setNegativeButton(
                layout.root.context.getString(R.string.cancel)
            ) { _, _ ->
                // Delete the file
                onUserCancel()
            }
            .setOnCancelListener {
                onUserCancel()
            }
        onDelete?.apply {
            mab.setNeutralButton(layout.root.context.getString(R.string.delete)) { _, _ ->
                this()
            }
        }

        audioPromptUri?.apply { layout.playButton.isEnabled = true }
        mab.show()
    }

    private fun DialogRecordAudioBinding.playButtonToggleAction(fileUri: Uri) {
        when (state) {
            State.IDLE -> {
                startPlaying(this, fileUri)
            }
            State.PLAYING -> { // button acts as a stop
                player?.stopPlaying(this)
            }
            else -> {
                // ignore
            }
        }
    }

    private fun MediaPlayer.stopPlaying(
        binding: DialogRecordAudioBinding
    ) {
        stop()
        release()
        player = null
        onPlayFinished(binding)
    }

    private fun onPlayFinished(binding: DialogRecordAudioBinding) {
        binding.playButton.apply {
            backgroundTintList = originalPlayButtonTintList
            setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.ic_baseline_play_arrow_24
                )
            )
        }

        originalPlayButtonTintList = null
        binding.recordButton.isEnabled = true
        state = State.IDLE
    }

    private fun DialogRecordAudioBinding.recordButtonToggleAction(fileUri: Uri) {
        when (state) {
            State.IDLE -> {
                startRecording(this, fileUri)
            }
            State.RECORDING -> {
                // Stop Recording
                recorder?.stopRecording(this, fileUri)
            }
            else -> {
                // ignore
            }
        }
    }

    private fun MediaRecorder.stopRecording(
        binding: DialogRecordAudioBinding,
        fileUri: Uri
    ) {
        // Stop can take a while sometimes, disable the button while processing
        binding.recordButton.isEnabled = false
        try {
            stop()
        } catch (e: RuntimeException) { // invalid file
            // ok already stopped
            Log.d(TAG, "error stopping recording: $e")
            fileUri.toFile().delete()
        }
        release()

        binding.recordButton.backgroundTintList = originalRecordButtonTintList
        originalRecordButtonTintList = null
        recorder = null

        // We enable only on success
        state = State.IDLE
        binding.playButton.isEnabled = true
        binding.recordButton.isEnabled = true
    }

    private fun startPlaying(
        binding: DialogRecordAudioBinding,
        fileUri: Uri
    ) {
        binding.apply {
            player = MediaPlayer()
            player?.let {

                var agc = if (AutomaticGainControl.isAvailable()) {
                    val agc = AutomaticGainControl.create(it.audioSessionId)
                    agc.enabled = true
                    agc
                } else null

                fun onFailure(e: Exception) {
                    Log.d(TAG, "exception with play: $e")
                    FirebaseCrashlytics.getInstance().recordException(e)
                    player = null
                    agc?.release()
                    agc = null
                }

                try {
                    it.setDataSource(fileUri.path)
                    it.prepare()
                    it.start()

                    // Disable the record button while playing
                    recordButton.isEnabled = false
                    state = State.PLAYING

                    originalPlayButtonTintList = playButton.backgroundTintList
                    playButton.apply {
                        backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(root.context, R.color.negativeColor)
                        )
                        setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context,
                                R.drawable.ic_baseline_stop_24
                            )
                        )
                    }
                    it.setOnCompletionListener {
                        onPlayFinished(this)
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
    }

    private fun startRecording(
        binding: DialogRecordAudioBinding,
        fileUri: Uri
    ) {

        binding.apply {

            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(root.context)
            } else {
                MediaRecorder()
            }
            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(fileUri.path)

                fun onFailure(e: Exception) {
                    Log.d(TAG, "exception with record: $e")
                    FirebaseCrashlytics.getInstance().recordException(e)
                    recorder = null
                }

                try {
                    prepare()
                    start()

                    // Update UI
                    originalRecordButtonTintList = recordButton.backgroundTintList

                    recordButton.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(root.context, R.color.negativeColor)
                    )
                    // Disable play while recording
                    playButton.isEnabled = false
                    state = State.RECORDING

                } catch (e: IOException) {
                    onFailure(e)
                } catch (e: RuntimeException) {
                    onFailure(e)
                }
            }
        }
    }
}