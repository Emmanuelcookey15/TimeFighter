package com.capricorn.baxims.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "outlet_table")
class OutletTable{

    @PrimaryKey(autoGenerate = true)
    var id: Int?=null
    var userId: Int?=null
    var outletName: String=""

}