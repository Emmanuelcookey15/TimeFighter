package com.capricorn.baxims.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "transactions_table")
data class TransactionTable (

    @PrimaryKey(autoGenerate = true)
    val id:Int?=null,
    var my_outlet_name: String="",
    var outlet_uid: String="",
    var transactions_json_string: String="{}"

)