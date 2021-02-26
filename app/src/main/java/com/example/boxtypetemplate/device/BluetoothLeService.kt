package com.example.boxtypetemplate.device

import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.boxtypetemplate.R
import java.util.*

// A service that interacts with the BLE device via the Android BLE API.
class BluetoothLeService() : Service() {



    // Various callback methods defined by the BLE API.
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            val intentAction: String
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    intentAction = ACTION_GATT_CONNECTED
                    connectionState = STATE_CONNECTED
                    broadcastUpdate(intentAction)
                    Log.i("DeviceCommunication",
                        "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                                "- Connected to GATT server ... Attempting to start service discovery: ${mBluetoothGatt?.discoverServices()}")


                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    intentAction = ACTION_GATT_DISCONNECTED
                    connectionState = STATE_DISCONNECTED
                    Log.i("DeviceCommunication",
                        "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                                "- Disconnected from GATT server.")
                    broadcastUpdate(intentAction)

                }
            }
        }

        // New services discovered
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d("DeviceCommunication",
                "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                        "- Service discovered now.")
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                    Log.d("DeviceCommunication",
                        "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                                "- Service discover gatt success and broadcast!")

                }
                else -> {
                    Log.w("DeviceCommunication",
                        "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                                "- received: $status")
                }
            }
        }

        // Result of a characteristic read operation
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
                    Log.d("DeviceCommunication", "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                            "- Characteristic read success")


                }
            }
        }
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return LocalBinder()
    }

    private fun broadcastUpdate(action: String){
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private fun broadcastUpdate(action : String, characteristic: BluetoothGattCharacteristic){
        val intent = Intent(action)
        Log.d("DeviceCommunication",
            "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                    "- Broadcast started")
        when(characteristic.uuid){
            UUID_DATA_NOTIFY -> {
                val data: String = getString(0);
                intent.putExtra(EXTRA_DATA, data)
            }
        }
        sendBroadcast(intent)

    }
    inner class LocalBinder : Binder(){
        val service: BluetoothLeService
            get() = this@BluetoothLeService
    }
    fun initialize(): Boolean {
        if(mBluetoothManager == null){
            mBluetoothManager =
                getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            if(mBluetoothManager == null){
                Log.e(TAG, "Unable to initialize BluetoothManager");
                return false
            }
        }
        mBluetoothAdapter = mBluetoothManager?.adapter
        if(mBluetoothAdapter == null){
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }

    fun connect(address: String?):Boolean {
        if(mBluetoothAdapter == null || mBluetoothManager == null){
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.")
            return false
        }
        if(mBluetoothDeviceAddress != null && address == mBluetoothDeviceAddress && mBluetoothGatt != null){
            return if(mBluetoothGatt!!.connect()){
                mConnectionState = STATE_CONNECTING
                true
            } else {
                false
            }
        }
        val device = mBluetoothAdapter!!.getRemoteDevice(address)
        if(device == null){
            return false
        }
        mBluetoothGatt = device.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        mBluetoothDeviceAddress = address
        mConnectionState = STATE_CONNECTING
        return true
    }
    fun disconnect(){
        mBluetoothGatt?.disconnect()
    }
    fun displayGattServices(){
        Log.d("DeviceCommunication", "display start!!!")
        val services = supportedGattServices
        if (services != null) {
            var uuid: String?
            val unknownServiceString: String = resources.getString(R.string.unknown_service)
            val unknownCharaString: String = resources.getString(R.string.unknown_characteristic)
            val gattServiceData: MutableList<HashMap<String, String>> = mutableListOf() //서비스 데이터(이름, uuid) 리스트
            val gattCharacteristicData: MutableList<ArrayList<HashMap<String, String>>> =
                mutableListOf() //특성 데이터(이름, uuid) 리스트
            mGattCharacteristics = mutableListOf()  //특성 객체 리스트
            // Loops through available GATT Services.
            services.forEach { gattService ->
                val currentServiceData = HashMap<String, String>()
                uuid = gattService.uuid.toString()
                currentServiceData["NAME"] = SampleGattAttributes.lookup(uuid, unknownServiceString)
                currentServiceData["UUID"] = uuid!!
                if(uuid!! == SCBT_SERVICE_UUID){
                    Log.d("DeviceCommunication",
                        "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                                "- Service NAME:${currentServiceData["NAME"]}, UUID:${currentServiceData["UUID"]}")
                }

                gattServiceData += currentServiceData

                val gattCharacteristicGroupData: ArrayList<HashMap<String, String>> = arrayListOf()
                val gattCharacteristics = gattService.characteristics
                val charas: MutableList<BluetoothGattCharacteristic> = mutableListOf()

                // Loops through available Characteristics.
                gattCharacteristics.forEach { gattCharacteristic ->
                    charas += gattCharacteristic
                    val currentCharaData: HashMap<String, String> = hashMapOf()
                    uuid = gattCharacteristic.uuid.toString()
                    currentCharaData["NAME"] = SampleGattAttributes.lookup(uuid, unknownCharaString)
                    currentCharaData["UUID"] = uuid!!

                    if(uuid!! == SCBT_CHARA_UUID){
                        Log.d("DeviceCommunication",
                            "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                                    "- Characteristic NAME:${currentCharaData["NAME"]}, UUID:${currentCharaData["UUID"]}" +
                                    "VALUE:${gattCharacteristic.value}")
                    }

                    gattCharacteristicGroupData += currentCharaData
                }
                mGattCharacteristics.add(charas)
                gattCharacteristicData += gattCharacteristicGroupData
            }
        }
    }

    companion object {
        const val TAG = "BluetoothLeService"

        const val SCBT_SERVICE_UUID = "0000aaa0-0000-1000-8000-00805f9b34fb"
        const val SCBT_CHARA_UUID = "00001234-0000-1000-8000-00805f9b34fb"
        const val STATE_DISCONNECTED = 0
        const val STATE_CONNECTING = 1
        const val STATE_CONNECTED = 2
        const val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
        const val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"
        val UUID_DATA_NOTIFY = UUID.fromString("00001234-0000-1000-80000-00805f9b34fb");

        var connectionState = STATE_DISCONNECTED

        private var mBluetoothManager : BluetoothManager? = null
        private var mBluetoothAdapter : BluetoothAdapter? = null
        private var mBluetoothDeviceAddress: String? = null
        private var mBluetoothGatt: BluetoothGatt? = null
        private var mConnectionState = STATE_DISCONNECTED
        val supportedGattServices: List<BluetoothGattService>?
            get() = if (mBluetoothGatt == null) null else mBluetoothGatt!!.services

        var mGattCharacteristics = mutableListOf<MutableList<BluetoothGattCharacteristic>>()

    }
}
