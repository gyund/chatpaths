package com.gy.chatpaths.aac.app.model.source.local

import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gy.chatpaths.aac.app.model.PathCollection

@Dao
interface PathCollectionDao {

    @Insert
    fun insert(pc: PathCollection): Long

    @Insert
    fun insertAll(vararg pc: PathCollection)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnoreAll(vararg pc: PathCollection)

    @Transaction
    fun populate(vararg pc: PathCollection) {
        insertOrIgnoreAll(*pc)
        update(*pc)
    }

    @Update
    fun update(vararg pc: PathCollection)

    @Query("SELECT COUNT(*) FROM PathCollection")
    fun getCount(): Int

    @Query("SELECT * FROM PathCollection")
    fun getAll(): List<PathCollection>

    @Query("SELECT * FROM PathCollection")
    fun getAllLive(): LiveData<List<PathCollection>>

    @Query("SELECT * FROM PathCollection WHERE collectionId = :collectionId")
    fun getById(collectionId: Int): PathCollection?

    @Query("SELECT * FROM PathCollection WHERE collectionId = :collectionId")
    fun getLive(collectionId: Int): LiveData<PathCollection?>

    @Query("DELETE FROM PathCollection WHERE collectionId = :collectionId")
    fun deleteById(collectionId: Int)

    @Query("UPDATE PathCollection SET imageUri = NULL WHERE collectionId = :collectionId")
    fun deleteUserDisplayImage(collectionId: Int)

    @Query("UPDATE PathCollection SET name = :title WHERE collectionId = :collectionId")
    fun setTitle(collectionId: Int, title: String)

    @Query("UPDATE PathCollection SET imageUri = :uri WHERE collectionId = :collectionId")
    fun setImage(collectionId: Int, uri: String)

    @Query("UPDATE PathCollection SET enabled = :enabled WHERE userId = :userId AND collectionId = :collectionId")
    fun setCollectionEnabled(userId: Int, collectionId: Int, enabled: Boolean)

    @Query("UPDATE PathCollection SET displayOrder = NULL WHERE userId = :userId")
    fun resetOrder(userId: Int)

    @Query("UPDATE PathCollection SET displayOrder = :position WHERE userId = :userId AND collectionId = :collectionId")
    fun setCollectionPosition(userId: Int, collectionId: Int, position: Int)

    @Query("UPDATE PathCollection SET autoSort = :enabled WHERE userId = :userId AND collectionId = :collectionId")
    fun setCollectionAutosort(userId: Int, collectionId: Int, enabled: Boolean)

    @Query("SELECT COUNT(*) FROM PathCollection WHERE imageUri = :uri")
    fun getImageUseCount(uri: String): Int

    @Query("SELECT imageUri FROM PathCollection WHERE userId = :userId AND imageUri IS NOT NULL AND imageUri NOT LIKE '${ContentResolver.SCHEME_ANDROID_RESOURCE}%' ")
    fun getUserImages(userId: Int): List<String>

    @Query("SELECT * FROM PathCollection WHERE userId = :userId ORDER BY displayOrder ASC")
    fun getViewLive(userId: Int): LiveData<List<PathCollection>>

    @Query("SELECT * FROM PathCollection WHERE userId = :userId AND enabled = :enabled ORDER BY displayOrder ASC")
    fun getViewLiveByState(userId: Int, enabled: Boolean): LiveData<List<PathCollection>>

    @Query("SELECT * FROM PathCollection WHERE userId = :userId AND enabled = :enabled ORDER BY displayOrder ASC")
    fun getViewByState(userId: Int, enabled: Boolean): List<PathCollection>

    @Query("SELECT * FROM PathCollection WHERE userId = :userId ORDER BY displayOrder ASC")
    fun getView(userId: Int): List<PathCollection>

    // @Transaction
    // @Query("SELECT * FROM PathCollection WHERE collectionId = :collectionId")
    // fun getAllPathsFromCollection(collectionId: Int): Array<PathCollectionWithPaths>
}
