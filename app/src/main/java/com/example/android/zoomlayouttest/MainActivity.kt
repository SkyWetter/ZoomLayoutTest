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
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import com.pawegio.kandroid.v
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.*


class MainActivity : AppCompatActivity() {

    companion object {

        private val adjacentSquares = mutableListOf<Square>()//List of squares adjacent to a given bed
        private var bedList = mutableListOf<Bed>()              //list of all saved beds
        private var allSquares = mutableListOf<Square>()       //full list of all squares in the grid
        private var tempBed = mutableListOf<Square>()       //bed containing newly selected squares pre-save
        private var paramMenuOpen = false

        private var bedCount = 1
        private var bedEdit = intArrayOf(0, 0)   //[0] is "boolean" for editing mode, [1] is bedID to be edited
        private val rvBedList = ArrayList<RVBedData>()      //bedlist for the recyclerview

        private var turretSquare: Square? = null         //The middle square of the bed, not to be used as a regular garden bed square
        private var buttonsPerRow = 19              /** MUST BE ODD NUMBER*/  //Number of squares per row of the garden bed
        private val constraintSet = ConstraintSet()    //Used to define constraint parameters of each square of garden bed

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()!!.hide();         //Removes the top action bar of the android ui
        setContentView(R.layout.activity_main)


        constraintSet.clone(gridContainer)                  //Clones the buttonContainer constraint layout settings
        val constraintLayout = findViewById<ConstraintLayout>(R.id.gridContainer)   //gets the layout of the garden bed container
        val doneButton = findViewById<Button>(R.id.doneButton)      //saves the current bed in tempbed to bedlist


        //this sets up the recyclerview
        RVBedXML.layoutManager = LinearLayoutManager(this)
        RVBedXML.adapter = BedAdapter(rvBedList,{bed : RVBedData -> bedClicked(bed)})

        //load bedList[0] so bed1 can be in bedList[1] lol
        val bedZero = Bed(0)
        bedList.add(bedZero)


        /** Init code*/
        initColors()   //Sets colors of various UI elements
        gridCreate(50, 2, constraintLayout, this@MainActivity)  //Creates the garden bed grid
        turretSquare = allSquares[((buttonsPerRow * buttonsPerRow) - 1) / 2]  //Gets the location of the central square of the garden bed
        initializeButtons(this@MainActivity, doneButton, deleteButton,bluetoothButton)  //Initializes button listeners

    }

    /***
     * DATA CLASSES
     */

    data class Square(val squareId: Int)        //object containing tile information
    {
        var row: Int = 0
        var column: Int = 0
        var bedID: Int = 0
        var hasBed: Boolean = false
        var angle: Double? = null
        var distance: Double? = null
        var button: Button? = null
        var color: Int = ColorData.deselected
        var isInvisible = false

        fun changeColor(newColor: Int) {
            button!!.setBackgroundColor(newColor)
        }
    }

    data class Bed(val bedID: Int) {
        var squaresInBed = mutableListOf<Square>()
        var bedColor: Int? = null
        //other variables
    }

    /***
     *  INIT FUNCTIONS
     */

    private fun initColors(){
        topBar.setBackgroundColor(ColorData.uiColor1_light)
        zoomLayout.setBackgroundColor(ColorData.uiInvisible)
        bottomText.setBackgroundColor(ColorData.uiColor_white)
        gridContainer.setBackgroundColor(ColorData.uiInvisible)
        topText.setBackgroundColor(ColorData.uiColor1_medium)
    }



