@file:Suppress("ktlint:standard:no-empty-file")

package com.gy.chatpaths.aac.app.builder
//
// import android.content.Context
// import android.util.Log
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
// class UserPathCollectionPrefsDBTest {
//    private lateinit var pathDao: PathDao
//    private lateinit var pathCollectionDao: PathCollectionDao
//    private lateinit var pathToCollectionsDao: PathToCollectionsDao
//    private lateinit var pathUserDao: PathUserDao
//    private lateinit var pathUserML: PathUserMLDao
//    private lateinit var userPathPrefsDao: UserPathCollectionPrefsDao
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
//        userPathPrefsDao = db.userPathCollectionPrefsDao()
//
//        val users = arrayOf(
//            PathUser(1, "default", null)
//        )
//
//        val pc = arrayOf(
//            PathCollection(1, "c1", null, null, null,1,false),
//            PathCollection(2, "c2", null, "test", null,2,false),
//            PathCollection(3, "c3", null, null, "test",3,false)
//        )
//
//        // This occurs on pre-populating the database on create
//        pathUserDao.insertAll(*users)
//        pathCollectionDao.insertAll(*pc)
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
//    fun verifyUserDetails() {
//        val prefDao = userPathPrefsDao
//
//        prefDao.insertAll(
//            UserPathCollectionPrefs(1,1,1,true,0, false),
//            UserPathCollectionPrefs(2,1,2,true,0, false),
//            UserPathCollectionPrefs(3,1,3,true,0, false)
//        )
//
//        val cprefs = prefDao.getView(1)
//        Log.d("gytest", cprefs.toString())
//        assertThat(cprefs).isNotNull()
//        assertThat(cprefs).hasSize(3)
//
//        assertThat(pathCollectionDao.get(2)?.displayImage).isEqualTo("test")
//        assertThat(pathCollectionDao.get(2)?.userDisplayImage).isNull()
//        assertThat(pathCollectionDao.get(3)?.userDisplayImage).isEqualTo("test")
//        assertThat(pathCollectionDao.get(3)?.displayImage).isNull()
//
//    }
//
// }
