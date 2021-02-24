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
import java.util.*

private val TAG = BluetoothLeService::class.java.simpleName
private const val STATE_DISCONNECTED = 0
private const val STATE_CONNECTING = 1
private const val STATE_CONNECTED = 2
const val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
const val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
const val ACTION_GATT_SERVICES_DISCOVERED =
    "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
const val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
const val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"
val STBT_SERVICE_UUID = UUID.fromString(DeviceActivity.SCBT_SERVICE_UUID)

// A service that interacts with the BLE device via the Android BLE API.
class BluetoothLeService() : Service() {

    private var connectionState = STATE_DISCONNECTED

    private var mBluetoothManager : BluetoothManager? = null
    private var mBluetoothAdapter : BluetoothAdapter? = null
    private var mBluetoothDeviceAddress: String? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mConnectionState = STATE_DISCONNECTED

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
                    Log.i(TAG, "Connected to GATT server.")
                    Log.i(TAG, "Attempting to start service discovery: " +
                            mBluetoothGatt?.discoverServices())
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    intentAction = ACTION_GATT_DISCONNECTED
                    connectionState = STATE_DISCONNECTED
                    Log.i(TAG, "Disconnected from GATT server.")
                    broadcastUpdate(intentAction)
                }
            }
        }

        // New services discovered
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                else -> Log.w(TAG, "onServicesDiscovered received: $status")
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
                }
            }
        }
    }
    private val mGattCallback : BluetoothGattCallback = object : BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            val intentAction : String
            if(newState == BluetoothProfile.STATE_CONNECTED){
                intentAction = ACTION_GATT_CONNECTED
                mConnectionState = STATE_CONNECTED
                broadcastUpdate(intentAction)
                Log.i(TAG, "Connected to GATT server.")
                // Attempts to discover services after successful connection.
                Log.i(
                    TAG, "Attempting to start service discovery:" +
                            mBluetoothGatt!!.discoverServices()

                )
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED
                mConnectionState = STATE_DISCONNECTED
                Log.i(
                    TAG,
                    "Disconnected from GATT server."
                )
                broadcastUpdate(intentAction)
            }
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

        when(characteristic.uuid){
            STBT_SERVICE_UUID -> {
                Log.d("connect", "connected.")
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
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback, BluetoothDevice.TRANSPORT_LE)
        mBluetoothDeviceAddress = address
        mConnectionState = STATE_CONNECTING
        return true
    }
    fun disconnect(){
        mBluetoothGatt?.disconnect()
    }

    companion object {
        const val TAG = "BluetoothLeService"

        const val STATE_DISCONNECTED = 0
        const val STATE_CONNECTING = 1
        const val STATE_CONNECTED = 2
        const val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
        const val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"


    }
}

class BluetoothHandler(){

}