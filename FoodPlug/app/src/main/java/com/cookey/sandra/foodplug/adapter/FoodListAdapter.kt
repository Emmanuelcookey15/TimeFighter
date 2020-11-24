package com.cookey.sandra.foodplug.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookey.sandra.foodplug.R
import com.cookey.sandra.foodplug.data.FoodItem

class FoodListAdapter(var items: ArrayList<FoodItem>, val context: Context): RecyclerView.Adapter<FoodListAdapter.FoodListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodListViewHolder {
        return FoodListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_food_list, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FoodListViewHolder, position: Int) {

        

    }

    class FoodListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val menuName = itemView.findViewById<TextView>(R.id.name_menu_daily)
        var menuImage = itemView.findViewById<ImageView>(R.id.image_menu_daily)
        var menuDescription = itemView.findViewById<Button>(R.id.description_menu_daily)
        var menuPrice = itemView.findViewById<TextView>(R.id.price_menu_daily)

    }

}