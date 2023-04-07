package com.gy.chatpaths.aac.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gy.chatpaths.aac.app.databinding.ActivityMainBinding
import com.gy.chatpaths.aac.app.di.module.Firebase
import com.gy.chatpaths.builder.InitialData
import com.gy.chatpaths.model.source.CPRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "CPActivity"
    private var _binder: ActivityMainBinding? = null
    protected val binder get() = _binder!!

    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var firebase: Firebase

    @Inject
    lateinit var repository: CPRepository

    val onDatabaseInitialized: MutableLiveData<Boolean> = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.extras?.apply {
//            keySet().forEach {
//                val value = get(it)
//                Log.d(
//                    TAG,
//                    "Extras received at onNewIntent:  Key: $it Value: $value"
//                )
//            }
            val uri: String? = getString("uri")
            if (uri != null) {
                if (uri.isNotEmpty()) {
                    // grab URI and navigate to it
                    sendViewIntent(uri, null)
                }
            }
        }

        installSplashScreen()
        // Do all the init stuff before loading the layouts, in case they depend on shared instances or singleton references

        setupFirebase()
        applyPreferences()

        lifecycleScope.launch {
            // Pull the update while we're in the splash screen

            initializeDatabase()
            onDatabaseInitialized.postValue(true)
        }

        _binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
        setSupportActionBar(binder.appBarMain.toolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binder.root.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (onDatabaseInitialized.value == true) {
                        binder.root.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            },
        )

        binder.navView.menu.findItem(R.id.license)?.apply {
            setOnMenuItemClickListener {
                launchLicenseActivity()
                return@setOnMenuItemClickListener true
            }
        }

        setupRemoteConfig()

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
            ),
            binder.drawerLayout,
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binder.navView.setupWithNavController(navController)
    }

    private fun applyPreferences() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val keepAwake = preferences.getBoolean("keep_screen_awake_preference", false)
        if (keepAwake) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binder = null
    }

    fun launchLicenseActivity() {
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
    }

    private fun setupRemoteConfig() {
        if (!BuildConfig.FLAVOR.contains("nofirebase")) {
            firebase.remoteConfig?.apply {
                activate().addOnCompleteListener {
                    if (!it.isComplete) return@addOnCompleteListener

                    if (null == _binder) { // received an NPE somewhere, but this is all I can think of, maybe lifecycle not bound
                        return@addOnCompleteListener
                    }
                    val uriFeedback = getString("uri_feedback")
                    if (uriFeedback.isNotBlank()) {
                        binder.navView.menu.findItem(R.id.sendFeedback)?.apply {
                            applyLinkToMenuItem(uriFeedback)
                        }
                    }
                    val uriHelp = getString("uri_help")
                    if (uriHelp.isNotBlank()) {
                        binder.navView.menu.findItem(R.id.help)?.apply {
                            applyLinkToMenuItem(uriHelp)
                        }
                    }
                    val uriFacebook = getString("uri_facebook")
                    if (uriFacebook.isNotBlank()) {
                        binder.navView.menu.findItem(R.id.facebook)?.apply {
                            applyLinkToMenuItem(uriFacebook)
                        }
                    }
                    val uriGithub = getString("uri_github")
                    if (uriGithub.isNotBlank()) {
                        binder.navView.menu.findItem(R.id.github)?.apply {
                            applyLinkToMenuItem(uriGithub)
                        }
                    }
                }
            }
        }
    }

    private fun MenuItem.applyLinkToMenuItem(
        webUri: String,
        packageName: String? = null,
    ) {
        setOnMenuItemClickListener {
            sendViewIntent(webUri, packageName)

            return@setOnMenuItemClickListener true
        }
        isVisible = true
    }

    private fun sendViewIntent(uri: String, packageName: String?) {
        try {
            val webpage: Uri = Uri.parse(uri)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            // Apply the package this is addressed to if specified
            packageName?.apply {
                intent.setPackage(this)
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun setupFirebase() {
        if (!BuildConfig.FLAVOR.contains("nofirebase")) {
            fun isTestDevice(): Boolean {
                val testLabSetting =
                    Settings.System.getString(contentResolver, "firebase.test.lab")
                return "true".equals(testLabSetting)
            }

            val collectionEnabled = !(isTestDevice() || BuildConfig.DEBUG)
            firebase.setAnalytics(collectionEnabled)
        }
    }

    private suspend fun initializeDatabase() {
        if (!repository.isInitialized()) { // first init
            InitialData.populate(applicationContext, repository)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun enableToolbar() {
        supportActionBar?.show()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.let {
                WindowCompat.setDecorFitsSystemWindows(it, true)
                it.decorView.let { view ->
                    WindowInsetsControllerCompat(
                        it,
                        view,
                    ).show(WindowInsetsCompat.Type.statusBars())
                }
            }
        } else {
            @Suppress("DEPRECATION")
            window?.decorView?.systemUiVisibility = 0
            @Suppress("DEPRECATION")
            window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    fun disableToolbar() {
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.let {
                WindowCompat.setDecorFitsSystemWindows(it, true)
                window?.decorView?.let { view ->
                    WindowInsetsControllerCompat(it, view).let { controller ->
                        controller.hide(
                            WindowInsetsCompat.Type.statusBars(),
                        )
                        controller.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val flags = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

            @Suppress("DEPRECATION")
            window?.decorView?.systemUiVisibility = flags
            // Note: This is the key to getting the status bar color at the top to disappear
            @Suppress("DEPRECATION")
            window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
}
