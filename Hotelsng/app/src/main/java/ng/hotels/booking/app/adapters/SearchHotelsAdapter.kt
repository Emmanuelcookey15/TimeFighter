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
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

class SearchHotelsAdapter(hotelSearchList: JsonArray?, var ctx: Context): RecyclerView.Adapter<SearchHotelsAdapter.SearchHotelsHolder>(){


    var list = hotelSearchList

    var tinydb = TinyDB(ctx)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SearchHotelsHolder {
        return SearchHotelsHolder(LayoutInflater.from(p0.context).inflate(R.layout.search_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return list!!.size()
    }

    override fun onBindViewHolder(p0: SearchHotelsHolder, p1: Int) {
        val item = list!!.get(p1).asJsonObject

        val price = priceWithoutDecimal(item.get("min_rate_ngn").asDouble)

        val propertyName = item.get("property_name").asString

        p0.hotelName.text = propertyName

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
        } else{
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



        p0.favouriteHotels.setOnClickListener {


            var alreadyAdded = false
            if (tinydb.getListString(TinyDB.FAVOURITES).size > 0){
                for (value in tinydb.getListString(TinyDB.FAVOURITES)){
                    val jsonFormat = JSONObject(value)
                    val name = jsonFormat.getString("property_name")
                    if (name == propertyName){
                        alreadyAdded = true
                    }
                }

                if (alreadyAdded == false){
                    val bookingHistory = tinydb.getListString(TinyDB.FAVOURITES)
                    bookingHistory.add(item.toString())
                    tinydb.putListString(TinyDB.FAVOURITES, bookingHistory)
                    Toast.makeText(ctx, "added", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(ctx, "Already added to Favourites", Toast.LENGTH_SHORT).show()
                }
            }else{
                val bookingHistory = tinydb.getListString(TinyDB.FAVOURITES)
                bookingHistory.add(item.toString())
                tinydb.putListString(TinyDB.FAVOURITES, bookingHistory)
                Toast.makeText(ctx, "added", Toast.LENGTH_SHORT).show()
            }

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

    private fun numberFormatting(number: Int): String{

        val solu = NumberFormat.getNumberInstance(Locale.US).format(number)
        val result = solu.toString()
        return result

    }


    class SearchHotelsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val hotelName = itemView.findViewById<TextView>(R.id.hotel_name_list)
        val hotelStatus = itemView.findViewById<TextView>(R.id.hotel_location_list)
        val hotelPrice = itemView.findViewById<TextView>(R.id.price)
        var hotelImage = itemView.findViewById<ImageView>(R.id.hotel_image)
        var hotelRating = itemView.findViewById<Button>(R.id.hotel_rating_list)
        var wholeView = itemView.findViewById<RelativeLayout>(R.id.full_listing_view)
        val hotelReviews = itemView.findViewById<TextView>(R.id.listing_reviews)
        val favouriteHotels = itemView.findViewById<ImageView>(R.id.fav_button)

    }
}