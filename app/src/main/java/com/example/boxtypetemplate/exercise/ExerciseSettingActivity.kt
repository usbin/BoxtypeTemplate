package com.example.boxtypetemplate.exercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import com.example.boxtypetemplate.R

class ExerciseSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_setting)
        supportFragmentManager.beginTransaction().replace(
            R.id.exercise_setting_frame,
            ExerciseSettingFragment()
        ).commit()
        setSupportActionBar(findViewById(R.id.toolbar_exercise_setting))
    }


    class ExerciseSettingFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_exercise, rootKey)
        }
    }
}

