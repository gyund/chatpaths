package com.gy.chatpaths.aac.app.ui.manager.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.gy.chatpaths.aac.app.di.module.CurrentUser
import com.gy.chatpaths.aac.app.model.PathUser
import com.gy.chatpaths.aac.app.model.source.CPRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel
    @Inject
    constructor(
        val savedStateHandle: SavedStateHandle,
        val repository: CPRepository,
        val currentUser: CurrentUser,
    ) : ViewModel() {
        suspend fun setUser(user: PathUser) {
            currentUser.setUser(user.userId)
        }

        /** Lazy load RoomDatabase stuff so we can use lifecycle management to initialize them on the
         *  appropriate thread in the
         *  corresponding fragments
         *
         *  All the users in the app
         */
        val users: LiveData<List<PathUser>> by lazy {
            repository.getLiveUsers()
        }
    }
