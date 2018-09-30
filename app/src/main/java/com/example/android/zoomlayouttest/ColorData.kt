package com.example.android.zoomlayouttest

import android.graphics.Color
import java.lang.Math

class ColorData{

    /**
     * If adding a new hardcoded color in color list, make sure to also add the color to the listOfColors list
     */



    /** Color List*/
    companion object {

        val deselected  = Color.argb(255,255,255,255)
        val selected = Color.argb(255,9,135,109)
        val adjacent = Color.argb(255,0,0,0)
        val turret = Color.argb(255,255,0,0)
        val listOfColors = mutableListOf(deselected, selected, adjacent,turret)

        /**
         * random bed color generator -- simplified version, could be replaced with hardcoded colors
         */

        fun randomBedColor():Int{
            var newColor : Int? = null

            while(newColor == null || listOfColors.contains(newColor)){

                var a = (0..255).shuffled().first()
                var r = (0..255).shuffled().first()
                var g = (0..255).shuffled().first()
                var b = (0..255).shuffled().first()

                newColor = Color.argb(a,r,b,g)

            }
            listOfColors.add(newColor)
            return newColor
        }
    }

}