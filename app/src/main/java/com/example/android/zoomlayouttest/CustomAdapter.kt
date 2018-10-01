package com.example.android.zoomlayouttest

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.bedlist_layout.view.*
import org.w3c.dom.Text
import com.example.android.zoomlayouttest.ColorData
import java.lang.NullPointerException

//I have no idea what is going on
// see https://youtu.be/67hthq6Y2J8

class CustomAdapter(val rvBedList: ArrayList<rvBed>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder
    {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.bedlist_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int
    {
        return rvBedList.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int)
    {

        val rvbed: rvBed = rvBedList[position]
        holder?.textViewBed?.text = rvbed.name
       holder?.textViewBed?.setBackgroundColor(ColorData.nextBedColor!!)

//        try{holder?.textViewBed?.setBackgroundColor(ColorData.nextBedColor!!)
//        }catch(e: NullPointerException){
//           Log.e("CustomAdapter","onBindViewHolder: nextBedColor is null ${e.printStackTrace()}")
//        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textViewBed = itemView.textViewBed as TextView

    }
}