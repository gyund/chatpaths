package com.gy.chatpaths.aac.app.ui.manager.userdetail

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gy.chatpaths.aac.app.ui.helper.DatabaseConversionHelper
import com.gy.chatpaths.aac.app.model.PathCollection
import com.gy.chatpaths.aac.app.model.source.CPRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class UserCollectionsViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val repository: CPRepository,
) : ViewModel() {

    /** Lazy load RoomDatabase stuff so we can use lifecycle management to initialize them on the
     *  appropriate thread in the
     *  corresponding fragments
     */

    var userId: Int by Delegates.notNull()

    fun getLiveCollection(enabledOnly: Boolean): LiveData<List<PathCollection>> {
        return UserDetailViewModel.getLiveCollection(repository, userId, enabledOnly)
    }

    suspend fun getCollection(enabledOnly: Boolean): List<PathCollection> {
        return UserDetailViewModel.getCollection(repository, userId, enabledOnly)
    }

    var collectionsPositions: MutableLiveData<Set<Int>>
        private set

    suspend fun addCollection(name: String): Boolean {
        var success = true

        val pc = PathCollection(
            collectionId = 0, // new
            name = name,
            userId = userId,
        )

        try {
            repository.addCollection(pc)
        } catch (e: Exception) {
            success = false
        }

        return success
    }

    fun removeCollection(collectionId: Int) {
        viewModelScope.launch {
            repository.deleteCollection(collectionId)
        }
    }

    fun organizeCollectionOrder() {
        viewModelScope.launch {
            repository.resetCollectionOrder(userId)
        }
    }

    suspend fun setIsEnabled(collectionId: Int, enabled: Boolean) {
        repository.setCollectionIsVisible(userId, collectionId, enabled)
    }

    suspend fun setCollectionPosition(collectionId: Int, position: Int) {
        repository.setCollectionPosition(userId, collectionId, position)
    }

    suspend fun updateCollectionOrder(collections: List<PathCollection>) {
        repository.updateCollectionOrder(userId, collections)
    }

    companion object {

        fun getDrawable(context: Context, collection: PathCollection): Drawable? {
            return DatabaseConversionHelper.getCollectionDrawable(context, collection)
        }

        fun getTitle(context: Context, collection: PathCollection): String? {
            return collection.name
        }
    }

    init {
        this.collectionsPositions = MutableLiveData(mutableSetOf())
    }
}
