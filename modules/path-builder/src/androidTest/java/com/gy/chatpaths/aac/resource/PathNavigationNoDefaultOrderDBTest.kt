package com.gy.chatpaths.aac.resource
//
// import android.content.Context
// import androidx.room.Room
// import androidx.test.core.app.ApplicationProvider
// import androidx.test.ext.junit.runners.AndroidJUnit4
// import com.google.common.truth.Truth.assertThat
// import com.gy.chatpaths.aac.data.PathCollection
// import com.gy.chatpaths.aac.data.PathUser
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
// class PathNavigationNoDefaultOrderDBTest {
//    private lateinit var pathDao: PathDao
//    private lateinit var pathCollectionDao: PathCollectionDao
//    private lateinit var pathToCollectionsDao: PathToCollectionsDao
//    private lateinit var pathUserDao: PathUserDao
//    private lateinit var pathUserML: PathUserMLDao
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
//            pathToCollections(1,1,null, AppDatabase.PATH.something_to_say, 0),
//            pathToCollections(2,1,null, AppDatabase.PATH.chat_words, 0),
//            pathToCollections(3,1,null, AppDatabase.PATH.i_want, 0),
//            pathToCollections(4,1,null, AppDatabase.PATH.something_is_wrong, 0)
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
//    fun verifyReorderAlgorithmWithReset() {
//        fun do100Count(it: PathUserDetail) {
//            var count = 0
//            while (count < 100) {
//                count++
//                pathUserML.increaseTimesUsedWithDefaults(
//                    it.userId,
//                    it.collectionId,
//                    it.pathId
//                )
//            }
//        }
//        fun verifySameOrderAndIncrement(): Array<PathUserDetail>? {
//            var rp = pathDao.getAllRootPathsForCollection(1, 1)
//
//            assertThat(rp != null).isTrue()
//            rp?.let {
//                assertThat(it.size).isEqualTo(4)
//                assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value) // 0
//                assertThat(it[1].pathId).isEqualTo(AppDatabase.PATH.chat_words.value) // 0
//                assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.i_want.value) // 0
//                assertThat(it[3].pathId).isEqualTo(AppDatabase.PATH.something_is_wrong.value) //100
//
//                do100Count(it[2]) // 100
//            }
//            return rp
//        }
//
//        pathUserML.reorderPathsAutomatically(1,1)
//        verifySameOrderAndIncrement()?.also {
//            assertThat(it.size).isEqualTo(4)
//            assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value)
//            assertThat(it[0].position).isEqualTo(0)
//            assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.i_want.value)
//            assertThat(it[2].position).isEqualTo(0)
//        }
//
//        pathUserML.reorderPathsAutomatically(1,1)
//        pathUserML.resetPathOrderToDefaults(1,1)
//        verifySameOrderAndIncrement()?.also {
//            assertThat(it.size).isEqualTo(4)
//            assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value)
//            assertThat(it[0].position).isEqualTo(0)
//            assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.i_want.value)
//            assertThat(it[2].position).isEqualTo(0)
//        }
//
//        pathUserML.resetTimesUsed(1,1)
//        pathUserML.reorderPathsAutomatically(1,1)
//        verifySameOrderAndIncrement()?.also {
//            assertThat(it.size).isEqualTo(4)
//            assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value)
//            assertThat(it[0].position).isEqualTo(0)
//            assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.i_want.value)
//            assertThat(it[2].position).isEqualTo(0)
//        }
//
//        // I'm not sure what this is supposed to test or do
// //        pathUserML.resetPathOrderToDefaults(1,1)
// //        pathUserML.reorderPathsAutomatically(1,1)
// //
// //        verifySameOrderAndIncrement()?.also {
// //            assertThat(it.size).isEqualTo(4)
// //            // 2 will be where 0 is because we didn't reset the count, just swapped order back and forth between defaults
// //            assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value)
// //            assertThat(it[2].pathOrder).isEqualTo(0)
// //            assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.i_want.value)
// //            assertThat(it[0].pathOrder).isEqualTo(0)
// //        }
//
//        val rp = pathDao.getAllRootPathsForCollection(1, 1)
//
//        assertThat(rp != null).isTrue()
//        rp?.let {
//            assertThat(it.size).isEqualTo(4)
//            assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value)
//            assertThat(it[0].position).isEqualTo(0)
//            assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.i_want.value)
//            assertThat(it[2].position).isEqualTo(0)
//        }
//
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun verifyReset() {
//        fun doOneCount(it: PathUserDetail) {
//            var count = 0
//            while (count < 1) {
//                count++
//                pathUserML.increaseTimesUsedWithDefaults(
//                    it.userId,
//                    it.collectionId,
//                    it.pathId
//                )
//            }
//        }
//        // Control paths
//        var cp = pathDao.getAllRootPathsForCollection(2,1)
//        assertThat(cp != null).isTrue()
//        cp?.let {
//            assertThat(it.size).isEqualTo(4)
//            doOneCount(it[0])
//            doOneCount(it[1])
//            doOneCount(it[2])
//            doOneCount(it[3])
//            doOneCount(it[3])
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
//            doOneCount(it[0])
//            doOneCount(it[1])
//            doOneCount(it[2])
//            doOneCount(it[3])
//        }
//
//        pathUserML.resetPathOrderToDefaults(1,1)
//        rp = pathDao.getAllRootPathsForCollection(1,1)
//        assertThat(rp != null).isTrue()
//        rp?.let {
//            assertThat(it.size).isEqualTo(4)
//            assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value) // 25
//            assertThat(it[1].pathId).isEqualTo(AppDatabase.PATH.chat_words.value) // 20
//            assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.i_want.value) // 15
//            assertThat(it[3].pathId).isEqualTo(AppDatabase.PATH.something_is_wrong.value) //10
//
//            assertThat(it[0].position).isEqualTo(0) // 25
//            assertThat(it[1].position).isEqualTo(0) // 20
//            assertThat(it[2].position).isEqualTo(0) // 15
//            assertThat(it[3].position).isEqualTo(0) // 10
//
//            // This function should have no effect unless it doesn't reset defaults exactly
//            pathUserML.resetPathOrderToDefaults(1,1)
//            pathUserML.resetTimesUsed(1,1)
//
//            doOneCount(it[3])
//            doOneCount(it[3])
//            doOneCount(it[3])
//        }
//
//        // Lets reorder and make sure the defaultOrder was set to the times used
//        pathUserML.reorderPathsAutomatically(1,1)
//        rp = pathDao.getAllRootPathsForCollection(1,1)
//        assertThat(rp != null).isTrue()
//        rp?.let {
//            assertThat(it.size).isEqualTo(4)
//            assertThat(it[1].pathId).isEqualTo(AppDatabase.PATH.something_to_say.value) // 25
//            assertThat(it[2].pathId).isEqualTo(AppDatabase.PATH.chat_words.value) // 20
//            assertThat(it[3].pathId).isEqualTo(AppDatabase.PATH.i_want.value) // 15
//            assertThat(it[0].pathId).isEqualTo(AppDatabase.PATH.something_is_wrong.value) // 10
//            // 3 should be in 1s place
//        }
//
//        cp = pathDao.getAllRootPathsForCollection(2,1)
//        assertThat(cp != null).isTrue()
//        cp?.let {
//            assertThat(it.size).isEqualTo(4)
//            assertThat(it[0].position).isEqualTo(0) // 25
//            assertThat(it[1].position).isEqualTo(0) // 20
//            assertThat(it[2].position).isEqualTo(0) // 15
//            assertThat(it[3].position).isEqualTo(0) //10
//
//            assertThat(it[0].timesUsed).isEqualTo(1)
//            assertThat(it[1].timesUsed).isEqualTo(1)
//            assertThat(it[2].timesUsed).isEqualTo(1)
//            assertThat(it[3].timesUsed).isEqualTo(2)
//
//            pathUserML.reorderPathsAutomatically(2,1)
//        }
//
//        // Test Reorder
//        cp = pathDao.getAllRootPathsForCollection(2,1)
//        assertThat(cp != null).isTrue()
//        cp?.let {
//            assertThat(it.size).isEqualTo(4)
//            assertThat(it[0].position).isEqualTo(2) // 25
//            assertThat(it[1].position).isEqualTo(1) // 20
//            assertThat(it[2].position).isEqualTo(1) // 15
//            assertThat(it[3].position).isEqualTo(1) //
//        }
//
//    }
// }
