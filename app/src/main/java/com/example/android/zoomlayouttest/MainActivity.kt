/*   ZoomLayout and Garden Grid Population Test

September 17th, 2018 -- David Barry
Added basic ZoomLayout functionality
 - Small representative grid of 13 squares can be 'pinch-zoomed' and scrolled in center of screen
 - Each square can be clicked, activating a button listener function, and showing a 'Toast' message on screen

September 18th, 2018 -- David Barry
Added function for creating a list of known squares (called createSquareList)
 - Function takes an int representing total # of squares on grid and a blank, mutable list to hold square names

Sept 19th, 2018 -- James Gillis
Added constraints for zoomLayout view
Added id tags for top and bottom menu bar
Fixed constraints for top and bottom menu bar

Sept. 20th, 2018 -- David Barry
Added test functionality (push button to zoom) for top button in garden grid area

 */


package com.example.android.zoomlayouttest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.otaliastudios.zoom.ZoomLayout
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val squareList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gardenBedView = findViewById<ZoomLayout>(R.id.zoomLayout)  // zoom

        /* Test Code */

        createSquareList(13,squareList)


        //  createSquareListeners(squareList)
        //sq000.setOnClickListener{ println(squareList)}

        /* Square listeners, output a simple Toast text response. Will be made in code with for loop eventually */



        sq000.setOnClickListener{ zoomCustom(gardenBedView,2.0f,true) }
        sq001.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 1'", Toast.LENGTH_SHORT).show() }
        sq002.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 2'", Toast.LENGTH_SHORT).show() }
        sq003.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 3'", Toast.LENGTH_SHORT).show() }
        sq004.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 4'", Toast.LENGTH_SHORT).show() }
        sq005.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 5'", Toast.LENGTH_SHORT).show() }
        sq006.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 6'", Toast.LENGTH_SHORT).show() }
        sq007.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 7'", Toast.LENGTH_SHORT).show() }
        sq008.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 8'", Toast.LENGTH_SHORT).show() }
        sq009.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 9'", Toast.LENGTH_SHORT).show() }
        sq010.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 10'", Toast.LENGTH_SHORT).show() }
        sq011.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 11'", Toast.LENGTH_SHORT).show() }
        sq012.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 12'", Toast.LENGTH_SHORT).show() }



    }


}
/* Function for populating a list of all squares in garden grid */

fun createSquareList(numberOfSquares: Int,squareList: MutableList<String>)
{

    for(i in 0..(numberOfSquares-1)){

        if(numberOfSquares < 10)
        {
            var tempString: String = "sq00" + i.toString()
            squareList.add(tempString)

        }

        else if(numberOfSquares < 100)
        {
            var tempString: String = "sq0" + i.toString()
            squareList.add(tempString)
        }

        else if(numberOfSquares < 1000)
        {
            squareList.add(i.toString())
        }
    }
}

fun createSquareListeners(listOfSquares: MutableList<String>)
{
    for(squares in listOfSquares)
    {

    }
}

fun zoomCustom(viewToZoom : ZoomLayout, amountToZoom : Float, animateZoom : Boolean )
{
    viewToZoom.zoomTo(amountToZoom,animateZoom)
}
