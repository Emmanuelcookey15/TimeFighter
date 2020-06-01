package com.example.routinecheck

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RoutineAdapter(var ctx: Context, var routines : List<Routines>): RecyclerView.Adapter<RoutineAdapter.RoutineHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineHolder {

        var value = LayoutInflater.from(ctx).inflate(R.layout.item_routine, parent, false)
        return  RoutineHolder(value)
    }

    override fun getItemCount(): Int {
        return routines.size
    }

    override fun onBindViewHolder(holder: RoutineHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    class RoutineHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val routineName = itemView.findViewById<LinearLayout>(R.id.text_name)
        val routineText = itemView.findViewById<TextView>(R.id.text_descrip)
        val routineFrequency = itemView.findViewById<TextView>(R.id.text_timing)

   }

}

