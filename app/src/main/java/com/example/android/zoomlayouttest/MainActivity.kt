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

import android.graphics.Matrix;
import android.widget.ImageView;
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.otaliastudios.zoom.ZoomLayout
import com.transitionseverywhere.Rotate
import com.transitionseverywhere.TransitionManager
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.util.*
import kotlin.math.*
import com.transitionseverywhere.*



class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private val debugging = false // Set true to access control debug menu within the bluetooth settings tab
    private val sprayEachSquare = false // set to true to have every square press send a spray command
    private val tag = "MainActivityDebug"  //Tag for debug


    /** Bluetooth Variables */

    var messages : StringBuilder? = null  // Used by broadcast receiver for
    private var mBluetoothAdapter : BluetoothAdapter? = null
    var mBTDevices = mutableListOf<BluetoothDevice>()
    var mDeviceListAdapter: DeviceListAdapter? = null
    var lvNewDevices : ListView? = null
    var mBTDevice : BluetoothDevice? = null
    private val myUUIDInsecure = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var mBluetoothConnection : BluetoothConnectionService? = null
    //Moved turretSquare outside of companion object under IDE recommendation to avoid memory leaks
    private var turretSquare: Square? = null         //The middle square of the bed, not to be used as a regular garden bed square

    /** Main Menu Variables */

    companion object {


        private val adjacentSquares = mutableListOf<Square>()//List of squares adjacentSquare to a given bed
        var bedList = mutableListOf<Bed>()              //list of all saved beds
        private var allSquares = mutableListOf<Square>()       //full list of all squares in the grid
        private var tempBed = mutableListOf<Square>()       //bed containing newly selected squares pre-save
        private var paramMenuOpen = false
        var bedBeingEdited  = RVBedData("",999999,Color.argb(255,0,0,0))  //Blank bedData class to hold currently edited bed
        var bedCount = 1
        var bedEdit = intArrayOf(0, 0)   //[0] is "boolean" for editing mode, [1] is bedID to be edited
        private val rvBedList = ArrayList<RVBedData>()      //bedlist for the recyclerview

        private var buttonsPerRow = 11              /** MUST BE ODD NUMBER*/  //Number of squares per row of the garden bed
        private val constraintSet = ConstraintSet()    //Used to define constraint parameters of each square of garden bed

    }

    /**
     *
     * BLUETOOTH RECEIVERS
     *
     * */

    private val mBroadcastReceiver1 = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {         //Function onReceive is a default member function of the BroadcastReceiver class
            val action: String = intent.action
            //When discovery finds a device
            if(action == BluetoothAdapter.ACTION_STATE_CHANGED){
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

                when(state){
                    BluetoothAdapter.STATE_OFF->{ Log.d(tag,"onReceive: STATE OFF")}
                    BluetoothAdapter.STATE_TURNING_OFF->{
                        Log.d(tag,"mBroadcastReceiver1: STATE TURNING OFF")}
                    BluetoothAdapter.STATE_ON->{
                        Log.d(tag,"mBroadcastReceiver1: STATE ON")}
                    BluetoothAdapter.STATE_TURNING_ON->{
                        Log.d(tag,"mBroadcastReceiver1: STATE TURNING ON")}

                }
            }
        }
    }

    private val mBroadcastReceiver2 = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {         //Function onReceive is a default member function of the BroadcastReceiver class

            val action: String = intent.action
            //When discovery finds a device
            if(action == BluetoothAdapter.ACTION_SCAN_MODE_CHANGED){
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

                when(state){
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE->{ Log.d(tag,"mBroadcastReceiver2: Discoverability Enabled")}
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE->{
                        Log.d(tag,"mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.")}
                    BluetoothAdapter.SCAN_MODE_NONE->{
                        Log.d(tag,"mBroadcastReceiver2: Discoverability Disabled. Unable to receive connections.")}
                    BluetoothAdapter.STATE_CONNECTING->{
                        Log.d(tag,"mBroadcastReceiver2: connecting... ")}
                    BluetoothAdapter.STATE_CONNECTED->{
                        Log.d(tag,"mBroadcastReceiver2: Connected.")}

                }
            }
        }
    }

    private val mBroadcastReceiver3 = object : BroadcastReceiver(){

        override fun onReceive(context: Context, intent: Intent){
            Log.d(tag,"onReceive: ACTION FOUND")
            val action: String = intent.action

            //When discovery finds a device
            if(action == BluetoothDevice.ACTION_FOUND){
                val device : BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                mBTDevices.add(device)
                Log.d(tag,"onReceive: " + device.name + ": " + device.address)
                mDeviceListAdapter = DeviceListAdapter(context,R.layout.device_adapter_view,mBTDevices)
                lvNewDevices!!.adapter = mDeviceListAdapter

            }
        }
    }

    private val mBroadcastReceiver4 = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action

            if(action == BluetoothDevice.ACTION_BOND_STATE_CHANGED){
                val mDevice : BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                //3 cases
                //case 1: bonded already
                if(mDevice.bondState== BluetoothDevice.BOND_BONDED){
                    Log.d(tag,"BroadcastReceiver: BOND_BONDED")
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice
                }
                //case 2: creating a bond
                if(mDevice.bondState == BluetoothDevice.BOND_BONDING){
                    Log.d(tag,"BroadcastReceiver: BOND_BONDING")

                }
                //case 3: breaking a bond
                if(mDevice.bondState == BluetoothDevice.BOND_NONE){
                    Log.d(tag,"BroadcastReceiver: BOND_NONE")
                }
            }
        }
    }

    /**  Broadcast receiver for receiving text from the Bluetooth Connection Service*/

    private val mMessageReceiver: BroadcastReceiver = object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
           var text = intent?.getStringExtra("theMessage")

            messages?.append(text + "\n")

            incomingTextBox.text = messages
        }
    }

    override fun onDestroy(){
        Log.d(tag,"onDestroy: called.")
        super.onDestroy()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver1)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver2)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver3)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver4)

    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()!!.hide()        //Removes the top action bar of the android ui
        setContentView(R.layout.activity_main)


        /**
         *
         * NEW BLUETOOTH STUFF
         *
         * */

        messages = StringBuilder()
        var incomingMessages = findViewById<TextView>(R.id.incomingTextBox)
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, IntentFilter("incomingMessage"))

        lvNewDevices = findViewById(R.id.lvNewDevices)
        val etSend: EditText = findViewById(R.id.editText)
        bluetoothContainer.visibility = View.GONE
        btnONOFF.visibility = View.VISIBLE
        btnDiscover.visibility =View.GONE
        btnEnableDisable_Discoverable.visibility = View.GONE
        btnStartConnection.visibility = View.GONE
        btnReset.visibility = View.GONE
        btnReturn.visibility = View.GONE


        messageText.text = "Hey there! Let's get your bluetooth started. \n Just press the BIG BUTTON"

        //Broadcasts when bond state changes (ie: pairing)
        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(mBroadcastReceiver4,filter)

        //Gets this phones default adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        lvNewDevices!!.onItemClickListener = this@MainActivity

        btnReset.setOnClickListener {
            btnReturn.visibility = View.GONE
            btnReset.visibility = View.GONE
            btnDiscover.visibility = View.VISIBLE
        }

        btnReturn.setOnClickListener {
            if(!debugging){
                bluetoothContainer.visibility = View.GONE
                mainScreenContainer.visibility = View.VISIBLE
            }
            else {
                debugWindow.visibility = View.VISIBLE
                btnReset.visibility = View.GONE
                btnReturn.visibility = View.GONE
            }

        }

        btnONOFF.setOnClickListener{

            if(mBluetoothAdapter!!.isEnabled){

                btnONOFF.visibility = View.GONE
                btnDiscover.visibility = View.VISIBLE
                messageText.text = "Great, it looks like your bluetooth is already enabled! \n Would you push the look for bluetooth button? "

            }
            else {

                messageText.text = "Alright, enabling bluetooth!"
                Log.d(tag, "onClick: enabling/disabling bluetooth.")
                enableDisableBT()
            }
        }

        btnEnableDisable_Discoverable.setOnClickListener{
            Log.d(tag,"onClick: btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.")

            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            startActivity(discoverableIntent)

            val intentFilter  = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
            registerReceiver(mBroadcastReceiver2,intentFilter)


        }

        btnDiscover.setOnClickListener{
            if(!mBluetoothAdapter!!.isEnabled){
                enableDisableBT()
            }

            if(mDeviceListAdapter != null) {
                mBTDevices.clear()
                mDeviceListAdapter!!.notifyDataSetChanged()
            }

            Log.d(tag,"btnDiscover: Looking for unpaired devices.")

            if(mBluetoothAdapter!!.isDiscovering){
                mBluetoothAdapter!!.cancelDiscovery()
                Log.d(tag,"btnDiscover: Cancelling discovery.")

                /** Required permission check for any API > Android Lollipop*/
                checkBTPermissions()

                mBluetoothAdapter!!.startDiscovery()
                val discoverDevicesIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent)
            }
            if(!mBluetoothAdapter!!.isDiscovering){

                //Check BT permission in manifest
                checkBTPermissions()

                mBluetoothAdapter!!.startDiscovery()
                val discoverDevicesIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent)
            }

            messageText.text = "Look for the Rainbow turret in the list below \n If you can't find it, please press 'Find Bluetooth Devices' again!"
        }

        btnStartConnection.setOnClickListener{

            if(mBTDevice != null) {

                /**  NEED TO ADD CHECK FOR ESP NAME HERE, TO ENSURE USER CONNECTS TO THE RAINBOW*/
                messageText.text = "Great, you're connected to the Rainbow, pew pew! \n" +
                        "Let's show you how to wet the bed!"

                btnStartConnection.visibility = View.GONE
                btnReset.visibility = View.VISIBLE
                btnReturn.visibility = View.VISIBLE

                startBTConnection(mBTDevice!!, myUUIDInsecure)

            }
            else{
                Toast.makeText(this,
                        "Must be paired to a device before opening connection", Toast.LENGTH_LONG).show()
            }
        }
        /***
         * DEBUG STEPPER CONTROL BUTTONS -- set to View.GONE at runtime
         *
         */

        btnSend.setOnClickListener{
            val tempString = etSend.text.toString()
            val tempByte: ByteArray = tempString.toByteArray(Charset.defaultCharset())
            if(mBluetoothConnection!=null) {
                mBluetoothConnection!!.write(tempByte)
            }
        }

        paramMenuContainer.visibility = View.GONE //Hides the bed settings menu on start
        constraintSet.clone(gridContainer)                  //Clones the bguttonContainer constraint layout settings
        val constraintLayout = findViewById<ConstraintLayout>(R.id.gridContainer)   //gets the layout of the garden bed container
        val doneButton = findViewById<Button>(R.id.doneButton)      //saves the current bed in tempbed to bedlist

        //this sets up the recyclerview
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = BedAdapter(rvBedList){bed : RVBedData -> bedClicked(bed)}

        val swipeHandler = object : SwipeToDeleteCallback(this)
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
            {
                bedEdit[0] = 0
                removeAdjacentSquares()
                val adapter = recyclerView.adapter as BedAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                deleteBed()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        //load bedList[0] so bed1 can be in bedList[1] lol
        val bedZero = Bed(0)
        bedList.add(bedZero)

        /** Init code*/
        initColorsFonts()   //Sets colors of various UI elements
        initUiVisibility()
        gridCreate(50, 2, constraintLayout, this@MainActivity)  //Creates the garden bed grid
        turretSquare = allSquares[((buttonsPerRow * buttonsPerRow) - 1) / 2]  //Gets the location of the central square of the garden bed
        turretSquare?.button?.setBackgroundResource(R.drawable.turret)
        initializeButtons(this@MainActivity, doneButton,bluetoothButton)  //Initializes button listeners
        getAngleDistanceAll(allSquares,turretSquare!!)



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
        var color: Int = ColorData.deselectedSquare
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
     * INIT FUNCTIONS // LISTENERS
     */

    fun setWaterLevelText(){
        when(bedBeingEdited.waterLevel){
            0 -> currentWaterLevel.text = "Low"
            1 -> currentWaterLevel.text = "Medium"
            2 -> currentWaterLevel.text = "High"
        }
    }

    private fun initUiVisibility(){

        bluetoothContainer.visibility = View.GONE
        bottomText.visibility = View.VISIBLE
        debugWindow.visibility = View.GONE
        btnONOFF.visibility = View.VISIBLE
        btnDiscover.visibility =View.GONE
        btnEnableDisable_Discoverable.visibility = View.GONE
        btnStartConnection.visibility = View.GONE
        btnReset.visibility = View.GONE
        btnReturn.visibility = View.GONE
        doneButtonContainer.visibility = View.GONE
        paramMenuContainer.visibility = View.GONE
        zoomLayout.visibility = View.VISIBLE
    }

    private fun initColorsFonts(){

        topBar.setBackgroundColor(ColorData.uiColorLightGreen)
        zoomLayout.setBackgroundColor(ColorData.uiInvisible)
        bottomText.setBackgroundColor(ColorData.uiColor_white)
        gridContainer.setBackgroundColor(ColorData.uiInvisible)
        topText.setBackgroundColor(ColorData.uiColorLightGreen)

        val titleFont = Typeface.createFromAsset(assets,"fonts/MontserratAlternates-Medium.ttf")
        bedNameText.typeface = titleFont
    }

    fun dayOfWeekClick(v: View){

        val thisDay = rvBedList[bedBeingEdited.position].daysOfWeek //Shortens code
        val thisAmPmDay = rvBedList[bedBeingEdited.position].amPm
        val tag = v.tag.toString().toInt()  //Converts tag type Any to Int
        var amPmButton : Button? = null
        var dayButton : Button? = null

        for(i in 0 until 7){
            when(tag){
                0 -> {amPmButton = findViewById(R.id.btn_timeSu) ; dayButton = findViewById(R.id.sundayButton)}
                1 -> { amPmButton = findViewById(R.id.btn_timeM) ; dayButton = findViewById(R.id.mondayButton)}
                2 -> { amPmButton = findViewById(R.id.btn_timeT) ; dayButton = findViewById(R.id.tuesdayButton)}
                3 -> { amPmButton = findViewById(R.id.btn_timeW) ; dayButton = findViewById(R.id.wednesdayButton)}
                4 -> { amPmButton = findViewById(R.id.btn_timeTh); dayButton = findViewById(R.id.thursdayButton)}
                5 -> { amPmButton = findViewById(R.id.btn_timeF); dayButton = findViewById(R.id.fridayButton)}
                6 -> { amPmButton = findViewById(R.id.btn_timeS); dayButton = findViewById(R.id.saturdayButton)}
            }
        }

        when(thisDay[tag]){
            0-> {thisDay[tag] = 1 ; v.setBackgroundResource(R.drawable.btn_day_button_1) ; dayButton?.setTextColor(ColorData.uiColorPureWhite)
                                    ; thisAmPmDay[tag] = 1;amPmButton?.setBackgroundResource(R.drawable.btn_am) }
            1-> {thisDay[tag] = 0 ; v.setBackgroundResource(R.drawable.btn_day_button_0)  ; dayButton?.setTextColor(ColorData.uiColorLightGrey)
                ; thisAmPmDay[tag] = 0;amPmButton?.setBackgroundResource(R.drawable.btn_no_am_pm) }

        }
    }

    fun amPmClick(v: View){

        val thisAmPmDay = rvBedList[bedBeingEdited.position].amPm
        val tag = v.tag.toString().toInt()

        when(thisAmPmDay[tag]){
            1-> {thisAmPmDay[tag] = 2 ; v.setBackgroundResource(R.drawable.btn_pm)}
            2 ->{thisAmPmDay[tag] = 3 ; v.setBackgroundResource(R.drawable.btn_am_pm)}
            3 ->{thisAmPmDay[tag] = 1 ; v.setBackgroundResource(R.drawable.btn_am)}
        }
    }

    fun onDebugButtonClick(v: View){
        val tempString = v.tag.toString()
        val tempByte: ByteArray = tempString.toByteArray(Charset.defaultCharset())
        if(mBluetoothConnection!=null) {
            mBluetoothConnection!!.write(tempByte)
        }
    }

    private fun initializeButtons(context: Context, doneButton: Button, bluetoothOpenMenuButton:Button)
    {

//Functions global to multiple listeners


        doneButton.setOnClickListener()
        {

            doneBed()

        }

        bluetoothOpenMenuButton.setOnClickListener {

            bluetoothContainer.visibility = View.VISIBLE
            mainScreenContainer.visibility = View.GONE

        }

        settingsButton.setOnClickListener {
        }



        /**
         * Bed name text listener -- needs to be adjusted for focus change actions
         */
        bedNameText.setOnClickListener{
            bedNameText.inputType = InputType.TYPE_CLASS_TEXT
       }

        //Edit text listener for Bed Name (all functions are required, even empty ones)

        bedNameText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                recyclerView.adapter.notifyDataSetChanged()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                //Change name of the rvBedList value to that of the current edit Text string
                rvBedList[bedBeingEdited.position].name = bedNameText.text.toString()


            }
        })

        //Seekbar listener for waterLevel bar, all functions (even empty ones) are required

        waterLevelBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {

            override
            fun onStopTrackingTouch(seekBar: SeekBar) {

            }

            override
            fun onStartTrackingTouch(seekbar: SeekBar) {

            }

            override
            fun onProgressChanged(seekBar: SeekBar, progress : Int,fromUser : Boolean) {

              // Gets and sets value from progress bar to RVBedData
                bedBeingEdited.waterLevel = waterLevelBar.progress
                rvBedList[bedBeingEdited.position].waterLevel = bedBeingEdited.waterLevel

                setWaterLevelText()
            }
        })

        btn_confirmBedSettings.setOnClickListener{
            openBedSettings()
        }

    }

