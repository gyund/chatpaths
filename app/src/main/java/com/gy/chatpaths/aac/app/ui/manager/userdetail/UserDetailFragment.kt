package com.gy.chatpaths.aac.app.ui.manager.userdetail

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gy.chatpaths.aac.app.CommonFeatureFragment
import com.gy.chatpaths.aac.app.DrawableUtils
import com.gy.chatpaths.aac.app.MainActivity
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.databinding.DialogEditCollectionBinding
import com.gy.chatpaths.aac.app.databinding.DialogUserNameBinding
import com.gy.chatpaths.aac.app.databinding.UserDetailBinding
import com.gy.chatpaths.aac.app.di.module.CurrentUser
import com.gy.chatpaths.aac.app.di.module.GuidedTour
import com.gy.chatpaths.aac.app.ui.helper.OnStartDragListener
import com.gy.chatpaths.aac.app.ui.helper.SimpleItemTouchHelperCallback
import com.gy.chatpaths.aac.data.PathCollection
import com.gy.chatpaths.aac.data.source.CPRepository
import com.gy.chatpaths.aac.resource.create.Collection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class UserDetailFragment : CommonFeatureFragment(), OnStartDragListener, CollectionManagerListener {

    private val columnCount: Int = 1
    private var _binding: UserDetailBinding? = null
    private val binding get() = _binding!!

    private val collectionsViewModel: UserCollectionsViewModel by viewModels()
    private val selectedUserDetailViewModel: UserDetailViewModel by viewModels()

    private val args: UserDetailFragmentArgs by navArgs()

    private lateinit var adapter: MyCollectionsRecyclerViewAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    @Inject
    lateinit var currentUser: CurrentUser

    @Inject
    lateinit var guidedTour: GuidedTour

    @Inject
    lateinit var repository: CPRepository

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    private fun setCurrentUserButtonState() {
        binding.deleteButton.isEnabled = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = UserDetailBinding.inflate(layoutInflater, container, false)

        handleImage()

        selectedUserDetailViewModel.userId = args.userId
        collectionsViewModel.userId = args.userId

        // Disable buttons until we know if they're not the current
        setCurrentUserButtonState()
        setupRecyclerView()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                context?.apply {
                    buildTemplateIfNeeded()

                    val collections = collectionsViewModel.getLiveCollection(false)
                    withContext(Dispatchers.Main) { // now that we know collections are retrieved, we can observer them
                        collections.observe(viewLifecycleOwner, {
                            adapter.setData(it)
                        })
                        collectionsViewModel.collectionsPositions.observe(viewLifecycleOwner, {
                            adapter.changedPositions(it)
                        })

                        selectedUserDetailViewModel.getUserLive().observe(viewLifecycleOwner, {
                            it?.let { pu ->

                                // got our selected user, update their info when it changes
                                binding.header.name.text = pu.name
                                // Keep image updated if it's changed
                                context?.let { ctx ->
                                    binding.header.image.setImageDrawable(
                                        DrawableUtils.getDrawableImage(
                                            ctx,
                                            pu.displayImage,
                                            R.drawable.ic_baseline_image_24,
                                        ),
                                    )
                                }

                                binding.header.image.setOnClickListener {
                                    showEditImageDialog(pu.displayImage)
                                }

                                if (pu.userId != 1) {
                                    // Everyone but user 1 can be deleted
                                    binding.deleteButton.isEnabled = true
                                }
                            }
                        })

                        binding.deleteButton.setOnClickListener {
                            showDeleteUserDialog()
                        }
                    }
                }
            }
        }

        binding.header.name.setOnClickListener {
            createEditUserDialog()
        }
        binding.addFab.setOnClickListener {
            addCollectionDialog()
        }

        this.setHasOptionsMenu(true)

        return binding.root
    }

    /**
     * If we called the [com.gy.chatpaths.aac.app.ui.selector.template.TemplateFragment],
     * then handle the response if the response is not null
     */
    private suspend fun Context.buildTemplateIfNeeded() {
        // If we returned from selecting a template, build it
        val templateId: Collection.Companion.Identifier? = getNavigationResult("template_id")
        val templateName: String? = getNavigationResult("template_name")
        if (templateId != null && templateName != null) {
            Collection.getBuilder(
                templateId,
                this,
                repository,
                args.userId,
            ).setName(templateName).build()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.manager_user_detail_fragment_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()

        return when (item.itemId) {
            R.id.fragmentTemplate -> {
                showChooseTemplateDialog()
                true
            }
            else -> item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
        }
    }

    private fun showChooseTemplateDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.add_from_template_dialog_title))
                .setMessage(getString(R.string.add_from_template_dialog_message))
                .setPositiveButton(it.getString(android.R.string.ok)) { _, _ ->
                    lifecycleScope.launch {
                        val action =
                            UserDetailFragmentDirections.actionUserDetailFragmentToTemplateFragment()
                        findNavController().navigate(action)
                    }
                }
                .setNegativeButton(it.getString(R.string.cancel)) { _, _ -> }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        guidedTour.dismiss()
        _binding = null
    }

    override fun showDeleteCollectionDialog(collectionId: Int, onItemRemoved: (success: Boolean) -> Unit) {
        context?.apply {
            MaterialAlertDialogBuilder(this)
                .setTitle(this.getString(R.string.delete_collection_title))
                .setMessage(this.getString(R.string.delete_collection_message))
                .setPositiveButton(this.getString(android.R.string.ok)) { _, _ ->
                    lifecycleScope.launch {
                        collectionsViewModel.removeCollection(collectionId)
                        if (args.usingCollection) {
                            // Bad things can happen if we delete and are using the collection
                            val action = UserDetailFragmentDirections.actionGlobalNavHome()
                            findNavController().navigate(action)
                        } else {
                            onItemRemoved(true)
                        }
                    }
                }
                .setNegativeButton(this.getString(R.string.cancel)) { _, _ ->
                    onItemRemoved(false)
                }
                .setOnCancelListener {
                    onItemRemoved(false)
                }
                .show()
        }
    }

    private fun setupRecyclerView() {
        adapter = MyCollectionsRecyclerViewAdapter(this, collectionsViewModel, this)
        binding.list.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }
        // We attach in onResume
        // binding.list.adapter = adapter

        val callback = SimpleItemTouchHelperCallback(adapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.list)

        // TODO fix the swipe to delete logic
