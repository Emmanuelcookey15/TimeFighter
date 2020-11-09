package com.capricorn.baxims.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.capricorn.baxims.R
import com.capricorn.baxims.models.ProductCategoryTable
import com.capricorn.baxims.models.ProductTable

class ProductCategoryAdapter(val productCategoryList: ArrayList<String>, var productCategoryTableList: ArrayList<ProductCategoryTable>): RecyclerView.Adapter<ProductCategoryAdapter.ProductCategoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoryViewHolder {
        return ProductCategoryViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(
                R.layout.product_category_item,parent, false), productCategoryList)
    }

    override fun getItemCount(): Int {
        return productCategoryTableList.size
    }

    fun setProductCategoryData(dataPackage: ArrayList<ProductCategoryTable>) {
        productCategoryTableList = dataPackage
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ProductCategoryViewHolder, position: Int) {
        val item = productCategoryTableList[position]
        val name = item.name
        holder.productName.text = name
        holder.productCount.text = holder.itemView.context.getString(R.string.products_count, item.products_count)
        holder.productCredential.text = name.substring(0, 1)

        holder.fullProductView.setOnClickListener {
            var pos = holder.adapterPosition
            if (productCategoryTableList.size > pos){
                pos = holder.adapterPosition - 1
                if (pos == -1){
                    pos = 0
                }
            }
            if (holder.fullProductView.get(pos).isSelected) {
                holder.fullProductView.setBackgroundResource(R.drawable.line)
                productCategoryList.remove(item.uid)
                holder.fullProductView.get(pos).isSelected = false
            } else {
                holder.fullProductView.setBackgroundResource(R.drawable.line_selected)
                productCategoryList.add(item.uid)
                holder.fullProductView.get(pos).isSelected = true
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, pos: Int)
    }

    fun display(isSelected: Boolean, constraintLayout: ConstraintLayout) {
        constraintLayout.setBackgroundResource(if (isSelected) R.drawable.line_selected else R.drawable.line)
    }


    class ProductCategoryViewHolder(val itemview: View, val productCategoryList: ArrayList<String>): RecyclerView.ViewHolder(itemview){


            val productName: TextView = itemview.findViewById(R.id.product_category_name)
            val productCount: TextView = itemview.findViewById(R.id.product_category_count)
            val productCredential: TextView = itemview.findViewById(R.id.product_category_credentials)
            val fullProductView: ConstraintLayout = itemview.findViewById(R.id.product_category_item_fullview)


    }

}