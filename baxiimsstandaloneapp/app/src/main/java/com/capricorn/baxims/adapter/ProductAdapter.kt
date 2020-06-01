package com.capricorn.baxims.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.capricorn.baxims.R
import com.capricorn.baxims.models.CartTable
import com.capricorn.baxims.models.ProductTable
import com.capricorn.baxims.ui.dashboard.DashboardViewModel
import com.capricorn.baxims.ui.dashboard.ProductDetails
import com.capricorn.baxims.utils.ProductViewHolder
import com.capricorn.baxims.utils.showToast
import kotlinx.coroutines.*

class ProductAdapter(val ctx: Context, val viewmodels: DashboardViewModel): RecyclerView.Adapter<ProductViewHolder>() {

    var dataSource = arrayListOf<ProductTable>()
    var count = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.product_item,parent, false))
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }


    fun setProductData(dataPackage: ArrayList<ProductTable>) {
        dataSource = dataPackage
        notifyDataSetChanged()
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = dataSource[position]
        holder.productName.text = item.name
        holder.productUnit.text = item.quantiy_in_stock.toString()+" Units"
        holder.productAmount.text = "₦${item.sellingPrice.toString()}"
        val image = StringToBitMap(item.image)
        if (image != null){
            holder.productImage.setImageBitmap(image)
        }else {
            Glide.with(holder.productImage.context).load(item.image).into(holder.productImage)
        }

        holder.cartButton.setOnClickListener {
            count=0
            MaterialDialog(ctx).show {
                cornerRadius(5F)
                customView(R.layout.item_add_to_cart)
                val pImage = this.view.findViewById<ImageView>(R.id.productImg)
                val pName = this.view.findViewById<TextView>(R.id.productName)
                val pPrize = this.view.findViewById<TextView>(R.id.amount)
                val pAdd:ImageView= this.view.findViewById(R.id.add)
                val pMinus:ImageView= this.view.findViewById(R.id.minus)
                val pUnits:TextView= this.view.findViewById(R.id.units)

                val imageTwo = StringToBitMap(item.image)
                if (imageTwo != null){
                    pImage.setImageBitmap(imageTwo)
                }else {
                    Glide.with(ctx).load(item.image).into(pImage)
                }

                pName.text=item.name
                pPrize.text="₦${item.sellingPrice}"
                pAdd.setOnClickListener {
                    pMinus.alpha=1F
                    if (count< item.quantiy_in_stock!!){
                        count++
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(200)
                            pUnits.text="$count"
                        }
                    }else {
                        pAdd.alpha = 0.6F
                        return@setOnClickListener
                    }
                }

                pMinus.setOnClickListener {
                    pAdd.alpha=1F
                    if (count>0){
                        count--
                        pUnits.text="$count"
                    }else{
                        pMinus.alpha=0.6F
                        return@setOnClickListener
                    }
                }

                this.view.findViewById<Button>(R.id.addToCarts).setOnClickListener {
                    if(count==0){
                        context.showToast("can't add an empty cart")
                        return@setOnClickListener
                    }
                    val cart= CartTable(
                        ProductId = item.id,
                        unit = count,
                        sellingPrice = item.sellingPrice,
                        restockLevel = item.restockLevel,
                        quantiy_in_stock = item.quantiy_in_stock,
                        name = item.name,
                        image = item.image,
                        barcode = item.barcode,
                        skuClient = item.skuClient
                    )

                    CoroutineScope(Dispatchers.IO).launch {

                        withContext(Dispatchers.Main){
                            viewmodels.insertCart(cart)
                        }
                    }

                    ctx.showToast("${cart.unit} item added to cart")

                    dismiss()

                }
                this.view.findViewById<Button>(R.id.dismiss).setOnClickListener {
                    dismiss()
                }
            }
        }

        holder.productFullView.setOnClickListener {
            val intentProductDetail = Intent(ctx, ProductDetails::class.java)
            intentProductDetail.putExtra("skuClient", item.skuClient)
            ctx.startActivity(intentProductDetail)
            val ctxx = ctx as Activity
            ctxx.overridePendingTransition(R.anim.slide_in, R.anim.slide_in)
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