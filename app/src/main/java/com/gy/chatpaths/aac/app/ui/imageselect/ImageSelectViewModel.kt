package com.gy.chatpaths.aac.app.ui.imageselect

import android.content.Context
import androidx.lifecycle.ViewModel
import com.gy.chatpaths.aac.data.source.CPRepository
import com.gy.chatpaths.aac.resource.UriHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ImageSelectViewModel @Inject constructor(
    val repository: CPRepository
) : ViewModel() {


    private suspend fun addLocalStorageImages(
        list: MutableList<String>,
        searchText: String? = null
    ) {
        val filteredFiles =
            repository.getPathImages(searchText).filter { // we only want the user images
                it.endsWith(".jpg")
            }
        filteredFiles.forEach {
            list.add(it)
        }
    }

    suspend fun getImages(context: Context, searchText: String? = null): List<String> {
        val imageUris = mutableListOf<String>()
        addLocalStorageImages(imageUris, searchText)
        UriHelper.getResourceUris(context, imageUris, searchText)

//        return repository.getPathImages()

        return imageUris
    }
}