package com.example.boxtypetemplate.management


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaController2
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.boxtypetemplate.R
import kotlinx.android.synthetic.main.rvitem_device_list.view.*
import kotlinx.android.synthetic.main.rvitem_management_video.view.*
import java.security.AccessController.getContext

class ManagementVideoAdapter : RecyclerView.Adapter<ManagementVideoAdapter.ViewHolder>(){
    private var data : ArrayList<VideoData> = arrayListOf(VideoData("test", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))
    var context : Context? = null
    override fun getItemCount(): Int= data.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val listener = View.OnClickListener {
            //아이템 클릭 시 리스너

        }
        holder.bind(listener, item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.rvitem_management_video, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val view = view;
        private var mediaController : MediaController?=null
        private var videoView: VideoView? =null

        fun bind(listener : View.OnClickListener, item: VideoData){
            view.imageview_management.setOnClickListener {
                val intent = Intent(context, FullScreenVideoActivity::class.java)
                intent.putExtra("path", item.path)
                intent.putExtra("title", "test")
                context?.startActivity(intent)
            }
        }
    }


}