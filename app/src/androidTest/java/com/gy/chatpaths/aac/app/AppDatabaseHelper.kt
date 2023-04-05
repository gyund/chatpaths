package com.gy.chatpaths.aac.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gy.chatpaths.model.source.CPRepository
import com.gy.chatpaths.model.source.local.AppDatabase
import com.gy.chatpaths.model.source.local.LocalCPDataSource
import com.gy.chatpaths.model.source.local.PathCollectionDao
import com.gy.chatpaths.model.source.local.PathDao
import com.gy.chatpaths.model.source.local.PathUserDao
import com.gy.chatpaths.builder.InitialData
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.ErrorCollector

open class AppDatabaseHelper {
    private lateinit var pathDao: PathDao
    private lateinit var pathCollectionDao: PathCollectionDao
    private lateinit var pathUserDao: PathUserDao
    lateinit var db: AppDatabase
    lateinit var context: Context
    lateinit var repository: CPRepository

    @get:Rule
    val collector: ErrorCollector = ErrorCollector()

    @Before
    fun createDb() = runBlocking {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()

        val local = LocalCPDataSource(db)
        repository = CPRepository(local)

        InitialData.populate(context, repository)

        pathDao = db.pathDao()
        pathCollectionDao = db.pathCollectionDao()
        pathUserDao = db.pathUserDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        db.close()
    }
}
