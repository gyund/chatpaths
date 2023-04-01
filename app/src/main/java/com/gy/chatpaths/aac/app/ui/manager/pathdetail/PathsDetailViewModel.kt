package com.gy.chatpaths.aac.app.ui.manager.pathdetail

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gy.chatpaths.aac.data.Path
import com.gy.chatpaths.aac.data.source.CPRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class PathsDetailViewModel @Inject constructor(
    val repository: CPRepository
) : ViewModel() {

    var collectionId by Delegates.notNull<Int>()
    var parentId: Int? = null
        set(value) {
            field =
                if (0 == value) { // there's no 0 parentId, but typesafe arg passing doesn't allow null
                    null
                } else {
                    value
                }
        }
    var pathId by Delegates.notNull<Int>()
    var userId by Delegates.notNull<Int>()

    val path by lazy {
        repository.getLivePath(userId, collectionId, pathId)
    }

    suspend fun getPath(): Path? {
        return repository.getPathFromCollection(userId, collectionId, pathId)
    }

    suspend fun setImage(uri: Uri) {
        setImage(uri.toString())
    }

    private suspend fun setImage(s: String) {
        repository.getUserById(userId)?.let { cu ->
            repository.setPathImageUser(pathId, s, cu.userId)
        }
    }

    suspend fun deleteImage() {
        getPath()?.apply {
            repository.setPathImageUser(pathId, null, userId)
        }
    }

    suspend fun deletePath() {
        repository.deletePath(
            collectionId = collectionId,
            pathId = pathId
        )
    }

    suspend fun updateTitle(title: String) {
        repository.updatePathTitle(pathId, title)
    }

    suspend fun setIsAnchored(anchored: Boolean) {
        repository.getUserById(userId)?.let {
            repository.setPathIsAnchored(
                userId = it.userId,
                collectionId = collectionId,
                pathId = pathId,
                anchored = anchored
            )
        }

    }

    /**
     * Add a child to the current path
     * @param title Name of the path
     */
    suspend fun addChildPath(title: String) {
        getPath()?.apply {
            val parent = pathId
            val position = defaultPosition

            repository.addPath(
                userId = userId,
                collectionId = collectionId,
                title = title,
                imageUri = null,
                parentId = parent,
                position = position,
                anchored = false
            )
        }
    }

    private var childPath: Path? = null

    private suspend fun getChildPath(): Path? {
        if (null == childPath) {
            childPath = repository.getChildOfParent(userId, collectionId, pathId, true)
        }
        return childPath
    }

    suspend fun hasChild(): Boolean {
        return null != getChildPath()
    }

    suspend fun setAudioPrompt(uri: Uri) {
        repository.setAudioPrompt(pathId, uri)
    }

    fun deleteAudioPrompt(pathId: Int) {
        viewModelScope.launch {
            repository.deleteAudioPrompt(pathId)
        }
    }


}