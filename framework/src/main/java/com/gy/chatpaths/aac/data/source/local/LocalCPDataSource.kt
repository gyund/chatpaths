package com.gy.chatpaths.aac.data.source.local

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gy.chatpaths.aac.data.FileUtils
import com.gy.chatpaths.aac.data.Path
import com.gy.chatpaths.aac.data.PathCollection
import com.gy.chatpaths.aac.data.PathUser
import com.gy.chatpaths.aac.data.StringUtils
import com.gy.chatpaths.aac.data.source.CPDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalCPDataSource @Inject constructor(val db: AppDatabase) : CPDataSource {
    val TAG = "LocalCPDataSource"
    override suspend fun isInitialized(): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext db.pathUserDao().getCount() > 0
        }
    }

    override suspend fun translateStrings(context: Context) {
        withContext(Dispatchers.IO) {
            db.runInTransaction {
                db.pathDao().getAll().forEach {
                    if (true == it.name?.startsWith("path_")) {
                        val transTitle = StringUtils.getStringFromStringResourceName(context, it.name, null)
                        if (null != transTitle) {
                            db.pathDao().setPathTitle(it.pathId, transTitle)
                        }
                    }
                }
                db.pathCollectionDao().getAll().forEach {
                    if (true == it.name?.startsWith("col_")) {
                        val transTitle = StringUtils.getStringFromStringResourceName(context, it.name, null)
                        if (null != transTitle) {
                            it.name = transTitle
                            db.pathCollectionDao().update(it)
                        }
                    }
                }
            }
        }
    }

    override suspend fun copyCollectionToUser(srcUserId: Int, dstUserId: Int, collectionId: Int) {
        withContext(Dispatchers.IO) {
            db.runInTransaction {
                val collection = db.pathCollectionDao().getById(collectionId)
                collection?.let { c ->
                    // Set new user info
                    c.collectionId = 0 // new insert
                    c.userId = dstUserId
                    c.enabled = true

                    val newCollectionId = db.pathCollectionDao().insert(c).toInt()

                    addChildrenNoSuspend(
                        srcUserId,
                        collectionId,
                        null,
                        null,
                        dstUserId,
                        newCollectionId,
                    )
                }
            }
        }
    }

    private fun addChildrenNoSuspend(
        srcUserId: Int,
        srcCollectionId: Int,
        srcParentId: Int?,
        dstParentId: Int?,
        dstUserId: Int,
        dstCollectionId: Int,
    ) {
        val children = db.pathDao().getChildrenAuto(srcUserId, srcCollectionId, srcParentId, true)
        children?.forEach {
            // Depth first add so we can get rid of the insert id as soon as possible
            val pathId = addPathNoSuspend(
                userId = dstUserId,
                collectionId = dstCollectionId,
                title = it.name,
                imageUri = it.imageUri,
                parentId = dstParentId,
                position = it.position,
                anchored = it.anchored,
            )
            addChildrenNoSuspend(
                srcUserId = srcUserId,
                srcCollectionId = srcCollectionId,
                srcParentId = it.pathId,
                dstParentId = pathId,
                dstUserId = dstUserId,
                dstCollectionId = dstCollectionId,
            )
        }
    }

    override suspend fun addUser(user: PathUser, overwrite: Boolean): Int {
        return withContext(Dispatchers.IO) {
            return@withContext if (overwrite) {
                db.pathUserDao().insert(user).toInt()
            } else {
                db.pathUserDao().insertOrIgnore(user).toInt()
            }
        }
    }

    override suspend fun getUserById(id: Int): PathUser? {
        return withContext(Dispatchers.IO) {
            return@withContext db.pathUserDao().get(id)
        }
    }

    override suspend fun updateUser(user: PathUser) {
        withContext(Dispatchers.IO) {
            db.pathUserDao().update(user)
        }
    }

    override suspend fun getChildrenOfParent(
        userId: Int,
        collectionId: Int,
        parentId: Int?,
        showAll: Boolean,
    ): List<Path>? {
        var result: MutableList<Path>? = null
        withContext(Dispatchers.IO) {
            db.runInTransaction {
                result = db.pathDao().getChildrenAuto(
                    userId = userId,
                    collectionId = collectionId,
                    parentId = parentId,
                    showAll = showAll,
                )

                result?.let {
                    db.pathCollectionDao().getById(collectionId)?.let { prefs ->
                        if (prefs.autoSort) {
                            val newList = mutableListOf<Path>()

                            // Get the anchored elements off the list and added to the new list
                            while (it.isNotEmpty()) {
                                if (it.first().anchored) {
                                    newList.add(it.removeFirst())
                                } else {
                                    break
                                }
                            }

                            // Sort the remainder and add it to the new list
                            it.sortByDescending { it.timesUsed }
                            newList.addAll(it)
                            result = newList
                        }
                    }
//                    if(showAll) {
//                        // If we reset the indexes, then we should present a view of all elements
//                        // with the new order
//                        it.forEachIndexed { index, it ->
//                            it.position = index
//                        }
//                    }
                }
            }
        }

        return result
    }

    override suspend fun getChildOfParent(
        userId: Int,
        collectionId: Int,
        parentId: Int?,
        showAll: Boolean,
    ): Path? {
        return getChildrenOfParent(userId, collectionId, parentId, showAll)?.let {
            return@let if (it.isNotEmpty()) {
                it.first()
            } else {
                null
            }
        }
    }

    override suspend fun getPathCollection(collectionId: Int): PathCollection? {
        return withContext(Dispatchers.IO) {
            db.pathCollectionDao().getById(collectionId)
        }
    }

    override suspend fun getPathCollections(
        userId: Int,
        enabledOnly: Boolean,
    ): List<PathCollection> {
        return withContext(Dispatchers.IO) {
            return@withContext if (enabledOnly) {
                db.pathCollectionDao().getViewByState(userId, enabledOnly)
            } else {
                db.pathCollectionDao().getView(userId)
            }
        }
    }

    override suspend fun deleteUserImage(userId: Int) {
        withContext(Dispatchers.IO) {
            db.runInTransaction {
                db.pathUserDao().get(userId)?.let {
                    db.pathUserDao().deleteDisplayImage(userId)
                    it.displayImage?.apply {
                        deleteImageIfNotUsed(this)
                    }
                }
            }
        }
    }

    override suspend fun setUserImage(userId: Int, uri: Uri) {
        withContext(Dispatchers.IO) {
            db.runInTransaction {
                db.pathUserDao().get(userId)?.let {
                    val orphanedImage = it.displayImage
                    it.displayImage = uri.toString()
                    db.pathUserDao().update(it)
                    orphanedImage?.apply {
                        deleteImageIfNotUsed(this)
                    }
                }
            }
        }
    }

    override fun getLivePath(userId: Int, collectionId: Int, pathId: Int): LiveData<Path?> {
        return db.pathDao().getLive(userId, collectionId, pathId)
    }

    override suspend fun getPath(pathId: Int): Path? {
        return withContext(Dispatchers.IO) {
            return@withContext db.pathDao().getById(pathId)
        }
    }

    override suspend fun incrementPathCount(userId: Int, pathCollectionId: Int, pathId: Int) {
        return withContext(Dispatchers.IO) {
            db.pathDao().increaseTimesUsedById(userId, pathCollectionId, pathId)
        }
    }

    override suspend fun setCollectionPathOrderToDefault(userId: Int, pathCollectionId: Int) {
        withContext(Dispatchers.IO) {
            db.pathDao().resetPathOrderToDefaults(
                userId,
                pathCollectionId,
            )
        }
    }

    override suspend fun sortCollection(userId: Int, pathCollectionId: Int) {
        withContext(Dispatchers.IO) {
            db.pathDao()
                .reorderPathsAutomatically(
                    userId,
                    pathCollectionId,
                )
        }
    }

    override suspend fun resetCollectionStatistics(userId: Int, pathCollectionId: Int) {
        withContext(Dispatchers.IO) {
            db.pathDao()
                .resetTimesUsed(
                    userId,
                    pathCollectionId,
                )
        }
    }

    override fun getLiveUser(userId: Int): LiveData<PathUser?> {
        return db.pathUserDao().getLive(userId)
    }

    override suspend fun addCollection(collection: PathCollection): Int {
        return withContext(Dispatchers.IO) {
            return@withContext db.pathCollectionDao().insert(collection).toInt()
        }
    }

    override suspend fun setCollectionTitle(collectionId: Int, title: String) {
        withContext(Dispatchers.IO) {
            db.pathCollectionDao().setTitle(collectionId, title)
        }
    }

    override suspend fun setCollectionImage(collectionId: Int, uri: Uri?) {
        withContext(Dispatchers.IO) {
            db.runInTransaction {
                val collection = db.pathCollectionDao().getById(collectionId)
                if (uri == null) {
                    db.pathCollectionDao().deleteUserDisplayImage(collectionId)
                } else {
                    db.pathCollectionDao().setImage(collectionId, uri.toString())
                }

                collection?.imageUri?.apply {
                    deleteImageIfNotUsed(this)
                }
            }
        }
    }

    override suspend fun setCollectionIsVisible(userId: Int, collectionId: Int, enabled: Boolean) {
        withContext(Dispatchers.IO) {
            db.pathCollectionDao().setCollectionEnabled(
                userId = userId,
                collectionId = collectionId,
                enabled = enabled,
            )
        }
    }

    override suspend fun setCollectionPosition(userId: Int, collectionId: Int, position: Int) {
        withContext(Dispatchers.IO) {
            db.pathCollectionDao().setCollectionPosition(
                userId = userId,
                collectionId = collectionId,
                position = position,
            )
        }
    }

    override suspend fun updateCollectionOrder(userId: Int, collections: List<PathCollection>) {
        withContext(Dispatchers.IO) {
            collections.forEachIndexed { index, collection ->
                db.pathCollectionDao().setCollectionPosition(
                    userId = userId,
                    collectionId = collection.collectionId,
                    position = index,
                )
            }
        }
    }

    override suspend fun setCollectionAutoSort(userId: Int, collectionId: Int, enabled: Boolean) {
        return withContext(Dispatchers.IO) {
            db.pathCollectionDao().setCollectionAutosort(
                userId,
                collectionId,
                enabled,
            )
        }
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
        return withContext(Dispatchers.IO) {
            return@withContext addPathNoSuspend(
                collectionId,
                parentId,
                position,
                title,
                imageUri,
                anchored,
                userId,
            )
        }
    }

    private fun addPathNoSuspend(
        collectionId: Int,
        parentId: Int?,
        position: Int?,
        title: String?,
        imageUri: String?,
        anchored: Boolean,
        userId: Int,
    ): Int {
        val path = Path(
            pathId = 0,
            collectionId = collectionId,
            parentId = parentId,
            position = position,
            name = title,
            imageUri = imageUri,
            defaultPosition = null,
            anchored = anchored,
            enabled = true,
            userId = userId,
            timesUsed = 0,
        )

        return db.pathDao().insert(path).toInt()
    }

    internal fun deleteImageIfNotUsed(uri: String?) {
        uri?.let {
            // Scan DB for image
            // if not in columns with image, delete
            if (0 == db.pathDao().getImageUseCount(it) &&
                0 == db.pathCollectionDao().getImageUseCount(it) &&
                0 == db.pathUserDao().getImageUseCount(it)
            ) {
                // We can't delete resources
                if (!it.startsWith(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                    Log.d("LCPDS", "deleting image uri: $it")
                    FileUtils.deleteUriImage(it)
                }
            }
        }
    }

    override suspend fun deletePath(collectionId: Int, pathId: Int) {
        withContext(Dispatchers.IO) {
            var img: String? = null
            db.runInTransaction {
                db.pathDao().getImageUri(pathId)?.apply {
                    img = this
                }

                db.pathDao().delete(pathId)

                // Delete the images
                deleteImageIfNotUsed(img)
            }
        }
    }

    override suspend fun setPathIsVisible(
        userId: Int,
        collectionId: Int,
        isVisible: Boolean,
        pathId: Int,
    ) {
        withContext(Dispatchers.IO) {
            db.pathDao().setPathEnable(
                userId,
                collectionId,
                pathId,
                isVisible,
            )
        }
    }

    override suspend fun setPathPosition(
        userId: Int,
        collectionId: Int,
        pathId: Int,
        position: Int?,
    ) {
        withContext(Dispatchers.IO) {
            db.pathDao().setPathPosition(userId, collectionId, pathId, position)
        }
    }

    override suspend fun setPathIsAnchored(
        userId: Int,
        collectionId: Int,
        pathId: Int,
        anchored: Boolean,
    ) {
        withContext(Dispatchers.IO) {
            db.runInTransaction {
                db.pathDao().setPathIsAnchored(userId, collectionId, pathId, anchored)
            }
        }
    }

    override suspend fun updatePathOrder(
        userId: Int,
        collectionId: Int,
        paths: List<Path>,
    ) {
        withContext(Dispatchers.IO) {
            db.runInTransaction {
                paths.forEachIndexed { index, path ->
                    db.pathDao().setPathPosition(userId, collectionId, path.pathId, index)
                }
            }
        }
    }

    override suspend fun getPathFromCollection(userId: Int, collectionId: Int, pathId: Int): Path? {
        return withContext(Dispatchers.IO) {
            db.pathDao().get(userId, collectionId, pathId)
        }
    }

    suspend fun getPathById(pathId: Int): Path? {
        return withContext(Dispatchers.IO) {
            db.pathDao().getById(pathId)
        }
    }

    override suspend fun updatePathTitle(pathId: Int, title: String) {
        withContext(Dispatchers.IO) {
            db.pathDao().setPathTitle(pathId, title)
        }
    }

    override suspend fun setPathImageUser(pathId: Int, imageUri: String?, userId: Int) {
        withContext(Dispatchers.IO) {
            db.runInTransaction {
                val img = db.pathDao().getImageUri(pathId)

                db.pathDao().setImageUri(
                    pathId = pathId,
                    imageUri = imageUri,
                )

                img?.let {
                    deleteImageIfNotUsed(it)
                }
            }
        }
    }

    override suspend fun getPathImages(name: String?): List<String> {
        return withContext(Dispatchers.IO) {
            if (null == name) {
                db.pathDao().getImages()
            } else {
                db.pathDao().getImagesByName(name)
            }
        }
    }

    override suspend fun setAudioPrompt(pathId: Int, uri: Uri) {
        withContext(Dispatchers.IO) {
            db.withTransaction {
                db.pathDao().getById(pathId)?.apply {
                    val originalPrompt = audioPromptUri
                    db.pathDao().setAudioPrompt(pathId, uri.toString())

                    deleteAudioPrompt(originalPrompt)
                }
            }
        }
    }

    override suspend fun deleteAudioPrompt(pathId: Int) {
        withContext(Dispatchers.IO) {
            db.withTransaction {
                db.pathDao().getById(pathId)?.apply {
                    val originalPrompt = audioPromptUri
                    db.pathDao().deleteAudioPrompt(pathId)

                    deleteAudioPrompt(originalPrompt)
                }
            }
        }
    }

    private fun deleteAudioPrompt(promptUriString: String?) {
        promptUriString?.let {
            // audio prompts are not shared, so we can just delete it
            try {
                val oldPromptUri = Uri.parse(promptUriString)
                oldPromptUri.toFile().delete()
            } catch (e: RuntimeException) {
                Log.d(TAG, "exception with deleting audio: $e")
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    override fun getLiveCollection(collectionId: Int): LiveData<PathCollection?> {
        return db.pathCollectionDao().getLive(collectionId)
    }

    override suspend fun updateCollection(collection: PathCollection) {
        withContext(Dispatchers.IO) {
            db.pathCollectionDao().update(collection)
        }
    }

    override suspend fun deleteCollection(collection: PathCollection) {
        withContext(Dispatchers.IO) {
            db.runInTransaction {
                val orphanedImages = mutableListOf<String>()
                orphanedImages.addAll(db.pathCollectionDao().getUserImages(userId = collection.userId))
                orphanedImages.addAll(db.pathDao().getUserIndividualImages(userId = collection.userId))
                db.pathCollectionDao().deleteById(collection.collectionId)
                collection.imageUri?.apply {
                    deleteImageIfNotUsed(this)
                }
            }
        }
    }

    override suspend fun deleteCollection(collectionId: Int) {
        withContext(Dispatchers.IO) {
            db.withTransaction {
                db.pathCollectionDao().getById(collectionId)?.apply {
                    deleteCollection(this)
                }
            }
        }
    }

    override suspend fun deleteUserDisplayImage(collectionId: Int) {
        withContext(Dispatchers.IO) {
            db.runInTransaction {
                db.pathCollectionDao().getById(collectionId)?.apply {
                    db.pathCollectionDao().deleteUserDisplayImage(collectionId)
                    imageUri?.let {
                        deleteImageIfNotUsed(it)
                    }
                }
            }
        }
    }

    override fun getLiveUsers(): LiveData<List<PathUser>> {
        return db.pathUserDao().getAllLive()
    }

    override fun getLivePathCollections(userId: Int, enabledOnly: Boolean): LiveData<List<PathCollection>> {
        return if (enabledOnly) {
            db.pathCollectionDao().getViewLiveByState(userId, enabledOnly)
        } else {
            db.pathCollectionDao().getViewLive(userId)
        }
    }

    override suspend fun resetCollectionOrder(userId: Int) {
        withContext(Dispatchers.IO) {
            db.pathCollectionDao().resetOrder(userId)
        }
    }

    override suspend fun deleteUser(user: PathUser) {
        withContext(Dispatchers.IO) {
            db.runInTransaction {
                val orphanedImages = mutableListOf<String>()
                val orphanedAudio = mutableListOf<String>()

                db.pathUserDao().get(user.userId)?.displayImage?.let {
                    orphanedImages.add(it)
                }
                orphanedImages.addAll(db.pathCollectionDao().getUserImages(user.userId))
                orphanedImages.addAll(db.pathDao().getUserIndividualImages(user.userId))
                orphanedAudio.addAll(db.pathDao().getAudioPrompts(user.userId))

                db.pathUserDao().delete(user)

                orphanedImages.forEach {
                    deleteImageIfNotUsed(it)
                }
                orphanedAudio.forEach {
                    deleteAudioPrompt(it)
                }
            }
        }
    }

    suspend fun getPathCount(): Int {
        return withContext(Dispatchers.IO) {
            return@withContext db.pathDao().getCount()
        }
    }

    suspend fun getCollectionCount(): Int {
        return withContext(Dispatchers.IO) {
            return@withContext db.pathCollectionDao().getCount()
        }
    }
}