//GRID CREATE
/* Function for populating a list of all squares in garden grid */

//Takes a button size (how large each individual button is), margins between each button, buttons per row (grid is always square)
//Must also pass the parent Constraint Layout view holding the grid, and pass this@MainAtivity into context


    private fun gridCreate(buttonSize: Int, buttonMargin: Int, constraintLayout: ConstraintLayout, context: Context) {

        var previousButton = Button(context)            //Tracks the previous button created
        var previousRowLeadButton = Button(context)     //Tracks the first button of the previous row
        var idNumber = 10000                           //id#, increments with each created button

        fun getXY(column: Int, row: Int):String{
            var xy : String

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


                    val tempSquare = Square(idNumber)      //create new tile object with tileID

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

                    val tempNum = (buttonsPerRow - 1) / 2

                    if (i == tempNum && row == tempNum) {
                        tempSquare.changeColor(ColorData.turret)
                        allSquares[((buttonsPerRow * buttonsPerRow) - 1) / 2].bedID = 56789              //arbitrary number to trigger 'unclickable' bed status
                    } else {
                        tempSquare.changeColor(ColorData.deselectedSquare)                            //Sets color (To be replaced with final button styling)
                    }

                    constraintLayout.addView(button)                                    //Add button into Constraint Layout view
                    constraintSet.applyTo(constraintLayout)                             //Apply constraint styling

                    button.setOnClickListener()                                         //TEST FUNCTION FOR CLICK OF SQUARE
                    {
                        buildBed(button)                         //add/remove tiles from bed when clicked
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


    /** Checks for adjacentSquare squares to current bed being created/edited */
    fun spraySingleSquare(){

    }

   private fun adjacentSquareColorCheck(squareID: Int) {


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


        /** Checks adjacentSquare areas for either the turret or null (no square), in which case do nothing
         * Otherwise, if the square in question doesn't belong to a bed, color it with the adjacentSquare square color*/

        if (leftSquare == null || leftSquare == turretSquare) { }
        else if (!leftSquare.hasBed)
        {
            leftSquare.changeColor(ColorData.adjacentSquare)
            adjacentSquares.add(leftSquare)
        }
        if (rightSquare == null || rightSquare == turretSquare) { }
        else if (!rightSquare.hasBed) {
            rightSquare.changeColor(ColorData.adjacentSquare)
            adjacentSquares.add(rightSquare)
        }
        if (belowSquare == null || belowSquare == turretSquare) { }
        else if (!belowSquare.hasBed) {
            belowSquare.changeColor(ColorData.adjacentSquare)
            adjacentSquares.add(belowSquare)
        }
        if (aboveSquare == null || aboveSquare == turretSquare) { }
        else if (!aboveSquare.hasBed) {
            aboveSquare.changeColor(ColorData.adjacentSquare)
            adjacentSquares.add(aboveSquare)
        }
    }


    private fun buildBed(button: Button) {

        val thisSquare = allSquares[button.id - 10000]      //square you have just clicke don

        TransitionManager.beginDelayedTransition(mainLayout,Rotate())
        turretSquare?.button?.rotation = thisSquare.angle!!.toFloat()


      //  turretSquare?.button?.rotation = thisSquare.angle!!.toFloat()

        if (!paramMenuOpen) {
            if (bedEdit[0] == 0)     //if not in editing mode (ie creating new bed)
            {
                doneButtonContainer.visibility = View.VISIBLE   //If creating a bed, make doneButton appear

                if (thisSquare.bedID == 0)          //check if tile is currently in any bed, do nothing if already in bed
                {
                    if (thisSquare.hasBed)    //remove selected square from tempbed
                    {
                        thisSquare.hasBed = false
                        thisSquare.changeColor(ColorData.deselectedSquare)
                        tempBed.remove(thisSquare)

                        removeAdjacentSquares()
                        for (i in 0 until tempBed.size) {
                            adjacentSquareColorCheck(tempBed[i].squareId - 10000)
                        }


                    } else         //add selected square to tempbed
                    {
                        if (isSquareAdjacent(button, tempBed))         //check is square is adjacentSquare
                        {
                            adjacentSquareColorCheck(thisSquare.squareId - 10000)
                            thisSquare.hasBed = true
                            thisSquare.changeColor(ColorData.selected)
                            adjacentSquares.removeAll(Collections.singleton(thisSquare))
                            tempBed.add(thisSquare)

                            Log.d("tempBed", "tempBed has: $tempBed")
                            Log.d("tempBed", "adjSq has $adjacentSquares")

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
                    thisSquare.changeColor(ColorData.deselectedSquare)
                    bedList[bedEdit[1]].squaresInBed.remove(thisSquare)


                } else if (thisSquare.bedID == 0)     //add new adjacentSquare squares to selected bed
                {
                    if (isSquareAdjacent(button, bedList[bedEdit[1]].squaresInBed)) {
                        thisSquare.bedID = bedEdit[1]
                        thisSquare.hasBed = true
                        thisSquare.changeColor(bedList[bedEdit[1]].bedColor!!)
                        bedList[bedEdit[1]].squaresInBed.add(thisSquare)
                    }
                }

                removeAdjacentSquares()
                for (i in 0 until bedList[bedEdit[1]].squaresInBed.size) {
                    adjacentSquareColorCheck(bedList[bedEdit[1]].squaresInBed[i].squareId - 10000)
                }
            }
        }
    }

    private fun editBed(bedToEdit: Int) {
        //val bedToEdit = 1   //figure out for to set this via clicking card in RV


        doneButtonContainer.visibility = View.VISIBLE

        bedEdit[0] = 1      //bool to toggle editing mode
        bedEdit[1] = bedToEdit      //bedID that is being edited


        removeAdjacentSquares()
        for (i in 0 until bedList[bedEdit[1]].squaresInBed.size)
        {
            adjacentSquareColorCheck(bedList[bedEdit[1]].squaresInBed[i].squareId - 10000)
        }

    }

    fun deleteBed()
    {
        for(i in 0 until bedList[bedEdit[1]].squaresInBed.size)
        {
            bedList[bedEdit[1]].squaresInBed[i].bedID = 0
            bedList[bedEdit[1]].squaresInBed[i].hasBed = false
            bedList[bedEdit[1]].squaresInBed[i].color = ColorData.deselectedSquare
            bedList[bedEdit[1]].squaresInBed[i].changeColor(ColorData.deselectedSquare)

        }
        bedList[bedEdit[1]].squaresInBed.clear()
        bedList[bedEdit[1]].bedColor = ColorData.deselectedSquare

        //add remove recyclerview functionality here
    }

    //check if the selected square is adjacentSquare to the current tiles in your bed
    private fun isSquareAdjacent(button: Button, bedSquares: MutableList<Square>): Boolean {
        var squareIsAdjacent = false

        val xBed = IntArray(bedSquares.size)      //x & y for each tile in bed
        val yBed = IntArray(bedSquares.size)

        val xButton = allSquares[button.id - 10000].column       //x & y for tile selected
        val yButton = allSquares[button.id - 10000].row

        val xPermitted = IntArray(bedSquares.size * 4)      //array of permitted tile positions
        val yPermitted = IntArray(bedSquares.size * 4)

        if (bedSquares.isEmpty()) //allow any tile for first selection of new bed
        {
            squareIsAdjacent = true
        }

        if (!squareIsAdjacent) {
            for (i in 0 until bedSquares.size)     //parse the x&y coordinates from the bed
            {
                xBed[i] = allSquares[bedSquares[i].squareId - 10000].column
                yBed[i] = allSquares[bedSquares[i].squareId - 10000].row
            }

            for (i in 0 until bedSquares.size * 4 step 4)        //create list of permitted positions
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

            for (i in 0 until bedSquares.size * 4)        //check button x&y against permitted x&y
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

    //adds numbered cards to recyclerview for each bed/
    private fun addBedToRV(bedColor : Int)
    {
        rvBedList.add(RVBedData("Bed # $bedCount", bedCount,bedColor))
        rvBedList[rvBedList.lastIndex].position = rvBedList.lastIndex    //Finds position of this bed being added, and stores it in the bed's data

        val adapter = BedAdapter(rvBedList){bed : RVBedData -> bedClicked(bed)}

        recyclerView.adapter = adapter
    }

    private fun bedClicked(bed : RVBedData)
    {
        if(doneButtonContainer.visibility != View.VISIBLE) {
            editBed(bed.rvBedID)
            bedBeingEdited = bed
            openBedSettings()
        }
    }

    //creates bed object, adds completed bed to list, sets stage for next bed
    private fun doneBed()
    {
        removeAdjacentSquares()   //Resets adjacentSquare square visibility
        doneButtonContainer.visibility = View.GONE   //Hides done button

        if (tempBed.isNotEmpty() && bedEdit[0] == 0) {      //only executes when there is new bed, otherwise updates done on click
         //   ColorData.newRandomBedColor()
            ColorData.newBedColor()
            addBedToRV(ColorData.nextBedColor!!)

            val finalBed = Bed(bedCount)     //contains final tile IDs
            finalBed.bedColor = ColorData.nextBedColor

            for (i in 0 until tempBed.size)       //update tiles with appropriate bed ID & adds tile IDs to list for Bed
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

    //clear all adjacentSquare square colouring
    fun removeAdjacentSquares()
    {
        for(i in adjacentSquares){
            i.changeColor(ColorData.deselectedSquare)
        }
        adjacentSquares.clear()
    }

    fun openBedSettings(){
        val thisBed = rvBedList[bedBeingEdited.position]

        //If bed settings is already open, makes sure RvBedData of edited bed is updated
        //Changes visibility of necessary components
        //Flips value of paramMenuOpen bool

        if(paramMenuOpen){

            //Loads edited bed values into rvBedData data class
            thisBed.daysOfWeek = bedBeingEdited.daysOfWeek
            thisBed.waterLevel = bedBeingEdited.waterLevel
            thisBed.amPm = bedBeingEdited.amPm

            paramMenuContainer.visibility = View.GONE
            doneButtonContainer.visibility = View.VISIBLE
            bottomText.visibility = View.VISIBLE
            paramMenuOpen = !paramMenuOpen
        }

        //If bed settings is current closed, prepare various bed settings values based on rvbed loaded intot bedBeingEdited var
        //Loading of rvbed data happens in bedClicked fxn in the bedClicked function located outside of initbuttons fxn
        else{

            waterLevelBar.progress = bedBeingEdited.waterLevel
            setWaterLevelText()
            bedNameText.setText(bedBeingEdited.name,TextView.BufferType.EDITABLE)

            //Loads in the day settings for the bed

            for(i in bedBeingEdited.daysOfWeek.indices){
                val dayButtons = arrayListOf<Button>(sundayButton,mondayButton,tuesdayButton,wednesdayButton,thursdayButton,fridayButton,saturdayButton)

                val thisDay = bedBeingEdited.daysOfWeek

                when(thisDay[i]){
                    0-> { dayButtons[i].setBackgroundResource(R.drawable.btn_day_button_0)
                            ; dayButtons[i].setTextColor(ColorData.uiColorLightGrey)}
                    1-> { dayButtons[i].setBackgroundResource(R.drawable.btn_day_button_1)
                            ; dayButtons[i].setTextColor(ColorData.uiColorPureWhite)}

                }
            }

            for(i in bedBeingEdited.amPm.indices){
                val amPmButtons = arrayListOf<Button>(btn_timeSu,btn_timeM,btn_timeT, btn_timeW, btn_timeTh,btn_timeF,btn_timeS)
                val thisAmPm = bedBeingEdited.amPm
                when(thisAmPm[i]){
                    0 -> { amPmButtons[i].setBackgroundResource(R.drawable.btn_no_am_pm)}
                    1 -> { amPmButtons[i].setBackgroundResource(R.drawable.btn_am)}
                    2 -> { amPmButtons[i].setBackgroundResource(R.drawable.btn_pm)}
                    3 -> { amPmButtons[i].setBackgroundResource(R.drawable.btn_am_pm)}

                }
            }



            paramMenuContainer.visibility = View.VISIBLE
            doneButtonContainer.visibility = View.INVISIBLE
            bottomText.visibility = View.GONE
            paramMenuOpen = !paramMenuOpen
        }
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

        val x = (targetSquare.column - turretSquare.column)
        val y = (targetSquare.row - turretSquare.column)
        val squaredCoords = (x * x) + (y * y)
        val quadrant: Int?

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

        when(quadrant){
            1 -> targetSquare.angle = (atan(temp) * 180) / PI
            2 -> targetSquare.angle = 180 - ((atan(temp) * 180) / PI)
            3 -> targetSquare.angle = 180 + (atan(temp) * 180) / PI
            4 -> targetSquare.angle = 360 - (atan(temp) * 180) / PI
        }

    }

    /***
     * BLUETOOTH FUNCTIONS
     */
    private fun startBTConnection(device : BluetoothDevice, uuid : UUID){
        Log.d(tag,"startBTConnection: Initializing RFCOM Bluetooth Connection.")

        mBluetoothConnection!!.startClient(device,uuid)
    }

    private fun enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(tag,"enableDisableBT: Does not have BT capabilities")
        }
        if(!mBluetoothAdapter!!.isEnabled){
            Log.d(tag,"enableDisableBT: enabling BT.")
            val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivity(enableBTIntent)

            /** Filter That intercepts and log changes to your bluetooth status */
            val bTIntent = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            registerReceiver(mBroadcastReceiver1,bTIntent)

            btnONOFF.visibility = View.GONE
            btnDiscover.visibility = View.VISIBLE
        }
        if(mBluetoothAdapter!!.isEnabled){
            mBluetoothAdapter!!.disable()

            val bTIntent = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            registerReceiver(mBroadcastReceiver1,bTIntent)

        }
    }

    /**
     * This method is required for all deices running API23+
     * Android must programmatically check the permission for bluetooth.
     * Putting the proper permissions in the manifest is not enough.
     */

    @SuppressLint("NewApi")
    private fun checkBTPermissions(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            var permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION")
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION")
            if(permissionCheck !=0){

                this.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),1001)
            }else{
                Log.d(tag,"checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.")
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onItemClick(adapterView: AdapterView<*>, view : View, i: Int, l: Long){
        //first cancel discovery because its very memory intensive
        mBluetoothAdapter!!.cancelDiscovery()

        Log.d(tag,"onItemClick: You Clicked on a device.")
        val deviceName : String = mBTDevices[i].name
        val deviceAddress : String = mBTDevices[i].address

        Log.d(tag, "onItemClick: deviceName = $deviceName")
        Log.d(tag, "onItemClick: deviceName = $deviceAddress")

        messageText.text = "You are now paired to $deviceName! \n" +
                "Press 'Connect' to finish the bluetooth set-up process\n" +
                "Press reset to start again and look for more bluetooth devices."
        btnDiscover.visibility = View.GONE
        btnStartConnection.visibility = View.VISIBLE
        btnReset.visibility = View.VISIBLE


        //create the bond
        //NOTE: Requires API 17+
        Log.d(tag,"Trying to pair with $deviceName")
        mBTDevices[i].createBond()

        mBTDevice = mBTDevices[i]

        mBluetoothConnection =  BluetoothConnectionService(this@MainActivity)
    }
}