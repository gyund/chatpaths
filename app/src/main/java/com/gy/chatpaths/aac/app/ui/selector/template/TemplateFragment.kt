package com.gy.chatpaths.aac.app.ui.selector.template

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gy.chatpaths.aac.app.MainActivity
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.databinding.DialogEditCollectionBinding
import com.gy.chatpaths.aac.app.databinding.FragmentHomeBinding
import com.gy.chatpaths.aac.app.di.module.CurrentUser
import com.gy.chatpaths.aac.app.ui.manager.userdetail.UserDetailFragment.Companion.onCollectionValidated
import com.gy.chatpaths.aac.resource.create.Collection
import com.gy.chatpaths.aac.resource.template.collection.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class TemplateFragment : Fragment(), TemplateSelectionListener {
    private var columnCount: Int = 2
    private var _binder: FragmentHomeBinding? = null
    private val binder get() = _binder!!

    @Inject
    lateinit var currentUser: CurrentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binder = FragmentHomeBinding.inflate(layoutInflater, container, false)
        // Apply configs if new ones were received in the background

        columnCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 3
            else -> 2
        }

//        val act = (activity as MainActivity)
//        act.showInAppReviewDialog()

        // Delay populating the grid until we know if the database has been initialized
        (activity as MainActivity).onDatabaseInitialized.observe(viewLifecycleOwner, {
            if (it) {
                setupViewContents()
            }
        })

        return binder.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binder = null
    }

    private fun setupViewContents() {
        context?.let { context ->
            val adapter = RVAdapter(context, this)
            binder.chatviewRecycler.layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            binder.chatviewRecycler.adapter = adapter

            lifecycleScope.launch {
                currentUser.getUserLive().observe(viewLifecycleOwner, {
                    lifecycleScope.launch {
                        val collections = listOf<Collection>(
                            Essentials(context, currentUser.repository, currentUser.userId),
                            Starter(context, currentUser.repository, currentUser.userId),
                            Breakfast(context, currentUser.repository, currentUser.userId),
                            Lunch(context, currentUser.repository, currentUser.userId),
                            Dinner(context, currentUser.repository, currentUser.userId),
                            Problem(context, currentUser.repository, currentUser.userId),
                            Family(context, currentUser.repository, currentUser.userId)
                        )

                        collections.apply {
                            adapter.setData(this)
                        }
                    }
                })
            }
        }
    }

    override fun onResume() {
        (activity as MainActivity).enableToolbar()
        binder.chatviewRecycler.adapter?.notifyDataSetChanged()

        super.onResume()
    }

    private var pendingTemplateId: Collection.Companion.Identifier? = null

    override fun onTemplateSelected(collection: Collection) {
        val frag = this
        context?.apply {
            pendingTemplateId = collection.id
            val binding = DialogEditCollectionBinding.inflate(layoutInflater)
            val m = MaterialAlertDialogBuilder(this)
                .setView(binding.root)
                .setTitle(getString(R.string.add_path_collection_dialog_title))
                .setMessage(getString(R.string.add_path_collection_dialog_name))
                .setPositiveButton(getString(android.R.string.ok)) { _, _ ->

                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    pendingTemplateId = null
                }
                .setOnCancelListener {
                    pendingTemplateId = null
                }.show()

            m.onCollectionValidated(frag, binding) {
                setNavigationResult(it, "template_name")
                setNavigationResult(pendingTemplateId, "template_id")
                findNavController().navigateUp()
            }
        }
    }

    private fun <T> Fragment.setNavigationResult(result: T, key: String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
    }
}