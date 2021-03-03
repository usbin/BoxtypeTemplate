package com.example.boxtypetemplate.management

//import android.R
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.view.Gravity
//import android.view.View
//import android.widget.FrameLayout
//import android.widget.ImageButton
//import android.widget.MediaController
//
//
//class FullScreenMediaController(context: Context?) : MediaController(context) {
//    private var btn_fullScreen: ImageButton? = null
//    private var isFullScreen: Boolean? = null
//    override fun setAnchorView(view: View?) {
//        super.setAnchorView(view)
//
//        //image button for full screen to be added to media controller
//        btn_fullScreen = ImageButton(super.getContext())
//        val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
//            LayoutParams.WRAP_CONTENT,
//            LayoutParams.WRAP_CONTENT
//        )
//        params.gravity = Gravity.RIGHT
//        params.rightMargin = 10
//        addView(btn_fullScreen, params)
//
//        //fullscreen indicator from intent
//        isFullScreen = (context as Activity).intent.getBooleanExtra("isFullScreen", false)
//        if (isFullScreen!!) {
//            btn_fullScreen!!.setImageResource(R.drawable.btn_minus)
//            btn_fullScreen!!.setOnClickListener {
//                (context as Activity).finish()
//            }
//        } else {
//            btn_fullScreen!!.setImageResource(R.drawable.btn_plus)
//            btn_fullScreen!!.setOnClickListener {
//                val intent = Intent(context, FullScreenVideoActivity::class.java)
//                intent.putExtra("fullScreenInd", "y")
//                (getContext() as Activity).startActivityForResult(intent, FitVideoFragment.FULLSCREEN_REQUEST)
//            }
//        }
//
//    }
//}