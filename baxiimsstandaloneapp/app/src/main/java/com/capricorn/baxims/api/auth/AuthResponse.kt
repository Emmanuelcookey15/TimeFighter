package com.capricorn.baxims.api.auth

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val data: Data,
    val message: String,
    val success: Boolean
)

//for login
data class User(
    @SerializedName("first_name") val first_name: String?=null,
    @SerializedName("id")val id: Int?=null,
    @SerializedName("last_name")val last_name: String?=null,
    @SerializedName("username")val username: String?=null
)

// for Signup
data class Data(
    @SerializedName("token") val token: String?=null,
    @SerializedName("user") val user: User,
    @SerializedName("created_at")val created_at: String?=null,
    @SerializedName("email")val email: String?=null,
    @SerializedName("first_name")val first_name: String?=null,
    @SerializedName("id")val id: Int?=null,
    @SerializedName("last_name")val last_name: String?=null,
    @SerializedName("phone_no")val phone_no: String?=null,
    @SerializedName("updated_at")val updated_at: String?=null,
    @SerializedName("username") val username: String?=null
)