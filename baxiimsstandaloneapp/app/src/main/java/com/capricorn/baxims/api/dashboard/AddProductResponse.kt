package com.capricorn.baxims.api.dashboard

import com.google.gson.annotations.SerializedName

data class AddProductResponse(
    @SerializedName("data")
    val addProductData: AddProductData,
    val message: String,
    val success: Boolean
)