package com.gy.chatpaths.aac.app.ui.license

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.gy.chatpaths.aac.app.databinding.FragmentLicenseBinding

class LicenseFragment : Fragment() {
    private var binder: FragmentLicenseBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binder = FragmentLicenseBinding.inflate(inflater, container, false)
        binder!!.button.setOnClickListener {
            launchLicenseActivity()
        }

        return binder!!.root
    }

    fun launchLicenseActivity() {
        startActivity(Intent(context, OssLicensesMenuActivity::class.java))
    }
}
