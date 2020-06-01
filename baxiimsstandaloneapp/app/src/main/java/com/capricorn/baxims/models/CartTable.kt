package com.capricorn.baxims.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "cartTable",indices = [Index(value = ["ProductId"], unique = true)])
data class CartTable (

    @PrimaryKey(autoGenerate = true)
    val id:Int?=null,
    @ColumnInfo(name = "ProductId")
    var ProductId:Int?=null,
    var unit:Int?=null,
    var sellingPrice: Int?=null,
    var restockLevel: Int?=null,
    var quantiy_in_stock: Int?=null,
    var name: String="",
    var image: String="",
    var barcode: String="",
    var skuClient: String=""

)