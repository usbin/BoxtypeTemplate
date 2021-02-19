package com.example.boxtypetemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar_main))

        btn_main_device.setOnClickListener{
            val intent = Intent(this, DeviceActivity::class.java);
            startActivity(intent);
        }
        btn_main_risk.setOnClickListener{
            val intent = Intent(this, RiskActivity::class.java)
            startActivity(intent);
        }
        btn_main_falldetection.setOnClickListener {
            val intent = Intent(this, FalldetectionActivity::class.java)
            startActivity(intent);

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.main_menu_setting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }
}