package com.example.android.zoomlayouttest

import android.text.TextUtils.indexOf
import android.util.Log
import com.example.android.zoomlayouttest.MainActivity.*
import com.example.android.zoomlayouttest.MainActivity.Companion.bedList
import com.example.android.zoomlayouttest.MainActivity.Companion.rvBedList
import com.pawegio.kandroid.w

/**
 * Garden Bed Data -- Functions and data for packaging outgoing garden bed information
 */

class GardenData{

    var commandList = mutableListOf<String>()

    companion object {


        /** Used for sorting squares in a bed by square id # in ascending order*/

        fun sortBed(squareList: MutableList<Square>): MutableList<Square> {
            return squareList.sortedBy { it.squareId }.toMutableList()
        }

        fun sortBedLists(){

        }


        data class finalBedData(val bed: Bed, val waterLevel: Int)

        private var bedList_sun_am = mutableListOf<finalBedData>()
        private var bedList_mon_am = mutableListOf<finalBedData>()
        private var bedList_tue_am = mutableListOf<finalBedData>()
        private var bedList_wed_am = mutableListOf<finalBedData>()
        private var bedList_thu_am = mutableListOf<finalBedData>()
        private var bedList_fri_am = mutableListOf<finalBedData>()
        private var bedList_sat_am = mutableListOf<finalBedData>()

        private var bedList_sun_pm = mutableListOf<finalBedData>()
        private var bedList_mon_pm = mutableListOf<finalBedData>()
        private var bedList_tue_pm = mutableListOf<finalBedData>()
        private var bedList_wed_pm = mutableListOf<finalBedData>()
        private var bedList_thu_pm = mutableListOf<finalBedData>()
        private var bedList_fri_pm = mutableListOf<finalBedData>()
        private var bedList_sat_pm = mutableListOf<finalBedData>()

        private var weeklySchedule = arrayListOf(
                arrayListOf(bedList_sun_am, bedList_sun_pm),
                arrayListOf(bedList_mon_am, bedList_mon_pm),
                arrayListOf(bedList_tue_am, bedList_tue_pm),
                arrayListOf(bedList_wed_am, bedList_wed_pm),
                arrayListOf(bedList_thu_am, bedList_thu_pm),
                arrayListOf(bedList_fri_am, bedList_fri_pm),
                arrayListOf(bedList_sat_am, bedList_sat_pm)
        )

        /** Takes the list of currents beds and puts them in the weekly schedule array ,
         * organized by am/pm, along with a given waterlevel for each bed
         *
         */

        fun getBedSchedule() {
            for(i in 0 until 7){
                for(j in 0 until 2){
                    weeklySchedule[i][j].clear()
                }
            }
            for (bed in rvBedList) {                      //for each bed in the rvBedList
                for (day in weeklySchedule.indices) {             //For each day in a bed's RVData
                    if (bed.daysOfWeek[day]) {                             //If bed is to be watered on that day

                        val newBed = finalBedData(bedList[bed.rvBedID], bed.waterLevel)              //Create a new bed, with bed and water level

                        when (bed.amPm[day]) {                               //When the amPm var for that given day is, at to appropriate bedList
                            1 -> { weeklySchedule[day][0].add(newBed) }
                            2 -> { weeklySchedule[day][1].add(newBed)}
                            3 -> { weeklySchedule[day][0].add(newBed); weeklySchedule[day][1].add(newBed)}
                        }
                    }
                }
            }

        }

        fun printBedsToLog(){
            Log.d("tester","sun_am: $bedList_sun_am  || sun_pm: $bedList_sun_pm")
            Log.d("tester","mon_am: $bedList_mon_am  || mon_pm: $bedList_mon_pm")
        }
    }
}