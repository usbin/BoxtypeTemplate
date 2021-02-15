package com.example.boxtypetemplate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

class FalldetectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_falldetection)
        setSupportActionBar(findViewById(R.id.toolbar_falldetection))
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.falldetection_frame, FalldetectionFragment())
            .commit()
    }

}