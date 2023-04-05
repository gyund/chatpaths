package com.gy.chatpaths.model.source

import android.net.Uri
import androidx.lifecycle.LiveData
import com.gy.chatpaths.model.PathCollection

interface CollectionOperations {
    fun getLiveCollection(collectionId: Int): LiveData<PathCollection?>
    fun getLivePathCollections(userId: Int, enabledOnly: Boolean): LiveData<List<PathCollection>>
    suspend fun getPathCollection(collectionId: Int): PathCollection?
    suspend fun getPathCollections(userId: Int, enabledOnly: Boolean): List<PathCollection>

    suspend fun addCollection(collection: PathCollection): Int
    suspend fun updateCollection(collection: PathCollection)
    suspend fun deleteCollection(collection: PathCollection)
    suspend fun deleteCollection(collectionId: Int)

    suspend fun resetCollectionOrder(userId: Int)

    suspend fun setCollectionTitle(collectionId: Int, title: String)
    suspend fun setCollectionImage(collectionId: Int, uri: Uri?)
    suspend fun setCollectionIsVisible(userId: Int, collectionId: Int, enabled: Boolean)
    suspend fun setCollectionPosition(userId: Int, collectionId: Int, position: Int)
    suspend fun setCollectionAutoSort(userId: Int, collectionId: Int, enabled: Boolean)
    suspend fun setCollectionPathOrderToDefault(userId: Int, pathCollectionId: Int)

    suspend fun updateCollectionOrder(userId: Int, collections: List<PathCollection>)
    suspend fun deleteUserDisplayImage(collectionId: Int)

    suspend fun incrementPathCount(userId: Int, pathCollectionId: Int, pathId: Int)
    suspend fun sortCollection(userId: Int, pathCollectionId: Int)
    suspend fun resetCollectionStatistics(userId: Int, pathCollectionId: Int)
}
