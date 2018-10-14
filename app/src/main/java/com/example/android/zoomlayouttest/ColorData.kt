package com.example.android.zoomlayouttest

import android.graphics.Color

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

        // Color values
        val uiColorLightGreen = Color.parseColor("#4CAF50")
        val uiColorBrightOrange = Color.parseColor("#FF9800")
        val uiColorAmber = Color.parseColor("#FFC107")
        val uiColorPureWhite = Color.parseColor("#FFFFFF")
        val uiColorDarkGreen = Color.parseColor("#388E3C")
        val uiColorLightBlue = Color.parseColor("#03A9F4")
        val uiColorPaleBlue = Color.parseColor("#B3E5FC")
        val uiColorDarkBlue = Color.parseColor("#0288D1")
        val uiColorBlackish = Color.parseColor("#212121")
        val uiColorLightGrey = Color.parseColor("#BDBDBD")


        /** Garden Bed Color Sets*/

        val gardenBedColor500_red = Color.parseColor("#f44336")
        val gardenBedColor500_pink = Color.parseColor("#e91e63")
        val gardenBedColor500_purple = Color.parseColor("#9c27b0")
        val gardenBedColor500_deepPurple = Color.parseColor("#673ab7")
        val gardenBedColor500_indigo = Color.parseColor("#3f51b5")
        val gardenBedColor500_blue = Color.parseColor("#2196f3")
        val gardenBedColor500_lightBlue = Color.parseColor("#03a9f4")
        val gardenBedColor500_cyan = Color.parseColor("#00bcd4")
        val gardenBedColor500_teal = Color.parseColor("#009688")
        val gardenBedColor500_green = Color.parseColor("#4caf50")
        val gardenBedColor500_lightGreen = Color.parseColor("#8bc34a")
        val gardenBedColor500_lime = Color.parseColor("#cddc39")
        val gardenBedColor500_yellow = Color.parseColor("#ffeb3b")
        val gardenBedColor500_amber = Color.parseColor("#ffc107")
        val gardenBedColor500_orange = Color.parseColor("#ff9800")
        val gardenBedColor500_deepOrange = Color.parseColor("#ff5722")


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

        val dayButtonAMPM =  Color.parseColor("#FFC107")
        val dayButtonPM = Color.parseColor("#00796b")
        val dayButtonAM    = Color.parseColor("#536DFE")
        val dayButtonOff     = Color.parseColor("#D32F2F")

        // Objects to be colored
        val deselectedSquare = Color.argb(255,230,241,235)
        val selected = Color.argb(255,29,233,182)
        val adjacentSquare = Color.argb(50,110,255,232)   //Light Blue

        val turret = Color.argb(255,255,0,0)
        private val listOfColors = mutableListOf(deselectedSquare, selected, adjacentSquare,turret)
        var nextBedColor : Int? = null

        /**
         * Color squares using hard-coded values (no random range)
         * Add any color vals above in to the below squareColorList to include it in the rotation for bed colors
         */

        private val squareColorList = mutableListOf<Int>(gardenBedColor500_red, gardenBedColor500_pink,
                gardenBedColor500_purple, gardenBedColor500_deepPurple, gardenBedColor500_indigo, gardenBedColor500_blue,
                gardenBedColor500_lightBlue, gardenBedColor500_cyan, gardenBedColor500_teal, gardenBedColor500_green,
                gardenBedColor500_lightGreen, gardenBedColor500_lime, gardenBedColor500_yellow, gardenBedColor500_amber,
                gardenBedColor500_orange, gardenBedColor500_deepOrange)
        private var nextColorIndex = 0

        /** Color ranges --> Used in random color generation, keeping colors within the bounds described
         * Color range must be added to listColorRange to be used in generation
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