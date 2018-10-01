/*   ZoomLayout and Garden Grid Population Test

September 17th, 2018 -- David Barry
Added basic ZoomLayout functionality
 - Small representative grid of 13 squares can be 'pinch-zoomed' and scrolled in center of screen
 - Each square can be clicked, activating a button listener function, and showing a 'Toast' message on screen

September 18th, 2018 -- David Barry
Added function for creating a list of known squares (called createSquareList)
 - Function takes an int representing total # of squares on grid and a blank, mutable list to hold square names

Sept 19th, 2018 -- James Gillis
Added constraints for zoomLayout view
Added id tags for top and bottom menu bar
Fixed constraints for top and bottom menu bar

Sept. 20th, 2018 -- David Barry
Added test functionality (push button to zoom) for top button in garden grid area

Sept. 22nd, 2018 -- David Barry

Grid-based button instantiation added. Program will hang if you choose too large of a number for your grid size, so we may
need to limit overall grid size.

25/row seems to load in a reasonable amount of time
35/row will hang for 5 seconds before loading in, and the grid size is so small at min zoom that it's difficult to tell what's happening.

Possibly removing pinch zoom options and changing to discrete zoom levels may make it more readable, however, we'll need to overlay some sort of
information so the user knows where in the garden the are at any given moment.

Sept 23, 2018 -- James Gillis
Add/remove tiles to a bed list

Sept 24, 2018 -- James
Each tile has object containing row, column, and bed data
Added "Done Bed" button
Added function to construct beds
Added function to add bed to master bed list

Sept 25, 2018 -- Dave Barry

Changed references of 'Tile' to references to 'Square', to avoid issues with pre-existing classes in the Android API
Added function for getting distance and angle measurements from central turret square
Added function for grabbing position of central square
Removed CreateSquareList function

Sept 26th -- James Gillis
Added tileIDs to completed Beds
Added rudimentary recyclerView

Sept 27th -- James Gillis
Added edit button + functionality

 */


package com.example.android.zoomlayouttest


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.*


class MainActivity : AppCompatActivity() {
    companion object {

        private var bedCount = 1
        private var bedEdit = intArrayOf(0, 0)   //[0] is "boolean" for editing mode, [1] is bedID to be edited
        private var editMode = false

        private var firstSquare = false
        private var turretSquare: Square? = null
        private var allSquares = mutableListOf<Square>()       //full list of all Tile objects

        private var tempBed = mutableListOf<Square>()
        private var bedList = mutableListOf<Bed>()

        private var buttonsPerRow = 9 //<--- must be odd number

        private val constraintSet = ConstraintSet()    //Creates a new constraint set variable

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        constraintSet.clone(buttonContainer)  //Clones the buttonContainer constraint layout settings
        val constraintLayout = findViewById<ConstraintLayout>(R.id.buttonContainer)
        val doneButton = findViewById<Button>(R.id.doneButton)

        //recyclerview for bedlist
        //https://youtu.be/67hthq6Y2J8
        val rvBedList = rvBedList as RecyclerView
        rvBedList.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val rvBeds = ArrayList<rvBed>()

        val bedZero = Bed(0)        //load bedList[0] so bed1 can be in bedList[1] lol
        bedList.add(bedZero)

        //buttons per row parameter in gridCreate must be odd
        gridCreate(50, 2, constraintLayout, this@MainActivity)

        turretSquare = allSquares[((buttonsPerRow * buttonsPerRow) - 1) / 2]

        getAngleDistanceAll(allSquares, turretSquare!!)

        // Test code for getAngleDistanceAll function
        for (square in allSquares.indices) {
            Log.i("AngleDis", "SquareId: " + allSquares[square].squareId.toString() + " Angle: " + allSquares[square].angle.toString() + " Distance: " + allSquares[square].distance.toString())
        }

        println("coolcool")

        initializeButtons(this@MainActivity, doneButton, editButton,  rvBeds, rvBedList)
    }


//GRID CREATE
/* Function for populating a list of all squares in garden grid */

//Takes a button size (how large each individual button is), margins between each button, buttons per row (grid is always square)
//Must also pass the parent Constraint Layout view holding the grid, and pass this@MainAtivity into context

