package com.gy.chatpaths.aac.resource

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gy.chatpaths.aac.data.Path
import com.gy.chatpaths.aac.data.PathCollection
import com.gy.chatpaths.aac.data.PathUser
import com.gy.chatpaths.aac.data.source.local.*
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PathUserDetailDBTest {
    private lateinit var pathDao: PathDao
    private lateinit var pathCollectionDao: PathCollectionDao
    private lateinit var pathToCollectionsDao: PathToCollectionsDao
    private lateinit var pathUserDao: PathUserDao
    private lateinit var pathUserML: PathUserMLDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        pathDao = db.pathDao()
        pathCollectionDao = db.pathCollectionDao()
        pathToCollectionsDao = db.pathToCollectionsDao()
        pathUserDao = db.pathUserDao()
        pathUserML = db.pathUserMLDao()

        val paths = arrayOf(
            Path(1, "path_p1", imageResource = "ic_p1"),
            Path(2,"path_p2","ic_p2")
        )
        val pc = arrayOf(
            PathCollection(1,"c1",null,null,null,1,false),
            PathCollection(2,"c2",null,null,null,2,false)
        )
        val p2c = arrayOf(
            PathToCollections(1,
                pathId = paths[0].pathId,
                pathCollectionId = pc[0].collectionId,
                parentId = null,
                defaultPosition = 0
            ),
            PathToCollections(
                id = 2,
                pathId = paths[1].pathId,
                pathCollectionId = pc[0].collectionId,
                parentId = null,
                defaultPosition = 0
            ),
            PathToCollections(
                id = 3,
                pathId = paths[0].pathId,
                pathCollectionId = pc[1].collectionId,
                parentId = null,
                defaultPosition = 0
            )
        )
        val users = arrayOf(
            PathUser(1, "default",null)
        )

        // This occurs on pre-populating the database on create
        pathDao.insertAll(*paths)
        pathCollectionDao.insertAll(*pc)
        pathToCollectionsDao.insertAll(*p2c)
        pathUserDao.insertAll(*users)
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        db.close()
    }


    @Test
    @Throws(Exception::class)
    fun verifyUserDetails() {
        val d = pathDao.getDetailAll()

        assertThat(d[0].pathId, equalTo(1)) // path 1, collection 1
        assertThat(d[1].pathId, equalTo(2)) // path 2, collection 1
        assertThat(d[2].pathId, equalTo(1)) // path 1, collection 2

        // verify some default rows
        assertThat(d[0].userId, equalTo(1))
        assertThat(d[1].userId, equalTo(1))
        assertThat(d[2].userId, equalTo(1))

        assertThat(d[0].enabled, equalTo(true))
        assertThat(d[1].enabled, equalTo(true))
        assertThat(d[2].enabled, equalTo(true))

        assertThat(d[0].timesUsed, equalTo(0))
        assertThat(d[1].timesUsed, equalTo(0))
        assertThat(d[2].timesUsed, equalTo(0))

        assertThat(d[0].position, equalTo(0))
        assertThat(d[1].position, equalTo(0))
        assertThat(d[2].position, equalTo(0))
    }

    @Test
    @Throws(Exception::class)
    fun verifyUserDetailsMLRequests() {
        pathUserML.increaseTimesUsedWithDefaults(
            userId = 1,
            pathId = 1,
            pathCollectionId = 1
        )
        pathUserML.increaseTimesUsedWithDefaults(
            userId = 1,
            pathId = 1,
            pathCollectionId = 1
        )

        val d = pathDao.getDetailAll()

        // verify some rows
        assertThat(d[0].userId, equalTo(1))
        assertThat(
            d[1].userId,
            equalTo(1)
        ) // an user statistic ads one row, so it'll be path 1,1,2
        assertThat(d[2].userId, equalTo(1))

        assertThat(d[0].enabled, equalTo(true))
        assertThat(d[1].enabled, equalTo(true))
        assertThat(d[2].enabled, equalTo(true))

        assertThat(d[0].timesUsed, equalTo(2))
        assertThat(d[1].timesUsed, equalTo(0))
        assertThat(d[2].timesUsed, equalTo(0))

        assertThat(d[0].position, equalTo(0))
        assertThat(d[1].position, equalTo(0))
        assertThat(d[2].position, equalTo(0))
    }
}