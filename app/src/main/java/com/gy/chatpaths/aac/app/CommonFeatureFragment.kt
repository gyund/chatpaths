package com.gy.chatpaths.aac.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
        ActivityResultContracts.StartActivityForResult()
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

    private val getImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = result.data?.data
            resultUri?.let { uri ->
                val time = System.currentTimeMillis()
                val newUri = Uri.fromFile(requireActivity().getFileStreamPath("img_$time.jpg"))
                Log.d("SCF", " new image uri: $newUri")
                cropImage(uri, newUri)
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            // cancled getting the image
        }
    }

    private fun cropImage(sourceUri: Uri, destinationUri: Uri) {
        this.destinationUri = destinationUri
        val intent = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(5f, 5f)
            .getIntent(requireActivity())
        cropImageLauncher.launch(intent)
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        getImageLauncher.launch(intent)
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
            } catch (e: Exception) {

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
                    getString(android.R.string.ok)
                ) { _, _ ->
                    openGalleryForImage()
                }
                .setNeutralButton(
                    getString(R.string.reuse_image)
                ) { _, _ ->
                    navigateToImageSelectFragment()
                }
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { _, _ ->
                    //nothing
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
                .setNegativeButton(it.getString(R.string.cancel)) { _, _ -> }
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