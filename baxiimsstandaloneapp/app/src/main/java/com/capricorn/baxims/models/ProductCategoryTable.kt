package com.capricorn.baxims.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_category_data")
class ProductCategoryTable() {

    @PrimaryKey
    var id: Int?=null
    var isActive: Boolean?=null
    var name: String=""
    var parentId: Int?=null
    var products_count: Int?=null
    var uid: String=""
    var createdAt: String=""
    var updatedAt: String=""

}