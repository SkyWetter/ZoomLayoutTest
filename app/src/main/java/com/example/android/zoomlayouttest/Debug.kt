package com.example.android.zoomlayouttest

import android.widget.TextView
import com.example.android.zoomlayouttest.R.id.debugWindow
import kotlinx.android.synthetic.main.activity_main.*

class Debug{
    companion object {


        const val on = false  //Set debug mode (precompile)

        val debugMessageList = mutableListOf<String>()
        var debugListIndex = 0
        var debugListMaxIndex = 0

        fun message(string: String,debugWindow : TextView) {

            if (debugMessageList.size > 0) {
                debugListIndex = debugMessageList.size
                debugListMaxIndex = debugListIndex
            }
            debugMessageList.add("#${debugListIndex}: $string")
            debugWindow.text = "#${debugListIndex}: $string"
        }

        fun nextMessage(debugWindow: TextView) {


            if (debugListIndex < debugListMaxIndex) {
                debugListIndex++
                debugWindow.text = debugMessageList[debugListIndex].toString()

            }
        }

        fun prevMessage(debugWindow: TextView) {
            if (debugListIndex > 0) {
                debugListIndex--
                debugWindow.text = debugMessageList[debugListIndex].toString()
            }
        }

    }

}