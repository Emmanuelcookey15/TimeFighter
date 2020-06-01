package com.nepa.ng.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface ApiService {


    @GET("power/discos/prepaid")
    fun getPrepaidPowerVendor(@Header("Accept")value:String): Call<JsonArray>

    @FormUrlEncoded
    @POST("power/verify")
    fun verifyMeterNumber(@Header("Accept")value:String,
                          @Field("disco_id") discoId: String,
                          @Field("account_number") meterNumber:String): Call<JsonObject>


    @FormUrlEncoded
    @POST("power/recharge")
    fun powerRecharge(@Header("Accept")value:String,
                      @Field("disco_id") discoId: String,
                      @Field("account_number") meterNumber:String,
                      @Field("phone") phone: String,
                      @Field("amount") amount: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("subscription/add")
    fun subscribeMail(@Field("name") name: String,
                      @Field("email") email:String): Call<JsonObject>


    @FormUrlEncoded
    @POST("order/confirm")
    fun sendToken(@Field("txRef") txRef: String,
                  @Field("amount") amount:String): Call<JsonObject>


    @FormUrlEncoded
    @POST("monnify/confirm")
    fun sendTokenMonnify(@Field("transactionReference") txRef: String): Call<JsonObject>


    @FormUrlEncoded
    @POST("order")
    fun confirmTokenRequest(@Field("email") email: String,
                            @Field("phone_number") phone_number:String,
                            @Field("meter_type") meter_type: String,
                            @Field("meter_number") meter_number: String,
                            @Field("amount") amount: String,
                            @Field("disco_id") disco_id: String): Call<JsonObject>


}