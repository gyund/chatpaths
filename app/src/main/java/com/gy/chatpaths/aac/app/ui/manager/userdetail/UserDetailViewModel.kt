package com.gy.chatpaths.aac.app.ui.manager.userdetail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gy.chatpaths.aac.app.model.PathCollection
import com.gy.chatpaths.aac.app.model.PathUser
import com.gy.chatpaths.aac.app.model.source.CPRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val repository: CPRepository,
) : ViewModel() {

    var userId by Delegates.notNull<Int>()

    /** Lazy load RoomDatabase stuff so we can use lifecycle management to initialize them on the
     *  appropriate thread in the
     *  corresponding fragments
     */

    suspend fun setUserImage(userId: Int, uri: Uri) {
        repository.setUserImage(userId, uri)
    }

    fun deleteImage(userId: Int) {
        viewModelScope.launch {
            repository.deleteUserImage(userId)
        }
    }

    fun getUserLive(): LiveData<PathUser?> {
        return repository.getLiveUser(userId)
    }

    companion object {

        fun getLiveCollection(
            repository: CPRepository,
            userId: Int,
            enabledOnly: Boolean,
        ): LiveData<List<PathCollection>> {
            return repository.getLivePathCollections(userId, enabledOnly)
        }

        suspend fun getCollection(
            repository: CPRepository,
            userId: Int,
            enabledOnly: Boolean,
        ): List<PathCollection> {
            return repository.getPathCollections(userId, enabledOnly)
        }
    }
}
