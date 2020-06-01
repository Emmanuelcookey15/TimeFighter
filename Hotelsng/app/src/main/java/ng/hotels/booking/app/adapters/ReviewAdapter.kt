package ng.hotels.booking.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import ng.hotels.booking.app.R

class ReviewAdapter(var ctx: Context?, listing: JsonArray) : RecyclerView.Adapter<ReviewAdapter.ReviewHolder>() {

    var list = listing
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ReviewHolder {
        return ReviewHolder(LayoutInflater.from(p0.context).inflate(R.layout.reviews_items, p0, false))
    }

    override fun getItemCount(): Int {
        return list.size()
    }

    override fun onBindViewHolder(p0: ReviewHolder, p1: Int) {

        val item = list.get(p1).asJsonObject


        if (item.get("name").asString != null){
            p0.name.text = item.get("name").asString
        }

        if (item.get("published_at").asString != null){
            p0.date.text = item.get("published_at").asString.removeRange(10, item.get("published_at").asString.length)
        }

        if (item.get("overall_rating").asString != null){
            val rateAsDouble = item.get("overall_rating").asDouble

            val rate = priceWithoutDecimal(rateAsDouble)

            p0.ratins.text = rate

            if (rateAsDouble <= 2){
                p0.excellence.text = "Poor"
            }else if (rateAsDouble > 2 && rateAsDouble <= 4 ){
                p0.excellence.text = "Fair"
            }else if(rateAsDouble > 4 && rateAsDouble <= 6 ){
                p0.excellence.text = "Good"
            }else if(rateAsDouble > 6 && rateAsDouble <= 8 ){
                p0.excellence.text = "Very Good"
            }else{
                p0.excellence.text = "Excellent"
            }
        }

        if (item.get("comment").asString != null){
            p0.reviews.text = item.get("comment").asString
        }


    }

    private fun priceWithoutDecimal(number: Double): String {
        val number3digits:Double = Math.round(number * 1000.0) / 1000.0
        val number2digits:Double = Math.round(number3digits * 100.0) / 100.0
        val solution:Double = Math.round(number2digits * 10.0) / 10.0
        val result = solution.toString()
        return result
    }


    class ReviewHolder(itemView: View): RecyclerView.ViewHolder(itemView){


        val name = itemView.findViewById<TextView>(R.id.reviewers_name)
        val date = itemView.findViewById<TextView>(R.id.reviewed_date)
        val excellence = itemView.findViewById<TextView>(R.id.review_excellence)
        val reviews = itemView.findViewById<TextView>(R.id.the_reviews)
        var ratins = itemView.findViewById<Button>(R.id.reviews_rating)

    }
}