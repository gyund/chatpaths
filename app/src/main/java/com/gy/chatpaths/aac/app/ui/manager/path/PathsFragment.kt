package com.gy.chatpaths.aac.app.ui.manager.path

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gy.chatpaths.aac.app.CommonFeatureFragment
import com.gy.chatpaths.aac.app.DrawableUtils
import com.gy.chatpaths.aac.app.MainActivity
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.SwipeToDeleteCallback
import com.gy.chatpaths.aac.app.databinding.DialogEditPathTitleBinding
import com.gy.chatpaths.aac.app.databinding.FragmentPathsBinding
import com.gy.chatpaths.aac.app.di.module.CurrentUser
import com.gy.chatpaths.aac.app.di.module.GuidedTour
import com.gy.chatpaths.aac.app.ui.helper.DatabaseConversionHelper
import com.gy.chatpaths.aac.app.ui.helper.OnStartDragListener
import com.gy.chatpaths.aac.app.ui.helper.SimpleItemTouchHelperCallback
import com.gy.chatpaths.model.Path
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PathsFragment : CommonFeatureFragment(), OnStartDragListener, PathManagerListener {

    private val columnCount: Int = 1
    private var _binding: FragmentPathsBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: PathsViewModel by viewModels()

    private val args: PathsFragmentArgs by navArgs()
    private lateinit var adapter: MyPathsRecyclerViewAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    @Inject
    lateinit var currentUser: CurrentUser

    @Inject
    lateinit var guidedTour: GuidedTour

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPathsBinding.inflate(layoutInflater, container, false)
        viewmodel.collectionId = args.collectionId
        viewmodel.parentId = args.parentId

        handleImage()

        setupRecyclerView()

        lifecycleScope.launch {
            copyPathIfNeeded()
            updateAdapterData()

            viewmodel.getLiveCollection().observe(viewLifecycleOwner) {
                context?.let { ctx ->
                    it?.apply {
                        lifecycleScope.launch {
                            if (0 == args.parentId) {
                                val drawable =
                                    DatabaseConversionHelper.getCollectionDrawable(ctx, it)
                                binding.header.image.setImageDrawable(drawable)
                                binding.header.name.text = name
                            } else {
                                // use the path ID
                                viewmodel.getPath(args.parentId)?.apply {
                                    val drawable = DrawableUtils.getDrawableImage(ctx, imageUri)
                                    binding.header.image.setImageDrawable(drawable)
                                    binding.header.name.text = name
                                }
                            }

                            binding.sortSwitch.isChecked = autoSort
                        }
                    }
                }
            }

            binding.sortSwitch.setOnCheckedChangeListener { _, isChecked ->
                lifecycleScope.launch {
                    viewmodel.setAutoSort(isChecked)
                    updateAdapterData()
                }
            }

            if (0 == args.parentId) {
                binding.header.image.setOnClickListener {
                    lifecycleScope.launch {
                        showEditImageDialog(viewmodel.getCollection()?.imageUri)
                    }
                }

                binding.header.name.setOnClickListener {
                    showEditCollectionTitleDialog()
                }
            } else { // edit path info
                // TODO: Add handlers to edit a path instead of a collection
            }
        }

        binding.addFab.setOnClickListener {
            showAddPathDialog()
        }

        this.setHasOptionsMenu(true)
        return binding.root
    }

    private fun getReadOutloudIcon(context: Context, readOutLoud: Boolean? = null): Drawable? {
        val preferences = activity?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        val ro = readOutLoud ?: preferences?.getBoolean("read_out_loud_preference", false)

        return if (ro == true) {
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_baseline_volume_up_24,
            )
        } else {
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_baseline_volume_off_24,
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        guidedTour.dismiss()
        _binding = null
    }

    private fun toggleReadOutloud() {
        val preferences = activity?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        val readOutLoud = preferences?.getBoolean("read_out_loud_preference", false)
        preferences?.edit {
            if (readOutLoud != null) {
                putBoolean("read_out_loud_preference", readOutLoud).apply()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.manager_path_fragment_menu, menu)

        context?.apply {
            menu.findItem(R.id.readOutLoud)?.icon = getReadOutloudIcon(this)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.readOutLoud -> {
                toggleReadOutloud()
                context?.apply {
                    item.icon = getReadOutloudIcon(this)
                }
                true
            }
            R.id.restoreOrderMenu -> {
                lifecycleScope.launch {
                    viewmodel.restoreDefaultPathOrder()

                    // Reinitialize
                    updateAdapterData()
                }
                true
            }
            R.id.resetSortingStatsMenu -> {
                lifecycleScope.launch {
                    viewmodel.resetCollectionStatistics()

                    // Reinitialize
                    updateAdapterData()
                }
                true
            }
            R.id.copyToUser -> {
                showCopyToUserDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showCopyToUserDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.copy_to_user_dialog_title))
                .setMessage(getString(R.string.copy_to_user_dialog_message))
                .setPositiveButton(it.getString(android.R.string.ok)) { _, _ ->
                    lifecycleScope.launch {
                        val action =
                            PathsFragmentDirections.actionPathsFragmentToUserSelectorFragment()
                        findNavController().navigate(action)
                    }
                }
                .setNegativeButton(it.getString(R.string.cancel)) { _, _ -> }
                .show()
        }
    }

    /**
     * If we called the [com.gy.chatpaths.aac.app.ui.selector.template.TemplateFragment],
     * then handle the response if the response is not null
     */
    private suspend fun copyPathIfNeeded() {
        // If we returned from selecting a template, build it
        val userId: Int? = getNavigationResult("user_id")

        userId?.let {
            viewmodel.copyTo(userId)
            showSwitchUserAfterCopyPathDialog(userId)
        }
    }

    private fun showSwitchUserAfterCopyPathDialog(userId: Int) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.switch_users_after_copy_path_dialog_title))
                .setMessage(getString(R.string.switch_users_after_copy_path_dialog_message))
                .setPositiveButton(it.getString(android.R.string.ok)) { _, _ ->
                    lifecycleScope.launch {
                        currentUser.setUser(userId)
                        val action = PathsFragmentDirections.actionGlobalNavHome()
                        findNavController().navigate(action)
                    }
                }
                .setNegativeButton(it.getString(R.string.cancel)) { _, _ -> }
                .show()
        }
    }

    private suspend fun updateAdapterData() {
        val paths = viewmodel.getPaths()
        Log.d("updateAdapterData", "Paths: $paths")
        paths?.apply { adapter.setData(this) }
    }

    override fun showEditCollectionTitleDialog() {
        val alertView = DialogEditPathTitleBinding.inflate(layoutInflater)
        if (!binding.header.name.text.isNullOrBlank()) {
            alertView.editPathTitleView.setText(binding.header.name.text.toString())
        }

        alertView.editPathTitleView.hint = getString(R.string.new_collection_edittext_description)

        context?.let {
            MaterialAlertDialogBuilder(it)
                .setView(alertView.root)
                .setTitle(getString(R.string.edit_collection_dialog_title))
                .setMessage(getString(R.string.edit_collection_dialog_message))
                .setPositiveButton(
                    getString(R.string.modify),
                ) { _, _ ->
                    val collectionName = alertView.editPathTitleView.text.toString()
                    binding.header.name.text = collectionName

                    lifecycleScope.launch {
                        viewmodel.updateCollectionTitle(collectionName)
                    }
                }
                .setNegativeButton(
                    getString(R.string.cancel),
                ) { _, _ ->
                    // nothing
                }
                .show()
        }
    }

    override fun setIsEnabled(pathId: Int, enabled: Boolean) {
        lifecycleScope.launch {
            viewmodel.setIsEnabled(pathId, enabled)
        }
    }

    override fun setPathPosition(pathId: Int, position: Int?) {
        lifecycleScope.launch {
            viewmodel.setPathPosition(pathId, position)
        }
    }

    override fun updatePathOrder(paths: List<Path>) {
        lifecycleScope.launch {
            viewmodel.updatePathOrder(paths)
        }
    }

    override fun showAddPathDialog() {
        val alertView = DialogEditPathTitleBinding.inflate(layoutInflater)

        context?.let {
            MaterialAlertDialogBuilder(it)
                .setView(alertView.root)
                .setTitle(getString(R.string.add_path))
                .setPositiveButton(
                    getString(R.string.modify),
                ) { _, _ ->
                    val pathName = alertView.editPathTitleView.text.toString()

                    lifecycleScope.launch {
                        viewmodel.addPath(pathName)
                        updateAdapterData()
                    }
                }
                .setNegativeButton(
                    getString(R.string.cancel),
                ) { _, _ ->
                    // nothing
                }
                .show()
        }
    }

    private fun setupRecyclerView() {
        val frag = this
        context?.apply {
            adapter = MyPathsRecyclerViewAdapter(frag, viewmodel, frag)
            binding.list.layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            // We attach in onResume
            // binding.list.adapter = adapter

            val callback = SimpleItemTouchHelperCallback(adapter)
            itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(binding.list)

            val swipeHandler = object : SwipeToDeleteCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    lifecycleScope.launch {
                        adapter.removeAt(viewHolder.bindingAdapterPosition)
                    }
                }

                override fun getSwipeDirs(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                ): Int {
                    viewHolder.itemView.getTag(R.id.collection_id)?.let {
                        return super.getSwipeDirs(recyclerView, viewHolder)
                    }

                    return 0
                }
            }
            ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.list)
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
            GuidedTour.Dialog.PATH_ADD,
            getString(R.string.onboard_add_path_primary),
            getString(R.string.onboard_add_path_secondary),
        ).addGuidedEntry(
            R.id.copyToUser,
            GuidedTour.Dialog.PATH_COPY_TO_USER,
            getString(R.string.onboard_path_copy_to_user_primary),
            getString(R.string.onboard_path_copy_to_user_secondary),
            targetIcon = R.drawable.ic_baseline_content_copy_24,
        ).addGuidedEntry(
            R.id.readOutLoud,
            GuidedTour.Dialog.PATH_READ_OUT_LOUD,
            getString(R.string.onboard_path_read_out_loud_primary),
            getString(R.string.onboard_path_read_out_loud_secondary),
            targetIcon = R.drawable.ic_baseline_volume_up_24,
        ).show(this)
    }

    override fun onStoreImageUri(uri: Uri) {
        lifecycleScope.launch {
            viewmodel.setCollectionImage(uri)
        }
    }

    override suspend fun onDeleteImage() {
        viewmodel.deleteCollectionImage()
    }

    override fun navigateToImageSelectFragment() {
        val action =
            PathsFragmentDirections.actionPathsFragmentToImageSelectFragment()
        findNavController().navigate(action)
    }
}
