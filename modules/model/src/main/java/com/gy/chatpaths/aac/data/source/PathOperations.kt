package com.gy.chatpaths.aac.data.source

import android.net.Uri
import androidx.lifecycle.LiveData
import com.gy.chatpaths.aac.data.Path

interface PathOperations {
    fun getLivePath(userId: Int, collectionId: Int, pathId: Int): LiveData<Path?>
    suspend fun getPath(pathId: Int): Path?

    suspend fun getChildrenOfParent(
        userId: Int,
        collectionId: Int,
        parentId: Int?,
        showAll: Boolean,
    ): List<Path>?
    suspend fun getChildOfParent(
        userId: Int,
        collectionId: Int,
        parentId: Int?,
        showAll: Boolean,
    ): Path?

    suspend fun addPath(
        userId: Int,
        collectionId: Int,
        title: String?,
        imageUri: String?,
        parentId: Int?,
        position: Int?,
        anchored: Boolean,
    ): Int
    suspend fun deletePath(collectionId: Int, pathId: Int)
    suspend fun setPathIsVisible(userId: Int, collectionId: Int, isVisible: Boolean, pathId: Int)
    suspend fun setPathPosition(userId: Int, collectionId: Int, pathId: Int, position: Int?)
    suspend fun setPathIsAnchored(userId: Int, collectionId: Int, pathId: Int, anchored: Boolean)
    suspend fun setPathImageUser(pathId: Int, imageUri: String?, userId: Int)

    suspend fun updatePathOrder(userId: Int, collectionId: Int, paths: List<Path>)
    suspend fun getPathFromCollection(userId: Int, collectionId: Int, pathId: Int): Path?

    suspend fun updatePathTitle(pathId: Int, title: String)

    suspend fun getPathImages(name: String? = null): List<String>
    suspend fun setAudioPrompt(pathId: Int, uri: Uri)
    suspend fun deleteAudioPrompt(pathId: Int)
}
