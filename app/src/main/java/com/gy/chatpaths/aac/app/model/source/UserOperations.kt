package com.gy.chatpaths.aac.app.model.source

import android.net.Uri
import androidx.lifecycle.LiveData
import com.gy.chatpaths.aac.app.model.PathUser

interface UserOperations {
    fun getLiveUser(userId: Int): LiveData<PathUser?>

    fun getLiveUsers(): LiveData<List<PathUser>>

    suspend fun getUserById(id: Int): PathUser?

    suspend fun addUser(
        user: PathUser,
        overwrite: Boolean,
    ): Int

    suspend fun updateUser(user: PathUser)

    suspend fun deleteUser(user: PathUser)

    suspend fun setUserImage(
        userId: Int,
        uri: Uri,
    )

    suspend fun deleteUserImage(userId: Int)
}
