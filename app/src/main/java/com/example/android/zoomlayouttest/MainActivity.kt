package com.example.android.zoomlayouttest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /* Square listeners, output a simple Toast text response. Will be made in code with for loop eventually */

        sq000.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 0'", Toast.LENGTH_SHORT).show() }
        sq001.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 1'", Toast.LENGTH_SHORT).show() }
        sq002.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 2'", Toast.LENGTH_SHORT).show() }
        sq003.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 3'", Toast.LENGTH_SHORT).show() }
        sq004.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 4'", Toast.LENGTH_SHORT).show() }
        sq005.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 5'", Toast.LENGTH_SHORT).show() }
        sq006.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 6'", Toast.LENGTH_SHORT).show() }
        sq007.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 7'", Toast.LENGTH_SHORT).show() }
        sq008.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 8'", Toast.LENGTH_SHORT).show() }
        sq009.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 9'", Toast.LENGTH_SHORT).show() }
        sq010.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 10'", Toast.LENGTH_SHORT).show() }
        sq011.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 11'", Toast.LENGTH_SHORT).show() }
        sq012.setOnClickListener{ Toast.makeText(this@MainActivity,"Square 12'", Toast.LENGTH_SHORT).show() }



    }


}
