package com.example.boxtypetemplate.device

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.boxtypetemplate.R
import kotlinx.android.synthetic.main.dialog_device_list.view.*
import kotlinx.android.synthetic.main.rvitem_device_list.view.*

class DeviceListAdapter(dialog : View) : RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {
    private var data = ArrayList<BluetoothDevice>()
    var dialog = dialog
    var selectedItem : BluetoothDevice? = null
        private set

    override fun getItemCount(): Int= data.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val listener = View.OnClickListener {
            dialog.btn_device_list_confirm?.isEnabled = true
            dialog.btn_device_list_confirm?.setTextColor(ContextCompat.getColor(dialog.context, R.color.colorAccent))
            dialog.tv_dialog_list_label.text = "${dialog.resources.getText(R.string.device_list_selected)} : ${data[position].name}"
            //selectedItem 설정
            selectedItem = data[position]
        }
        holder.bind(listener, item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.rvitem_device_list, parent, false)
        return ViewHolder(inflatedView)
    }

    fun addDevice(device: BluetoothDevice){
        if(!data.contains(device)){
            data.add(device)
            Log.d("device", "addDevice : ${device.name}");
        }

    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val view = view;
        fun bind(listener : View.OnClickListener, item: BluetoothDevice){
            //다이얼로그(dialog)<리사이클러뷰<아이템(view)
            //아이템을 클릭하면 다이얼로그 위의 버튼이 활성화되고 색깔이 들어옴.
            view.setOnClickListener(listener)
            view.tv_device_name.text = item.name
            view.tv_device_id.text = item.address
        }
    }

}