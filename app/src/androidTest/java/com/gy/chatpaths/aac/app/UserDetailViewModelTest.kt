package com.gy.chatpaths.aac.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.gy.chatpaths.aac.data.source.CPRepository
import com.gy.chatpaths.aac.data.source.local.LocalCPDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UserDetailViewModelTest : AppDatabaseHelper() {

    @Test
    fun validate_populateUserDefaultCollectionPrefs() = runBlocking {
        val userId = 1
        val prefsDao = db.userPathCollectionPrefsDao()
        var collections = prefsDao.getView(userId)
        assertThat(collections).isNotEmpty()
        collections.forEach {
            assertThat(it.prefId).isNull()
        }

        val user = User()
        user.repository = CPRepository(LocalCPDataSource(db))
        user.setUser(userId) // This initializes the preferences

        // Verify it is updated
        collections = prefsDao.getView(userId)
        assertThat(collections).isNotEmpty()
        collections.forEachIndexed { index, it ->
            assertThat(it.prefId).isNotNull()
            assertThat(it.enabled).isTrue()
        }

        prefsDao.setCollectionEnabled(1, 1, false)

        collections = prefsDao.getView(userId)
        assertThat(collections).isNotEmpty()
        collections.forEachIndexed { index, it ->
            assertThat(it.prefId).isNotNull()
            if (it.prefId == 1) {
                assertThat(it.enabled).isFalse()
            } else {
                assertThat(it.enabled).isTrue()
            }
        }

    }
}