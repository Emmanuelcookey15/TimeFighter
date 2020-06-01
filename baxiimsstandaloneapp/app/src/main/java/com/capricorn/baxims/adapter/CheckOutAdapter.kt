package com.capricorn.baxims.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capricorn.baxims.R
import com.capricorn.baxims.models.CartTable
import com.capricorn.baxims.utils.CheckOutViewHolder
import com.capricorn.baxims.utils.TinyDB

class CheckOutAdapter(val tinyDB: TinyDB, var txtOne: TextView, var txtTwo: TextView): RecyclerView.Adapter<CheckOutViewHolder>() {

    var dataCheckOut = ArrayList<CartTable>()
    var arrayAmount = ArrayList<Int>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckOutViewHolder {
        return CheckOutViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.checkout_item, parent, false))
    }

    override fun getItemCount(): Int {
        return dataCheckOut.size
    }

    fun setCheckOutData(dataCartPackage: ArrayList<CartTable>) {
        dataCheckOut = dataCartPackage
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CheckOutViewHolder, position: Int) {
        if(dataCheckOut.size > 0) {
            val checkOutItem = dataCheckOut[position]

            holder.checkoutname.text = checkOutItem.name
            holder.checkoutId.text = checkOutItem.unit.toString()
            holder.checkoutamount.text = "₦${checkOutItem.unit?.times(checkOutItem.sellingPrice!!)}"
            arrayAmount.add(checkOutItem.unit?.times(checkOutItem.sellingPrice!!)!!)
            txtOne.text = "₦${arrayAmount.sum()}"
            txtTwo.text = "₦${arrayAmount.sum()}"
            tinyDB.putInt(TinyDB.TotalTransactionPrice, arrayAmount.sum())
        }
    }

}