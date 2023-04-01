package com.gy.chatpaths.aac.app.ui.manager.userdetail

import com.gy.chatpaths.aac.data.PathCollection

interface CollectionManagerListener {

    fun setIsEnabled(collectionId: Int, enabled: Boolean)
    fun setCollectionPosition(collectionId: Int, position: Int)
    fun updateCollectionOrder(collections: List<PathCollection>)
    fun editCollection(collectionId: Int)
    fun showDeleteCollectionDialog(collectionId: Int, onItemRemoved: (success: Boolean) -> Unit)

}