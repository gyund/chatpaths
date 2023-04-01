package com.gy.chatpaths.aac.app.ui.home

import android.os.Bundle
import android.view.WindowManager
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.gy.chatpaths.aac.app.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        findPreference<SwitchPreferenceCompat>("keep_screen_awake_preference")?.setOnPreferenceClickListener {
            if (it.isEnabled) {
                activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            true
        }
    }
}