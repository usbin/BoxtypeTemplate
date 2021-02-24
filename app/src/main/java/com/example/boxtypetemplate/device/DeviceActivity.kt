package com.example.boxtypetemplate.device

import android.Manifest
import android.app.Dialog
import android.bluetooth.*
import android.content.*
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxtypetemplate.R
import kotlinx.android.synthetic.main.activity_device.*
import kotlinx.android.synthetic.main.dialog_device_list.view.*
import java.util.*
import kotlin.collections.ArrayList


class DeviceActivity : AppCompatActivity(){
    //버튼 이미지 상태
    var connectionState = Companion.BLUETOOTH_DISCONNECTED
    //블루투스 연결과 동기화할 변수
    var connected = false
    var mScanning : Boolean = false
    private val loadingDialog: LoadingDialog? by lazy(LazyThreadSafetyMode.NONE){
        LoadingDialog(this)
    }
    var deviceAddress : String? = null
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE){
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private var bluetoothLeService : BluetoothLeService? = null
    private val handler = Handler()
    private var bluetoothGatt: BluetoothGatt? = null
//    private val gattUpdateReceiver = object : BroadcastReceiver(){
//        private lateinit var bluetoothLeService: BluetoothLeService
//        override fun onReceive(context: Context, intent: Intent) {
//            val action = intent.action
//            when (action){
//                ACTION_GATT_CONNECTED -> {
//                    connected = true
//                    updateConnectionState(R.string.connected)
//                    (context as? Activity)?.invalidateOptionsMenu()
//                }
//                ACTION_GATT_DISCONNECTED -> {
//                    connected = false
//                    updateConnectionState(R.string.disconnected)
//                    (context as? Activity)?.invalidateOptionsMenu()
//                    clearUI()
//                }
//                ACTION_GATT_SERVICES_DISCOVERED -> {
//                    // Show all the supported services and characteristics on the
//                    // user interface.
//                    displayGattServices(bluetoothLeService.getSupportedGattServices())
//                }
//                ACTION_DATA_AVAILABLE -> {
//                    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
//                }
//            }
//        }
//    }
//    private val serviceConnection : ServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(componentName: ComponentName, service : IBinder){
//            bluetoothLeService = (service as BluetoothLeService.LocalBinder).service
//            Log.d("connect", "service connected.")
//            if(!bluetoothLeService?.initialize()!!){
//                finish()
//            }
//
//        }
//        override fun onServiceDisconnected(componentName: ComponentName){
//            bluetoothLeService = null
//        }
//    }
    //응답이 돌아올 때 실행.
    private val gattUpdateReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    connected = true
                    loadingDialog?.dismiss()
                    updateConnectionState(BLUETOOTH_CONNECTED)

                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    connected = false
                    loadingDialog?.dismiss()
                    updateConnectionState(BLUETOOTH_DISCONNECTED)

                }
            }
        }
    }
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
                    broadcastUpdate(intentAction)
                    Log.i(TAG, "Connected to GATT server.")
                    Log.i(TAG, "Attempting to start service discovery: " +
                            bluetoothGatt?.discoverServices())
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    intentAction = ACTION_GATT_DISCONNECTED
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
    val autoStopConnecting = Runnable{
        Log.d("thread", "connecting timeout. - ${CONNECTION_PERIOD}")
        connected = false
        updateConnectionState(STATE_DISCONNECTED);
        loadingDialog?.dismiss()
    }
    private var rvAdapter : DeviceListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        setSupportActionBar(findViewById(R.id.toolbar_device))





    }

    override fun onResume() {
        super.onResume()
        //현재 연결된 상태인지 체크.
        //페어링 목록 로드
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceMacAddress = device.address
            Log.d("device", "name: ${deviceName}, address : $deviceMacAddress")
            //나중에 여기에 페어링 되어있으면 자동 연결하고 버튼 상태 바꾸는 것도 추가해야함!
        }
//        val mDevices = bluetoothAdapter?.getProfileConnectionState()
//        for(i in 0 until mDevices?.size!!){
//            Log.d("device", "!!!!!!!! +${mDevices?.iterator().next()}")
//        }
//        val nDevice = bluetoothAdapter?.getRemoteDevice("00:35:FF:1F:75:E9")
//        Log.d("device", "HM10 connected state - ${nDevice?.bondState}")


