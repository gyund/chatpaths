package com.gy.chatpaths.aac.app

import android.app.Application
import androidx.annotation.Keep
import com.gy.chatpaths.aac.app.di.module.Firebase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var firebase: Firebase
}

/**
 * Fix code obsfucation issue:
 * https://stackoverflow.com/questions/66082157/java-lang-runtimeexception-unknown-animation-name-x
 *
 */
@Keep
fun keep() = listOf(
    androidx.navigation.ui.R.anim.nav_default_enter_anim,
    androidx.navigation.ui.R.anim.nav_default_exit_anim,
    androidx.navigation.ui.R.anim.nav_default_pop_enter_anim,
    androidx.navigation.ui.R.anim.nav_default_pop_exit_anim,

    androidx.navigation.ui.R.animator.nav_default_enter_anim,
    androidx.navigation.ui.R.animator.nav_default_exit_anim,
    androidx.navigation.ui.R.animator.nav_default_pop_enter_anim,
    androidx.navigation.ui.R.animator.nav_default_pop_exit_anim,
)
