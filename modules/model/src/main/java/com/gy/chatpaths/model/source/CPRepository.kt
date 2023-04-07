package com.gy.chatpaths.model.source

import android.net.Uri
import androidx.lifecycle.LiveData
import com.gy.chatpaths.model.Path
import com.gy.chatpaths.model.PathCollection
import com.gy.chatpaths.model.PathUser
import com.gy.chatpaths.model.source.local.LocalCPDataSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * See for example:
 * https://github.com/android/architecture-samples/blob/todo-mvp-dagger/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/data/source/TasksRepository.java
 *
 */

@Singleton
class CPRepository @Inject constructor(private val localDataSource: LocalCPDataSource) :
    CPDataSource {

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    var mCacheIsDirty = false

    override suspend fun isInitialized(): Boolean {
        return localDataSource.isInitialized()
    }

    override suspend fun addUser(user: PathUser, overwrite: Boolean): Int {
        return localDataSource.addUser(user, overwrite)
    }

    override suspend fun getUserById(id: Int): PathUser? {
        return localDataSource.getUserById(id)
    }

    override suspend fun updateUser(user: PathUser) {
        return localDataSource.updateUser(user)
    }

    override suspend fun getChildrenOfParent(
        userId: Int,
        collectionId: Int,
        parentId: Int?,
        showAll: Boolean,
    ): List<Path>? {
        return localDataSource.getChildrenOfParent(
            userId = userId,
            collectionId = collectionId,
            parentId = parentId,
            showAll = showAll,
        )
    }

    override suspend fun getChildOfParent(
        userId: Int,
        collectionId: Int,
        parentId: Int?,
        showAll: Boolean,
    ): Path? {
        return localDataSource.getChildOfParent(
            userId = userId,
            collectionId = collectionId,
            parentId = parentId,
            showAll = showAll,
        )
    }

    override suspend fun getPathCollection(collectionId: Int): PathCollection? {
        return localDataSource.getPathCollection(collectionId)
    }

    override suspend fun getPathCollections(
        userId: Int,
        enabledOnly: Boolean,
    ): List<PathCollection> {
        return localDataSource.getPathCollections(userId, enabledOnly)
    }

    override suspend fun incrementPathCount(userId: Int, pathCollectionId: Int, pathId: Int) {
        return localDataSource.incrementPathCount(userId, pathCollectionId, pathId)
    }

    override suspend fun setCollectionPathOrderToDefault(userId: Int, pathCollectionId: Int) {
        localDataSource.setCollectionPathOrderToDefault(userId, pathCollectionId)
    }

    override suspend fun sortCollection(userId: Int, pathCollectionId: Int) {
        localDataSource.sortCollection(userId, pathCollectionId)
    }

    override suspend fun resetCollectionStatistics(userId: Int, pathCollectionId: Int) {
        localDataSource.resetCollectionStatistics(userId, pathCollectionId)
    }

    override fun getLiveUser(userId: Int): LiveData<PathUser?> {
        return localDataSource.getLiveUser(userId)
    }

    override suspend fun addCollection(collection: PathCollection): Int {
        return localDataSource.addCollection(collection)
    }

    override suspend fun setCollectionTitle(collectionId: Int, title: String) {
        localDataSource.setCollectionTitle(collectionId, title)
    }

    override suspend fun setCollectionImage(collectionId: Int, uri: Uri?) {
        localDataSource.setCollectionImage(collectionId, uri)
    }

    override suspend fun setCollectionIsVisible(userId: Int, collectionId: Int, enabled: Boolean) {
        localDataSource.setCollectionIsVisible(userId, collectionId, enabled)
    }

    override suspend fun setCollectionPosition(userId: Int, collectionId: Int, position: Int) {
        localDataSource.setCollectionPosition(userId, collectionId, position)
    }

    override suspend fun updateCollectionOrder(userId: Int, collections: List<PathCollection>) {
        localDataSource.updateCollectionOrder(userId, collections)
    }

    override suspend fun setCollectionAutoSort(userId: Int, collectionId: Int, enabled: Boolean) {
        localDataSource.setCollectionAutoSort(userId, collectionId, enabled)
    }

    override suspend fun addPath(
        userId: Int,
        collectionId: Int,
        title: String?,
        imageUri: String?,
        parentId: Int?,
        position: Int?,
        anchored: Boolean,
    ): Int {
        return localDataSource.addPath(
            userId,
            collectionId,
            title,
            imageUri,
            parentId,
            position,
            anchored,
        )
    }

    override suspend fun deletePath(collectionId: Int, pathId: Int) {
        localDataSource.deletePath(collectionId, pathId)
    }

    override suspend fun setPathIsVisible(
        userId: Int,
        collectionId: Int,
        isVisible: Boolean,
        pathId: Int,
    ) {
        localDataSource.setPathIsVisible(userId, collectionId, isVisible, pathId)
    }

    override suspend fun setPathPosition(
        userId: Int,
        collectionId: Int,
        pathId: Int,
        position: Int?,
    ) {
        localDataSource.setPathPosition(userId, collectionId, pathId, position)
    }

    override suspend fun setPathIsAnchored(
        userId: Int,
        collectionId: Int,
        pathId: Int,
        anchored: Boolean,
    ) {
        localDataSource.setPathIsAnchored(userId, collectionId, pathId, anchored)
    }

    override suspend fun updatePathOrder(
        userId: Int,
        collectionId: Int,
        paths: List<Path>,
    ) {
        localDataSource.updatePathOrder(userId, collectionId, paths)
    }

    override suspend fun getPathFromCollection(
        userId: Int,
        collectionId: Int,
        pathId: Int,
    ): Path? {
        return localDataSource.getPathFromCollection(userId, collectionId, pathId)
    }

    override suspend fun updatePathTitle(pathId: Int, title: String) {
        return localDataSource.updatePathTitle(pathId, title)
    }

    override suspend fun setPathImageUser(pathId: Int, imageUri: String?, userId: Int) {
        return localDataSource.setPathImageUser(pathId, imageUri, userId)
    }

    override suspend fun getPathImages(name: String?): List<String> {
        return localDataSource.getPathImages(name)
    }

    override suspend fun setAudioPrompt(pathId: Int, uri: Uri) {
        localDataSource.setAudioPrompt(pathId, uri)
    }

    override suspend fun deleteAudioPrompt(pathId: Int) {
        localDataSource.deleteAudioPrompt(pathId)
    }

    override fun getLiveCollection(collectionId: Int): LiveData<PathCollection?> {
        return localDataSource.getLiveCollection(collectionId)
    }

    override suspend fun updateCollection(collection: PathCollection) {
        localDataSource.updateCollection(collection)
    }

    override suspend fun deleteCollection(collection: PathCollection) {
        localDataSource.deleteCollection(collection)
    }

    override suspend fun deleteCollection(collectionId: Int) {
        localDataSource.deleteCollection(collectionId)
    }

    override suspend fun deleteUserDisplayImage(collectionId: Int) {
        localDataSource.deleteUserDisplayImage(collectionId)
    }

    override fun getLiveUsers(): LiveData<List<PathUser>> {
        return localDataSource.getLiveUsers()
    }

    override fun getLivePathCollections(
        userId: Int,
        enabledOnly: Boolean,
    ): LiveData<List<PathCollection>> {
        return localDataSource.getLivePathCollections(userId, enabledOnly)
    }

    override suspend fun resetCollectionOrder(userId: Int) {
        localDataSource.resetCollectionOrder(userId)
    }

    override suspend fun deleteUser(user: PathUser) {
        localDataSource.deleteUser(user)
    }

    override suspend fun deleteUserImage(userId: Int) {
        localDataSource.deleteUserImage(userId)
    }

    override suspend fun setUserImage(userId: Int, uri: Uri) {
        localDataSource.setUserImage(userId, uri)
    }

    override fun getLivePath(
        userId: Int,
        collectionId: Int,
        pathId: Int,
    ): LiveData<Path?> {
        return localDataSource.getLivePath(userId, collectionId, pathId)
    }

    override suspend fun getPath(pathId: Int): Path? {
        return localDataSource.getPath(pathId)
    }

    override suspend fun copyCollectionToUser(srcUserId: Int, dstUserId: Int, collectionId: Int) {
        localDataSource.copyCollectionToUser(srcUserId, dstUserId, collectionId)
    }
}
