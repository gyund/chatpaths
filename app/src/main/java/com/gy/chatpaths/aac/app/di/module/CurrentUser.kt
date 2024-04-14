package com.gy.chatpaths.aac.app.di.module

import android.content.Context
import androidx.preference.PreferenceManager
import com.gy.chatpaths.aac.app.model.PathUser
import com.gy.chatpaths.aac.app.model.source.CPRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Module
@InstallIn(ActivityComponent::class)
class CurrentUser @Inject constructor(
    @ApplicationContext val context: Context,
) {

    @Inject
    lateinit var repository: CPRepository

    private var cachedUserId: Int? = null

    var userId: Int
        get() = cachedUserId ?: getCurrentUserId()
        private set(value) {
            cachedUserId = value
        }

    fun getUserLive() = repository.getLiveUser(userId)

    /**
     * Get the default user ID
     *
     * For a new user, this is 1, for an existing user, it is the last user they selected.
     * Should initialize this at the start of the activity
     */
    private fun getCurrentUserId(): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getInt("user_id", 1)
    }

    /**
     * Sets the current user for the scope of this module (Application)
     */
    suspend fun setUser(id: Int): PathUser? {
        return repository.getUserById(id)?.apply {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.edit().putInt("user_id", id).apply()
            userId = id
        }
    }

    suspend fun getUser() = repository.getUserById(userId)

    suspend fun addUser(name: String) = repository.addUser(
        PathUser(
            userId = 0,
            name = name,
            displayImage = null,
        ),
        false,
    )

    suspend fun delete() {
        getUser()?.apply {
            if (userId != 1) {
                setUser(1)
                repository.deleteUser(this)
            }
        }
    }

    suspend fun updateName(name: String): Boolean {
        return try {
            repository.getUserById(userId)?.apply {
                this.name = name
                repository.updateUser(this)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
