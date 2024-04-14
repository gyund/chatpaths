package com.gy.chatpaths.aac.app.di.module

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.gy.chatpaths.aac.app.BuildConfig
import com.gy.chatpaths.aac.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Firebase
    @Inject
    constructor(
        @ApplicationContext appContext: Context,
    ) {
        var remoteConfig: FirebaseRemoteConfig? = null
            private set

        fun setAnalytics(enabled: Boolean) {
            if (BuildConfig.FLAVOR.contains("WithFirebase")) {
                Firebase.analytics.setAnalyticsCollectionEnabled(enabled)
            }
        }

        init {
            if (BuildConfig.FLAVOR.contains("WithFirebase")) {
                FirebaseApp.initializeApp(appContext)
                remoteConfig = Firebase.remoteConfig
            }

            remoteConfig?.apply {
                val configSettings =
                    remoteConfigSettings {
                        // 12 hours
                        minimumFetchIntervalInSeconds = 43200

                        // Override build
                        if (BuildConfig.DEBUG) {
                            minimumFetchIntervalInSeconds = 3600
                        }
                    }
                setConfigSettingsAsync(configSettings)
                setDefaultsAsync(R.xml.remote_config_defaults)
                activate()
                fetch()
            }
        }

        fun refresh() {
            remoteConfig?.activate()
        }

        fun getReviewCheckFrequency(): Long {
            var maxCount = 50L
            remoteConfig?.apply {
                maxCount = getLong("review_check_interval")
                if (maxCount <= 0) {
                    maxCount = 50L
                }
            }
            return maxCount
        }
    }
