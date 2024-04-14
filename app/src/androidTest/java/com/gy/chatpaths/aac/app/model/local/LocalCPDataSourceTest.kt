package com.gy.chatpaths.aac.app.model.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.gy.chatpaths.aac.app.model.PathCollection
import com.gy.chatpaths.aac.app.model.PathUser
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@MediumTest
class LocalCPDataSourceTest {
    private lateinit var pathDao: com.gy.chatpaths.aac.app.model.source.local.PathDao
    private lateinit var db: com.gy.chatpaths.aac.app.model.source.local.AppDatabase
    private lateinit var lds: com.gy.chatpaths.aac.app.model.source.local.LocalCPDataSource

    @get:Rule
    var tempFolder: TemporaryFolder = TemporaryFolder()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, com.gy.chatpaths.aac.app.model.source.local.AppDatabase::class.java).build()
        pathDao = db.pathDao()
        lds = com.gy.chatpaths.aac.app.model.source.local.LocalCPDataSource(db)
        runBlocking {
            lds.addUser(
                com.gy.chatpaths.aac.app.model.PathUser(
                    1,
                    "",
                    null,
                ),
                false,
            )
        }
    }

    @After
    fun tearDown() {}

    @Test
    fun deleteImageIfNotUsed() {
        val name = "test123.jpg"
        val file = tempFolder.newFile(name)
        val fileUri = file.absoluteFile.toURI().toString()

        runBlocking {
            lds.deleteImageIfNotUsed(fileUri)
            // File should not be deleted because it's in use
            assertThat(file.exists()).isFalse()
        }
    }

    @Test
    fun deleteImageIfNotUsedCollection() {
        val name = "test123.jpg"
        val file = tempFolder.newFile(name)
        val fileUri = file.absoluteFile.toURI().toString()

        runBlocking {
            lds.addCollection(
                com.gy.chatpaths.aac.app.model.PathCollection(
                    1,
                    1,
                    imageUri = fileUri,
                ),
            )
            lds.deleteImageIfNotUsed(fileUri)
            // File should not be deleted because it's in use
            assertThat(file.exists()).isTrue()
        }
    }

    @Test
    fun deleteImageIfNotUsedPath() {
        val name = "test123.jpg"
        val file = tempFolder.newFile(name)
        val fileUri = file.absoluteFile.toURI().toString()

        runBlocking {
            lds.addCollection(
                com.gy.chatpaths.aac.app.model.PathCollection(
                    1,
                    1,
                    imageUri = null,
                ),
            )

            lds.addPath(
                userId = 1,
                collectionId = 1,
                imageUri = fileUri,
                anchored = false,
                position = null,
                parentId = null,
                title = null,
            )
            lds.deleteImageIfNotUsed(fileUri)
            // File should not be deleted because it's in use
            assertThat(file.exists()).isTrue()
        }
    }
}