//        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
//            &&mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET
//            ) == STATE_CONNECTED)
        bluetoothAdapter?.stopLeScan(leScanCallback)
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        checkPermission()

        // 클릭 깜빡임 효과
        btn_bluetooth_state.setOnTouchListener(fun(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    btn_bluetooth_state.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.circle_background_down
                    )
                }
                MotionEvent.ACTION_UP -> {
                    btn_bluetooth_state.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.circle_background
                    )
                }
            }
            return false
        })

        //연결 버튼 리스너
        btn_bluetooth_state.setOnClickListener {
            when(connectionState){
                //연결하기
                BLUETOOTH_DISCONNECTED -> {
                    Log.d("button", "connect button clicked")

                    //블루투스 연결 과정 . . .
                    //나중에 텍스트를 여기가 아니라 실제 동작부분에서 구현할 것.

                    //디바이스 선택 다이얼로그 띄움.
                    val dialog = Dialog(this)
                    val dialogView = layoutInflater.inflate(R.layout.dialog_device_list, null)
                    val dialogList = dialogView.findViewById<RecyclerView>(R.id.rv_device_list)

                    //리사이클러뷰 어댑터 초기화.
                    //어댑터 내에서 다이얼로그뷰 위의 버튼을 조작해야 하므로 인자로 다이얼로그뷰를 줌.
                    rvAdapter = DeviceListAdapter(dialogView)
                    //리사이클러뷰 초기 설정.
                    dialogList.layoutManager = LinearLayoutManager(this)
                    dialogList.adapter = rvAdapter

                    //확인 버튼 설정
                    dialogView.btn_device_list_confirm.isEnabled = false;
                    dialogView.btn_device_list_confirm.setTextColor(ContextCompat.getColor(this, R.color.selectedGray))
                    dialogView.btn_device_list_confirm.setOnTouchListener(BlinkingBtnEffectListener())  //깜빡임 효과
                    dialogView.btn_device_list_confirm.setOnClickListener {
                        //Confirm 버튼 누르면 연결 동작 시작.
                        Log.d("device", "device selected - ${rvAdapter?.selectedItem?.name}")
                        deviceAddress = rvAdapter?.selectedItem?.address!!
                        connect()
                        dialog.dismiss()
                    }

                    //취소 버튼 설정
                    dialogView.btn_device_list_cancel.setOnTouchListener(BlinkingBtnEffectListener())
                    dialogView.btn_device_list_cancel.setOnClickListener {
                        dialog.dismiss()

                    }
                    dialog.setContentView(dialogView)
                    val params = dialog.window?.attributes
                    params?.width = WindowManager.LayoutParams.MATCH_PARENT
                    params?.height = WindowManager.LayoutParams.WRAP_CONTENT
                    dialog.window?.attributes = params as WindowManager.LayoutParams

                    dialog.show()
                    scanLeDevice(true)

                }
                //연결 끊기(동작 취소)
                BLUETOOTH_CONNECTING -> {
                    Log.d("button", "disconnect button clicked(connecting)")
                    connectionState = BLUETOOTH_DISCONNECTED
                    tv_bluetooth_state.text = getString(R.string.disconnected_upper)
                    btn_bluetooth_state.setImageResource(R.drawable.ic_baseline_bluetooth_24)

                    disconnect()
                    //블루투스 연결 과정 중지, 처음으로 돌아감.
                    //스테이터스 변경은 실제 동작 함수에서!
                }
                //연결 끊기
                BLUETOOTH_CONNECTED -> {
                    Log.d("button", "disconnect button clicked")

                    disconnect()
                    //블루투스 연결 해제
                    //스테이터스 변경은 실제 동작 함수에서!
                }
            }
        }

    }

    //----------------------블루투스 관련 권한 체크 --------------------//
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            //블루투스 활성화 요청을 거부했을 때
            REQUEST_ENABLE_BT -> {
                if(resultCode!=RESULT_OK){
                    finish()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            //위치 정보 활성화 요청을 거부했을 때
            PERMISSION_REQUEST_CODE ->{
                for(result in grantResults){
                    result.takeIf{ it != PackageManager.PERMISSION_GRANTED}?.apply{
                        Toast.makeText(this@DeviceActivity, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
    private fun checkPermission(){
        //블루투스 활성화 요청
        bluetoothAdapter?.takeIf{ !it.isEnabled }?.apply{
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        //권한 체크
        var permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        var arrayPermission = ArrayList<String>()
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            arrayPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if(arrayPermission.size > 0){
            ActivityCompat.requestPermissions(this, arrayPermission.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    //--------------------- 블루투스 동작 -------------------//
    private fun scanLeDevice(enable: Boolean){
        when (enable){
            true -> {
                val uuidList = arrayOf(UUID.fromString(SCBT_SERVICE_UUID))
                // 설정된 SCAN_PERIOD 후 스캔 작업 중단
                handler.postDelayed({
                    mScanning = false
                    bluetoothAdapter?.stopLeScan(leScanCallback)
                }, SCAN_PERIOD)
                mScanning = true
                bluetoothAdapter?.startLeScan(uuidList, leScanCallback)
            }
            false -> {
                mScanning = false
                bluetoothAdapter?.stopLeScan(leScanCallback)
            }
        }
    }
    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecored ->
        runOnUiThread{
            rvAdapter?.addDevice(device)
            rvAdapter?.notifyDataSetChanged()
        }
    }

    private fun connect(){
        loadingDialog?.show()
        connectionState = BLUETOOTH_CONNECTING
        tv_bluetooth_state.text = getString(R.string.connecting_upper)
        btn_bluetooth_state.setImageResource(R.drawable.ic_baseline_bluetooth_24_searching)
        bluetoothGatt = rvAdapter?.selectedItem?.connectGatt(this, false, gattCallback)

//        val gattServiceIntent = Intent(this, BluetoothLeService::class.java);
//        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
//        //bluetoothGatt = rvAdapter?.selectedItem?.connectGatt(this, false, gattCallback)
//
//        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
//        if(bluetoothLeService != null){
//            Log.d("connect", "bluetooth connecting started")
//            var result = bluetoothLeService?.connect(deviceAddress)
//
//        }
    }
    fun disconnect(){
        bluetoothGatt?.disconnect()
    }
    private fun makeGattUpdateIntentFilter() : IntentFilter? {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_GATT_CONNECTED)
        intentFilter.addAction(ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(ACTION_DATA_AVAILABLE)
        return intentFilter
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
    //------------------ UI 제어 ------------------//
    class BlinkingBtnEffectListener : View.OnTouchListener{
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action){
                MotionEvent.ACTION_DOWN -> {
                    (v as Button).setTextColor(ContextCompat.getColor(v.context, R.color.selectedGray))
                }
                MotionEvent.ACTION_UP -> {
                    (v as Button).setTextColor(ContextCompat.getColor(v.context, R.color.colorAccent))
                }
            }
            return false
        }
    }
    fun updateConnectionState(state : Int){
        when(state){
            BLUETOOTH_CONNECTED -> {
                connectionState = BLUETOOTH_CONNECTED
                tv_bluetooth_state.text = getString(R.string.connected_upper)
                btn_bluetooth_state.setImageResource(R.drawable.ic_baseline_bluetooth_24)

            }
            BLUETOOTH_DISCONNECTED -> {
                connectionState = BLUETOOTH_DISCONNECTED
                tv_bluetooth_state.text = getString(R.string.disconnected_upper)
                btn_bluetooth_state.setImageResource(R.drawable.ic_baseline_bluetooth_24_disconnected)

            }
        }
    }
    companion object {
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

        const val SCBT_SERVICE_UUID = "0000aaa0-0000-1000-8000-00805f9b34fb"
        private const val BLUETOOTH_DISCONNECTED = 1
        private const val BLUETOOTH_CONNECTING = 2
        private const val BLUETOOTH_CONNECTED = 3

        private const val REQUEST_ENABLE_BT = 1
        private const val PERMISSION_REQUEST_CODE = 100

        private const val SCAN_PERIOD : Long = 1000
        private const val CONNECTION_PERIOD : Long = 10000
    }
}