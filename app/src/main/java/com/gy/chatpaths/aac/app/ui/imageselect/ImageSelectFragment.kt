package com.gy.chatpaths.aac.app.ui.imageselect

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.databinding.FragmentImageselectBinding
import com.gy.chatpaths.aac.app.di.module.CurrentUser
import com.gy.chatpaths.aac.app.di.module.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ImageSelectFragment : Fragment(), ImageSelectListener {
    private val viewmodel: ImageSelectViewModel by viewModels()

    private var columnCount: Int = 2
    private var _binder: FragmentImageselectBinding? = null
    private val binder get() = _binder!!

    @Inject
    lateinit var currentUser: CurrentUser

    @Inject
    lateinit var firebase: Firebase

    var adapter: RVAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binder = FragmentImageselectBinding.inflate(layoutInflater, container, false)

        columnCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 3
        }

        context?.let { context ->
            adapter = RVAdapter(this)
            binder.recycler.layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            binder.recycler.adapter = adapter

            lifecycleScope.launch {
                val images = viewmodel.getImages(context)
                adapter!!.setData(images)
            }
        }

        setHasOptionsMenu(true)
        return binder.root
    }

    override fun onDestroyView() {
        _binder = null
        super.onDestroyView()
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.image_select, menu)
        val search = menu.findItem(R.id.app_bar_search).actionView as SearchView

        search.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(text: String?): Boolean {
                    // nothing
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    lifecycleScope.launch {
                        val images = viewmodel.getImages(context, newText)
                        adapter?.setData(images)
                    }
                    return true
                }
            })
        }
    }

    override fun selectImage(image: String) {
        setNavigationResult(image, "image")
        findNavController().navigateUp()
    }

    private fun <T> Fragment.setNavigationResult(result: T, key: String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
    }
}
