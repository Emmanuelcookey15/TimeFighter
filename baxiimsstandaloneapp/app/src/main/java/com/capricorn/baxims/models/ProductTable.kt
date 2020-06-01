package com.capricorn.baxims.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "products_data_list", indices = arrayOf(Index(value = ["skuClient"], unique = true)))
class ProductTable() {

    @PrimaryKey
    var id: Int?=null
    var barcode: String=""
    var buyingPrice: Int?=null
    var sellingPrice: Int?=null
    var restockLevel: Int= 0
    var quantiy_in_stock: Int = 0
    var isActive: Boolean?=null
    var name: String=""
    var image: String=""
    var skuClient: String=""
    var my_outlet_name: String=""
    var outlet_uid: String=""
}