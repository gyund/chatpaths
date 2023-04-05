package com.gy.chatpaths.model.source.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.gy.chatpaths.model.PathCollection
import com.gy.chatpaths.model.PathUser
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@MediumTest
class LocalCPDataSourceTest {

    private lateinit var pathDao: PathDao
    private lateinit var db: AppDatabase
    private lateinit var lds: LocalCPDataSource

    @get:Rule
    var tempFolder: TemporaryFolder = TemporaryFolder()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        pathDao = db.pathDao()
        lds = LocalCPDataSource(db)
        runBlocking {
            lds.addUser(
                PathUser(
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
                PathCollection(
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
                PathCollection(
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
