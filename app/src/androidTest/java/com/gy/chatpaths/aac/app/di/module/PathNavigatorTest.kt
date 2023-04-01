package com.gy.chatpaths.aac.app.di.module


import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.gy.chatpaths.aac.app.AppDatabaseHelper
import com.gy.chatpaths.aac.data.PathUserDetail
import com.gy.chatpaths.aac.data.source.CPRepository
import com.gy.chatpaths.aac.data.source.local.AppDatabase
import com.gy.chatpaths.aac.data.source.local.LocalCPDataSource
import com.gy.chatpaths.aac.resource.InitialData
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PathNavigatorTest : AppDatabaseHelper() {

    private suspend fun initializePathNavigator(oot: PathNavigator) {

        oot.repository = CPRepository(LocalCPDataSource(db))
        oot.currentUser = CurrentUser(context)
        oot.currentUser.user = User()
        oot.currentUser.user.repository = oot.repository
        oot.currentUser.setUser(1)
    }

    private suspend fun initAndCheck(
        oot: PathNavigator,
        expectedPath: Int = AppDatabase.PATH.something_to_say.value
    ): PathUserDetail? {
        val path = oot.setCollection(InitialData.COLLECTION_ID_LETSCHAT)
        path?.apply {
            assertThat(this.pathId).isEqualTo(expectedPath)
        }
        return path
    }

    @Test
    fun setCollectionId(): Unit = runBlocking {
        val oot = PathNavigator()
        initializePathNavigator(oot)
        initAndCheck(oot)
    }

    @Test
    fun next(): Unit = runBlocking {
        val oot = PathNavigator()
        initializePathNavigator(oot)
        initAndCheck(oot)

        var path = oot.next()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.chat_words.value)
        }
        path = oot.next()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.i_want.value)
        }
        path = oot.next()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.something_is_wrong.value)
        }

    }

    @Test
    fun nextDisabled(): Unit = runBlocking {
        db.pathUserMLDao()
            .insertOrIgnoreDefault(1, 1, AppDatabase.PATH.chat_words.value)
        db.pathUserMLDao()
            .setPathEnableWithDefaults(
                1,
                1,
                AppDatabase.PATH.chat_words.value,
                false  // we just set this above....
            )

        val oot = PathNavigator()
        initializePathNavigator(oot)
        initAndCheck(oot)

        val path = oot.next()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.i_want.value)
        }

    }

    @Test
    fun nextDisabledAdminMode(): Unit = runBlocking {
        db.pathUserMLDao()
            .insertOrIgnoreDefault(1, 1, AppDatabase.PATH.chat_words.value)
        db.pathUserMLDao()
            .setPathEnableWithDefaults(
                1,
                1,
                AppDatabase.PATH.chat_words.value,
                false  // we just set this above....
            )

        val oot = PathNavigator()
        initializePathNavigator(oot)
        oot.setShowDisabled(true)
        // Should still be on the same path
        // Init is ignored the second time you call it
        var path = initAndCheck(oot, AppDatabase.PATH.chat_words.value)
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.chat_words.value)
        }
        path = oot.next()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.something_to_say.value)
        }

    }

    @Test
    fun previous(): Unit = runBlocking {
        val oot = PathNavigator()
        initializePathNavigator(oot)
        initAndCheck(oot)

        var path = oot.previous()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.something_to_say.value)
        }

        path = oot.next()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.chat_words.value)
        }

        path = oot.previous()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.something_to_say.value)
        }

    }

    @Test
    fun select(): Unit = runBlocking {
        val oot = PathNavigator()
        initializePathNavigator(oot)
        initAndCheck(oot)

        // Note: These tests may need to be updated if this path changes the first few entries
        var path = oot.select()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.something_to_say.value) // weight 20
        }
        path = oot.select()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.something_to_say.value) // weight 20
        }
    }

    @Test
    fun selectAndBack(): Unit = runBlocking {
        val oot = PathNavigator()
        initializePathNavigator(oot)
        initAndCheck(oot)

        // Note: These tests may need to be updated if this path changes the first few entries
        var path = oot.select()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.something_to_say.value) // weight 20
        }
        path = oot.select()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.something_to_say.value) // weight 20
        }
        path = oot.next()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.chat_words.value)
        }

        path = oot.select()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.all_done.value) // weight 20
        }

        path = oot.next()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.more.value)
        }

        // Go in reverse, should follow the same trail

        path = oot.back()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.all_done.value)
        }

        path = oot.back()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.chat_words.value)
        }

        path = oot.back()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.something_to_say.value)
        }
    }

    @Test
    fun goto(): Unit = runBlocking {
        val oot = PathNavigator()
        initializePathNavigator(oot)
        initAndCheck(oot)

        var path = oot.next()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.chat_words.value)
        }
        val jumpval = path
        path = oot.select()
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.all_done.value) // weight 20
        }
        assertThat(jumpval).isNotNull()
        path = oot.goto(jumpval!!)
        path?.apply {
            assertThat(this.pathId).isEqualTo(AppDatabase.PATH.chat_words.value) // weight 20
        }


    }


}