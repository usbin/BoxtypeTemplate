package com.example.boxtypetemplate.exercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.boxtypetemplate.R

class ExerciseSettingGoalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_setting_goal)
        setSupportActionBar(findViewById(R.id.toolbar_exercise_setting_goal))
        supportFragmentManager.beginTransaction()
            .replace(R.id.exercise_setting_goal_frame,
            ExerciseSettingGoalFragment())
            .commit()
    }

    class ExerciseSettingGoalFragment : PreferenceFragmentCompat(){
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_exercise_goal, rootKey)
        }

    }
}