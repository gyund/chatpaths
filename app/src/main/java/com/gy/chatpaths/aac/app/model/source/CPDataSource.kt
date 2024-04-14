package com.gy.chatpaths.aac.app.model.source

interface CPDataSource :
    PathOperations,
    CollectionOperations,
    UserOperations {
    suspend fun isInitialized(): Boolean

    suspend fun copyCollectionToUser(
        srcUserId: Int,
        dstUserId: Int,
        collectionId: Int,
    )
}
