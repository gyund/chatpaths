package com.gy.chatpaths.aac.app.builder
//
// import android.content.Context
// import androidx.room.Room
// import androidx.test.core.app.ApplicationProvider
// import androidx.test.ext.junit.runners.AndroidJUnit4
// import com.gy.chatpaths.CPRepository
// import com.gy.chatpaths.aac.data.source.local.*
// import kotlinx.coroutines.runBlocking
// import org.hamcrest.Matchers.greaterThan
// import org.junit.After
// import org.junit.Assert.assertThat
// import org.junit.Before
// import org.junit.Rule
// import org.junit.Test
// import org.junit.rules.ErrorCollector
// import org.junit.runner.RunWith
//
// /**
// * Instrumented test, which will execute on an Android device.
// *
// * See [testing documentation](http://d.android.com/tools/testing).
// */
// @RunWith(AndroidJUnit4::class)
// class PrepopulateDBTest {
//    private lateinit var pathDao: PathDao
//    private lateinit var pathCollectionDao: PathCollectionDao
//    private lateinit var pathToCollectionsDao: PathToCollectionsDao
//    private lateinit var pathUserDao: PathUserDao
//    private lateinit var pathUserML: PathUserMLDao
//    private lateinit var db: AppDatabase
//    private lateinit var repository: CPRepository
//
//    @get:Rule
//    val collector: ErrorCollector = ErrorCollector()
//
//    @Before
//    fun createDb() = runBlocking {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
//            .build()
//        val local = LocalCPDataSource(db)
//        repository = CPRepository(local)
//
//
//        InitialData.populate(context, repository)
//
//        pathDao = db.pathDao()
//        pathCollectionDao = db.pathCollectionDao()
//        pathToCollectionsDao = db.pathToCollectionsDao()
//        pathUserDao = db.pathUserDao()
//        pathUserML = db.pathUserMLDao()
//    }
//
//    @After
//    @Throws(Exception::class)
//    fun closeDb() {
//        db.close()
//    }
//
//
//    /**
//     * Verify no foreign key constraints are violated
//     */
//    @Test
//    @Throws(Exception::class)
//    fun verifyPrepop() {
//        val d = pathDao.getDetailAll()
//        assertThat(d.size, greaterThan(1))
//        val context = ApplicationProvider.getApplicationContext<Context>()
//
//        d.forEach {
//            // make sure the strings are good
//            val id = context.resources.getIdentifier(
//                it.defaultTitleStringResource,
//                "string",
//                context.packageName
//            )
//            collector.checkThat(it.defaultTitleStringResource + " is missing", id, greaterThan(0))
//
//            // make sure the images are good
//        }
//    }
//
// }