    fun initializeButtons(context: Context, doneButton: Button, deleteButton: Button,bluetoothButton:Button)
    {
        doneButton.setOnClickListener()
        {

            doneBed(context)

        }

        deleteButton.setOnClickListener()
        {
            deleteBed()
        }

        bluetoothButton.setOnClickListener {
            val intent = Intent(this, BluetoothActivity::class.java).apply {

            }
            startActivity(intent)
        }

        bedSettings.setOnClickListener {
            if(paramMenuOpen){
                paramMenuContainer.visibility = View.GONE
                doneButton.visibility = View.VISIBLE
                deleteButton.visibility = View.VISIBLE
                paramMenuOpen = !paramMenuOpen
            }
            else{
                paramMenuContainer.visibility = View.VISIBLE
                doneButton.visibility = View.INVISIBLE
                deleteButton.visibility = View.INVISIBLE
                paramMenuOpen = !paramMenuOpen
            }
        }

        //space for further buttons (setting, bluetooth, etc)
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


    /**
     * POST-INIT FUCTIONS
     * */


    /** Checks for adjacent squares to current bed being created/edited */

    fun adjacentSquareColorCheck(squareID: Int) {


        //Holds the square id for squares surrounding the current square
        var leftSquare : Square? = null
        var rightSquare : Square? = null
        var aboveSquare : Square? = null
        var belowSquare : Square? = null

        /** get adj square ids -- null if square is beyond bed bounds */

        if(squareID % buttonsPerRow == 0){ }
        else{ leftSquare = allSquares[squareID - 1]}         //Left

        if((squareID + 1) % buttonsPerRow == 0) { }
        else{ rightSquare = allSquares[squareID + 1]}    //Right

        if(squareID < buttonsPerRow){}
        else {aboveSquare = allSquares[squareID - buttonsPerRow]}   //Above

        if(squareID >= ((buttonsPerRow * buttonsPerRow)- buttonsPerRow)){}
        else {belowSquare = allSquares[squareID + buttonsPerRow]}   //Below


        /** Checks adjacent areas for either the turret or null (no square), in which case do nothing
         * Otherwise, if the square in question doesn't belong to a bed, color it with the adjacent square color*/

        if (leftSquare == null || leftSquare == turretSquare) { }
        else if (!leftSquare.hasBed)
        {
            leftSquare.changeColor(ColorData.adjacent)
            adjacentSquares.add(leftSquare)
        }
        if (rightSquare == null || rightSquare == turretSquare) { }
        else if (!rightSquare.hasBed) {
            rightSquare.changeColor(ColorData.adjacent)
            adjacentSquares.add(rightSquare)
        }
        if (belowSquare == null || belowSquare == turretSquare) { }
        else if (!belowSquare.hasBed) {
            belowSquare.changeColor(ColorData.adjacent)
            adjacentSquares.add(belowSquare)
        }
        if (aboveSquare == null || aboveSquare == turretSquare) { }
        else if (!aboveSquare.hasBed) {
            aboveSquare.changeColor(ColorData.adjacent)
            adjacentSquares.add(aboveSquare)
        }
    }


    fun buildBed(context: Context, button: Button) {

        val thisSquare = allSquares[button.id - 10000]      //square you have just clicke don
        if (!paramMenuOpen) {
            if (bedEdit[0] == 0)     //if not in editing mode (ie creating new bed)
            {
                doneButton.visibility = View.VISIBLE
                if (thisSquare.bedID == 0)          //check if tile is currently in any bed, do nothing if already in bed
                {
                    if (thisSquare.hasBed == true)    //remove selected square from tempbed
                    {
                        thisSquare.hasBed = false
                        thisSquare.changeColor(ColorData.deselected)
                        tempBed.remove(thisSquare)

                        removeAdjacentSquares()
                        for (i in 0..tempBed.size - 1) {
                            adjacentSquareColorCheck(tempBed[i].squareId - 10000)
                        }


                    } else         //add selected square to tempbed
                    {
                        if (isSquareAdjacent(button, tempBed))         //check is square is adjacent
                        {
                            adjacentSquareColorCheck(thisSquare.squareId - 10000)
                            thisSquare.hasBed = true
                            thisSquare.changeColor(ColorData.selected)
                            adjacentSquares.removeAll(Collections.singleton(thisSquare))
                            tempBed.add(thisSquare)

                            Log.d("tempBed", "tempBed has: " + tempBed)
                            Log.d("tempBed", "adjSq has " + adjacentSquares)

                        }
                    }
                }
            } else  //if in editing mode
            {
                adjacentSquares.removeAll(Collections.singleton(thisSquare))

                if (thisSquare.bedID == bedEdit[1])     //remove square from selected bed
                {
                    thisSquare.bedID = 0
                    thisSquare.hasBed = false
                    thisSquare.changeColor(ColorData.deselected)
                    bedList[bedEdit[1]].squaresInBed.remove(thisSquare)


                } else if (thisSquare.bedID == 0)     //add new adjacent squares to selected bed
                {
                    if (isSquareAdjacent(button, bedList[bedEdit[1]].squaresInBed)) {
                        thisSquare.bedID = bedEdit[1]
                        thisSquare.hasBed = true
                        thisSquare.changeColor(bedList[bedEdit[1]].bedColor!!)
                        bedList[bedEdit[1]].squaresInBed.add(thisSquare)
                    }
                }

                removeAdjacentSquares()
                for (i in 0..bedList[bedEdit[1]].squaresInBed.size - 1) {
                    adjacentSquareColorCheck(bedList[bedEdit[1]].squaresInBed[i].squareId - 10000)
                }
            }
        }
    }

    fun editBed(bedToEdit: Int) {
        //val bedToEdit = 1   //figure out for to set this via clicking card in RV
        deleteButton.visibility = View.VISIBLE
        bedSettings.visibility = View.VISIBLE
        doneButton.visibility = View.VISIBLE

        bedEdit[0] = 1      //bool to toggle editing mode
        bedEdit[1] = bedToEdit      //bedID that is being edited


        removeAdjacentSquares()
        for (i in 0..bedList[bedEdit[1]].squaresInBed.size - 1)
        {
            adjacentSquareColorCheck(bedList[bedEdit[1]].squaresInBed[i].squareId - 10000)
        }

    }

    fun deleteBed()
    {
        for(i in 0..bedList[bedEdit[1]].squaresInBed.size - 1)
        {
            bedList[bedEdit[1]].squaresInBed[i].bedID = 0
            bedList[bedEdit[1]].squaresInBed[i].hasBed = false
            bedList[bedEdit[1]].squaresInBed[i].color = ColorData.deselected
            bedList[bedEdit[1]].bedColor = null
        }
        bedList[bedEdit[1]].bedColor = null

        //add remove recyclerview functionality here

    }

    //check if the selected square is adjacent to the current tiles in your bed
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
    fun addBedToRV(bedColor : Int)
    {
        rvBedList.add(RVBedData("Bed #" + bedCount, bedCount,bedColor))

        val adapter = BedAdapter(rvBedList, {bed : RVBedData -> bedClicked(bed)})

        RVBedXML.adapter = adapter
    }

    private fun bedClicked(bed : RVBedData)
    {
        editBed(bed.rvBedID)

        Toast.makeText(this, "Editing: ${bed.name}", Toast.LENGTH_LONG).show()
    }


    //creates bed object, adds completed bed to list, sets stage for next bed
    fun doneBed(context: Context)
    {
        removeAdjacentSquares()   //Resets adjacent square visibility
        doneButton.visibility = View.GONE   //Hides done button
        deleteButton.visibility = View.GONE    //Hides
        bedSettings.visibility = View.GONE
        if (tempBed.isNotEmpty() && bedEdit[0] == 0) {      //only executes when there is new bed, otherwise updates done on click
         //   ColorData.newRandomBedColor()
            ColorData.newBedColor()
            addBedToRV(ColorData.nextBedColor!!)

            var finalBed = Bed(bedCount)     //contains final tile IDs
            finalBed.bedColor = ColorData.nextBedColor

            for (i in 0..tempBed.size - 1)       //update tiles with appropriate bed ID & adds tile IDs to list for Bed
            {
                allSquares[tempBed[i].squareId - 10000].bedID = bedCount
                allSquares[tempBed[i].squareId - 10000].changeColor(ColorData.nextBedColor!!)
                allSquares[tempBed[i].squareId - 10000].color = ColorData.nextBedColor!!
                finalBed.squaresInBed.add(tempBed[i])
            }

            bedList.add(finalBed)               //add completed Bed to master list and set up for the next
            tempBed.clear()

            bedCount++
            removeAdjacentSquares()

        } else {
            bedEdit[0] = 0      //reset editing bool
        }
    }



    //clear all adjacent square colouring
    fun removeAdjacentSquares()
    {
        for(i in adjacentSquares){
            i.changeColor(ColorData.deselected)
        }
        adjacentSquares.clear()
    }




/* Angle and Distance Function
   b Takes a target square and a central turret square
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