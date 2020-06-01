package com.example.routinecheck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timing = ArrayList<String>()
        addRoutineTime(timing)
        val adapterTime = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, timing)
        routine_frequency.adapter = adapterTime

        btn_routine_save.setOnClickListener {
            var routineName = routine_name.text
            var routinedescrp = routine_descrip.text
            var routineTime = routine_frequency.selectedItem

            Toast.makeText(this, "Your selection is $routineName $routineTime", Toast.LENGTH_LONG).show()
        }

    }

    private fun addRoutineTime(timing: ArrayList<String>) {
        timing.add("Hourly")
        timing.add("Daily")
        timing.add("Weekly")
        timing.add("Monthly")
        timing.add("Yearly")
    }
}
