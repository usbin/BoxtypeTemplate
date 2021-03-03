package com.example.boxtypetemplate.management

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.boxtypetemplate.R


class FullScreenVideoActivity : AppCompatActivity() {
    private var videoView: VideoView? = null
    private var mediaController: MediaController? = null
    private var videoData : VideoData = VideoData("","")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_media)
        videoData.path = intent.getStringExtra("path")!!
        videoData.title = intent.getStringExtra("title")!!
        videoView = findViewById(R.id.videoview_fullscreen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar!!.hide()
        val videoUri =
            Uri.parse(videoData?.path)
        videoView?.setVideoURI(videoUri)
        mediaController = MediaController(this)
        mediaController?.setAnchorView(videoView)
        videoView?.setMediaController(mediaController)

        videoView?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent()
        intent.putExtra("savedTime", videoView?.currentPosition)
        setResult(0, intent)
    }
}