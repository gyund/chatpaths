package com.gy.chatpaths.aac.app

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Store common features that can be replaced with some simpler calls
 */
@AndroidEntryPoint
abstract class CommonFeatureFragment : Fragment() {

    /**
     * When a dialog completes and [openGalleryForImage] is called. When the image is selected
     * and cropping is complete, then this will be called to tell the fragment to store the
     * image wherever it is required.
     */
    abstract fun onStoreImageUri(uri: Uri)

    /**
     * When the dialog indicates the image should be deleted, this callback is called.
     */
    abstract suspend fun onDeleteImage()

    private var destinationUri: Uri? = null

    private val cropImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let {
                onStoreImageUri(it)
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Log.d("SCF", "Crop Canceled")
            destinationUri?.toFile()?.delete()
        }
        destinationUri = null
    }

    private fun cropImage(sourceUri: Uri, destinationUri: Uri) {
        this.destinationUri = destinationUri
        val intent = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(5f, 5f)
            .getIntent(requireActivity())
        cropImageLauncher.launch(intent)
    }

    private fun hasImagePermission(context: Context): Boolean {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            (
                ContextCompat.checkSelfPermission(context, READ_MEDIA_IMAGES) == PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, READ_MEDIA_VIDEO) == PERMISSION_GRANTED
                )
        ) {
            // Full access on Android 13 (API level 33) or higher
        } else if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
            ContextCompat.checkSelfPermission(context, READ_MEDIA_VISUAL_USER_SELECTED) == PERMISSION_GRANTED
        ) {
            // Partial access on Android 14 (API level 34) or higher
        } else if (ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            // Full access up to Android 12 (API level 32)
        } else {
            // Access denied
            return false
        }
        return true
    }

    private var requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        // Handle permission requests results
        // See the permission example in the Android platform samples: https://github.com/android/platform-samples
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")

                val time = System.currentTimeMillis()
                val newUri =
                    Uri.fromFile(requireActivity().getFileStreamPath("img_$time.jpg"))
                Log.d("SCF", " new image uri: $newUri")
                cropImage(uri, newUri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    private fun requestImagePermission() {
// Permission request logic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermission.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermission.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }
    }

    private fun openGalleryForImage() {
        if (hasImagePermission(requireContext())) {
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            requestImagePermission()
        }
    }

    /**
     * Handle any images that may have been received as a result of reusing an image
     * by calling a child fragment
     */
    protected fun handleImage() {
        val userSelectedImage: String? = getNavigationResult("image")
        if (null != userSelectedImage) { // save the image as the new path image
            try {
                val uri = Uri.parse(userSelectedImage)
                onStoreImageUri(uri)
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Callback to tell the holding fragment that they should navigate to
     * the ImageSelectFragment for reusing a path
     */
    abstract fun navigateToImageSelectFragment()

    /**
     *
     */
    protected fun showEditImageDialog(currentImageUri: String?) {
        if (!currentImageUri.isNullOrEmpty()) {
            showDeletePathImageDialog()
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.edit_image_dialog_title))
                .setMessage(getString(R.string.edit_image_dialog_message))
                .setPositiveButton(
                    getString(android.R.string.ok),
                ) { _, _ ->
                    openGalleryForImage()
                }
                .setNeutralButton(
                    getString(R.string.reuse_image),
                ) { _, _ ->
                    navigateToImageSelectFragment()
                }
                .setNegativeButton(
                    getString(android.R.string.cancel),
                ) { _, _ ->
                    // nothing
                }
                .show()
        }
    }

    private fun showDeletePathImageDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(it.getString(R.string.delete_image_dialog_title))
                .setMessage(it.getString(R.string.delete_image_dialog_message))
                .setPositiveButton(it.getString(android.R.string.ok)) { _, _ ->
                    lifecycleScope.launch {
                        onDeleteImage()
                    }
                }
                .setNegativeButton(it.getString(android.R.string.cancel)) { _, _ -> }
                .show()
        }
    }

    companion object {
        fun <T> Fragment.getNavigationResult(key: String): T? {
            val handle = findNavController().currentBackStackEntry?.savedStateHandle
            val result = handle?.get<T>(key)
            handle?.remove<T>(key)
            return result
        }
    }
}
