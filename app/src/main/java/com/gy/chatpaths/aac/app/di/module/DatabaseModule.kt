package com.gy.chatpaths.aac.app.di.module

import android.content.Context
import com.gy.chatpaths.model.source.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun providesAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        @Suppress("DEPRECATION")
        return AppDatabase.getDatabase(appContext)
    }
}
