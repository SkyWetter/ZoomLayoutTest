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


 */


package com.example.android.zoomlayouttest


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.otaliastudios.zoom.ZoomLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.PI
import kotlin.math.sign
import kotlin.math.sqrt
import kotlin.math.atan


class MainActivity : AppCompatActivity() {

    val squareList = mutableListOf<String>()
    var allTiles = mutableListOf<Square>()       //full list of all Tile objects

    var tempBed = mutableListOf<Int>()
    var bedList = mutableListOf<Bed>()
    var turretSquare : Square? = null


    var buttonsPerRow = 5
    var bedCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gardenBedView = findViewById<ZoomLayout>(R.id.zoomLayout)  // zoom

        val constraintSet = ConstraintSet()    //Creates a new constraint set variable
        constraintSet.clone(buttonContainer)  //Clones the buttonContainer constraint layout settings
        val constraintLayout = findViewById(R.id.buttonContainer) as ConstraintLayout

        val doneButton = findViewById(R.id.doneButton) as Button


        //buttons per row parameter in gridCreate must be odd
        gridCreate(50, 2, buttonsPerRow, constraintSet, constraintLayout, this@MainActivity, allTiles, tempBed)

        turretSquare = allTiles[((buttonsPerRow*buttonsPerRow) -1 )/2]

        getAngleDistanceAll(allTiles,turretSquare!!)

   /*   //Test code for getAngleDistanceAll function
        var tempSq = 3
        Log.i("Squareid: ",allTiles[tempSq].squareId.toString())
        Log.i("angle and dis",allTiles[tempSq].angle.toString() + " " + allTiles[tempSq].distance.toString())

   */
        initializeButtons(this@MainActivity, doneButton, bedList, tempBed, bedCount)


    }
}

//GRID CREATE
/* Function for populating a list of all squares in garden grid */

//Takes a button size (how large each individual button is), margins between each button, buttons per row (grid is always square)
//Must also pass the parent Constraint Layout view holding the grid, and pass this@MainAtivity into context

fun gridCreate(buttonSize : Int, buttonMargin : Int, buttonsPerRow: Int, constraintSet : ConstraintSet, constraintLayout: ConstraintLayout,
               context : Context, allTiles: MutableList<Square>, tempBed: MutableList<Int>)
{

    var previousButton = Button(context)            //Tracks the previous button created
    var previousRowLeadButton = Button(context)     //Tracks the first button of the previous row
    var idNumber = 10000                           //id#, increments with each created button

    if((buttonsPerRow % 2) !=0)
    {   for(row in 0..(buttonsPerRow - 1))             //For this given row
        {

                for (i in 0..(buttonsPerRow - 1))      //And this given square
                {

                    val button = Button(context)       //Create a new button
                    button.setId(idNumber)             //Set id based on idNumber incrementor

                    var tempTile = Square(idNumber)      //create new tile object with tileID

                    tempTile.column = i                 //set rows and columns
                    tempTile.row = row

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

                        }

                        else
                        {
                            constraintSet.connect(button.id, ConstraintSet.LEFT, constraintLayout.id, ConstraintSet.LEFT, buttonMargin)         //SETS CONSTRAINTS
                            constraintSet.connect(button.id, ConstraintSet.TOP, previousRowLeadButton.id, ConstraintSet.BOTTOM, buttonMargin)
                            constraintSet.setMargin(button.id, ConstraintSet.LEFT, 0)
                        }

                        previousRowLeadButton = button

                    }

                    else
                    {

                            constraintSet.connect(button.id, ConstraintSet.LEFT, previousButton.id, ConstraintSet.RIGHT, buttonMargin)          //SETS CONSTRAINTS
                            constraintSet.connect(button.id, ConstraintSet.BASELINE, previousButton.id, ConstraintSet.BASELINE, buttonMargin)
                            constraintSet.setMargin(button.id, ConstraintSet.TOP, 0)

                    }


                        constraintSet.constrainWidth(button.id, buttonSize)                 //Sets size of created button
                        constraintSet.constrainHeight(button.id, buttonSize)

                        var tempNum = (buttonsPerRow - 1) / 2

                        if(i == tempNum && row == tempNum)
                        {
                            button.setBackgroundColor(Color.RED)
                        }
                        else
                        {
                            button.setBackgroundColor(Color.WHITE)                              //Sets color (To be replaced with final button styling)
                        }

                        constraintLayout.addView(button)                                    //Add button into Constraint Layout view
                        constraintSet.applyTo(constraintLayout)                             //Apply constraint styling

                        button.setOnClickListener()                                         //TEST FUNCTION FOR CLICK OF SQUARE
                        {

                                buildBed(context, button, allTiles, tempBed)                         //add/remove tiles from bed when clicked
                        }

                        previousButton = button
                }

        }

    } else{
        Log.i("ERROR!","You can't have gridCreate buttonsPerRow == an even number")
    }
}

