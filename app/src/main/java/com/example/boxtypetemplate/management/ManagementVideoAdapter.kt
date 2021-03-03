package com.example.boxtypetemplate.management


import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.boxtypetemplate.R
import kotlinx.android.synthetic.main.rvitem_device_list.view.*
import kotlinx.android.synthetic.main.rvitem_management_video.view.*

class ManagementVideoAdapter : RecyclerView.Adapter<ManagementVideoAdapter.ViewHolder>(){
    private var data : ArrayList<VideoData> = arrayListOf(VideoData("test", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))

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

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), SurfaceHolder.Callback {
        private val view = view;
        private var mediaPlayer : MediaPlayer? = null
        private var surfaceHolder : SurfaceHolder? = null
        private var item : VideoData?=null

        fun bind(listener : View.OnClickListener, item: VideoData){

            mediaPlayer = MediaPlayer()
            this.item = item
            surfaceHolder = view.surfaceview_management.holder
            surfaceHolder?.addCallback(this)
            //다이얼로그(dialog)<리사이클러뷰<아이템(view)
            //아이템을 클릭하면 다이얼로그 위의 버튼이 활성화되고 색깔이 들어옴.
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {

            try {
                val title = item?.title
                val path = item?.path
                mediaPlayer!!.setDataSource(path)

                //mediaPlayer.setVolume(0, 0); //볼륨 제거
                mediaPlayer!!.setDisplay(surfaceHolder) // 화면 호출
                mediaPlayer!!.prepare() // 비디오 load 준비

                //mediaPlayer.setOnCompletionListener(completionListener); // 비디오 재생 완료 리스너
                mediaPlayer!!.start()
            } catch (e: Exception) {
                Log.e("MyTag", "surface view error : " + e.message)
            }
        }
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            mediaPlayer?.release()
        }


    }


}