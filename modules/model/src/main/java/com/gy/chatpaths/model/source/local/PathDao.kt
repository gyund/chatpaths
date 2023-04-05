package com.gy.chatpaths.model.source.local

import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gy.chatpaths.model.Path

@Dao
interface PathDao {

    @Query("SELECT COUNT(*) FROM path")
    fun getCount(): Int

    @Query("SELECT * FROM path")
    fun getAll(): List<Path>

    @Query("SELECT * FROM Path WHERE userId = :userId AND collectionId = :collectionId AND pathId = :pathId")
    fun get(userId: Int, collectionId: Int, pathId: Int): Path?

    @Query("SELECT * FROM Path WHERE pathId = :pathId")
    fun getById(pathId: Int): Path?

    @Query("SELECT * FROM Path WHERE userId = :userId AND collectionId = :collectionId AND pathId = :pathId")
    fun getLive(userId: Int, collectionId: Int, pathId: Int): LiveData<Path?>

    @Query("SELECT * FROM path WHERE pathId IN (:chatPathIds)")
    fun loadAllByIds(chatPathIds: IntArray): List<Path>

    @Query("SELECT * FROM path WHERE pathId = :id")
    fun findById(id: Int): Path?

    @Transaction
    @Query("SELECT * FROM Path WHERE pathId = :id AND collectionId = :collectionId")
    fun findWithParentById(id: Int, collectionId: Int): Path?

    @Query("SELECT * FROM Path WHERE  collectionId = :collectionId AND parentId = :parentId AND enabled = 1 AND userId = :userId ORDER BY anchored DESC, position NOT NULL DESC, position")
    fun getChildrenByParent(
        userId: Int,
        collectionId: Int,
        parentId: Int,
    ): MutableList<Path>?

    @Query("SELECT * FROM Path WHERE  collectionId = :collectionId AND parentId = :parentId AND userId = :userId ORDER BY anchored DESC, position NOT NULL DESC, position")
    fun getChildrenByParentAll(
        userId: Int,
        collectionId: Int,
        parentId: Int,
    ): MutableList<Path>?

    @Query("SELECT * FROM Path WHERE  collectionId = :collectionId AND parentId IS NULL AND enabled = 1 AND userId = :userId ORDER BY anchored DESC, position NOT NULL DESC, position")
    fun getChildrenOfNull(
        userId: Int,
        collectionId: Int,
    ): MutableList<Path>?

    @Query("SELECT * FROM Path WHERE  collectionId = :collectionId AND parentId IS NULL AND userId = :userId ORDER BY anchored DESC, position NOT NULL DESC, position")
    fun getChildrenOfNullAll(
        userId: Int,
        collectionId: Int,
    ): MutableList<Path>?

    @Transaction
    fun getChildrenAuto(
        userId: Int,
        collectionId: Int,
        parentId: Int?,
        showAll: Boolean,
    ): MutableList<Path>? {
        return if (null == parentId) {
            if (showAll) {
                getChildrenOfNullAll(userId, collectionId)
            } else {
                getChildrenOfNull(userId, collectionId)
            }
        } else {
            if (showAll) {
                getChildrenByParentAll(userId, collectionId, parentId)
            } else {
                getChildrenByParent(userId, collectionId, parentId)
            }
        }
    }

    @Query("SELECT * FROM Path WHERE collectionId = :collectionId AND parentId IS NULL AND enabled = 1 AND userId = :userId  ORDER BY position DESC")
    fun getRootPathsForCollection(
        userId: Int,
        collectionId: Int,
    ): Array<Path>?

    @Query("SELECT * FROM Path WHERE collectionId = :collectionId AND parentId IS NULL AND userId = :userId ORDER BY position DESC")
    fun getAllRootPathsForCollection(userId: Int, collectionId: Int): Array<Path>?

    @Transaction
    @Query("SELECT * FROM Path WHERE  collectionId = :collectionId AND parentId = :parentId AND enabled = 1  AND userId = :userId ORDER BY position DESC")
    fun getChildPathsForCollectionById(
        userId: Int,
        collectionId: Int,
        parentId: Int,
    ): Array<Path>?

