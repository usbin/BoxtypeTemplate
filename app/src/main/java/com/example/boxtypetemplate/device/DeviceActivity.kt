package com.example.boxtypetemplate.device

import android.Manifest
import android.app.ActivityManager
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
import android.widget.SimpleExpandableListAdapter
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
    var localConnectionState = BluetoothLeService.STATE_DISCONNECTED
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
    private val serviceConnection : ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service : IBinder){
            bluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            Log.d("DeviceConnection", "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                    "- Service created.}")

            if(!bluetoothLeService?.initialize()!!){
                Log.e(BluetoothLeService.TAG, "Unable to initialize Bluetooth")

            }
            if(deviceAddress != null){
                //deviceAdress: Confirm 버튼 누를 때 초기화.
                bluetoothLeService?.connect(deviceAddress)
            }


        }
        override fun onServiceDisconnected(componentName: ComponentName){
            bluetoothLeService = null
        }
    }
    //브로드캐스트가 돌아올 때 실행.
    private val gattUpdateReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    connected = true
                    loadingDialog?.dismiss()
                    updateConnectionState(BluetoothLeService.STATE_CONNECTED)
                    bluetoothAdapter?.stopLeScan(leScanCallback)
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    connected = false
                    loadingDialog?.dismiss()
                    updateConnectionState(BluetoothLeService.STATE_DISCONNECTED)
                }
                BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED -> {
                    Log.d("DeviceConnection", "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                            "- Service discover broadcast received.")
                    bluetoothLeService?.displayGattServices()
                }

            }
        }
    }

    val autoStopConnecting = Runnable{
        Log.d("Thread", "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}()" +
                " - connecting timeout. - ${CONNECTION_PERIOD}")

        connected = false
        updateConnectionState(BluetoothLeService.STATE_DISCONNECTED);
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

        //권한 체크
        checkPermission()

        //페어링 목록 로드
        Log.d("DeviceSearch", "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() ")
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceMacAddress = device.address

            Log.d("DeviceSearch", "name: ${deviceName}, address : $deviceMacAddress")
            //나중에 여기에 페어링 되어있으면 자동 연결하고 버튼 상태 바꾸는 것도 추가해야함!
        }

        bluetoothAdapter?.stopLeScan(leScanCallback)
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        //현재 연결된 상태인지 체크.
        if(isBleServiceRunning()){
            Intent(this, BluetoothLeService::class.java).also { intent ->
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
            Log.d("DeviceSearch", "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                    "- already connected!")

            updateConnectionState(BluetoothLeService.STATE_CONNECTED)

        } else{
            val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
            Log.d("DeviceSearch",
                "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                        "- Previous connection not found. Creating new bind ... ${bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)}")

        }




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
            when(localConnectionState){
                //연결하기
                BluetoothLeService.STATE_DISCONNECTED -> {

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
                        Log.d("DeviceConnection",
                            "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                                    "- Connecting to device : ${rvAdapter?.selectedItem?.name}")
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
                BluetoothLeService.STATE_CONNECTING -> {
                    updateConnectionState(BluetoothLeService.STATE_CONNECTING)
                    tv_bluetooth_state.text = getString(R.string.disconnected_upper)
                    btn_bluetooth_state.setImageResource(R.drawable.ic_baseline_bluetooth_24)

                    disconnect()
                    //블루투스 연결 과정 중지, 처음으로 돌아감.
                    //스테이터스 변경은 실제 동작 함수에서!
                }
                //연결 끊기
                BluetoothLeService.STATE_CONNECTED -> {

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
    private fun isBleServiceRunning() : Boolean{
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for(service in manager.getRunningServices(Int.MAX_VALUE)){
            val serviceName = BluetoothLeService.javaClass.name.split("$")[0]
            if(serviceName == service.service.className
                &&BluetoothLeService.connectionState == BluetoothLeService.STATE_CONNECTED){
                return true;
            }
        }
        return false;
    }
    private fun scanLeDevice(enable: Boolean){
        when (enable){
            true -> {
                val uuidList = arrayOf(UUID.fromString(BluetoothLeService.SCBT_SERVICE_UUID))
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

        Log.d("DeviceConnection",
            "${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
                    "- Null checking - bluetoothLeService is null :${bluetoothLeService == null}")

        if(bluetoothLeService!=null){
            bluetoothLeService!!.connect(deviceAddress)
            updateConnectionState(BluetoothLeService.STATE_CONNECTING)
            //bluetoothGatt = rvAdapter?.selectedItem?.connectGatt(this, false, gattCallback)

            loadingDialog?.show()
        }



    }
    fun disconnect(){
        bluetoothLeService?.disconnect()
    }
    private fun makeGattUpdateIntentFilter() : IntentFilter? {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
        return intentFilter
    }


//    private fun displayGattServices(gattServices: List<BluetoothGattService>?) {
//        if (gattServices == null) return
//        var uuid: String? = null
//        val unknownServiceString = resources.getString(R.string.unknown_service)
//        val unknownCharaString =
//            resources.getString(R.string.unknown_characteristic)
//        val gattServiceData =
//            java.util.ArrayList<HashMap<String, String?>>()
//        val gattCharacteristicData =
//            java.util.ArrayList<java.util.ArrayList<HashMap<String, String?>>>()
//
//        gattCharacteristics =
//            java.util.ArrayList<java.util.ArrayList<BluetoothGattCharacteristic>>()
//
//        // Loops through available GATT Services.
//        for (gattService in gattServices) {
//            val currentServiceData =
//                HashMap<String, String?>()
//            uuid = gattService.uuid.toString()
//            currentServiceData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownServiceString)
//            currentServiceData[LIST_UUID] = uuid
//            gattServiceData.add(currentServiceData)
//            //특성 그룹 데이터
//            val gattCharacteristicGroupData =
//                java.util.ArrayList<HashMap<String, String?>>()
//            //해당 서비스 안의 모든 특성들.
//            val gattCharacteristicsLocal = gattService.characteristics
//            val charas =
//                java.util.ArrayList<BluetoothGattCharacteristic>()
//
//            // Loops through available Characteristics.
//            for (gattCharacteristic in gattCharacteristicsLocal) {
//                charas.add(gattCharacteristic)
//                val currentCharaData =
//                    HashMap<String, String?>()
//                uuid = gattCharacteristic.uuid.toString()
//                currentCharaData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownCharaString)
//                currentCharaData[LIST_UUID] = uuid
//                gattCharacteristicGroupData.add(currentCharaData)
//            }
//            gattCharacteristics.add(charas)
//            gattCharacteristicData.add(gattCharacteristicGroupData)
//        }
//        val gattServiceAdapter = SimpleExpandableListAdapter(
//            this,
//            gattServiceData,
//            android.R.layout.simple_expandable_list_item_2,
//            arrayOf(LIST_NAME, LIST_UUID),
//            intArrayOf(android.R.id.text1, android.R.id.text2),
//            gattCharacteristicData,
//            android.R.layout.simple_expandable_list_item_2,
//            arrayOf(LIST_NAME, LIST_UUID),
//            intArrayOf(android.R.id.text1, android.R.id.text2)
//        )
//        gattServiceList.setAdapter(gattServiceAdapter)
//    }
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
            BluetoothLeService.STATE_CONNECTED -> {
                localConnectionState = BluetoothLeService.STATE_CONNECTED
                tv_bluetooth_state.text = getString(R.string.connected_upper)
                btn_bluetooth_state.setImageResource(R.drawable.ic_baseline_bluetooth_24)

            }
            BluetoothLeService.STATE_CONNECTING -> {
                localConnectionState = BluetoothLeService.STATE_CONNECTING
                tv_bluetooth_state.text = getString(R.string.connecting_upper)
                btn_bluetooth_state.setImageResource(R.drawable.ic_baseline_bluetooth_24_searching)
            }
            BluetoothLeService.STATE_DISCONNECTED -> {
                localConnectionState = BluetoothLeService.STATE_DISCONNECTED
                tv_bluetooth_state.text = getString(R.string.disconnected_upper)
                btn_bluetooth_state.setImageResource(R.drawable.ic_baseline_bluetooth_24_disconnected)

            }

        }
    }
    companion object {

        private const val REQUEST_ENABLE_BT = 1
        private const val PERMISSION_REQUEST_CODE = 100

        private const val SCAN_PERIOD : Long = 10000
        private const val CONNECTION_PERIOD : Long = 10000
    }
}