package com.capricorn.baxims.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capricorn.baxims.R
import com.capricorn.baxims.models.CartTable
import com.capricorn.baxims.models.ProductTable
import com.capricorn.baxims.ui.cart.CartViewModel
import com.capricorn.baxims.utils.CartViewHolder

class CartAdapter(val ctx: Context, val totalamount: ArrayList<Int>, val checkOut: Button, val viewHolder: CartViewModel):
    RecyclerView.Adapter<CartViewHolder>() {


    var dataCart = ArrayList<CartTable>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_item,parent, false))
    }

    override fun getItemCount(): Int {
        return dataCart.size
    }

    fun setCartData(dataCartPackage: ArrayList<CartTable>) {
        dataCart = dataCartPackage
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = dataCart[position]

        totalamount.clear()
        holder.cartProductName.text = cartItem.name
        holder.cartProductUnit.text = cartItem.unit.toString()
        holder.cartproductAmount.text = "₦${cartItem.unit?.times(cartItem.sellingPrice!!)}"
        totalamount.add(cartItem.unit?.times(cartItem.sellingPrice!!)!!)
        checkOut.text="Check Out ₦${totalamount.sum()}"
        val image = StringToBitMap(cartItem.image)
        if (image != null){
            holder.cartproductImage.setImageBitmap(image)
        }else {
            Glide.with(holder.cartproductImage.context).load(cartItem.image).into(holder.cartproductImage)
        }

        holder.add.setOnClickListener {
            cartItem.unit = cartItem.unit!! + 1
            if (cartItem.unit!! <= cartItem.quantiy_in_stock!!) {
                viewHolder.updateUnitCart(cartItem.unit!!, cartItem.ProductId!!)
                totalamount.add(cartItem.unit?.times(cartItem.sellingPrice!!)!!)
                notifyDataSetChanged()
            }else{
                Toast.makeText(ctx, "Your Cart exceeds current quantity of available product", Toast.LENGTH_LONG).show()
            }
        }
        holder.minus.setOnClickListener {
            cartItem.unit = cartItem.unit!! - 1
            if (cartItem.unit!! >= 0) {
                viewHolder.updateUnitCart(cartItem.unit!!, cartItem.ProductId!!)
                totalamount.add(cartItem.unit?.times(cartItem.sellingPrice!!)!!)
                notifyDataSetChanged()
            }else{
                Toast.makeText(ctx, "Your cart cannot contain a negative Quantity", Toast.LENGTH_LONG).show()
                cartItem.unit = cartItem.unit!! + 1
            }
        }

    }


    private fun StringToBitMap(encodedString: String): Bitmap? {
        try {
            val encodeByte = android.util.Base64.decode(encodedString, android.util.Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            return null
        }

    }
}