//        val swipeHandler = object : SwipeToDeleteCallback(binding.root.context) {
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                adapter.removeAt(viewHolder.bindingAdapterPosition)
//            }
//
//        }
//        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.list)
    }

    private fun showDeleteUserDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(it.getString(R.string.delete_user_dialog_title))
                .setMessage(it.getString(R.string.delete_user_dialog_message))
                .setPositiveButton(it.getString(android.R.string.ok)) { _, _ ->
                    setCurrentUserButtonState()
                    lifecycleScope.launch {
                        context?.apply {
                            currentUser.delete()
                            val action =
                                UserDetailFragmentDirections.actionGlobalNavHome()
                            findNavController().navigate(action)
                        }
                    }
                }
                .setNegativeButton(it.getString(R.string.cancel)) { _, _ -> }
                .show()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.list.adapter = null
    }

    override fun onResume() {
        super.onResume()
        binding.list.adapter = adapter

        (activity as MainActivity).enableToolbar()

        guidedTour.addGuidedEntry(
            binding.addFab,
            GuidedTour.Dialog.COLLECTION_ADD,
            getString(R.string.onboard_add_collection_primary),
            getString(R.string.onboard_add_collection_secondary),
        ).addGuidedEntry(
            R.id.fragmentTemplate,
            GuidedTour.Dialog.COLLECTION_ADD_TEMPLATE,
            getString(R.string.onboard_add_collection_template_primary),
            getString(R.string.onboard_add_collection_template_secondary),
            targetIcon = R.drawable.ic_baseline_image_24,
        ).show(this)
    }

    override fun onStoreImageUri(uri: Uri) {
        lifecycleScope.launch {
            selectedUserDetailViewModel.setUserImage(args.userId, uri)
        }
    }

    override suspend fun onDeleteImage() {
        selectedUserDetailViewModel.deleteImage(args.userId)
    }

    override fun navigateToImageSelectFragment() {
        val action =
            UserDetailFragmentDirections.actionUserDetailFragmentToImageSelectFragment()
        findNavController().navigate(action)
    }

    private fun createEditUserDialog() {
        val binding = DialogUserNameBinding.inflate(layoutInflater)
        val m = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.edit_user_dialog_title))
            .setMessage(getString(R.string.edit_user_dialog_message))
            .setView(binding.root)
            .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                // if the insert fails, the username already exists
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }
            .show()
        setUsernameValidationOnClickListener(m, binding, ::editUser)
    }

    private fun addCollectionDialog() {
        val binding = DialogEditCollectionBinding.inflate(layoutInflater)
        val m = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.add_path_collection_dialog_title))
            .setMessage(getString(R.string.add_path_collection_dialog_name))
            .setView(binding.root)
            .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }.show()
        m.onCollectionValidated(this, binding) {
            collectionsViewModel.addCollection(it)
        }
    }

    private fun setUsernameValidationOnClickListener(
        dialog: AlertDialog,
        binding: DialogUserNameBinding,
        action: suspend (name: String) -> Boolean,
    ) {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            if (binding.username.text.toString().isBlank()) {
                binding.errorMessage.text = getString(R.string.user_empty_not_allowed)
                binding.errorMessage.visibility = View.VISIBLE
            } else {
                lifecycleScope.launch {
                    if (action(binding.username.text.toString())) { // success
                        dialog.dismiss()
                    } else {
                        binding.errorMessage.text = getString(R.string.user_already_exists)
                        binding.errorMessage.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private suspend fun editUser(name: String): Boolean {
        return currentUser.updateName(name)
    }

    override fun setIsEnabled(collectionId: Int, enabled: Boolean) {
        lifecycleScope.launch {
            collectionsViewModel.setIsEnabled(collectionId, enabled)
        }
    }

    override fun setCollectionPosition(collectionId: Int, position: Int) {
        lifecycleScope.launch {
            collectionsViewModel.setCollectionPosition(collectionId, position)
        }
    }

    override fun updateCollectionOrder(collections: List<PathCollection>) {
        lifecycleScope.launch {
            collectionsViewModel.updateCollectionOrder(collections)
        }
    }

    override fun editCollection(collectionId: Int) {
        val action =
            UserDetailFragmentDirections.actionUserDetailFragmentToCollectionDetailFragment(
                collectionId,
                args.usingCollection,
            )
        findNavController().navigate(action)
    }

    companion object {
        fun AlertDialog.onCollectionValidated(
            fragment: Fragment,
            binding: DialogEditCollectionBinding,
            action: suspend (name: String) -> Unit,
        ): AlertDialog {
            getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                if (binding.editCollectionTitleView.text.toString().isBlank()) {
                    binding.errorMessage.text =
                        fragment.getString(R.string.error_empty_string_not_allowed)
                    binding.errorMessage.visibility = View.VISIBLE
                } else {
                    fragment.lifecycleScope.launch {
                        action(binding.editCollectionTitleView.text.toString())
                        dismiss()
                    }
                }
            }
            return this
        }
    }
}
