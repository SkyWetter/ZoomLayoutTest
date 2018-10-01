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
import com.example.android.zoomlayouttest.MainActivity.Companion.editMode
import com.otaliastudios.zoom.ZoomLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.NumberFormatException
import kotlin.math.*


class MainActivity : AppCompatActivity() {
    companion object {
        private var editMode = false
        private var firstSquare = false
    }

    val squareList = mutableListOf<String>()
    var allTiles = mutableListOf<Square>()       //full list of all Tile objects

    var tempBed = mutableListOf<Int>()
    var bedList = mutableListOf<Bed>()

    var turretSquare: Square? = null


    var buttonsPerRow = 9
    var bedCount = intArrayOf(1)
    var bedEdit = intArrayOf(0, 0)   //[0] is "boolean" for editing mode, [1] is bedID to be edited


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val constraintSet = ConstraintSet()    //Creates a new constraint set variable
        constraintSet.clone(buttonContainer)  //Clones the buttonContainer constraint layout settings
        val constraintLayout = findViewById(R.id.buttonContainer) as ConstraintLayout

        val doneButton = findViewById(R.id.doneButton) as Button

        //recyclerview for bedlist
        //https://youtu.be/67hthq6Y2J8
        val rvBedList = rvBedList as RecyclerView
        rvBedList.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val rvBeds = ArrayList<rvBed>()

        val bedZero = Bed(0)        //load bedList[0] so bed1 can be in bedList[1] lol
        bedList.add(bedZero)

        //buttons per row parameter in gridCreate must be odd
        gridCreate(50, 2, buttonsPerRow, constraintSet, constraintLayout, this@MainActivity, allTiles, tempBed, bedEdit, bedList)

        turretSquare = allTiles[((buttonsPerRow * buttonsPerRow) - 1) / 2]

        getAngleDistanceAll(allTiles, turretSquare!!)

        // Test code for getAngleDistanceAll function
        for (square in allTiles.indices) {
            Log.i("AngleDis", "SquareId: " + allTiles[square].squareId.toString() + " Angle: " + allTiles[square].angle.toString() + " Distance: " + allTiles[square].distance.toString())
        }

        println("coolcool")

        initializeButtons(this@MainActivity, doneButton, editButton, bedList, tempBed, bedCount, allTiles, rvBeds, rvBedList, bedEdit)
    }




//GRID CREATE
/* Function for populating a list of all squares in garden grid */

