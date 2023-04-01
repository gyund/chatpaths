package com.gy.chatpaths.aac.data.source

import android.content.Context

interface CPDataSource :
    PathOperations,
    CollectionOperations,
    UserOperations {

    suspend fun isInitialized(): Boolean

    /**
     * Translate resource IDs that might be in the database so that
     * they are purely strings
     *
     */
    suspend fun translateStrings(context: Context)
    suspend fun copyCollectionToUser(srcUserId: Int, dstUserId: Int, collectionId: Int)


}