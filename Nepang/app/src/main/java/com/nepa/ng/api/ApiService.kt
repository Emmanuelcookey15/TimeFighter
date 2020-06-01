package com.nepa.ng.api

import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {


    @GET("power/discos")
    fun getAllPowerVendor(@Header("Token")token:String): Call<JsonArray>

}