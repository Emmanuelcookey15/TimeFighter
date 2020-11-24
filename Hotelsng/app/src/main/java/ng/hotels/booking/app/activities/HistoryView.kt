package ng.hotels.booking.app.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_history_view.*
import ng.hotels.booking.app.R
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

class HistoryView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_view)

        setSupportActionBar(history_toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.title = "Booking Details"

        val fullData = intent.getStringExtra("history")

        val item = JSONObject(fullData!!)

        history_booking_id.text = item.getString("booking_id")

        history_hotel_name.text = item.getString("hotel_name")

        history_hotel_address_name.text = item.getString("hotel_address")

        val rating = item.getString("hotel_ratings")

        val rateAsDouble = rating.toDouble()

        if (rateAsDouble == 0.0){
            history_hotel_excellence.text = ""
            history_hotel_rating.visibility = View.GONE
        }else if (rateAsDouble > 1 && rateAsDouble <= 2){
            history_hotel_excellence.text = "Poor"
            history_hotel_rating.setBackgroundColor(Color.parseColor("#EB5757"))
        }else if (rateAsDouble > 2 && rateAsDouble <= 4 ){
            history_hotel_excellence.text = "Fair"
            history_hotel_rating.setBackgroundColor(Color.parseColor("#F2C94C"))
        }else if(rateAsDouble > 4 && rateAsDouble <= 6 ){
            history_hotel_excellence.text = "Good"
            history_hotel_rating.setBackgroundColor(Color.parseColor("#6fcf97"))
        }else if(rateAsDouble > 6 && rateAsDouble <= 8 ){
            history_hotel_excellence.text = "Very Good"
            history_hotel_rating.setBackgroundColor(Color.parseColor("#32A636"))
        }

        val imagesRoomNum = item.getString("hotel_images_number")

        val imagesRoom = item.getString("hotel_images")

        val jObj = JSONArray(imagesRoom)

        if (imagesRoomNum.toInt() > 0) {
            Glide.with(this)
                .load(jObj.getJSONObject(0).get("url"))
                .placeholder(R.drawable.top)
                .into(history_default_image)
        }else{
            history_default_image.setImageResource(R.drawable.top)
        }


        history_check_in.text = item.getString("check_in")

        history_check_out.text = item.getString("check_out")

        val roomNum = item.getString("num_of_rooms")

        val duration = item.getLong("duration")

        val totalPrice = item.getDouble("price")

        history_number_of_rooms.text = "$roomNum room"

        history_duration.text =  "$duration night"

        history_price.text = priceWithoutDecimal(totalPrice)

        history_user_name.text = item.getString("user_name")

        history_user_email.text = item.getString("email")

        history_phone_number.text = item.getString("phone")

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
