package com.gy.chatpaths.aac.app.ui.manager.collectiondetail

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.gy.chatpaths.aac.app.DrawableUtils
import com.gy.chatpaths.aac.data.PathCollection
import com.gy.chatpaths.aac.data.source.CPRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: CPRepository,
) : ViewModel() {

    var collectionId by Delegates.notNull<Int>()

    fun getLivePathCollectionInfo(collectionId: Int) = repository.getLiveCollection(collectionId)

    suspend fun delete(collectionId: Int) {
        repository.getPathCollection(collectionId)?.let {
            delete(it)
        }
    }

    suspend fun delete(collection: PathCollection) {
        repository.deleteCollection(collection)
    }

    suspend fun setImage(collectionId: Int, uri: Uri) {
        repository.getPathCollection(collectionId)?.let {
            setImage(it, uri)
        }
    }

    private suspend fun setImage(collection: PathCollection, uri: Uri) {
        repository.setCollectionImage(collection.collectionId, uri)
    }

    suspend fun deleteImage(collectionId: Int) {
        deleteImage(repository, collectionId)
    }

    suspend fun updateDisplayName(collectionId: Int, name: String) {
        repository.setCollectionTitle(collectionId, name)
    }

    suspend fun getImage(): String? {
        return repository.getPathCollection(collectionId)?.imageUri
    }

    companion object {

        suspend fun userImageIsNotSet(
            repository: CPRepository,
            collectionId: Int,
        ): Boolean {
            val pc = repository.getPathCollection(collectionId)
            return pc?.imageUri?.isBlank() ?: return true
        }

        fun isDrawableDefault(collection: PathCollection): Boolean {
            return collection.imageUri.isNullOrBlank()
        }

        fun getDrawable(context: Context, collection: PathCollection): Drawable? {
            var img: Drawable? = null
            if (!collection.imageUri.isNullOrBlank()) {
                img = DrawableUtils.getDrawableImage(context, collection.imageUri, null)
            }
            return img
        }

        fun getTitle(collection: PathCollection): String? {
            return collection.name
        }

        suspend fun deleteImage(repository: CPRepository, collectionId: Int) {
            repository.getPathCollection(collectionId)?.let {
                deleteImage(repository, it)
            }
        }

        private suspend fun deleteImage(repository: CPRepository, collection: PathCollection) {
            withContext(Dispatchers.IO) {
                collection.apply {
                    this.imageUri?.let {
                        repository.deleteUserDisplayImage(collectionId)
                    }
                }
            }
        }
    }
}
