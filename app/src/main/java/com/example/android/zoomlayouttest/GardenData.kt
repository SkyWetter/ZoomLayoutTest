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

        fun sortBed(squareList: MutableList<Square>): MutableList<Square> {
            return squareList.sortedBy { it.squareId }.toMutableList()
        }

        fun getDailyBedSchedule_v2(){
            for(bed in rvBedList){
                for(day in bed.daysOfWeek){
                    if(day){
                        val newBed = finalBedData(bedList[bed.rvBedID],bed.waterLevel)
                        when(bed.amPm[bed.daysOfWeek.indexOf(day)]){
                            1 -> { weeklySchedule[bed.daysOfWeek.indexOf(day)][0].add(newBed)}
                            2 -> { weeklySchedule[bed.daysOfWeek.indexOf(day)][1].add(newBed)}
                            3 -> { weeklySchedule[bed.daysOfWeek.indexOf(day)][0].add(newBed)
                                    ; weeklySchedule[bed.daysOfWeek.indexOf(day)][1].add(newBed)}
                        }
                    }
                }
            }
        }

        fun getDailyBedSchedule(){
            for(bed in rvBedList){
                //Sun
                if(bed.daysOfWeek[0]){

                    val newBed = finalBedData(bedList[bed.rvBedID],bed.waterLevel)

                    when(bed.amPm[0]){
                        1 -> { bedList_sun_am.add(newBed) }
                        2 -> { bedList_sun_pm.add(newBed) }
                        3 -> { bedList_sun_am.add(newBed) ; bedList_sun_pm.add(newBed) }
                    }
                }
                //Mon
                if(bed.daysOfWeek[1]){

                    val newBed = finalBedData(bedList[bed.rvBedID],bed.waterLevel)

                    when(bed.amPm[0]){
                        1 -> { bedList_mon_am.add(newBed) }
                        2 -> { bedList_mon_pm.add(newBed) }
                        3 -> { bedList_mon_am.add(newBed) ; bedList_mon_pm.add(newBed) }
                    }

                }
                //Tue
                if(bed.daysOfWeek[2]){

                    val newBed = finalBedData(bedList[bed.rvBedID],bed.waterLevel)

                    when(bed.amPm[0]){
                        1 -> { bedList_tue_am.add(newBed) }
                        2 -> { bedList_tue_pm.add(newBed) }
                        3 -> { bedList_tue_am.add(newBed) ; bedList_tue_pm.add(newBed) }
                    }
                }
                //Wed
                if(bed.daysOfWeek[3]){

                    val newBed = finalBedData(bedList[bed.rvBedID],bed.waterLevel)

                    when(bed.amPm[0]){
                        1 -> { bedList_wed_am.add(newBed) }
                        2 -> { bedList_wed_pm.add(newBed) }
                        3 -> { bedList_wed_am.add(newBed) ; bedList_wed_pm.add(newBed) }
                    }

                }
                //Thu
                if(bed.daysOfWeek[4]){

                    val newBed = finalBedData(bedList[bed.rvBedID],bed.waterLevel)

                    when(bed.amPm[0]){
                        1 -> { bedList_thu_am.add(newBed) }
                        2 -> { bedList_thu_pm.add(newBed) }
                        3 -> { bedList_thu_am.add(newBed) ; bedList_thu_pm.add(newBed) }
                    }

                }
                //Fri
                if(bed.daysOfWeek[5]){

                    val newBed = finalBedData(bedList[bed.rvBedID],bed.waterLevel)

                    when(bed.amPm[0]){
                        1 -> { bedList_fri_am.add(newBed) }
                        2 -> { bedList_fri_pm.add(newBed) }
                        3 -> { bedList_fri_am.add(newBed) ; bedList_fri_pm.add(newBed) }
                    }

                }
                //Sat
                if(bed.daysOfWeek[6]){

                    val newBed = finalBedData(bedList[bed.rvBedID],bed.waterLevel)

                    when(bed.amPm[0]){
                        1 -> { bedList_sat_am.add(newBed) }
                        2 -> { bedList_sat_pm.add(newBed) }
                        3 -> { bedList_sat_am.add(newBed) ; bedList_sat_pm.add(newBed) }
                    }
                }
            }
        }

        fun finalCommandList(){

        }


    }
}