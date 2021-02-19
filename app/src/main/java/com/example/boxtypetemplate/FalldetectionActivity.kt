package com.example.boxtypetemplate

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class FalldetectionActivity : AppCompatActivity(), Preference.OnPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_falldetection)
        setSupportActionBar(findViewById(R.id.toolbar_falldetection))
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.falldetection_frame, FalldetectionFragment())
            .commit()

    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        TODO("Not yet implemented")
    }


}