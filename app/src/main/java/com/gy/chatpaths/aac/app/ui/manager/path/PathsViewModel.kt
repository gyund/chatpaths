package com.gy.chatpaths.aac.app.ui.manager.path

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gy.chatpaths.aac.app.di.module.CurrentUser
import com.gy.chatpaths.aac.app.model.Path
import com.gy.chatpaths.aac.app.model.PathCollection
import com.gy.chatpaths.aac.app.model.source.CPRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class PathsViewModel
    @Inject
    constructor(
        val repository: CPRepository,
        val currentUser: CurrentUser,
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

        suspend fun getPaths(): List<Path>? {
            return currentUser.getUser()?.let { user ->
                return repository.getChildrenOfParent(
                    userId = user.userId,
                    collectionId = collectionId,
                    parentId = parentId,
                    showAll = true,
                )
            }
        }

        suspend fun setCollectionImage(uri: Uri) {
            repository.setCollectionImage(collectionId, uri)
        }

        suspend fun updateCollectionTitle(collectionTitle: String) {
            repository.setCollectionTitle(collectionId, collectionTitle)
        }

        suspend fun addPath(name: String) {
            currentUser.getUser()?.let {
                repository.addPath(
                    userId = it.userId,
                    collectionId = collectionId,
                    title = name,
                    imageUri = null,
                    parentId = parentId,
                    position = null,
                    anchored = false,
                )
            }
        }

        suspend fun setIsEnabled(
            pathId: Int,
            enabled: Boolean,
        ) {
            currentUser.getUser()?.apply {
                repository.setPathIsVisible(
                    userId = this.userId,
                    collectionId = collectionId,
                    isVisible = enabled,
                    pathId = pathId,
                )
            }
        }

        suspend fun setPathPosition(
            pathId: Int,
            position: Int?,
        ) {
            currentUser.getUser()?.apply {
                repository.setPathPosition(
                    userId = this.userId,
                    collectionId = collectionId,
                    pathId = pathId,
                    position = position,
                )
            }
        }

        suspend fun getCollection(): PathCollection? {
            return repository.getPathCollection(collectionId)
        }

        suspend fun deleteCollectionImage() {
            repository.setCollectionImage(collectionId, null)
        }

        suspend fun removePath(pathId: Int) {
            repository.deletePath(collectionId, pathId)
        }

        suspend fun updatePathOrder(paths: List<Path>) {
            currentUser.getUser()?.apply {
                repository.updatePathOrder(userId, collectionId, paths)
            }
        }

        suspend fun setAutoSort(enabled: Boolean) {
            currentUser.getUser()?.apply {
                repository.setCollectionAutoSort(userId, collectionId, enabled)
            }
        }

        suspend fun restoreDefaultPathOrder() {
            currentUser.getUser()?.apply {
                repository.setCollectionPathOrderToDefault(userId, collectionId)
            }
        }

        suspend fun resetCollectionStatistics() {
            currentUser.getUser()?.apply {
                repository.resetCollectionStatistics(userId, collectionId)
            }
        }

        fun getLiveCollection(): LiveData<PathCollection?> {
            return repository.getLiveCollection(collectionId)
        }

        suspend fun getPath(pathId: Int): Path? {
            return repository.getPath(pathId)
        }

        suspend fun copyTo(userId: Int) {
            repository.copyCollectionToUser(
                srcUserId = currentUser.userId,
                dstUserId = userId,
                collectionId = collectionId,
            )
        }
    }
