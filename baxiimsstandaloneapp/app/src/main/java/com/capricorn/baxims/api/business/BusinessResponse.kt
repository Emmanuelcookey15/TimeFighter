package com.capricorn.baxims.api.business

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "category_list_table")
data class BusinessCategoryResponse(

    @SerializedName("data")
    val data: List<CategoryResponseData>,
    @PrimaryKey
    val message: String,
    val success: Boolean
)

@Entity
data class CategoryResponseData(
    @PrimaryKey
    val id: Int,
    val is_active: Boolean,
    @SerializedName("name")
    val name: String?=null
)