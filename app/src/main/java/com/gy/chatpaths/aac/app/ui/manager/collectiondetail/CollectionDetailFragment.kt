package com.gy.chatpaths.aac.app.ui.manager.collectiondetail

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gy.chatpaths.aac.app.CommonFeatureFragment
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.databinding.FragmentCollectionDetailBinding
import com.gy.chatpaths.aac.app.model.PathCollection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CollectionDetailFragment : CommonFeatureFragment() {
    @Suppress("ktlint:standard:property-naming")
    private var _binding: FragmentCollectionDetailBinding? = null
    private val binding get() = _binding!!

    private val collectionViewModel: CollectionViewModel by viewModels()
    private val args: CollectionDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCollectionDetailBinding.inflate(layoutInflater, container, false)
        collectionViewModel.collectionId = args.collectionId

        handleImage()

        lifecycleScope.launch {
            collectionViewModel.getLivePathCollectionInfo(args.collectionId)
                .observe(viewLifecycleOwner) {
                    // Update the view whenever it changes or is updated
                    it?.apply {
                        context?.let { ctx ->
                            setupCollectionImage(ctx, it)
                            binding.editName.setText(
                                CollectionViewModel.getTitle(
                                    it,
                                ),
                            )
                        }
                    }
                }
            binding.displayImage.setOnClickListener {
                lifecycleScope.launch {
                    showEditImageDialog(collectionViewModel.getImage())
                }
            }
        }

        binding.deleteButton.setOnClickListener {
            showDeleteCollectionDialog(args.collectionId)
        }

        binding.saveButton.setOnClickListener { view ->
            lifecycleScope.launch {
                collectionViewModel.updateDisplayName(
                    args.collectionId,
                    binding.editName.text.toString(),
                )
                view.findNavController().navigateUp()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCollectionImage(
        ctx: Context,
        it: PathCollection,
    ) {
        val drawable = CollectionViewModel.getDrawable(ctx, it)
        if (CollectionViewModel.isDrawableDefault(it)) {
            binding.overlay.cardViewOverlay.visibility = View.VISIBLE
            binding.overlay.overlayTextView.text = getString(R.string.configure_image_message)
        } else {
            binding.overlay.cardViewOverlay.visibility = View.GONE
        }
        binding.displayImage.setImageDrawable(drawable)
    }

    override fun onStoreImageUri(uri: Uri) {
        lifecycleScope.launch {
            collectionViewModel.setImage(args.collectionId, uri)
        }
    }

    override suspend fun onDeleteImage() {
        collectionViewModel.deleteImage(args.collectionId)
    }

    override fun navigateToImageSelectFragment() {
        val action =
            CollectionDetailFragmentDirections.actionCollectionDetailFragmentToImageSelectFragment()
        findNavController().navigate(action)
    }

    private fun showDeleteCollectionDialog(collectionId: Int) {
        context?.apply {
            MaterialAlertDialogBuilder(this)
                .setTitle(this.getString(R.string.delete_collection_title))
                .setMessage(this.getString(R.string.delete_collection_message))
                .setPositiveButton(this.getString(android.R.string.ok)) { _, _ ->
                    lifecycleScope.launch {
                        collectionViewModel.delete(collectionId)
                        if (args.usingCollection) {
                            // Bad things can happen if we delete and are using the collection
                            val action = CollectionDetailFragmentDirections.actionGlobalNavHome()
                            findNavController().navigate(action)
                        } else {
                            findNavController().navigateUp()
                        }
                    }
                }
                .setNegativeButton(this.getString(android.R.string.cancel)) { _, _ -> }
                .show()
        }
    }
}
