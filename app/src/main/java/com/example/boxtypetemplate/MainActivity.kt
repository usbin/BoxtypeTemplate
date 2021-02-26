package com.example.boxtypetemplate

import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.boxtypetemplate.device.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    var bluetoothLeService : BluetoothLeService? = null
    private val gattUpdateReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    Log.d("MainActivity::onReceive", "connected")

                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    Log.d("MainActivity::onReceive", "disconnected")

                }
                BluetoothLeService.ACTION_DATA_AVAILABLE -> {
                    Log.d("MainActivity::onReceive", "data available")
                }
            }
        }
    }
    private val serviceConnection : ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service : IBinder){
            bluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            Log.d("${this.javaClass.simpleName}::onServiceConnected", "service connected")
            if(!bluetoothLeService?.initialize()!!){
                Log.e(BluetoothLeService.TAG, "Unable to initialize Bluetooth")

            }
            Log.d("service", "${BluetoothLeService?.connectionState}")
//            if(deviceAddress != null){
//                //deviceAdress: Confirm 버튼 누를 때 초기화.
//                bluetoothLeService?.connect(deviceAddress)
//            }
        }
        override fun onServiceDisconnected(componentName: ComponentName){
            bluetoothLeService = null
        }
    }
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

        



    }

    override fun onResume() {
        super.onResume()

        Log.d("service", "BLE Running State : ${isBleServiceRunning()}")
        if(isBleServiceRunning()){
            Intent(this, BluetoothLeService::class.java).also { intent ->
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
        }
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
        btn_main_device.setOnClickListener{
            val intent = Intent(this, DeviceActivity::class.java);
            startActivity(intent);
        }
        btn_main_falldetection.setOnClickListener {
            val intent = Intent(this, FalldetectionActivity::class.java)
            startActivity(intent);

        }


    }

    private fun makeGattUpdateIntentFilter() : IntentFilter? {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
        return intentFilter
    }
    private fun isBleServiceRunning() : Boolean{
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for(service in manager.getRunningServices(Int.MAX_VALUE)){
            Log.d("service", "${BluetoothLeService.javaClass.name.split("$")} ----- ${service.service.className}")
            if(BluetoothLeService.javaClass.name.split("$")[0] == service.service.className){

                return true;
            }
        }
        return false;
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