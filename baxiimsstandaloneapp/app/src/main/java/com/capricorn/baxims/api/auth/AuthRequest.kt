package com.capricorn.baxims.api.auth

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username")
    var username:String?=null,
    @SerializedName("password")
    var password:String?=null
)

data class SignUpRequest(
    var first_name:String?=null,
    var last_name:String?=null,
    var username:String?=null,
    var phone_no:String?=null,
    var email:String?=null,
    var gender:String?=null,
    var password:String?=null
)

data class UpdateRequest(
    var first_name:String?=null,
    var last_name:String?=null,
    var username:String?=null,
    var email:String?=null
)