    fun gridCreate(buttonSize: Int, buttonMargin: Int, constraintLayout: ConstraintLayout, context: Context) {

        var previousButton = Button(context)            //Tracks the previous button created
        var previousRowLeadButton = Button(context)     //Tracks the first button of the previous row
        var idNumber = 10000                           //id#, increments with each created button

        fun getXY(column: Int, row: Int):String{
            var xy = ""
            if(column < 10) { xy = "0$column"} else{xy = "$column"}
            if(row < 10) { xy += "0$row"} else{xy += "$row"}

            return xy
        }

        if ((buttonsPerRow % 2) != 0) {
            for (row in 0..(buttonsPerRow - 1))             //For this given row
            {

                for (i in 0..(buttonsPerRow - 1))      //And this given square
                {

                    val button = Button(context)       //Create a new button
                    button.id = idNumber             //Set id based on idNumber incrementor


                    var tempSquare = Square(idNumber)      //create new tile object with tileID

                    tempSquare.column = i                 //set rows and columns
                    tempSquare.row = row
                    tempSquare.button = button
                    button.tag = getXY(i,row)
                    Log.d("Coords","Button id = " + button.id.toString() + " and coord = " + button.tag )

                    allSquares.add(tempSquare)              //add to master list of tiles

                    idNumber += 1                       //increment id number

                    if (i == 0)                         //If its the first square in a row
                    {
                        if (row == 0)                   //If its the first row of the grid
                        {

                            constraintSet.connect(button.id, ConstraintSet.LEFT, constraintLayout.id, ConstraintSet.LEFT, buttonMargin)         //SETS CONSTRAINTS
                            constraintSet.connect(button.id, ConstraintSet.TOP, constraintLayout.id, ConstraintSet.TOP, buttonMargin)
                            constraintSet.setMargin(button.id, ConstraintSet.TOP, 0)
                            constraintSet.setMargin(button.id, ConstraintSet.LEFT, 0)

                        } else {
                            constraintSet.connect(button.id, ConstraintSet.LEFT, constraintLayout.id, ConstraintSet.LEFT, buttonMargin)         //SETS CONSTRAINTS
                            constraintSet.connect(button.id, ConstraintSet.TOP, previousRowLeadButton.id, ConstraintSet.BOTTOM, buttonMargin)
                            constraintSet.setMargin(button.id, ConstraintSet.LEFT, 0)
                        }

                        previousRowLeadButton = button

                    } else {

                        constraintSet.connect(button.id, ConstraintSet.LEFT, previousButton.id, ConstraintSet.RIGHT, buttonMargin)          //SETS CONSTRAINTS
                        constraintSet.connect(button.id, ConstraintSet.BASELINE, previousButton.id, ConstraintSet.BASELINE, buttonMargin)
                        constraintSet.setMargin(button.id, ConstraintSet.TOP, 0)

                    }


                    constraintSet.constrainWidth(button.id, buttonSize)                 //Sets size of created button
                    constraintSet.constrainHeight(button.id, buttonSize)

                    var tempNum = (buttonsPerRow - 1) / 2

                    if (i == tempNum && row == tempNum) {
                        tempSquare.changeColor(ColorData.turret)
                        allSquares[((buttonsPerRow * buttonsPerRow) - 1) / 2].bedID = 56789              //arbitrary number to trigger 'unclickable' bed status
                    } else {
                        tempSquare.changeColor(ColorData.deselected)                            //Sets color (To be replaced with final button styling)
                    }

                    constraintLayout.addView(button)                                    //Add button into Constraint Layout view
                    constraintSet.applyTo(constraintLayout)                             //Apply constraint styling

                    button.setOnClickListener()                                         //TEST FUNCTION FOR CLICK OF SQUARE
                    {

                        buildBed(context, button)                         //add/remove tiles from bed when clicked
                    }

                    previousButton = button
                }
            }

        } else {
            Log.i("ERROR!", "You can't have gridCreate" +
                    " buttonsPerRow == an even number")
        }

    }

    fun adjacentTileColor(button: Button) {


        var buttonIDnorm = button.id - 10000
        var thisSquare = allSquares[buttonIDnorm]
        var leftID : Square? = null
        var rightID : Square? = null
        var aboveID : Square? = null
        var belowID : Square? = null

        /** get adj square ids -- null if square is beyond bed bounds */

        if(buttonIDnorm % buttonsPerRow == 0){ } else{ leftID = allSquares[buttonIDnorm-1]}         //Left
        if((buttonIDnorm+1) % buttonsPerRow == 0) { } else{ rightID = allSquares[buttonIDnorm+1]}    //Right
        if(buttonIDnorm < buttonsPerRow){} else {aboveID = allSquares[buttonIDnorm - buttonsPerRow]} //Above
        if(buttonIDnorm > ((buttonsPerRow * buttonsPerRow)- buttonsPerRow)){} else {belowID = allSquares[buttonIDnorm + buttonsPerRow]} //Below

        if(!editMode){
            if(thisSquare.bedID == 0){


            }

        }

//        fun checkAdjacentSquares(){
//
//            val thisSquareCoord: String = thisSquare.button.tag.toString()
//            val squareAbove
//        }
    }
    fun initializeButtons(context: Context, doneButton: Button, editButton: Button, rvBeds: ArrayList<rvBed>, rvBedList: RecyclerView) {
        doneButton.setOnClickListener()
        {
            doneBed(context, rvBeds, rvBedList)
        }

        editButton.setOnClickListener()
        {
            editBed()
        }

        //space for further buttons (setting, bluetooth, etc)
    }

