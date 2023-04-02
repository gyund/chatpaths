package com.gy.chatpaths.aac.resource
//
// import android.content.Context
// import androidx.room.Room
// import androidx.test.core.app.ApplicationProvider
// import androidx.test.ext.junit.runners.AndroidJUnit4
// import com.gy.chatpaths.aac.data.source.local.AppDatabase
// import com.gy.chatpaths.aac.data.source.local.PathCollectionDao
// import com.gy.chatpaths.aac.data.source.local.PathDao
// import org.junit.After
// import org.junit.Before
// import org.junit.runner.RunWith
//
// /**
// * Instrumented test, which will execute on an Android device.
// *
// * See [testing documentation](http://d.android.com/tools/testing).
// */
// @RunWith(AndroidJUnit4::class)
// class PathDaoDBTest {
//    private lateinit var pathDao: PathDao
//    private lateinit var pathCollectionDao: PathCollectionDao
//    private lateinit var db: AppDatabase
//
//    @Before
//    fun createDb() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
//        pathDao = db.pathDao()
//    }
//
//    @After
//    @Throws(Exception::class)
//    fun closeDb() {
//        db.close()
//    }
// }
