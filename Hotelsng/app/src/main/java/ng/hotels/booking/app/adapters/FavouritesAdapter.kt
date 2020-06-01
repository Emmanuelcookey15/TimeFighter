package ng.hotels.booking.app.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import ng.hotels.booking.app.R
import ng.hotels.booking.app.activities.HotelBookingActivity
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class FavouritesAdapter(hotelSearchList: ArrayList<String>, var ctx: Context): RecyclerView.Adapter<FavouritesAdapter.FavouritesHolder>() {


    var listed = hotelSearchList

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FavouritesHolder {
        return FavouritesHolder(LayoutInflater.from(p0.context).inflate(R.layout.fav_list_item, p0, false))

    }

    override fun getItemCount(): Int {
        return listed.size
    }

    override fun onBindViewHolder(p0: FavouritesHolder, p1: Int) {

        val item = JsonParser().parse(listed[p1]).asJsonObject

        val price = priceWithoutDecimal(item.get("min_rate_ngn").asDouble)

        p0.hotelName.text = item.get("property_name").asString

        if((item.get("min_rate_ngn").asDouble) > 100.0){
            p0.hotelPrice.text = "₦$price"
        } else{
            p0.hotelPrice.text = "Price Unknown"
            p0.hotelPrice.textSize = p0.hotelPrice.context.resources.getDimension(R.dimen.textsize)
        }


        p0.hotelStatus.text = ("" + item.get("city_code")).replace("\"", "")

        var rateAsDouble = item.get("rating").asDouble

        val rate = priceWithoutDecimal(rateAsDouble)


        if (rateAsDouble == 0.0){
            p0.hotelRating.visibility = View.GONE
        }else if (rateAsDouble > 1 && rateAsDouble <= 2){
            p0.hotelRating.setBackgroundColor(Color.parseColor("#EB5757"))
        }else if (rateAsDouble > 2 && rateAsDouble <= 4 ){
            p0.hotelRating.setBackgroundColor(Color.parseColor("#F2C94C"))
        }else if(rateAsDouble > 4 && rateAsDouble <= 6 ){
            p0.hotelRating.setBackgroundColor(Color.parseColor("#6fcf97"))
        }else if(rateAsDouble > 6 && rateAsDouble <= 8 ){
            p0.hotelRating.setBackgroundColor(Color.parseColor("#32A636"))
        }
        p0.hotelRating.text = rate

        p0.hotelReviews.text = item.get("review_count").asString + " Reviews"


        if(item.get("images").asJsonArray.size() == 0){
            p0.hotelImage.setImageResource(R.drawable.default_bed)
        }
        else{
            Glide.with(p0.hotelImage.context)
                    .load(item.get("images").asJsonArray.get(0).asJsonObject.get("url").asString)
                    .placeholder(R.drawable.default_bed)
                    .into(p0.hotelImage)
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
        val solu = NumberFormat.getNumberInstance(Locale.US).format(solution)
        val result = solu.toString()
        return result
    }



    class FavouritesHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val hotelName = itemView.findViewById<TextView>(R.id.hotel_name_fav)
        val hotelStatus = itemView.findViewById<TextView>(R.id.hotel_location_fav)
        val hotelPrice = itemView.findViewById<TextView>(R.id.price_fav)
        var hotelImage = itemView.findViewById<ImageView>(R.id.hotel_image_fav)
        var hotelRating = itemView.findViewById<Button>(R.id.hotel_rating_fav)
        var wholeView = itemView.findViewById<RelativeLayout>(R.id.full_fav_view)
        val hotelReviews = itemView.findViewById<TextView>(R.id.fav_reviews)
        val favouriteHotels = itemView.findViewById<ImageView>(R.id.fav_button_selected)
    }


}