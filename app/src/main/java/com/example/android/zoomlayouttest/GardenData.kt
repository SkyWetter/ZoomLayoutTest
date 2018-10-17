package com.example.android.zoomlayouttest

import android.util.Log
import com.example.android.zoomlayouttest.MainActivity.*
import com.example.android.zoomlayouttest.MainActivity.Companion.bedList
import com.example.android.zoomlayouttest.MainActivity.Companion.rvBedList

/**
 * Garden Bed Data -- Functions and data for packaging outgoing garden bed information
 */

class GardenData{



    companion object {
        var dataToSendFull = arrayListOf<String>()

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

      //  private var dataToSend = arrayListOf<String>("SuAM: ","SuPM: ","MoAM: ","MoPM: ","TuAM: ","TuPM: ","WeAM","WePM: ","ThAM: ","ThPM: ","FrAM: ","FrPm: ","SaAM: ","SaPM: ")


        /** Used for sorting squares in a bed by square id # in ascending order*/

        fun sortBed(squareList: MutableList<Square>): MutableList<Square> {
            return squareList.sortedBy { it.squareId }.toMutableList()
        }

        /** Takes the list of currents beds and puts them in the weekly schedule array ,
         * organized by am/pm, along with a given waterlevel for each bed
         *
         */

        fun getBedSchedule() {

            //Function clears the previous weekly schedule

            fun clearSchedule(){
                for(i in 0 until 7){
                    for(j in 0 until 2){
                        weeklySchedule[i][j].clear()
                    }
                }
            }

            clearSchedule()


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

        /** Takes weekly schedule and turns it into a continuous string. Might be chopped up into 14 separate strings (one per day + time combo)*/

        fun prepDatData():ArrayList<String>{

            //Clear the program
            val dataToSend = arrayListOf<String>("SuAM: ","SuPM: ","MoAM: ","MoPM: ","TuAM: ","TuPM: ","WeAM: ","WePM: ","ThAM: ","ThPM: ","FrAM: ","FrPm: ","SaAM: ","SaPM: ")

            var index = -1

            for(day in weeklySchedule.indices){  //For each day of the week

                for(time in weeklySchedule[day].indices){  //For each time of day

                    when(day){          // gets index for

                        0 -> when(time){ 0 -> index = 0
                                         1 -> index = 1 }

                        1 -> when(time){ 0 -> index = 2
                                         1 -> index = 3 }

                        2 -> when(time){ 0 -> index = 4
                                         1 -> index = 5 }

                        3 -> when(time){ 0 -> index = 6
                                         1 -> index = 7 }

                        4 -> when(time){ 0 -> index = 8
                                         1 -> index = 9 }

                        5 -> when(time){ 0 -> index = 10
                                         1 -> index = 11 }

                        6 -> when(time){ 0 -> index = 12
                                         1 -> index = 13 }
                    }

                    for(finalBed in weeklySchedule[day][time]){  //For each finalBed in the given day + time


                        dataToSend[index] += "%%"   // Shows the start of a bed

                        for(i in 0..finalBed.waterLevel){                                // Repeats the bed by the given waterLevel value (aka > waterlevel = more bed repetition

                            for(j in finalBed.bed.squaresInBed){                         // For each square in the bed
                                dataToSend[index] += (j.squareId - 10000).toString() + ","              //Append the square id
                            }
                        }

                    }
                }
            }

            for(i in dataToSend.indices){
                if(dataToSend[i].length > 6)
                Log.d("itsWorking",dataToSend[i])
            }

            return dataToSend
        }
    }
}