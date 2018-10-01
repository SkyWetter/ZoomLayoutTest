package com.example.android.zoomlayouttest

import android.graphics.Color
import java.lang.Math

class ColorData {

    /**
     * If adding a new hardcoded color in color list, make sure to also add the color to the listOfColors list
     *
     *
     */



    /** Color List*/
    companion object {

        val deselected  = Color.argb(255,255,255,255)
        val selected = Color.argb(255,9,135,109)
        val adjacent = Color.argb(255,0,0,0)
        val turret = Color.argb(255,255,0,0)
        val listOfColors = mutableListOf(deselected, selected, adjacent,turret)
        var nextBedColor : Int? = null


        /** Color ranges --> Used in random color generation, keeping colors within the bounds described
         * Color range must be added to listColorRange to be used in generation*/
        private var color1 = ColorRange(63,189,0,67,145,255)
        private var color2 = ColorRange(206,255,100,150,0,33)
        private var color3 = ColorRange(189,215,35,87,240,255)

        private var listColorRange = mutableListOf<ColorRange>(color1,color2,color3)
        private var colorRangeIndex = 0

        /**
         * random bed color generator -- simplified version, could be replaced with hardcoded colors
         */



        fun newRandomBedColor(){


            var newColor : Int? = null
            var fromRange = listColorRange[colorRangeIndex]

            while(newColor == null || listOfColors.contains(newColor)){



                var a = 255
                var r = (fromRange.rMin..fromRange.rMax).shuffled().first()
                var g = (fromRange.gMin..fromRange.gMax).shuffled().first()
                var b = (fromRange.bMin..fromRange.bMax).shuffled().first()

                newColor = Color.argb(a,r,b,g)

            }

            colorRangeIndex += 1

            if(colorRangeIndex >= listColorRange.size){ colorRangeIndex = 0 }

            listOfColors.add(newColor)
            nextBedColor = newColor


        }
    }

    data class ColorRange(val rMin:Int, val rMax :Int, val gMin : Int, val gMax : Int, val bMin : Int, val bMax : Int)


}