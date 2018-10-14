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
        val uiColorOffWhite = Color.parseColor("#e6f1eb")
        val uiColorLightOrange = Color.parseColor("#ffa726")
        val uiColorCyan = Color.parseColor("#6effe8")


        /** Garden Bed Color Sets*/

        private val gardenBedColor500_red = Color.parseColor("#f44336")
        private val gardenBedColor500_pink = Color.parseColor("#e91e63")
        private val gardenBedColor500_purple = Color.parseColor("#9c27b0")
        private val gardenBedColor500_deepPurple = Color.parseColor("#673ab7")
        private val gardenBedColor500_indigo = Color.parseColor("#3f51b5")
        private val gardenBedColor500_blue = Color.parseColor("#2196f3")
        private val gardenBedColor500_lightBlue = Color.parseColor("#03a9f4")
        private val gardenBedColor500_cyan = Color.parseColor("#00bcd4")
        private val gardenBedColor500_teal = Color.parseColor("#009688")
        private val gardenBedColor500_green = Color.parseColor("#4caf50")
        private val gardenBedColor500_lightGreen = Color.parseColor("#8bc34a")
        private val gardenBedColor500_lime = Color.parseColor("#cddc39")
        private val gardenBedColor500_yellow = Color.parseColor("#ffeb3b")
        private val gardenBedColor500_amber = Color.parseColor("#ffc107")
        private val gardenBedColor500_orange = Color.parseColor("#ff9800")
        private val gardenBedColor500_deepOrange = Color.parseColor("#ff5722")


        val uiInvisible = Color.argb(0,255,255,255) //Use to set target bg alpha to 0

        //Views to be colored

        val swipeDelete = gardenBedColor500_red
        val deselectedSquare = uiColorOffWhite
        val selected = Color.parseColor("#2979ff")
        val adjacentSquare = uiColorLightGrey

        val turret = Color.argb(255,255,0,0)


        /**
         * Color squares using hard-coded values (no random range)
         * Add any color vals above in to the below gardenBedColorList to include it in the rotation for bed colors
         */

        private val gardenBedColorList = mutableListOf(gardenBedColor500_red, gardenBedColor500_pink,
                gardenBedColor500_purple, gardenBedColor500_deepPurple, gardenBedColor500_indigo, gardenBedColor500_blue,
                gardenBedColor500_lightBlue, gardenBedColor500_cyan, gardenBedColor500_teal, gardenBedColor500_green,
                gardenBedColor500_lightGreen, gardenBedColor500_lime, gardenBedColor500_yellow, gardenBedColor500_amber,
                gardenBedColor500_orange, gardenBedColor500_deepOrange)

        /** Get new bed color for the garden bed*/

        private var nextColorIndex = 0
        var nextBedColor : Int? = null

        fun newBedColor(){
            nextColorIndex++
            if(nextColorIndex >= gardenBedColorList.size){
                nextColorIndex = 0
            }

            nextBedColor = gardenBedColorList[nextColorIndex]
        }
    }
}