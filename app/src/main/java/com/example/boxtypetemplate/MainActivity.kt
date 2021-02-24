package com.example.boxtypetemplate

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.boxtypetemplate.device.DeviceActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar_main))

        //BLE가 지원되지 않는 디바이스는 자동 종료.
        packageManager.takeIf{ !it.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)}?.also{
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show()
            moveTaskToBack(true); // 태스크를 백그라운드로 이동
            if(android.os.Build.VERSION.SDK_INT >= 21){
                finishAndRemoveTask()   //api 21 이상에서 태스크 삭제
            }
            else {
                finish()    //api 21 미만에서 태스크 삭제
            }
        }

        

        btn_main_device.setOnClickListener{
            val intent = Intent(this, DeviceActivity::class.java);
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