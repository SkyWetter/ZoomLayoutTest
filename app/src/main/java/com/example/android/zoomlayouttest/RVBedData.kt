package com.example.android.zoomlayouttest

import android.view.View
import android.widget.SeekBar

//https://www.andreasjakl.com/kotlin-recyclerview-for-high-performance-lists-in-android/
//https://www.andreasjakl.com/recyclerview-kotlin-style-click-listener-android/

/**Jamezo, not sure if there's a better way to deal with this, but I'm using the RVBedData class
to pass information about the bed to the specific textview associated with that bed. In this case,
 the value for bedColor is assigned when a bed is created (MainActivity, line 496). !! <-- are required

 To add a new parameter you need to add it here as well as AddBedToRV function parameters (Main Activity Line 472),
 and then in all references to that function

 */

data class RVBedData(val name: String, val rvBedID: Int,val bedColor: Int){

 var waterLevel = 1
}