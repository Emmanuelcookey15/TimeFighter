package com.capricorn.baxims.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.recyclical.ViewHolder
import com.capricorn.baxims.R

class ProductViewHolder(itemview: View):RecyclerView.ViewHolder(itemview){
    val productName: TextView =itemview.findViewById(R.id.product_name_item)
    val productUnit: TextView =itemview.findViewById(R.id.product_units_item)
    val productAmount: TextView =itemview.findViewById(R.id.product_amount)
    val productImage: ImageView =itemview.findViewById(R.id.product_image_item)
    val cartButton: CardView =itemview.findViewById(R.id.add_to_cart)
    val productFullView: ConstraintLayout = itemview.findViewById(R.id.product_full_view)
}



//inner class for recyclerview viewholder
class CartViewHolder(itemview:View):RecyclerView.ViewHolder(itemview){
    val cartProductName: TextView =itemview.findViewById(R.id.cart_product_name)
    val cartProductUnit: TextView =itemview.findViewById(R.id.cart_units)
    val cartproductAmount: TextView =itemview.findViewById(R.id.cart_amount)
    val cartproductImage: ImageView =itemview.findViewById(R.id.cart_image)
    val add:ImageView=itemview.findViewById(R.id.add)
    val minus:ImageView=itemview.findViewById(R.id.minus)
    val unitsCounter:TextView=itemview.findViewById(R.id.cart_units)
}

//inner class for recyclerview viewholder
class CheckOutViewHolder(itemview:View):RecyclerView.ViewHolder(itemview){
    val checkoutname: TextView =itemview.findViewById(R.id.checkout_name)
    val checkoutId: TextView =itemview.findViewById(R.id.checkout_id)
    val checkoutamount: TextView =itemview.findViewById(R.id.checkout_amount)
}

class TransactionViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){
    val transactionDate: TextView =itemview.findViewById(R.id.transaction_time_item)
    val transactionamount: TextView =itemview.findViewById(R.id.transaction_amount_item)
}