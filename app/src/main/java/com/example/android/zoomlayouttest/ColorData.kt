package com.example.android.zoomlayouttest

import android.content.Context
import android.graphics.Color
import java.lang.Math

/**
 * ColorData Class
 *
 * Assign color values to be used in runtime color change operations, create and store
 *
 */


class ColorData {



    /** Color List
     *
     * Assign color vals to be used at runtime. color val must be in Int form
     *
     * */

    companion object {

        // Color values for ui backgrounds
        val uiColor1_dark = Color.argb(255,0,178,72)
        val uiColor1_medium = Color.argb(255,0,229,117)  //Med Green
        val uiColor1_light = Color.argb(255,102,255,166) //Light Green

        val uiColor2_dark = Color.argb(255,0,182,134)
        val uiColor2_medium =  Color.argb(255,29,233,182)
        val uiColor2_light = Color.argb(255,110,255,232)   //Light Blue

        val uiColor_white = Color.argb(255,230,241,235) //Off White
        val uiColor_grey = Color.argb(255,194,216,204)

        val textColor1 = Color.argb(255,0,0,0)  //Black
        val uiInvisible = Color.argb(0,255,255,255)
        val swipeDelete = Color.argb(255,240,241,210)

        val dayButtonOn    = Color.argb(255,66,95,244)
        val dayButtonOff     = Color.argb(255,201,207,242)


        // Color values for squares
        val deselected = uiColor_white

        val selected = uiColor2_medium
        val adjacent = Color.argb(50,110,255,232)   //Light Blue

        val turret = Color.argb(255,255,0,0)
        val listOfColors = mutableListOf(deselected, selected, adjacent,turret)
        var nextBedColor : Int? = null


        /**
         * Color squares using hard-coded values (no random range)
         * Add any color vals above in to the below squareColorList to include it in the rotation for bed colors
         */



        val squareColorList = mutableListOf<Int>(uiColor1_dark, uiColor1_medium, uiColor1_dark, uiColor2_dark, uiColor2_medium, uiColor2_light)

        var nextColorIndex = 0


        /** Color ranges --> Used in random color generation, keeping colors within the bounds described
         * Color range must be added to listColorRange to be used in generation
         *
         *
         * CURRENTLY NOT USED
         * */
        private var color1 = ColorRange(63,189,0,67,145,255)
        private var color2 = ColorRange(206,255,100,150,0,33)
        private var color3 = ColorRange(189,215,35,87,240,255)

        private var listColorRange = mutableListOf<ColorRange>(color1,color2,color3)
        private var colorRangeIndex = 0

        /**
         * random bed color generator -- simplified version, could be replaced with hardcoded colors
         * CURRENTLY NOT USED
         */


        fun newBedColor(){
            nextColorIndex++
            if(nextColorIndex >= squareColorList.size){
                nextColorIndex = 0
            }

            nextBedColor = squareColorList[nextColorIndex]
        }

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

        /** Use to toggle a square completely transparent or completely opaque*/

        fun toggleInvisible(square: MainActivity.Square ){
            if(!square.isInvisible){
                square.button!!.background.alpha = 0
                square.isInvisible = true
            }
            else{
                square.button!!.background.alpha = 255
                square.isInvisible = false
            }
        }
    }

    data class ColorRange(val rMin:Int, val rMax :Int, val gMin : Int, val gMax : Int, val bMin : Int, val bMax : Int)

}