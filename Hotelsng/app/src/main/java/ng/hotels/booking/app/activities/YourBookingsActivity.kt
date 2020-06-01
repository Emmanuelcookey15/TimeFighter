package ng.hotels.booking.app.activities

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_your_bookings.*
import ng.hotels.booking.app.R
import ng.hotels.booking.app.utils.TinyDB
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class YourBookingsActivity : AppCompatActivity() {

    val sdf = SimpleDateFormat("dd, MMM yyyy")

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    lateinit var tinyDB: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_bookings)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        tinyDB = TinyDB(this)

        setSupportActionBar(your_hotels_bookings_toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.title = "Your Booking"

        val propertyId = intent.getStringExtra("hotel_Id")

        val hotel_name = intent.getStringExtra("hotel_name")

        val address_hotel = intent.getStringExtra("hotel_address")

        val images_room = intent.getStringExtra("images")

        val jObj = JSONArray(images_room)

        val images_room_num = intent.getStringExtra("numOfImage")

        val rating = intent.getStringExtra("rating")

        val rateAsDouble = rating.toDouble()

        val checkIn = intent.getStringExtra("checkin")

        val checkOut = intent.getStringExtra("checkout")

        val diff = (sdf.parse(checkOut).time - sdf.parse(checkIn).time)/(24 * 60 * 60 * 1000)

        val bookingsList = JSONObject()

        bookingsList.put("hotel_name", hotel_name)
        bookingsList.put("hotel_address",address_hotel)
        bookingsList.put("hotel_ratings",rating)
        bookingsList.put("hotel_images", images_room)
        bookingsList.put("hotel_images_number",images_room_num)

        val bookingHistory = tinyDB.getListString(TinyDB.BookingsHistory)

        bookingHistory.add(bookingsList.toString())

        tinyDB.putListString(TinyDB.BookingsHistory, bookingHistory)

        your_bookings_hotel_name.text = hotel_name

        your_bookings_hotel_address_name.text = address_hotel

        your_booking_hotel_rating.text = rating

        val bookings = intent.getStringExtra("bookings")

        val jsonBooking = JSONObject(bookings)

        val bookingID = jsonBooking.getJSONObject("data").get("id").toString()

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Booked Hotels: $bookingID")
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Bookings ID: $checkIn to $checkOut.")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        val firstName =  jsonBooking.getJSONObject("data").getJSONObject("guest").get("fname").toString()

        val lastName = jsonBooking.getJSONObject("data").getJSONObject("guest").get("lname").toString()

        val email = jsonBooking.getJSONObject("data").getJSONObject("guest").get("email").toString()

        val phone = jsonBooking.getJSONObject("data").getJSONObject("guest").get("phone").toString()

        val totalPrice = jsonBooking.getJSONObject("data").get("price").toString().toDouble()

        val roomsBooked =  jsonBooking.getJSONObject("data").getJSONArray("rooms")

        val numofRooms = roomsBooked.length()


        if (rateAsDouble == 0.0){
            your_booking_hotel_excellence.text = ""
            your_booking_hotel_rating.visibility = View.GONE
        }else if (rateAsDouble > 1 && rateAsDouble <= 2){
            your_booking_hotel_excellence.text = "Poor"
            your_booking_hotel_rating.setBackgroundColor(Color.parseColor("#EB5757"))
        }else if (rateAsDouble > 2 && rateAsDouble <= 4 ){
            your_booking_hotel_excellence.text = "Fair"
            your_booking_hotel_rating.setBackgroundColor(Color.parseColor("#F2C94C"))
        }else if(rateAsDouble > 4 && rateAsDouble <= 6 ){
            your_booking_hotel_excellence.text = "Good"
            your_booking_hotel_rating.setBackgroundColor(Color.parseColor("#6fcf97"))
        }else if(rateAsDouble > 6 && rateAsDouble <= 8 ){
            your_booking_hotel_excellence.text = "Very Good"
            your_booking_hotel_rating.setBackgroundColor(Color.parseColor("#32A636"))
        }


        if (images_room_num.toInt() > 0) {
            Glide.with(this)
                    .load(jObj.getJSONObject(0).get("url"))
                    .placeholder(R.drawable.top)
                    .into(your_booking_default_image)
        }else{
            your_booking_default_image.setImageResource(R.drawable.top)
        }

        your_booking_check_in.text = checkIn

        your_booking_check_out.text = checkOut

        bookings_id.text = "Booking ID " + bookingID

        your_room_and_night_details.text =   "$numofRooms room and $diff night"

        your_name_in_booking.text = "$firstName $lastName"

        your_email_in_booking.text = email

        your_phone_num_in_booking.text = phone

        your_sum_total_bookings.text = priceWithoutDecimal(totalPrice)

    }


    private fun priceWithoutDecimal(number: Double): String {
        val number3digits:Double = Math.round(number * 1000.0) / 1000.0
        val number2digits:Double = Math.round(number3digits * 100.0) / 100.0
        val solution:Double = Math.round(number2digits * 10.0) / 10.0
        val solu = NumberFormat.getNumberInstance(Locale.US).format(solution)
        val result = solu.toString()
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

}
