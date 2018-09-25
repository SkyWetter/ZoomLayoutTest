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


 */


package com.example.android.zoomlayouttest

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.otaliastudios.zoom.ZoomLayout
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val squareList = mutableListOf<String>()
    var allTiles = mutableListOf<Tile>()       //full list of all Tile objects
    var tempBedTiles = mutableListOf<String>()      //temp list of tiles to go in to a bed
    var bedList = mutableMapOf<Int, Bed>()

    var bedNum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gardenBedView = findViewById<ZoomLayout>(R.id.zoomLayout)  // zoom

        val constraintSet = ConstraintSet()    //Creates a new constraint set variable
        constraintSet.clone(buttonContainer)  //Clones the buttonContainer constraint layout settings
        val constraintLayout = findViewById(R.id.buttonContainer) as ConstraintLayout

        val doneButton = findViewById(R.id.doneButton) as Button

        createSquareList(1200, squareList)
        gridCreate(50, 2, 9, constraintSet, constraintLayout, this@MainActivity, tempBedTiles, allTiles, bedList)
        initializeButtons(this@MainActivity, doneButton, tempBedTiles, bedNum, bedList)
    }
}

//GRID CREATE
/* Function for populating a list of all squares in garden grid */

//Takes a button size (how large each individual button is), margins between each button, buttons per row (grid is always square)
//Must also pass the parent Constraint Layout view holding the grid, and pass this@MainAtivity into context

fun gridCreate(buttonSize : Int,buttonMargin : Int, buttonsPerRow: Int,constraintSet : ConstraintSet,constraintLayout: ConstraintLayout,
               context : Context, tempBedTiles: MutableList<String>, allTiles: MutableList<Tile>, bedList: Map<Int, Bed>){

    var previousButton = Button(context)            //Tracks the previous button created
    var previousRowLeadButton = Button(context)     //Tracks the first button of the previous row
    var idNumber = 100000                           //id#, increments with each created button


    for(row in 0..(buttonsPerRow - 1))             //For this given row
    {

            for (i in 0..(buttonsPerRow - 1))      //And this given square
            {

                val button = Button(context)       //Create a new button
                button.setId(idNumber)             //Set id based on idNumber incrementor

                var tempTile = Tile(idNumber)      //create new tile object with tileID

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

                            addTileToBed(context, button, tempBedTiles)                         //add/remove tiles from bed when clicked
                    }

                    previousButton = button
            }

    }
}

fun initializeButtons(context: Context, doneButton: Button, tempBedTiles: MutableList<String>, bedNum: Int, bedList: Map<Int, Bed>)
{
    doneButton.setOnClickListener()
    {
        doneBed(context,tempBedTiles, bedNum, bedList)
    }

    //space for further buttons (setting, bluetooth, etc)
}

fun addTileToBed(context: Context, button: Button, tempBedTiles: MutableList<String>)
{
    if (tempBedTiles.contains(button.id.toString()))    //remove from bed if present in list
    {
        tempBedTiles.remove(button.id.toString())
        Toast.makeText(context, "Removed " + button.id + " from bed", Toast.LENGTH_SHORT).show()
        button.setBackgroundColor(Color.WHITE)
    }
    else    //add to bed if not
    {
        tempBedTiles.add(button.id.toString())
        Toast.makeText(context, "Added " + button.id + " to bed", Toast.LENGTH_SHORT).show()
        button.setBackgroundColor(Color.BLUE)
    }
}

fun doneBed(context: Context, tempBedTiles: MutableList<String>, bedNum: Int, bedList: Map<Int, Bed>)
{
    if(tempBedTiles.isEmpty())
    {
        Toast.makeText(context, "Select some tiles for your bed", Toast.LENGTH_SHORT).show()
    }
    else
    {
        var tempBed = Bed(bedNum)
        tempBed.tilesInBed = tempBedTiles
        bedList[bedNum] = tempBed                       //whack
        tempBedTiles.clear()
        bedNum.inc()
        Toast.makeText(context, "Bed created", Toast.LENGTH_SHORT).show()

    }
}

class Tile (val tileID: Int)        //object containing tile information
{
    var row: Int = 0
    var column: Int = 0
    var bedID: Int = 0
    var hasBed: Boolean = false
}

class Bed (val bedID: Int)
{
    var tilesInBed = mutableListOf<String>()
}

fun createSquareList(buttonsPerRow: Int, squareList: MutableList<String>)
{

    for(i in 0..(buttonsPerRow-1)){

        if(i < 10)
        {
            var tempString: String = "sq000" + i.toString()
            squareList.add(tempString)

        }

        else if(i < 100)
        {
            var tempString: String = "sq00" + i.toString()
            squareList.add(tempString)
        }

        else if(i < 1000)
        {
            var tempString: String = "sq0" + i.toString()
            squareList.add(tempString)
        }

        else{
            squareList.add(i.toString())
        }
    }
}

fun buttonIdToString(currentButtonNumber: Int):String{

    if(currentButtonNumber < 10){
        return "sq00$currentButtonNumber"
    }

    else if(currentButtonNumber < 100){
        return "sq0$currentButtonNumber"
    }

    else {
        return "sq$currentButtonNumber"
    }


}
