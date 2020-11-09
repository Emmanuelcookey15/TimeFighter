package ng.hotels.booking.app.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ng.hotels.booking.app.R
import ng.hotels.booking.app.activities.HistoryView
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class BookingHistoryAdapter(var bookingList: ArrayList<String>, var ctx: Context): RecyclerView.Adapter<BookingHistoryAdapter.BookingHistoryHolder>(){



    class BookingHistoryHolder(itemView: View): RecyclerView.ViewHolder(itemView){


        val hotelName = itemView.findViewById<TextView>(R.id.booking_history_hotel_name)
        val hotelAddress = itemView.findViewById<TextView>(R.id.booking_history_hotel_address_name)
        var hotelImage = itemView.findViewById<ImageView>(R.id.booking_history_default_image)
        var hotelRating = itemView.findViewById<Button>(R.id.booking_history_hotel_rating)
        var hotelExcellence = itemView.findViewById<TextView>(R.id.booking_history_hotel_excellence)
        var fullView = itemView.findViewById<CardView>(R.id.full_his_view)

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BookingHistoryHolder {
        return BookingHistoryHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_booking_history, p0, false))
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    override fun onBindViewHolder(p0: BookingHistoryHolder, p1: Int) {
        val item = JSONObject(bookingList[p1])

        p0.hotelName.text = item.getString("hotel_name")

        p0.hotelAddress.text = item.getString("hotel_address")

        val rate = item.getString("hotel_ratings")

        val rateAsDouble = rate.toDouble()

        p0.hotelRating.text = rate

        if (rateAsDouble == 0.0){
            p0.hotelExcellence.text = ""
            p0.hotelRating.visibility = View.GONE
        }else if (rateAsDouble > 1 && rateAsDouble <= 2){
            p0.hotelExcellence.text = "Poor"
            p0.hotelRating.setBackgroundColor(Color.parseColor("#EB5757"))
        }else if (rateAsDouble > 2 && rateAsDouble <= 4 ){
            p0.hotelExcellence.text = "Fair"
            p0.hotelRating.setBackgroundColor(Color.parseColor("#F2C94C"))
        }else if(rateAsDouble > 4 && rateAsDouble <= 6 ){
            p0.hotelExcellence.text = "Good"
            p0.hotelRating.setBackgroundColor(Color.parseColor("#6fcf97"))
        }else if(rateAsDouble > 6 && rateAsDouble <= 8 ){
            p0.hotelExcellence.text = "Very Good"
            p0.hotelRating.setBackgroundColor(Color.parseColor("#32A636"))
        }

        val images_room = item.getString("hotel_images")

        val jObj = JSONArray(images_room)

        val images_room_num = item.getString("hotel_images_number")

        if (images_room_num.toInt() > 0) {
            Glide.with(p0.hotelImage.context)
                    .load(jObj.getJSONObject(0).get("url"))
                    .placeholder(R.drawable.top)
                    .into(p0.hotelImage)
        }else{
            p0.hotelImage.setImageResource(R.drawable.top)
        }

        p0.fullView.setOnClickListener {
            val historyIntent = Intent(ctx, HistoryView::class.java)
            historyIntent.putExtra("history", item.toString())
            ctx.startActivity(historyIntent)
            (ctx as Activity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

    }

    private fun priceWithoutDecimal(number: Double): String {
        val number3digits:Double = Math.round(number * 1000.0) / 1000.0
        val number2digits:Double = Math.round(number3digits * 100.0) / 100.0
        val solution:Double = Math.round(number2digits * 10.0) / 10.0
        val result = NumberFormat.getNumberInstance(Locale.US).format(solution)
        //val result = solu.toString()
        return result
    }

}