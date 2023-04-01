package com.gy.chatpaths.aac.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gy.chatpaths.aac.data.PathUser

@Dao
interface PathUserDao {
    @Query("SELECT * FROM PathUser ORDER BY name ASC")
    fun getAll(): List<PathUser>

    @Query("SELECT * FROM PathUser ORDER BY name ASC")
    fun getAllLive(): LiveData<List<PathUser>>

    @Query("SELECT COUNT(*) FROM PathUser")
    fun getCount(): Int

    @Query("SELECT COUNT(*) FROM PathUser WHERE displayImage = :uri")
    fun getImageUseCount(uri: String) : Int

    @Query("SELECT * FROM PathUser WHERE userId = :userId")
    fun get(userId: Int): PathUser?

    @Query("SELECT * FROM PathUser WHERE userId = :userId")
    fun getLive(userId: Int): LiveData<PathUser?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnoreAll(vararg pathUsers: PathUser)

    @Insert
    fun insert(pathUser: PathUser): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(pathUser: PathUser): Long

    @Transaction
    fun populate(vararg pathUsers: PathUser) {
        insertOrIgnoreAll(*pathUsers)
        // Don't update user details because they may have changed the name
        //update(*pathUsers)
    }

    @Update
    fun update(vararg pathUsers: PathUser)

    @Query("UPDATE PathUser SET name = :name WHERE userId = :id")
    fun updateName(id: Int, name: String)

    @Query("UPDATE PathUser SET displayImage = NULL WHERE userId = :id")
    fun deleteDisplayImage(id: Int)

    @Insert
    fun insertAll(vararg pathUser: PathUser)

    @Delete
    fun delete(pathUser: PathUser)

    @Query("DELETE FROM PathUser WHERE userId = :userId")
    fun delete(userId: Int)
}