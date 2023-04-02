package com.gy.chatpaths.aac.app
//
// import android.content.Context
// import androidx.room.Room
// import androidx.test.core.app.ApplicationProvider
// import androidx.test.ext.junit.runners.AndroidJUnit4
// import com.google.common.truth.Truth.assertThat
// import com.gy.chatpaths.aac.app.di.module.CurrentUser
// import com.gy.chatpaths.aac.data.source.CPRepository
// import com.gy.chatpaths.aac.data.source.local.*
// import com.gy.chatpaths.aac.resource.InitialData
// import kotlinx.coroutines.runBlocking
// import org.junit.After
// import org.junit.Before
// import org.junit.Rule
// import org.junit.Test
// import org.junit.rules.ErrorCollector
// import org.junit.runner.RunWith
//
//
// /**
// * Instrumented test, which will execute on an Android device.
// *
// * See [testing documentation](http://d.android.com/tools/testing).
// */
// @RunWith(AndroidJUnit4::class)
// class UserCollectionDbViewModelTest {
//    private lateinit var pathDao: PathDao
//    private lateinit var pathCollectionDao: PathCollectionDao
//    private lateinit var pathUserDao: PathUserDao
//    private lateinit var db: AppDatabase
//    private lateinit var context: Context
//    private lateinit var repository: CPRepository
//
//    @get:Rule
//    val collector: ErrorCollector = ErrorCollector()
//
//    @Before
//    fun createDb() = runBlocking {
//        context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
//            .build()
//        repository = CPRepository(LocalCPDataSource(db))
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
//    fun verifyPrepop() = runBlocking {
//        val d = pathDao.getDetailAll()
//        val userId = 1
//
//        val userVM = CurrentUser(context)
//        userVM.user = User()
//
//        // inject our DB
//        userVM.user.repository = CPRepository(LocalCPDataSource(db))
//
//        // Verify Initial State
//        val prefsDao = db.userPathCollectionPrefsDao()
//        var collections = prefsDao.getView(userId)
//        assertThat(collections).isNotEmpty()
//        collections.forEach {
//            assertThat(it.prefId).isNull()
//        }
//        // this should initiate the preferences
//        userVM.setUser(1)
//
//        collections = prefsDao.getView(userId)
//        assertThat(collections).isNotEmpty()
//        collections.forEach {
//            assertThat(it.prefId).isNotNull()
//        }
//    }
//
// }
