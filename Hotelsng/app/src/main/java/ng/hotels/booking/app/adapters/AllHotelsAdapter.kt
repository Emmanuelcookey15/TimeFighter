package ng.hotels.booking.app.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.JsonArray
import ng.hotels.booking.app.R
import ng.hotels.booking.app.activities.HotelBookingActivity
import ng.hotels.booking.app.utils.TinyDB
import java.text.NumberFormat
import java.util.*

class AllHotelsAdapter(hotelSearchList: JsonArray?, var ctx: Context): RecyclerView.Adapter<AllHotelsAdapter.AllHotelsHolder>() {

    var list = hotelSearchList

    val tinyDB = TinyDB(ctx)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AllHotelsHolder {
        return AllHotelsHolder(LayoutInflater.from(p0.context).inflate(R.layout.hotels_for_you_layout, p0, false))
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size()
    }

    fun setHotelList(hotelsList: JsonArray) {
        list = hotelsList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(p0: AllHotelsHolder, p1: Int) {
        val item = list!!.get(p1).asJsonObject

        val price = priceWithoutDecimal(item.get("min_rate_ngn").asDouble)

        p0.hotelName.setText(item.get("property_name").asString)
        p0.hotelPrice.setText("₦" + price /*holder.hotelSearch.getMinRate()*/)
        p0.hotelStatus.text = ("" + item.get("city_code") + ", " + item.get("state_code")).replace("\"", "")

        val rateAsDouble = item.get("rating").asDouble

        val rate = priceWithoutDecimal(rateAsDouble)

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


        p0.hotelReview.text = item.get("review_count").asString + " Reviews"

        val arrayOfHotelsImages = item.get("images").asJsonArray

        if (item.get("num_of_images").asInt > 0){

                Glide.with(p0.hotelImage.context)
                        .load(arrayOfHotelsImages.get(0).asJsonObject.get("url").asString)
                        .placeholder(R.drawable.top)
                        .into(p0.hotelImage)
                tinyDB.putInt(TinyDB.ARRAYOFHOTELSIMAGES, (item.get("images").asJsonArray).size())

        }else{
            p0.hotelImage.setImageResource(R.drawable.default_bed)
        }

        p0.wholeView.setOnClickListener {
            val i = Intent(ctx, HotelBookingActivity::class.java)
            i.putExtra("hotel_id", item.toString())
            ctx.startActivity(i)
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


    class AllHotelsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val hotelPrice = itemView.findViewById<TextView>(R.id.hotel_prize)
        val hotelName = itemView.findViewById<TextView>(R.id.hotel_name_list)
        val hotelStatus = itemView.findViewById<TextView>(R.id.hotel_location_list)
        var hotelImage = itemView.findViewById<ImageView>(R.id.hotel_thumbnail)
        var hotelRating = itemView.findViewById<Button>(R.id.hotel_rating)
        var hotelReview = itemView.findViewById<TextView>(R.id.reviews)
        var hotelExcellence = itemView.findViewById<TextView>(R.id.hotel_excellence)
        var wholeView = itemView.findViewById<RelativeLayout>(R.id.the_hotel_view)



    }

}