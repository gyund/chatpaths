package com.gy.chatpaths.aac.app.ui.manager.pathdetail

import android.Manifest.permission.RECORD_AUDIO
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gy.chatpaths.aac.app.CommonFeatureFragment
import com.gy.chatpaths.aac.app.DrawableUtils
import com.gy.chatpaths.aac.app.MainActivity
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.databinding.DialogEditPathTitleBinding
import com.gy.chatpaths.aac.app.databinding.FragmentPathDetailBinding
import com.gy.chatpaths.aac.app.di.module.GuidedTour
import com.gy.chatpaths.aac.app.di.module.MediaRecordingDialog
import com.gy.chatpaths.aac.app.ui.helper.DatabaseConversionHelper
import com.gy.chatpaths.aac.data.Path
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PathsDetailFragment : CommonFeatureFragment(), PathDetailManagerListener {


    private var _binding: FragmentPathDetailBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: PathsDetailViewModel by viewModels()
    private val args: PathsDetailFragmentArgs by navArgs()

    @Inject
    lateinit var guidedTour: GuidedTour

    @Inject
    lateinit var mediaRecordingDialog: MediaRecordingDialog

    private val recordAudioPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            context?.let { ctx ->
                launchPromptRecordingDialog()
            }
        }
    }

    private fun launchPromptRecordingDialog() {
        val fragment = this
        lifecycleScope.launch {
            var audioPromptUri: Uri? = null
            val onDelete: (() -> Unit)? = viewmodel.getPath()?.let { p ->
                if (true == p.audioPromptUri?.isNotEmpty()) {
                    try {
                        audioPromptUri = Uri.parse(p.audioPromptUri)
                    } catch (e: RuntimeException) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                    return@let {
                        // lambda for onDelete
                        viewmodel.deleteAudioPrompt(p.pathId)
                    }
                }
                null
            }


            mediaRecordingDialog.recordAudio(fragment, audioPromptUri, onDelete) { uri ->
                if (null != uri) {
                    lifecycleScope.launch {
                        viewmodel.setAudioPrompt(uri)
                    }
                }
                true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPathDetailBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)

        viewmodel.collectionId = args.collectionId
        viewmodel.parentId = args.parentId
        viewmodel.pathId = args.pathId
        viewmodel.userId = args.userId

        handleImage()

        var firedOnce = false
        viewmodel.path.observe(viewLifecycleOwner, {
            it?.apply {
                // Only update the text the first round
                if (!firedOnce) {
                    firedOnce = true
                    binding.editName.setText(
                        DatabaseConversionHelper.getPathTitleString(
                            binding.root.context,
                            this
                        )
                    )
                    binding.anchorPath.isChecked = anchored

                    binding.editName.addTextChangedListener {
                        lifecycleScope.launch {
                            viewmodel.updateTitle(binding.editName.text.toString())
                        }
                    }

                    binding.anchorPath.setOnCheckedChangeListener { _, isChecked ->
                        lifecycleScope.launch {
                            viewmodel.setIsAnchored(isChecked)
                        }
                    }

                    binding.recordAudioButton.setOnClickListener {
                        recordAudioPermission.launch(RECORD_AUDIO)
                    }
                }
                setupPathImage(this)
            }
        })

        binding.deleteButton.setOnClickListener {
            showDeletePathConfirmationDialog()
        }

        binding.displayImage.setOnClickListener {
            lifecycleScope.launch {
                showEditPathImageDialog()
            }
        }

        binding.addFab.setOnClickListener {
            showAddChildPathDialog()
        }

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.manager_pathdetail_fragment_menu, menu)
        lifecycleScope.launch {
            if (viewmodel.hasChild()) {
                menu.findItem(R.id.gotoChild)?.apply {
                    isVisible = true
                }
            }
        }
        return super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.gotoChild -> {
                lifecycleScope.launch {
                    val action =
                        PathsDetailFragmentDirections.actionPathsDetailFragmentToPathsFragment(
                            args.collectionId,
                            args.pathId
                        )
                    findNavController().navigate(action)
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        guidedTour.dismiss()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        (activity as MainActivity).enableToolbar()

        guidedTour.addGuidedEntry(
            binding.addFab,
            GuidedTour.Dialog.PATH_DETAIL_ADD_CHILD,
            getString(R.string.onboard_add_subpath_primary),
            getString(R.string.onboard_add_subpath_secondary)
        ).addGuidedEntry(
            R.id.gotoChild,
            GuidedTour.Dialog.PATH_DETAIL_GOTO_CHILD,
            getString(R.string.onboard_goto_subpath_primary),
            getString(R.string.onboard_goto_subpath_secondary),
            targetIcon = R.drawable.ic_baseline_subdirectory_arrow_right_24
        ).show(this)
    }

    private fun showAddChildPathDialog() {
        val alertView = DialogEditPathTitleBinding.inflate(layoutInflater)
        alertView.editPathTitleView.setText(getString(R.string.new_path_text))

        MaterialAlertDialogBuilder(requireContext())
            .setView(alertView.root)
            .setTitle(getString(R.string.add_child_path))
            .setMessage(getString(R.string.create_path_message))
            .setPositiveButton(
                getString(R.string.create)
            ) { _, _ ->
                lifecycleScope.launch {
                    viewmodel.getPath()?.apply {

                        viewmodel.addChildPath(alertView.editPathTitleView.text.toString())
                        // Now navigate to it
                        val action =
                            PathsDetailFragmentDirections.actionPathsDetailFragmentToPathsFragment(
                                collectionId,
                                pathId
                            )
                        findNavController().navigate(action)
                    }

                }
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ ->
                //nothing
            }
            .show()
    }

    override fun onStoreImageUri(uri: Uri) {
        lifecycleScope.launch {
            viewmodel.setImage(uri)
        }
    }

    override suspend fun onDeleteImage() {
        viewmodel.deleteImage()
    }

    override fun navigateToImageSelectFragment() {
        val action =
            PathsDetailFragmentDirections.actionPathsDetailFragmentToImageSelectFragment()
        findNavController().navigate(action)
    }

    private suspend fun showEditPathImageDialog() {
        context?.let {
            viewmodel.getPath()?.let { pud ->
                showEditImageDialog(pud.imageUri)
            }
        }
    }

    private fun setupPathImage(path: Path) {
        val drawable = DrawableUtils.getDrawableImage(binding.root.context, path)
        if (null == drawable) {
            DrawableUtils.getDrawableImage(
                binding.root.context,
                null,
                R.drawable.ic_baseline_image_24
            )
            binding.overlay.cardViewOverlay.visibility = VISIBLE
            binding.overlay.overlayTextView.text = getString(R.string.configure_image_message)
        } else {
            binding.overlay.cardViewOverlay.visibility = GONE
        }

        binding.displayImage.setImageDrawable(drawable)
    }

    override fun showDeletePathConfirmationDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(it.getString(R.string.delete_path_dialog_title))
                .setMessage(it.getString(R.string.delete_path_dialog_message))
                .setPositiveButton(it.getString(android.R.string.ok)) { _, _ ->
                    lifecycleScope.launch {
                        viewmodel.deletePath()
                        findNavController().navigateUp()
                    }
                }
                .setNegativeButton(it.getString(R.string.cancel)) { _, _ -> }
                .show()
        }
    }
}