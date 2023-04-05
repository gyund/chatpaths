package com.gy.chatpaths.aac.resource
//
// import android.content.Context
// import androidx.room.Room
// import androidx.test.core.app.ApplicationProvider
// import androidx.test.ext.junit.runners.AndroidJUnit4
// import com.google.common.truth.Truth.assertThat
// import com.gy.chatpaths.PathCollection
// import com.gy.chatpaths.PathUser
// import com.gy.chatpaths.aac.data.source.local.*
// import org.junit.After
// import org.junit.Before
// import org.junit.Test
// import org.junit.runner.RunWith
//
//
// /**
// * Instrumented test, which will execute on an Android device.
// *
// * See [testing documentation](http://d.android.com/tools/testing).
// */
// @RunWith(AndroidJUnit4::class)
// class PathNavigationDBTest {
//    private lateinit var pathDao: PathDao
//    private lateinit var pathCollectionDao: PathCollectionDao
//    private lateinit var pathUserDao: PathUserDao
//    private lateinit var db: AppDatabase
//
//    @Before
//    fun createDb() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
//        pathDao = db.pathDao()
//        pathCollectionDao = db.pathCollectionDao()
//        pathToCollectionsDao = db.pathToCollectionsDao()
//        pathUserDao = db.pathUserDao()
//        pathUserML = db.pathUserMLDao()
//
//        val paths = InitialData.listOfPaths()
//
//        val pc = arrayOf(
//            PathCollection(1,"c1",null, null, null, 1,false)
//        )
//        val p2c = arrayOf(
//            pathToCollections(1,1,null, AppDatabase.PATH.something_to_say, Int.MAX_VALUE),
//            pathToCollections(2,1,null, AppDatabase.PATH.chat_words, 20),
//            pathToCollections(3,1,null, AppDatabase.PATH.i_want, 15),
//            pathToCollections(4,1,null, AppDatabase.PATH.something_is_wrong, 10)
//        )
//        val users = arrayOf(
//            PathUser(1, "default",null),
//            PathUser(2, "second",null)
//        )
//
//        // This occurs on pre-populating the database on create
//        pathDao.populate(paths)
//        pathCollectionDao.populate(*pc)
//        pathToCollectionsDao.populate(*p2c)
//        pathUserDao.populate(*users)
//    }
//
//    @After
//    @Throws(Exception::class)
//    fun closeDb() {
//        db.close()
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun verifyIncrementDoesNotChangePathOrder() {
//        fun assertSizeAndOrder(rp: Array<PathUserDetail>) {
//            assertThat(rp.size).isEqualTo(4)
//            assertThat(rp[0].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value) // 25
//            assertThat(rp[1].pathId).isEqualTo(AppDatabase.PATH.chat_words.value) // 20
//            assertThat(rp[2].pathId).isEqualTo(AppDatabase.PATH.i_want.value) // 15
//            assertThat(rp[3].pathId).isEqualTo(AppDatabase.PATH.something_is_wrong.value) //10
//        }
//
//        var rp = pathDao.getAllRootPathsForCollection(1,1)
//
//        assertThat(rp != null).isTrue()
//        rp?.let {
//            assertSizeAndOrder(it)
//            var count = 0
//            while(count < 30) {
//                count++
//                pathUserML.increaseTimesUsedWithDefaults(
//                    it[3].userId,
//                    it[3].collectionId,
//                    it[3].pathId
//                )
//            }
//        }
//
//        rp = pathDao.getAllRootPathsForCollection(1,1)
//        assertThat(rp != null).isTrue()
//        rp?.let {
//            assertSizeAndOrder(it)
//        }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun verifyReorderAlgorithm() {
//        fun doFiveCount(it: PathUserDetail) {
//            var count = 0
//            while (count < 5) {
//                count++
//                pathUserML.increaseTimesUsedWithDefaults(
//                    it.userId,
//                    it.collectionId,
//                    it.pathId
//                )
//            }
//        }
//
//        var rp = pathDao.getAllRootPathsForCollection(1,1)
//
//        assertThat(rp != null).isTrue()
//        rp?.let {
//            assertThat(it.size).isEqualTo(4)
//            assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value) // 25
//            assertThat(it[1].pathId).isEqualTo(AppDatabase.PATH.chat_words.value) // 20
//            assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.i_want.value) // 15
//            assertThat(it[3].pathId).isEqualTo(AppDatabase.PATH.something_is_wrong.value) //10
//
//            doFiveCount(it[3]) // 5
//            doFiveCount(it[3]) // 10
//            doFiveCount(it[3]) // 15
//        }
//
//        pathUserML.reorderPathsAutomatically(1,1)
//        rp = pathDao.getAllRootPathsForCollection(1,1)
//
//        assertThat(rp != null).isTrue()
//        rp?.let {
//            // Should be even with 15 but the pathId will bias the sort
//            assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value)
//            assertThat(it[1].pathId).isEqualTo(AppDatabase.PATH.chat_words.value)
//            assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.i_want.value)
//            assertThat(it[3].pathId).isEqualTo(AppDatabase.PATH.something_is_wrong.value)
//
//            doFiveCount(it[3]) //20
//        }
//
//        pathUserML.reorderPathsAutomatically(1,1)
//        rp = pathDao.getAllRootPathsForCollection(1,1)
//
//        assertThat(rp != null).isTrue()
//        rp?.let {
//            pathUserML.reorderPathsAutomatically(1, 1)
//            assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value)
//            assertThat(it[1].pathId).isEqualTo(AppDatabase.PATH.chat_words.value)
//            assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.something_is_wrong.value)
//            assertThat(it[3].pathId).isEqualTo(AppDatabase.PATH.i_want.value)
//
//            doFiveCount(it[2]) //25
//        }
//        pathUserML.reorderPathsAutomatically(1,1)
//        rp = pathDao.getAllRootPathsForCollection(1,1)
//
//        assertThat(rp != null).isTrue()
//        rp?.let {
//            pathUserML.reorderPathsAutomatically(1, 1)
//            assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value)
//            assertThat(it[1].pathId).isEqualTo(AppDatabase.PATH.something_is_wrong.value) //25
//            assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.chat_words.value)
//            assertThat(it[3].pathId).isEqualTo(AppDatabase.PATH.i_want.value)
//
//            doFiveCount(it[1]) // 30
//        }
//        pathUserML.reorderPathsAutomatically(1,1)
//        rp = pathDao.getAllRootPathsForCollection(1,1)
//
//        assertThat(rp != null).isTrue()
//        rp?.let {//30 - but position 0 has a default of max int so nothing should happen
//            pathUserML.reorderPathsAutomatically(1,1)
//            assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value) // 30
//            assertThat(it[1].pathId).isEqualTo(AppDatabase.PATH.something_is_wrong.value) //30
//            assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.chat_words.value)
//            assertThat(it[3].pathId).isEqualTo(AppDatabase.PATH.i_want.value)
//
//            doFiveCount(it[1]) // 30
//        }
//
//        pathUserML.reorderPathsAutomatically(1,1)
//        rp = pathDao.getAllRootPathsForCollection(1,1)
//
//        assertThat(rp != null).isTrue()
//        rp?.let {//35 - but position 0 has a default of max int so nothing should happen
//            pathUserML.reorderPathsAutomatically(1,1)
//            assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value) // 30
//            assertThat(it[1].pathId).isEqualTo(AppDatabase.PATH.something_is_wrong.value) //35
//            assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.chat_words.value)
//            assertThat(it[3].pathId).isEqualTo(AppDatabase.PATH.i_want.value)
//        }
//
//    }
//
// }
