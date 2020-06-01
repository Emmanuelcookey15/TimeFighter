package com.capricorn.baxims.api.business

import com.google.gson.annotations.SerializedName

data class OutletResponse(
    @SerializedName("data")
    val data: List<OutletList>,
    val message: String,
    val success: Boolean
)