    fun buildBed(context: Context, button: Button) {

        val thisSquare = allSquares[button.id - 10000]
        if(tempBed.isEmpty()){
            firstSquare = true
        }
        else{
            firstSquare = false
        }

        if (bedEdit[0] == 0)     //if not in editing
        {
            if (thisSquare.bedID == 0)          //check if tile is currently in a bed
            {
                if (thisSquare.hasBed == true)    //'unselect' a selected tile from the new bed
                {
                    thisSquare.hasBed = false
                    thisSquare.changeColor(ColorData.deselected)
                    tempBed.remove(thisSquare)
                    Toast.makeText(context, "Removed " + thisSquare.squareId + " from bed", Toast.LENGTH_SHORT).show()
                } else                                     //select tile for the new bed
                {
                    if (isSquareAdjacent(button, tempBed)) {
                        thisSquare.hasBed = true
                        thisSquare.changeColor(ColorData.selected)
                        tempBed.add(thisSquare)
                        Toast.makeText(context, "Added " + thisSquare.squareId + " to bed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else  //if in editing
        {
            if (thisSquare.bedID == bedEdit[1])     //remove if tile is in bed
            {
                thisSquare.bedID = 0
                thisSquare.hasBed = false
                thisSquare.changeColor(ColorData.deselected)
                bedList[bedEdit[1]].squaresInBed.remove(thisSquare)
                Toast.makeText(context, "Removed " + thisSquare.squareId + " from Bed #" + bedEdit[1], Toast.LENGTH_SHORT).show()
            } else if (thisSquare.bedID == 0)                                    //add new tiles to bed
            {
                if (isSquareAdjacent(button, bedList[bedEdit[1]].squaresInBed)) {
                    thisSquare.bedID = bedEdit[1]
                    thisSquare.hasBed = true
                    thisSquare.changeColor(ColorData.selected)
                    bedList[bedEdit[1]].squaresInBed.add(thisSquare)
                    Toast.makeText(context, "Added " + thisSquare.squareId + " to Bed #" + bedEdit[1], Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun editBed() {
        val bedToEdit = 1   //figure out for to set this via clicking card in RV

        bedEdit[0] = 1      //bool to toggle editing mode
        bedEdit[1] = bedToEdit      //bedID that is being edited
        editMode = true

    }

    fun isSquareAdjacent(button: Button, bedSquares: MutableList<Square>): Boolean {
        var squareIsAdjacent = false

        var xBed = IntArray(bedSquares.size)      //x & y for each tile in bed
        var yBed = IntArray(bedSquares.size)

        val xButton = allSquares[button.id - 10000].column       //x & y for tile selected
        val yButton = allSquares[button.id - 10000].row

        var xPermitted = IntArray(bedSquares.size * 4)      //array of permitted tile positions
        var yPermitted = IntArray(bedSquares.size * 4)

        if (bedSquares.isEmpty()) //allow any tile for first selection of new bed
        {
            squareIsAdjacent = true
        }

        if (squareIsAdjacent == false) {
            for (i in 0..bedSquares.size - 1)     //parse the x&y coordinates from the bed
            {
                xBed[i] = allSquares[bedSquares[i].squareId - 10000].column
                yBed[i] = allSquares[bedSquares[i].squareId - 10000].row
            }

            for (i in 0..bedSquares.size * 4 - 1 step 4)        //create list of permitted positions
            {
                xPermitted[i] = xBed[i / 4] - 1
                xPermitted[i + 1] = xBed[i / 4]
                xPermitted[i + 2] = xBed[i / 4]
                xPermitted[i + 3] = xBed[i / 4] + 1

                yPermitted[i] = yBed[i / 4]
                yPermitted[i + 1] = yBed[i / 4] - 1
                yPermitted[i + 2] = yBed[i / 4] + 1
                yPermitted[i + 3] = yBed[i / 4]
            }

            for (i in 0..bedSquares.size * 4 - 1)        //check button x&y against permitted x&y
            {
                if (xButton == xPermitted[i] && yButton == yPermitted[i]) {
                    squareIsAdjacent = true

                }
                if (squareIsAdjacent) {
                    break
                }
            }
        }

        return squareIsAdjacent
    }

    //adds numbered cards to recyclerview for each bed
    fun addBedToRV(rvBeds: ArrayList<rvBed>, rvBedList: RecyclerView, bedNum: Int) {
        rvBeds.add(rvBed("Bed #" + bedNum))

        val adapter = CustomAdapter(rvBeds)

        rvBedList.adapter = adapter


    }

    //creates bed object, adds completed bed to list, sets stage for next bed
    fun doneBed(context: Context, rvBeds: ArrayList<rvBed>, rvBedList: RecyclerView) {
        if (tempBed.isNotEmpty() && bedEdit[0] == 0) {      //only executes when there is new bed, otherwise updates done on click
            ColorData.newRandomBedColor()
            addBedToRV(rvBeds, rvBedList, bedCount)

            var finalBed = Bed(bedCount)     //contains final tile IDs

            for (i in 0..tempBed.size - 1)       //update tiles with appropriate bed ID & adds tile IDs to list for Bed
            {
                allSquares[tempBed[i].squareId - 10000].bedID = bedCount

                allSquares[tempBed[i].squareId - 10000].changeColor(ColorData.nextBedColor!!)
                finalBed.squaresInBed.add(tempBed[i])
            }

            bedList.add(finalBed)               //add completed Bed to master list and set up for the next
            tempBed.clear()
            Toast.makeText(context, "Bed #" + bedCount + " created", Toast.LENGTH_SHORT).show()
            bedCount++
        } else {
            bedEdit[0] = 0      //reset editing bool
        }
    }

    data class Square(val squareId: Int)        //object containing tile information
    {
        var row: Int = 0
        var column: Int = 0
        var bedID: Int = 0
        var hasBed: Boolean = false
        var angle: Double? = null
        var distance: Double? = null
        var button: Button? = null
        var color: Color? = null

        fun changeColor(newColor: Int) {
            button!!.setBackgroundColor(newColor)
        }
    }

    data class Bed(val bedID: Int) {
        var squaresInBed = mutableListOf<Square>()
        //other variables
    }


/* Angle and Distance Function
    Takes a target square and a central turret square
 */

    private fun getAngleDistanceAll(allSquares: MutableList<Square>, turretSquare: Square) {
        for (square in allSquares.indices) {
            getAngleDistance(allSquares[square], turretSquare)
        }
    }


    private fun getAngleDistance(targetSquare: Square, turretSquare: Square) {


        var x = (targetSquare.column - turretSquare.column)
        var y = (targetSquare.row - turretSquare.column)
        val squaredCoords = (x * x) + (y * y)
        var quadrant: Int? = null

        targetSquare.distance = sqrt(squaredCoords.toDouble())
/*
    Log.i("xy", "square" + targetSquare.squareId)
    Log.i("xy", "tS: " + targetSquare.column + " " + targetSquare.row)
    Log.i("xy", "trtS: " + turretSquare.column + " " + turretSquare.row)
    Log.i("xy", "coords: " + x + " " + y)
*/

        if (x != 0 && y != 0) {

            if (x.sign == -1) {
                if (y.sign == -1) {
                    quadrant = 4
                } else {
                    quadrant = 3
                }
            } else {
                if (y.sign == -1) {
                    quadrant = 1
                } else {
                    quadrant = 2
                }
            }

        } else {

            quadrant = 0

            if (x == 0 && y == 0) {
                targetSquare.angle = Double.NaN
            } else if (x == 0) {
                if (y.sign == -1) {
                    targetSquare.angle = 0.0
                } else {
                    targetSquare.angle = 180.0
                }
            } else {
                if (x.sign == -1) {
                    targetSquare.angle = 270.0
                } else {
                    targetSquare.angle = 90.0
                }

            }
        }
        //  Log.i("quadrant", "SquareId: " + targetSquare.squareId.toString() + " Quad: " + quadrant.toString())
        val temp: Double = (abs(x).toDouble() / abs(y).toDouble())

        if (quadrant == 1) {
            targetSquare.angle = (atan(temp) * 180) / PI
        } else if (quadrant == 2) {
            targetSquare.angle = 180 - ((atan(temp) * 180) / PI)
        } else if (quadrant == 3) {
            targetSquare.angle = 180 + (atan(temp) * 180) / PI
        } else if (quadrant == 4) {
            targetSquare.angle = 360 - (atan(temp) * 180) / PI
        } else {// Empty for squares at perp/parallel angles

        }
    }
}