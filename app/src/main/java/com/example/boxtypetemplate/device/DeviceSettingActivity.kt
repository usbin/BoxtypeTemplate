package com.example.boxtypetemplate.device

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.example.boxtypetemplate.R

class DeviceSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_setting)
        setSupportActionBar(findViewById(R.id.toolbar_device_setting))
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.device_setting_frame, DeviceSettingFragment())
            .commit()
    }

    class DeviceSettingFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_device, rootKey)
        }
    }
}