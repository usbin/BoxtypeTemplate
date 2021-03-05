package com.example.boxtypetemplate

import android.content.Context
import android.util.Log

class PrintLog {
    companion object{
        fun full(context : Context, tag: String, function : String, log : String){
            Log.d(tag, "${context::class.java.simpleName}:$function() - $log")
        }
    }
}


/*
Log.d("DeviceConnection",
"${this.javaClass.simpleName}:${object{}.javaClass.enclosingMethod?.name}() " +
"- Connecting to device : ${rvAdapter?.selectedItem?.name}")*/
