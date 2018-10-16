package com.example.android.zoomlayouttest

import android.view.View
import android.widget.SeekBar

//https://www.andreasjakl.com/kotlin-recyclerview-for-high-performance-lists-in-android/
//https://www.andreasjakl.com/recyclerview-kotlin-style-click-listener-android/


class RVBedData(var name: String, val rvBedID: Int, val bedColor: Int){


    var daysOfWeek = arrayListOf(false,false,false,false,false,false,false)
    var amPm = arrayListOf(0,0,0,0,0,0,0)
    var waterLevel = 1
    var position = 0

}