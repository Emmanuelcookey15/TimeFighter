package com.cookey.sandra.foodplug.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cookey.sandra.foodplug.R
import com.cookey.sandra.foodplug.data.FoodItem

class TodayMenuAdapter(var items: ArrayList<FoodItem>, val context: Context): RecyclerView.Adapter<TodayMenuAdapter.TodayMenuViewHoder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayMenuViewHoder {
        return TodayMenuViewHoder(LayoutInflater.from(parent.context).inflate(R.layout.item_today_menu, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TodayMenuViewHoder, position: Int) {

        holder.apply {

            Glide.with(menuImage.context)
                .load(items[position].foodImg)
                .centerCrop()
                .into(menuImage)

            menuName.text = items[position].foodName

            menuDescription.text =items[position].foodDesc

            menuPrice.text = "N" + items[position].foodName

        }

    }

    class TodayMenuViewHoder(itemView: View): RecyclerView.ViewHolder(itemView){

        val menuName = itemView.findViewById<TextView>(R.id.name_menu_daily)
        var menuImage = itemView.findViewById<ImageView>(R.id.image_menu_daily)
        var menuDescription = itemView.findViewById<TextView>(R.id.description_menu_daily)
        var menuPrice = itemView.findViewById<TextView>(R.id.price_menu_daily)

    }


}