    @Transaction
    @Query("SELECT * FROM Path WHERE collectionId = :collectionId AND parentId = :parentId AND userId = :userId ORDER BY position DESC")
    fun getAllChildPathsForCollectionById(
        userId: Int,
        collectionId: Int,
        parentId: Int,
    ): Array<Path>?

    @Query("SELECT DISTINCT imageUri FROM path WHERE imageUri IS NOT NULL")
    fun getImages(): List<String>

    @Query("SELECT DISTINCT imageUri FROM path WHERE imageUri IS NOT NULL AND name LIKE '%' || :name || '%'")
    fun getImagesByName(name: String): List<String>

    @Query("SELECT COUNT(*) FROM path WHERE imageUri = :uri")
    fun getImageUseCount(uri: String): Int

    @Insert
    fun insert(path: Path): Long

    @Insert
    fun insertAll(vararg paths: Path): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnoreAll(vararg paths: Path)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnoreAllList(paths: List<Path>)

    @Transaction
    fun populate(paths: List<Path>) {
        insertOrIgnoreAllList(paths)
        updateList(paths)
    }

    @Update
    fun update(vararg paths: Path)

    @Update
    fun updateList(paths: List<Path>)

    @Query("UPDATE Path SET timesUsed = 0 WHERE userId = :userId AND collectionId = :pathCollectionId")
    fun resetTimesUsed(userId: Int, pathCollectionId: Int)

    @Query("UPDATE Path SET timesUsed = timesUsed + 1 WHERE userId = :userId AND collectionId = :pathCollectionId AND pathId = :pathId")
    fun increaseTimesUsedById(userId: Int, pathCollectionId: Int, pathId: Int)

    @Query("UPDATE Path SET position = timesUsed WHERE userId = :userId AND collectionId = :collectionId AND anchored = 0")
    fun reorderPathsAutomatically(userId: Int, collectionId: Int)

    @Query("UPDATE Path SET position = defaultPosition WHERE userId = :userId AND collectionId = :collectionId")
    fun resetPathOrderToDefaults(userId: Int, collectionId: Int)

    @Query("UPDATE Path SET enabled = :enabled WHERE userId = :userId AND collectionId = :pathCollectionId AND pathId = :pathId")
    fun setPathEnable(userId: Int, pathCollectionId: Int, pathId: Int, enabled: Boolean)

    @Query("UPDATE Path SET position = :position WHERE userId = :userId AND collectionId = :pathCollectionId AND pathId = :pathId")
    fun setPathPosition(userId: Int, pathCollectionId: Int, pathId: Int, position: Int?)

    @Query("UPDATE Path SET anchored = :anchored WHERE userId = :userId AND collectionId = :pathCollectionId AND pathId = :pathId")
    fun setPathIsAnchored(userId: Int, pathCollectionId: Int, pathId: Int, anchored: Boolean)

    @Query("UPDATE Path SET name = :title WHERE pathId = :pathId")
    fun setPathTitle(pathId: Int, title: String)

    @Query("SELECT imageUri FROM Path WHERE userId = :userId AND imageUri IS NOT NULL AND imageUri NOT LIKE '${ContentResolver.SCHEME_ANDROID_RESOURCE}%' ")
    fun getUserIndividualImages(userId: Int): List<String>

    @Query("SELECT imageUri FROM Path WHERE pathId = :pathId")
    fun getImageUri(pathId: Int): String?

    @Delete
    fun delete(path: Path)

    @Query("DELETE FROM Path WHERE pathId = :pathId")
    fun delete(pathId: Int)

    @Query("UPDATE Path SET imageUri = :imageUri WHERE pathId = :pathId")
    fun setImageUri(pathId: Int, imageUri: String?)

    @Query("UPDATE Path SET audioPromptUri = :audioUri WHERE pathId = :pathId")
    fun setAudioPrompt(pathId: Int, audioUri: String)

    @Query("SELECT audioPromptUri FROM Path WHERE userId = :userId AND audioPromptUri NOT NULL")
    fun getAudioPrompts(userId: Int): List<String>

    @Query("UPDATE Path SET audioPromptUri = NULL WHERE pathId = :pathId")
    fun deleteAudioPrompt(pathId: Int)
}
