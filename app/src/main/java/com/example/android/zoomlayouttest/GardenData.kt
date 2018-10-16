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

        data class finalBedData(val bed: Bed,val waterLevel: Int)

        var bedList_sun_am = mutableListOf<finalBedData>()
        var bedList_mon_am = mutableListOf<finalBedData>()
        var bedList_tue_am = mutableListOf<finalBedData>()
        var bedList_wed_am = mutableListOf<finalBedData>()
        var bedList_thu_am = mutableListOf<finalBedData>()
        var bedList_fri_am = mutableListOf<finalBedData>()
        var bedList_sat_am = mutableListOf<finalBedData>()

        var bedList_sun_pm = mutableListOf<finalBedData>()
        var bedList_mon_pm = mutableListOf<finalBedData>()
        var bedList_tue_pm = mutableListOf<finalBedData>()
        var bedList_wed_pm = mutableListOf<finalBedData>()
        var bedList_thu_pm = mutableListOf<finalBedData>()
        var bedList_fri_pm = mutableListOf<finalBedData>()
        var bedList_sat_pm = mutableListOf<finalBedData>()

        var weeklySchedule = arrayListOf<ArrayList<MutableList<finalBedData>>>(
                arrayListOf(bedList_sun_am, bedList_sun_pm),
                arrayListOf(bedList_mon_am, bedList_mon_pm),
                arrayListOf(bedList_tue_am, bedList_tue_pm),
                arrayListOf(bedList_wed_am, bedList_wed_pm),
                arrayListOf(bedList_thu_am, bedList_thu_pm),
                arrayListOf(bedList_fri_am, bedList_fri_pm),
                arrayListOf(bedList_sat_am, bedList_sat_pm)
        )

        /** Used for sorting squares in a bed by square id # in ascending order*/

        fun sortBed(squareList: MutableList<Square>): MutableList<Square> {
            return squareList.sortedBy { it.squareId }.toMutableList()
        }

        /** Takes the list of currents beds and puts them in the weekly schedule array ,
         * organized by am/pm, along with a given waterlevel for each bed
         *
         */

        fun getDailyBedSchedule(){
            for(bed in rvBedList){                      //for each bed in the rvBedList
                for(day in bed.daysOfWeek){             //For each day in a bed's RVData
                    if(day){                             //If bed is to be watered on that day

                        val newBed = finalBedData(bedList[bed.rvBedID],bed.waterLevel)              //Create a new bed, with bed and water level

                        when(bed.amPm[bed.daysOfWeek.indexOf(day)]){                               //When the amPm var for that given day is, at to appropriate bedList
                            1 -> { weeklySchedule[bed.daysOfWeek.indexOf(day)][0].add(newBed)}
                            2 -> { weeklySchedule[bed.daysOfWeek.indexOf(day)][1].add(newBed)}
                            3 -> { weeklySchedule[bed.daysOfWeek.indexOf(day)][0].add(newBed)
                                 ; weeklySchedule[bed.daysOfWeek.indexOf(day)][1].add(newBed)}
                        }
                    }
                }
            }
        }
    }
}