package com.nepa.ng.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nepa.ng.R
import com.nepa.ng.model.AllVendorTable
import java.lang.reflect.Type

class AllVendorsAdapter: RecyclerView.Adapter<AllVendorsAdapter.AllVendorsViewHolder>() {


    var allVendorsArray = ArrayList<AllVendorTable>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllVendorsViewHolder {
        return AllVendorsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_all_power_vendors, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return allVendorsArray.size
    }

    override fun onBindViewHolder(holder: AllVendorsViewHolder, position: Int) {
        val item = allVendorsArray[position]

        holder.cities.text = (item.cities).replace(",", "\n")
        holder.discoName.text = item.disco_name
        Glide.with(holder.logo.context).load(item.logo).into(holder.logo)

    }

    fun setDataForVendors(data: ArrayList<AllVendorTable>){
        allVendorsArray = data
        notifyDataSetChanged()
    }


    class AllVendorsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val cities: TextView =itemView.findViewById(R.id.item_cities_vendor)
        val discoName: TextView =itemView.findViewById(R.id.item_all_vendor_name)
        val logo: ImageView = itemView.findViewById(R.id.item_all_vendor_logo)

    }


}