package com.nepa.ng.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "all_vendor_table")
class AllVendorTable {

    @PrimaryKey
    var id: Int?=null
    var name: String=""
    var logo: String=""
    var slug: String=""
    var cities: String=""
    var disco_name: String=""

}