fun initializeButtons(context: Context, doneButton: Button, bedList: MutableList<Bed>, tempBed: MutableList<Int>, bedCount: Int)
{
    doneButton.setOnClickListener()
    {
        doneBed(context, bedList, tempBed, bedCount)
    }

    //space for further buttons (setting, bluetooth, etc)
}

fun buildBed(context: Context, button: Button, allTiles: MutableList<Square>, tempBed: MutableList<Int> )
{
    val buttonID: Int = button.id - 10000

    if (allTiles[buttonID].hasBed == true)    //remove from bed if present in list
    {
        tempBed.remove(buttonID + 10000)
        allTiles[buttonID].hasBed = false
        button.setBackgroundColor(Color.WHITE)
        Toast.makeText(context, "Removed " + button.id + " from bed", Toast.LENGTH_SHORT).show()
    }
    else    //add to bed if not
    {
        tempBed.add(buttonID + 10000)
        allTiles[buttonID].hasBed = true
        button.setBackgroundColor(Color.BLUE)
        Toast.makeText(context, "Added " + button.id + " to bed", Toast.LENGTH_SHORT).show()
    }
}

fun doneBed(context: Context, bedList: MutableList<Bed>, tempBed: MutableList<Int>, bedCount: Int)
{
    var finalBed = Bed(bedCount)
    finalBed.tilesInBed = tempBed
    bedList.add(finalBed)
    tempBed.clear()
    Toast.makeText(context, "Bed #" + bedCount + " created", Toast.LENGTH_SHORT).show()
}

data class Square (val squareId: Int)        //object containing tile information
{
    var row: Int = 0
    var column: Int = 0
    var bedID: Int = 0
    var hasBed: Boolean = false
    var angle: Double? = null
    var distance: Double? = null
}

data class Bed (val bedID: Int)
{
    var tilesInBed = mutableListOf<Int>()
    //other variables
}


/* Angle and Distance Function
    Takes a target square and a central turret square
 */

private fun getAngleDistanceAll(allTiles: MutableList<Square>,turretSquare: Square){
    for(square in allTiles.indices){
        getAngleDistance(allTiles[square],turretSquare)
    }
}


private fun getAngleDistance(targetSquare:Square, turretSquare:Square){

    var x = (targetSquare.column - turretSquare.column)
    var y = (targetSquare.row - turretSquare.column)


    val squaredCoords = (x*x) + (y*y)

    targetSquare.distance = sqrt(squaredCoords.toDouble())


    var quadrant: Int

    if(x.sign==-1) {
        if (y.sign == -1) { quadrant = 2 } else { quadrant = 3 }
    } else {
        if(y.sign == -1) { quadrant = 1 } else { quadrant = 4 }
    }

    val temp : Double = (x.toDouble()/y.toDouble())

    if(quadrant == 1){

        targetSquare.angle = (atan(temp)*180)/ PI
    }
    else if(quadrant == 2){

        targetSquare.angle = 180 - ((atan(temp)*180)/ PI)
    }
    else if(quadrant == 3){

        targetSquare.angle = 180 + (atan(temp)*180)/ PI
    }else{

        targetSquare.angle = 360 - (atan(temp)*180)/ PI
    }
}


/*
Dave increment code

myNumber.x += 1

class MainActivity : AppCompatActivity() {

val myNumber = IncVar(2)

override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContentView(R.layout.activity_main)
}

fun incremNumber(intToChange: IncVar){
intToChange.number +=1
}

data class IncVar(var number: Int)
}

 */