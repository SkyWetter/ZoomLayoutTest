package com.example.android.zoomlayouttest

import android.bluetooth.BluetoothAdapter
import android.util.Log
import com.example.android.zoomlayouttest.MainActivity.*
import com.example.android.zoomlayouttest.MainActivity.Companion.bedList
import com.example.android.zoomlayouttest.MainActivity.Companion.bedPacketNumber
import com.example.android.zoomlayouttest.MainActivity.Companion.rvBedList
import org.w3c.dom.Text
import java.nio.charset.Charset
import android.text.method.TextKeyListener.clear
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import com.example.android.zoomlayouttest.R.id.incomingTextBox
import kotlinx.android.synthetic.main.activity_main.*



/**
 * Garden Bed Data -- Functions and data for packaging outgoing garden bed information
 */

class SerialDataService{

    companion object {
        var singleSquaresSent = mutableListOf<String>()
        var singleSquaresSentPacketNumber = mutableListOf<String>()

        var espStat_LastPacketNumber = 0
        var espStat_CurrentPacketNumber = 0
        var espStat_Checksum = 0
        var firstStatPacket = true

        var dataToSendFull = arrayListOf<String>()

        var lastSquareSent = "";
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

            //Clear the program, set each day with given day prefix. Added for clarity in debugger, will be removed in final program

           // val dataToSend = arrayListOf("sum#","sue#","mom#","moe#","tum#","tue#","wem#","wee#","thm#","the#","frm#","fre#","sam#","sae#")
             val dataToSend = arrayListOf("#","#","#","#","#","#","#","#","#","#","#","#","#","#")

            var index = -1

            for(day in weeklySchedule.indices){  //For each day of the week

                for(time in weeklySchedule[day].indices){  //For each time of day

                    when(day){          // gets index for give day + time combination

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


                        /**  Concat dataToSend[index] here to attach bed-specific prefix data */

                        for(i in 0..finalBed.waterLevel) {                                // Repeats the bed by the given waterLevel value (aka > waterlevel = more bed repetition

                            for (j in finalBed.bed.squaresInBed) {                         // For each square in the bed

                                    dataToSend[index] += (j.squareId - 10000).toString()              //Append the square id as a straight

                                if(weeklySchedule[day][time].indexOf(finalBed) < weeklySchedule[day][time].size - 1){
                                    dataToSend[index] += ","
                                }
                                else if(i < finalBed.waterLevel){
                                    dataToSend[index] += ","
                                }

                                else if(finalBed.bed.squaresInBed.indexOf(j) < finalBed.bed.squaresInBed.size - 1){
                                    dataToSend[index] += ","
                                }
                            }
                        }
                        /**  Concat dataToSend[index] here to attach bed-specific suffix data */
                    }
                }
            }

            //Print each string of beds to Logcat
            for(i in dataToSend.indices) {
                if (dataToSend[i].length > 1) {
                    Log.d("itsWorking", dataToSend[i])
                }
            }
            return dataToSend
        }

        fun sendFullData(scheduleArray : ArrayList<String>,mBluetoothConnection: BluetoothConnectionService?){

            var thisPacketNumber = bedPacketNumber.toString()

            while(thisPacketNumber.length < 3) {   //Append 0's to make the packet length uniform
                thisPacketNumber = "0$thisPacketNumber"
            }

            writeToSerial("***$thisPacketNumber",mBluetoothConnection) // Signifier for full program start

            bedPacketNumber = incPacketNumber(bedPacketNumber)

            var checkSumString = ""

            for(i in scheduleArray){
                checkSumString += i


                var stringLength = i
                while(stringLength.length > 256){
                    writeToSerial(stringLength.substring(0,256),mBluetoothConnection)
                    stringLength = stringLength.substring(256)
                }
                writeToSerial(stringLength,mBluetoothConnection)
                writeToSerial("\n",mBluetoothConnection)
            }

            checkSumString = "&$checkSumString"
            Log.d("Serial","Checksum sent: ${getCheckSum(checkSumString)}")
            writeToSerial(getCheckSum(checkSumString).toString(),mBluetoothConnection)
        }

        fun writeToSerial(string : String,mBluetoothConnection : BluetoothConnectionService?){
            val tempByte: ByteArray = string.toByteArray(Charset.defaultCharset())
            if(mBluetoothConnection!=null){
                mBluetoothConnection.write(tempByte)
            }
        }

        //Send single square packet
        // [%][000][000][000]

