package com.example.boxtypetemplate.risk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.boxtypetemplate.R

class RiskSettingActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_setting)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.risk_setting_frame, RiskSettingFragment())
            .commit()
        setSupportActionBar(findViewById(R.id.toolbar_risk_setting))


    }

    class RiskSettingFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_risk, rootKey)
        }
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?,
        pref: Preference?
    ): Boolean {
        TODO("Not yet implemented")
    }
}