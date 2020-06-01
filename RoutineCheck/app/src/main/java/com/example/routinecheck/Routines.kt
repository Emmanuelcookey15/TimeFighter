package com.example.routinecheck

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "routines_table")
class Routines(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "routine_name")
    val name: String,

    @ColumnInfo(name = "routine_description")
    val text: String,

    @ColumnInfo(name = "routine_timing")
    val timing: String)