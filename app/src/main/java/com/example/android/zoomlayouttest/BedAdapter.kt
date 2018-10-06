package com.example.android.zoomlayouttest

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.bedlist_layout.view.*

//I have no idea what is going on
// see https://youtu.be/67hthq6Y2J8


class BedAdapter (val bedList: List<RVBedData>, val clickListener: (RVBedData) -> Unit) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.bedlist_layout, parent, false)
        return BedViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BedViewHolder).bind(bedList[position], clickListener)
    }

    override fun getItemCount()= bedList.size

    class BedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(bed: RVBedData, clickListener: (RVBedData) -> Unit) {
            itemView.textViewBed.text = bed.name
            itemView.setOnClickListener {clickListener(bed)}
            itemView.bedListColor.setBackgroundColor(bed.bedColor)



        }
    }
}
