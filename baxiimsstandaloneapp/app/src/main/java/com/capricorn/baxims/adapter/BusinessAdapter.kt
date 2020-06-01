package com.capricorn.baxims.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.capricorn.baxims.R
import com.capricorn.baxims.ui.business.Business
import com.capricorn.baxims.ui.business.BusinessContainer
import com.capricorn.baxims.ui.business.BusinessDirections
import com.capricorn.baxims.utils.TinyDB
import com.google.gson.JsonArray
import com.google.gson.JsonParser

class BusinessAdapter(val ctx: Context, val nav: NavController): RecyclerView.Adapter<Business.BusinessViewHolder>() {


    private var dataSource = JsonArray()
    var ff = BusinessContainer()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Business.BusinessViewHolder {
        return Business.BusinessViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.business_item,parent, false))
    }

    override fun getItemCount(): Int {
        return dataSource.size()
    }

    fun setBusinessData(dataPackage: JsonArray) {
        this.dataSource = dataPackage
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: Business.BusinessViewHolder, position: Int) {
        if (dataSource.size() > 0) {
            val item = JsonParser.parseString(dataSource.get(position).toString()).asJsonObject

            val name = (item.get("name").toString()).replace("\"", "")
            val businessId = item.get("business_category_id").asInt
            val domainName = item.get("domain").toString()// Used toString() in case the value being returned is null, so we have "null"
            holder.businessName.text = name
            holder.businessCategory.text = item.getAsJsonObject("category").get("name").asString
            holder.businessCredential.text = name.substring(0, 1)

            val domain = domainName.replace("\"", "")
            val tinyDB = TinyDB(ctx)


            holder.businessFullItemView.setOnClickListener {
                tinyDB.putString(TinyDB.Domain, domain)
                Log.d("SUBDOMAIN", tinyDB.getString(TinyDB.Domain))
                val action = BusinessDirections.actionBusinessToOutlet(domain)
                nav.navigate(action)
            }
        }

    }


}