package ng.hotels.booking.app.interfaces

import com.google.gson.JsonObject
import ng.hotels.booking.app.models.Bookings
import ng.hotels.booking.app.models.Token
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface HotelsngApiService {

    //Hotels Search
    @GET("oauth/authenticate")
    fun getToken(@Query("grant_type") grantType: String, @Query("client_id") clientID: String,
                          @Query("client_secret") clientSecret: String, @Query("scope") scope: String): Call<Token>




    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("search")
    fun getHotelsNearYou(@Query("query") query: String,
                                  @Query("access_token") accessToken: String,
                                  @Query("with_images") withImages: Boolean,
                                  @Query("with_rates") withRates: Boolean,
                                  @Query("filters") filters: String,
                                  @Query("search_type") searchType: String,
                                  @Query("property_type") propertyType: String): Call<JsonObject>


    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("search")
    fun getRecommendedHotels(@Query("query") query: String,
                                      @Query("access_token") accessToken: String,
                                      @Query("with_images") withImages: Boolean,
                                      @Query("with_rates") withRates: Boolean,
                                      @Query("filters") filters: String,
                                      @Query("search_type") searchType: String,
                                      @Query("property_type") propertyType: String): Call<JsonObject>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("search")
    fun getSearchHotels(@Query("query") query: String,
                             @Query("access_token") accessToken: String,
                             @Query("with_images") withImages: Boolean,
                             @Query("with_rates") withRates: Boolean,
                             @Query("filters") filters: String,
                             @Query("search_type") searchType: String,
                             @Query("property_type") propertyType: String): Call<JsonObject>


    @GET("rooms/property/{hotel_id}?")
    fun getRoomCategories(@Path("hotel_id") hotel_id: Int,
                                   @Query("access_token") access_token: String): Call<JsonObject>


    @GET("hotels/{hotel_id}/provider_details?")
    fun getAllSinglePageDetails(@Path("hotel_id") hotel_id: String,
                                @Query("access_token") accessToken: String,
                                @Query("currency_code") currencyCode: String,
                                @Query("checkin") checkIn: String,
                                @Query("checkout") checkOut: String,
                                @Query("number_of_rooms") numberOfRooms: String,
                                @Query("with_images") withImages: String,
                                @Query("with_facilities") withFacilities: String): Call<JsonObject>

    @GET("hotels/{hotel_id}/reviews")
    fun getHotelsReview(@Path("hotel_id") hotel_id: String,
                                 @Query("access_token") access_token: String): Call<JsonObject>


    @FormUrlEncoded
    @POST("bookings/create")
    fun bookHotel(
            @Query("access_token") access_token: String,
            @Field("country") country: String,
            @Field("currency_code") currency_code: String,
            @Field("title") title: String,
            @Field("fname") fname: String,
            @Field("lname") lname: String,
            @Field("email") email: String,
            @Field("phone") phone: String,
            @Field("address") address: String,
            @Field("checkin") checkin: String,
            @Field("checkout") checkout: String,
            @Field("ptype") ptype: String,
            @Field("property") property: String,
            @Field("additional_info") additional_info: String,
            @Field("payment_type") payment_type: String,
            @Field("agent") agent: String,
            @Field("provider") provider: String,
            @Field("access_token") access_tokens: String,
            @Field("on_behalf_of_fname") on_behalf_of_fname: String,
            @Field("on_behalf_of_lname") on_behalf_of_lname: String,
            @Field("on_behalf_of_phone") on_behalf_of_phone: String,
            @Field("on_behalf_of_email") on_behalf_of_email: String,
            @Field("rooms") rooms: ArrayList<JSONObject>): Call<JsonObject>


    @FormUrlEncoded
    @POST("bookings/create")
    fun bookHotel(
            @Query("access_token") access_token: String,
            @Field("rooms") rooms: ArrayList<JSONObject>): Call<JsonObject>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("bookings/create")
    fun bookingHotel(
            @Body bookings: Bookings
    ): Call<JsonObject>

}