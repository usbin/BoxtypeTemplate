package com.example.boxtypetemplate

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_device.*


class DeviceActivity : AppCompatActivity() {

    val BLUETOOTH_DISCONNECTED = 1
    val BLUETOOTH_CONNECTING = 2
    val BLUETOOTH_CONNECTED = 3
    var connectState = BLUETOOTH_DISCONNECTED
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        setSupportActionBar(findViewById(R.id.toolbar_device))

        btn_bluetooth_state.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN ->{
                    btn_bluetooth_state.background = ContextCompat.getDrawable(this, R.drawable.circle_background_down)
                }
                MotionEvent.ACTION_UP ->{
                    btn_bluetooth_state.background = ContextCompat.getDrawable(this, R.drawable.circle_background)
                }
            }
            false
        }
        btn_bluetooth_state.setOnClickListener {
            when(connectState){
                BLUETOOTH_DISCONNECTED -> {
                    connectState = BLUETOOTH_CONNECTING
                    tv_bluetooth_state.text = getString(R.string.connecting_upper)
                    btn_bluetooth_state.setImageResource(R.drawable.ic_baseline_bluetooth_24_searching)
                    //블루투스 연결 과정 . . .
                    //나중에 텍스트를 여기가 아니라 실제 동작부분에서 구현할 것.

                }
                BLUETOOTH_CONNECTING -> {
                    connectState = BLUETOOTH_DISCONNECTED
                    tv_bluetooth_state.text = getString(R.string.disconnected_upper)
                    btn_bluetooth_state.setImageResource(R.drawable.ic_baseline_bluetooth_24)
                    //블루투스 연결 과정 중지, 처음으로 돌아감.
                    //스테이터스 변경은 실제 동작 함수에서!
                }
                BLUETOOTH_CONNECTED -> {
                    connectState = BLUETOOTH_DISCONNECTED
                    tv_bluetooth_state.text = getString(R.string.disconnected_upper)
                    btn_bluetooth_state.setImageResource(R.drawable.ic_baseline_bluetooth_24_disconnected)
                    //블루투스 연결 해제
                    //스테이터스 변경은 실제 동작 함수에서!
                }
            }
        }
    }
}