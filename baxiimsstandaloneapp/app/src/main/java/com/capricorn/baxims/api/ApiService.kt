package com.capricorn.baxims.api

import com.capricorn.baxims.api.auth.LoginRequest
import com.capricorn.baxims.api.auth.SignUpRequest
import com.capricorn.baxims.api.auth.UpdateRequest
import com.capricorn.baxims.api.business.*
import com.capricorn.baxims.api.dashboard.ProductRequest
import com.capricorn.baxims.api.transaction.FullTransaction
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    /**
     * Authentication
     * */
    @POST("users/login")
    fun login(@Body loginRequest: LoginRequest): Call<JsonObject>

    @POST("users")
    fun signUp(@Body signUpRequest: SignUpRequest):Call<JsonObject>

    @PUT("users")
    fun update(@Body signUpRequest: UpdateRequest):Call<JsonObject>


    /**
     * Business
     * */
    @GET("business_categories")
    fun getBusinessCategories(@Header("Authorization")authorization:String):Call<BusinessCategoryResponse>

    @GET("my/businesses")
    fun getBusinessList(@Header("Authorization")authorization:String):Call<JsonObject>//requires authtoken

    @POST("my/businesses")
    fun addBusiness(@Header("Authorization")authorization:String, @Body businessRequest: BusinessRequest):Call<JsonObject>

    @GET("my/outlets")
    fun getOutletList(@Header("Authorization")authorization:String,@Query("domain")subdomain:String):Call<JsonObject>//requires auth token and requires subdomain

    @POST("my/outlets")
    fun addOutletList(@Header("Authorization")authorization:String,
                      @Body outlet:OutletRequest,@Query("domain") subdomain: String):Call<JsonObject>


    /**
     * Product
     * */
    @GET("product_categories")
    fun getProductCategory(@Query("bc_id") business_category_id: Int):Call<JsonObject>

    @POST("my/outlets/{outlet_id}/products")
    fun AddProduct(@Header("Authorization") authorization:String,@Path("outlet_id")outlet_id:Int,@Query("domain")domain:String,@Body productRequest: ProductRequest):Call<JsonObject>

    @POST("my/product_categories/save")
    fun saveProductCategory(@Header("Authorization") authorization:String,
                            @Query("domain")domain:String, @Body category: CategoryRequest):Call<JsonObject>

    @GET("products")
    fun getAllProduct():Call<JsonObject>

    @GET("my/outlets/{outlet_id}/products")
    fun getOutletProduct(@Header("Authorization")authorization:String,@Path("outlet_id")outlet_id:Int,@Query("domain")domain:String):Call<JsonObject>


    /**
     * Transaction
     * */
    @POST("my/transactions/sync")
    fun postTransactionHistory(@Header("Authorization")authorization:String, @Query("domain")domain:String,
                               @Body jsonObject: FullTransaction):Call<JsonObject>

}