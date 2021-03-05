package com.example.boxtypetemplate.video



import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.boxtypetemplate.R
import kotlinx.android.synthetic.main.rvitem_video_video.view.*
import java.io.InputStream


class videoVideoAdapter : RecyclerView.Adapter<videoVideoAdapter.ViewHolder>(){
    private var data : ArrayList<VideoData> = arrayListOf(
        VideoData("test", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
        VideoData("test2", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
        VideoData("test3", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
        VideoData("test4", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
        VideoData("test5", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
        VideoData("test6", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))
    var context : Context? = null
    override fun getItemCount(): Int= data.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.bind(item, position)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.rvitem_video_video, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val view = view;//rvitem view

        fun bind(item: VideoData, index: Int){
            view.btn_video_play.setOnClickListener {
                val intent = Intent(context, FullScreenVideoActivity::class.java)
                intent.putExtra("path", item.path)
                intent.putExtra("title", "test")
                context?.startActivity(intent)
            }
            view.tv_fit_videoTitle.text = data[index].title

            val assetManager: AssetManager? = context?.assets
            var inputStream: InputStream? = null
            try {
                inputStream = assetManager?.open("sample_image.jpg")
                var bitmap = BitmapFactory.decodeStream(inputStream)
                view.imageview_video.setImageBitmap(bitmap)
                inputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}