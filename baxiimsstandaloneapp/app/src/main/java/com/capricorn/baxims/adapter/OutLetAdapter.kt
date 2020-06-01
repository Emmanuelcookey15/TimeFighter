package com.capricorn.baxims.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.capricorn.baxims.R
import com.capricorn.baxims.ui.business.Outlet
import com.capricorn.baxims.ui.dashboard.DashboardContainer
import com.capricorn.baxims.utils.TinyDB
import com.google.gson.JsonArray
import com.google.gson.JsonParser

class OutLetAdapter(val ctx: Context): RecyclerView.Adapter<Outlet.OutletBusiness>() {

    var dataSource = JsonArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Outlet.OutletBusiness {
        return Outlet.OutletBusiness(LayoutInflater.from(parent.getContext()).inflate(
            R.layout.outlet_item,parent, false))
    }

    override fun getItemCount(): Int {
        return dataSource.size()
    }

    fun setOutLetData(dataPackage: JsonArray) {
        dataSource = dataPackage
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: Outlet.OutletBusiness, position: Int) {
        if (dataSource.size() > 0) {
            val item = JsonParser.parseString(dataSource.get(position).toString()).asJsonObject

            val name = (item.get("name").toString()).replace("\"", "")
            val id = item.get("id").asInt
            val address = (item.get("address").toString()).replace("\"", "")
            val uid =  (item.get("uid").toString()).replace("\"", "")

            holder.outletName.text = name
            if (address != "null"){
                holder.outletAddress.text = address
            }else{
                holder.outletAddress.text = ""
            }
            holder.outletCredentials.text = name.substring(0, 1)
            val tinyDB = TinyDB(ctx)

            holder.outletFullItemView.setOnClickListener {
                tinyDB.putString(TinyDB.OutletName, name)
                tinyDB.putInt(TinyDB.OutletID, id)
                tinyDB.putString(TinyDB.OutletUID, uid)

                MaterialDialog(ctx).show {
                    cornerRadius(5F)
                    title(text = "Confirm outlet")
                    message(
                        text = "You selected from ${name} outlet. You would only see " +
                                "products and services available in ${name} outlet Only"
                    )

                    positiveButton(R.string.agree) { dialog ->
                        val intent = Intent(ctx, DashboardContainer::class.java)
                        ctx.startActivity(intent)
                    }

                    negativeButton { hide() }

                }
            }
        }

    }
}