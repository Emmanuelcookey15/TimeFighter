package com.nepa.ng.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.nepa.app.R
import ng.nepa.app.activities.BuyElectricityActivity
import com.nepa.ng.model.AllVendorTable

class AllVendorsAdapter(val ctx: Context): RecyclerView.Adapter<AllVendorsAdapter.AllVendorsViewHolder>(),
    Filterable {

    var mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(ctx)

    var allVendorsArray = ArrayList<AllVendorTable>()

    var listCopy: ArrayList<AllVendorTable> = ArrayList()

    init {
        listCopy = allVendorsArray
    }


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


        holder.fullView.setOnClickListener {


            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Vendor Selected")
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, item.disco_name)
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, item.logo)
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)



            val payUnitIntent = Intent(ctx, BuyElectricityActivity::class.java)
            payUnitIntent.putExtra("image", item.logo)
            payUnitIntent.putExtra("payment_type", item.slug)
            payUnitIntent.putExtra("disco_id", item.id.toString())
            ctx.startActivity(payUnitIntent)
            val act = ctx as Activity
            act.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

    }

    fun setDataForVendors(data: ArrayList<AllVendorTable>){
        allVendorsArray = data
        listCopy = data
        notifyDataSetChanged()
    }


    class AllVendorsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val cities: TextView =itemView.findViewById(R.id.item_cities_vendor)
        val discoName: TextView =itemView.findViewById(R.id.item_all_vendor_name)
        val logo: ImageView = itemView.findViewById(R.id.item_all_vendor_logo)
        val fullView: RelativeLayout = itemView.findViewById(R.id.full_vendor_view)

    }


    override fun getFilter(): Filter {
        return simpleFilter
    }

    private val simpleFilter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults? {

            if (constraint.toString().isEmpty()) {

                allVendorsArray = listCopy

            } else {

                allVendorsArray = listCopy


                val filteredList = ArrayList<AllVendorTable>()


                for (item in allVendorsArray) {

                    if (item.cities.toLowerCase().contains(constraint.toString().toLowerCase())) {

                        filteredList.add(item)

                    }
                }
                allVendorsArray = filteredList

            }

            val results = FilterResults()
            results.values = allVendorsArray
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

//            listCopy.clear()
            allVendorsArray = results!!.values as ArrayList<AllVendorTable>


            notifyDataSetChanged()

        }

    }


}