//Takes a button size (how large each individual button is), margins between each button, buttons per row (grid is always square)
//Must also pass the parent Constraint Layout view holding the grid, and pass this@MainAtivity into context

    fun gridCreate(buttonSize: Int, buttonMargin: Int, buttonsPerRow: Int, constraintSet: ConstraintSet, constraintLayout: ConstraintLayout,
                   context: Context, allTiles: MutableList<Square>, tempBed: MutableList<Int>, bedEdit: IntArray, bedList: MutableList<Bed>) {

        var previousButton = Button(context)            //Tracks the previous button created
        var previousRowLeadButton = Button(context)     //Tracks the first button of the previous row
        var idNumber = 10000                           //id#, increments with each created button

        if ((buttonsPerRow % 2) != 0) {
            for (row in 0..(buttonsPerRow - 1))             //For this given row
            {

                for (i in 0..(buttonsPerRow - 1))      //And this given square
                {

                    val button = Button(context)       //Create a new button
                    button.id = idNumber             //Set id based on idNumber incrementor


                    var tempTile = Square(idNumber)      //create new tile object with tileID

                    tempTile.column = i                 //set rows and columns
                    tempTile.row = row
                    tempTile.button = button
                    button.tag =

                    allTiles.add(tempTile)              //add to master list of tiles

                    idNumber = idNumber + 1            //increment id number

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
                        tempTile.changeColor(ColorData.turret)
                        allTiles[((buttonsPerRow * buttonsPerRow) - 1) / 2].bedID = 56789              //arbitrary number to trigger 'unclickable' bed status
                    } else {
                        tempTile.changeColor(ColorData.deselected)                            //Sets color (To be replaced with final button styling)
                    }

                    constraintLayout.addView(button)                                    //Add button into Constraint Layout view
                    constraintSet.applyTo(constraintLayout)                             //Apply constraint styling

                    button.setOnClickListener()                                         //TEST FUNCTION FOR CLICK OF SQUARE
                    {

                        buildBed(context, button, allTiles, tempBed, bedEdit, bedList)                         //add/remove tiles from bed when clicked
                    }

                    previousButton = button
                }
            }

        } else {
            Log.i("ERROR!", "You can't have gridCreate buttonsPerRow == an even number")
        }

        fun getXY(column: Int, row: Int):String{
           var xy = ""
            if(column < 10) {

           }
            return xy
        }
    }

    fun adjacentTileColor(button: Button, allSquares: MutableList<Square>) {

        var thisSquare = allSquares[button.id - 10000]

        if(!editMode){
            if(firstSquare){
                if(thisSquare.bedID == 0){
                    //check if any adjacent squares are empty and set color

                }
            }
        }

        fun checkAdjacentSquare(){

        }
    }
    fun initializeButtons(context: Context, doneButton: Button, editButton: Button, bedList: MutableList<Bed>, tempBed: MutableList<Int>, bedCount: IntArray,
                          allTiles: MutableList<Square>, rvBeds: ArrayList<rvBed>, rvBedList: RecyclerView, bedEdit: IntArray) {
        doneButton.setOnClickListener()
        {
            doneBed(context, bedList, tempBed, bedCount, allTiles, rvBeds, rvBedList, bedEdit)
        }

        editButton.setOnClickListener()
        {
            editBed(bedEdit)
        }

        //space for further buttons (setting, bluetooth, etc)
    }

    fun buildBed(context: Context, button: Button, allTiles: MutableList<Square>, tempBed: MutableList<Int>, bedEdit: IntArray, bedList: MutableList<Bed>) {

        val thisSquare = allTiles[button.id - 10000]
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
                    tempBed.remove(button.id)
                    thisSquare.hasBed = false
                    thisSquare.changeColor(ColorData.deselected)
                    Toast.makeText(context, "Removed " + button.id + " from bed", Toast.LENGTH_SHORT).show()
                } else                                     //select tile for the new bed
                {
                    if (isTileAdjacent(button, tempBed, allTiles)) {
                        tempBed.add(button.id)
                        thisSquare.hasBed = true
                        thisSquare.changeColor(ColorData.selected)
                        Toast.makeText(context, "Added " + button.id + " to bed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else  //if in editing
        {
            if (thisSquare.bedID == bedEdit[1])     //remove if tile is in bed
            {
                bedList[bedEdit[1]].tilesInBed.remove(button.id)
                thisSquare.bedID = 0
                thisSquare.hasBed = false
                thisSquare.changeColor(ColorData.deselected)
                Toast.makeText(context, "Removed " + button.id + " from Bed #" + bedEdit[1], Toast.LENGTH_SHORT).show()
            } else if (thisSquare.bedID == 0)                                    //add new tiles to bed
            {
                if (isTileAdjacent(button, bedList[bedEdit[1]].tilesInBed, allTiles)) {
                    bedList[bedEdit[1]].tilesInBed.add(button.id)
                    thisSquare.bedID = bedEdit[1]
                    thisSquare.hasBed = true
                    thisSquare.changeColor(ColorData.selected)
                    Toast.makeText(context, "Added " + button.id + " to Bed #" + bedEdit[1], Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun editBed(bedEdit: IntArray) {
        val bedToEdit = 1   //figure out for to set this via clicking card in RV

        bedEdit[0] = 1      //bool to toggle editing mode
        bedEdit[1] = bedToEdit      //bedID that is being edited
        editMode = true

    }

    fun isTileAdjacent(button: Button, bedTiles: MutableList<Int>, allTiles: MutableList<Square>): Boolean {
        var tileIsAdjacent = false

        var xBed = IntArray(bedTiles.size)      //x & y for each tile in bed
        var yBed = IntArray(bedTiles.size)

        val xButton = allTiles[button.id - 10000].column       //x & y for tile selected
        val yButton = allTiles[button.id - 10000].row

        var xPermitted = IntArray(bedTiles.size * 4)      //array of permitted tile positions
        var yPermitted = IntArray(bedTiles.size * 4)

        if (bedTiles.isEmpty()) //allow any tile for first selection of new bed
        {
            tileIsAdjacent = true
        }

        if (tileIsAdjacent == false) {
            for (i in 0..bedTiles.size - 1)     //parse the x&y coordinates from the bed
            {
                xBed[i] = allTiles[bedTiles[i] - 10000].column
                yBed[i] = allTiles[bedTiles[i] - 10000].row
            }

            for (i in 0..bedTiles.size * 4 - 1 step 4)        //create list of permitted positions
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

            for (i in 0..bedTiles.size * 4 - 1)        //check button x&y against permitted x&y
            {
                if (xButton == xPermitted[i] && yButton == yPermitted[i]) {
                    tileIsAdjacent = true

                }
                if (tileIsAdjacent) {
                    break
                }
            }
        }

        return tileIsAdjacent
    }

    //adds numbered cards to recyclerview for each bed
    fun addBedToRV(rvBeds: ArrayList<rvBed>, rvBedList: RecyclerView, bedNum: Int) {
        rvBeds.add(rvBed("Bed #" + bedNum))

        val adapter = CustomAdapter(rvBeds)

        rvBedList.adapter = adapter


    }

    //creates bed object, adds completed bed to list, sets stage for next bed
    fun doneBed(context: Context, bedList: MutableList<Bed>, tempBed: MutableList<Int>, bedCount: IntArray, allTiles: MutableList<Square>,
                rvBeds: ArrayList<rvBed>, rvBedList: RecyclerView, bedEdit: IntArray) {
        if (tempBed.isNotEmpty() && bedEdit[0] == 0) {
            ColorData.newRandomBedColor()
            addBedToRV(rvBeds, rvBedList, bedCount[0])

            var finalBed = Bed(bedCount[0])     //contains final tile IDs

            for (i in 0..tempBed.size - 1)       //update tiles with appropriate bed ID & adds tile IDs to list for Bed
            {
                allTiles[tempBed[i] - 10000].bedID = bedCount[0]

                allTiles[tempBed[i] - 10000].changeColor(ColorData.nextBedColor!!)
                finalBed.tilesInBed.add(tempBed[i])
            }

            bedList.add(finalBed)               //add completed Bed to master list and set up for the next
            tempBed.clear()
            Toast.makeText(context, "Bed #" + bedCount[0] + " created", Toast.LENGTH_SHORT).show()
            bedCount[0]++
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
        var tilesInBed = mutableListOf<Int>()
        //other variables
    }


/* Angle and Distance Function
    Takes a target square and a central turret square
 */

    private fun getAngleDistanceAll(allTiles: MutableList<Square>, turretSquare: Square) {
        for (square in allTiles.indices) {
            getAngleDistance(allTiles[square], turretSquare)
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