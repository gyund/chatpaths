package com.gy.chatpaths.aac.app.ui.home

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.gy.chatpaths.aac.app.CommonFeatureFragment.Companion.getNavigationResult
import com.gy.chatpaths.aac.app.MainActivity
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.builder.create.Collection
import com.gy.chatpaths.aac.app.databinding.DialogEditCollectionBinding
import com.gy.chatpaths.aac.app.databinding.FragmentHomeBinding
import com.gy.chatpaths.aac.app.di.module.CurrentUser
import com.gy.chatpaths.aac.app.di.module.Firebase
import com.gy.chatpaths.aac.app.di.module.GuidedTour
import com.gy.chatpaths.aac.app.di.module.InAppReview
import com.gy.chatpaths.aac.app.model.source.CPRepository
import com.gy.chatpaths.aac.app.ui.manager.userdetail.UserCollectionsViewModel
import com.gy.chatpaths.aac.app.ui.manager.userdetail.UserDetailFragment.Companion.onCollectionValidated
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var columnCount: Int = 2

    @Suppress("ktlint:standard:property-naming")
    private var _binder: FragmentHomeBinding? = null
    private val binder get() = _binder!!

    @Inject
    lateinit var currentUser: CurrentUser

    @Inject
    lateinit var firebase: Firebase

    @Inject
    lateinit var inAppReview: InAppReview

    @Inject
    lateinit var guidedTour: GuidedTour

    @Inject
    lateinit var repository: CPRepository

    private val collectionsViewModel: UserCollectionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binder = FragmentHomeBinding.inflate(layoutInflater, container, false)
        collectionsViewModel.userId = currentUser.userId
        // Apply configs if new ones were received in the background
        firebase.refresh()

        columnCount =
            when (resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> 3
                else -> 2
            }

        // When we create the view, check if we should display
        // older versions of resume might use fragments which would cause resume to continually
        // be called
        context?.let {
            inAppReview.showInAppReviewDialog(activity as MainActivity)
        }

        // Delay populating the grid until we know if the database has been initialized
        (activity as MainActivity).onDatabaseInitialized.observe(viewLifecycleOwner, {
            if (it) {
                setupViewContents()
            }
        })

        this.setHasOptionsMenu(true)
        return binder.root
    }

    private fun setupViewContents() {
        context?.apply {
            val adapter = ActiveCollectionsRVAdapter(this)
            binder.chatviewRecycler.layoutManager =
                when {
                    columnCount <= 1 -> LinearLayoutManager(this)
                    else -> GridLayoutManager(this, columnCount)
                }
            binder.chatviewRecycler.adapter = adapter

            lifecycleScope.launch {
                buildTemplateIfNeeded()
                currentUser.getUserLive().observe(viewLifecycleOwner) {
                    // The userId is only valid after validate is called
                    it?.apply {
                        val navigationView =
                            (activity as MainActivity).findViewById(R.id.nav_view) as NavigationView
                        val headerView = navigationView.getHeaderView(0)

                        // get menu from navigationView
                        //                                val menu = navigationView.menu
                        //                                val username = menu.findItem(R.id.username)
                        val username = headerView.findViewById<TextView>(R.id.textView)
                        if (name != getString(R.string.username_default)) {
                            username.text = name
                            username.visibility = View.VISIBLE
                        } else {
                            username.visibility = View.GONE
                            username.text = null
                        }
                        //                                if(displayImage != null) {
                        //                                    User.getDrawable(binder.root.context, it)?.apply {
                        //                                        setBounds(0,0,48,48)
                        //                                        username.setCompoundDrawables(null,null,this,null)
                        //                                    }
                        //                                }
                    }
                }
                collectionsViewModel.getLiveCollection(true).observe(viewLifecycleOwner) {
                    lifecycleScope.launch {
                        context?.apply {
                            val collections = collectionsViewModel.getCollection(true)

                            collections.apply {
                                adapter.setData(this)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        guidedTour.dismiss()
        _binder = null
    }

    override fun onResume() {
        (activity as MainActivity).enableToolbar()
        binder.chatviewRecycler.adapter?.notifyDataSetChanged()

        super.onResume()
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater,
    ) {
        inflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()

        return when (item.itemId) {
            R.id.createCollection -> {
                addCollectionDialog()
                true
            }
            R.id.organizeCollections -> {
                collectionsViewModel.organizeCollectionOrder()
                true
            }
            R.id.resetHelp -> {
                guidedTour.resetHelpDialogs(this)
                true
            }
            R.id.userDetailFragment -> {
                lifecycleScope.launch {
                    currentUser.getUser()?.let { user ->
                        val action =
                            HomeFragmentDirections.actionUserFragmentToUserDetailFragment(
                                user.userId,
                                false,
                            )
                        findNavController().navigate(action)
                    }
                }
                true
            }
            else -> item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
        }
    }

    private fun addCollectionDialog() {
        val binding = DialogEditCollectionBinding.inflate(layoutInflater)
        val m =
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.add_path_collection_dialog_title))
                .setMessage(getString(R.string.add_path_collection_dialog_name))
                .setView(binding.root)
                .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                }
                .setNegativeButton(getString(android.R.string.cancel)) { _, _ ->
                }
                .setNeutralButton(getString(R.string.add_template)) { _, _ ->
                    val action = HomeFragmentDirections.actionNavHomeToTemplateFragment()
                    findNavController().navigate(action)
                }.show()

        m.onCollectionValidated(this, binding) {
            collectionsViewModel.addCollection(it)
        }
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
                currentUser.userId,
            ).setName(templateName).build()
        }
    }
}