        fun sendData(string: String, squarePacketNumber:Int,mBluetoothConnection: BluetoothConnectionService?, startChar : String = "") : Int{

            var currentPacketNumber = squarePacketNumber
            var thisString = string                     //the string to send
            var squarePacketNumberString : String       //packet number str
            var checkSumValue : Int

            squarePacketNumberString = currentPacketNumber.toString()// Convert packet # to string

            while(squarePacketNumberString.length < 3) {   //Append 0's to make the packet length uniform
                squarePacketNumberString = "0$squarePacketNumberString"
            }

            while (thisString.length < 3){              //Add spacer to front of string
                thisString = "0$thisString"
            }

            checkSumValue = getCheckSum(thisString)                     // Gets value of checksum

            thisString = "$startChar$squarePacketNumberString$thisString$checkSumValue"   //Create final packet string to send


            Log.d("checksum","SerialData: $thisString")

            //Tracks the last 50 squares sent to the esp
            singleSquaresSent.add(thisString)
            singleSquaresSentPacketNumber.add(squarePacketNumberString)

            if(singleSquaresSent.size > 50)
            {
                singleSquaresSentPacketNumber.removeAt(0)
                singleSquaresSent.removeAt(0)
            }

            if(MainActivity.connectedToRainbow) {  //Send it
                lastSquareSent = thisString;

                for(i in 0..2)
                {
                    writeToSerial(thisString,mBluetoothConnection)
                }

            }

            return incPacketNumber(squarePacketNumber)
        }

        fun receivingData(mBluetoothConnection: BluetoothConnectionService?, text: String?)
        {
            if(MainActivity.connectedToRainbow)
            {

                // TEMPORARY -- currently only checking for single square packet train reset requests
                if(text?.length == 3)
                {
                    //Looks for the packet being requested, removes preceeding packets as they are not needed
                    for(i in singleSquaresSentPacketNumber)
                    {
                        //If the requested packet is found, stop removing packets from front of list
                        if(i == text)
                        {
                            break
                        }

                        singleSquaresSentPacketNumber.removeAt(0)
                        singleSquaresSent.removeAt(0)
                    }

                    //Resend packets remaining in list.
                    for(i in singleSquaresSent)
                    {
                        writeToSerial(i,mBluetoothConnection)
                    }

                    writeToSerial("*",mBluetoothConnection)

                }

                /** Stats Data incoming from ESP*/

                else if(text!!.startsWith("$"))
                {

                    espStat_packetHandler(text!!)

                }
            }
        }

        fun espStat_packetHandler(packet: String)
        {

            //Check packet length here (currently unknown)

            //Assumes packet length okay.
            if(firstStatPacket)
            {
                espStat_LastPacketNumber = checkPacketNumber(packet,1,4)
            }

            else
            {
                espStat_CurrentPacketNumber = checkPacketNumber(packet,1,4)

                if(espStat_CurrentPacketNumber == espStat_LastPacketNumber +1)  //Correct in-sequence packet received
                {
                    //Check the checksum, excecute if statement
                    if(checkChecksum(packet))
                    {
                        /**  GRAB DATA AND FEED TO DEBUG MONITOR*/

                    }
                }

                else
                {
                    if(espStat_CurrentPacketNumber <= espStat_LastPacketNumber) //Old packet received
                    {
                        //Ignore packet, contains old or repeat data
                    }

                    else if(espStat_CurrentPacketNumber > espStat_LastPacketNumber + 1) //Missed a packet
                    {
                        //request missed packet -- Possibly ignore
                    }
                }
            }
        }

        fun checkChecksum(packet: String) : Boolean
        {
            val calculatedChecksum = getCheckSum(packet.substring(4,packet.length-3))
            val receivedChecksum = packet.substring(packet.length -3,packet.length).toInt()

            return (calculatedChecksum == receivedChecksum)
        }

        fun checkPacketNumber(packet: String,start: Int,length: Int) : Int
        {
            return packet.substring(start,length).toInt()
        }

        // Turns string to byte array, adds value of each byte, returns as Int

       private fun getCheckSum(string: String):Int{
            val stringCheckSum = string.toByteArray(Charset.defaultCharset())  //convert string (without packet#, start char and chksm) to byte array
            var checkSumValue = 0
            for(i in stringCheckSum){   //Calculate checksum
                checkSumValue += i
            }

            return checkSumValue
        }

       private fun incPacketNumber(packetNumber : Int) : Int{

            var newPacketNumber = packetNumber
            newPacketNumber ++

            if(newPacketNumber > 999){
                newPacketNumber = 0
            }

            return newPacketNumber
        }

    }
}