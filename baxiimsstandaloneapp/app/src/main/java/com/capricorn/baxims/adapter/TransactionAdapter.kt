package com.capricorn.baxims.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capricorn.baxims.R
import com.capricorn.baxims.utils.TinyDB
import com.capricorn.baxims.utils.TransactionViewHolder
import com.google.gson.JsonParser
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TransactionAdapter(val ctx: Context): RecyclerView.Adapter<TransactionViewHolder>() {

    var dataSource = ArrayList<String>()
    var arrayAmount = ArrayList<Int>()

    var tinyDB = TinyDB(ctx)

    val sdf = SimpleDateFormat("MMM dd, yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.transaction_item,parent, false))
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    fun setTransactionData(dataPackage: ArrayList<String>) {
        this.dataSource = dataPackage
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = JsonParser.parseString(dataSource.get(position).toString()).asJsonObject
        var timeStamp = Timestamp((item.get("processed_at").toString()).replace("\"", "").toLong() * 1000)
        var date =  Date(timeStamp.time)

        holder.transactionDate.text = sdf.format(date)
        holder.transactionamount.text = "₦${item.get("amount").asInt}"
        arrayAmount.add(item.get("amount").asInt)
        tinyDB.putInt(TinyDB.OverallTransactionPrice, arrayAmount.sum())
    }
}