package com.capricorn.baxims.ui.dashboard

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.capricorn.baxims.R
import com.capricorn.baxims.adapter.ProductAdapter
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.models.ProductTable
import com.capricorn.baxims.utils.TinyDB
import com.capricorn.baxims.utils.showDialog
import kotlinx.android.synthetic.main.activity_searched_result.*

class SearchedResultActivity : AppCompatActivity() {



    lateinit var viewmodel: DashboardViewModel

    var tinyDB: TinyDB? = null

    var productAdapter: ProductAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searched_result)
        viewmodel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        toolbar_searched.title = "Searched Results"
        setSupportActionBar(toolbar_searched)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        tinyDB = TinyDB(this)
        productAdapter = ProductAdapter(this, viewmodel)
        searched_list.adapter = productAdapter

        val currentQuery = "%" + intent.getStringExtra("searched_result") + "%"

        viewmodel.selectProductByName(currentQuery!!)?.observe(this, Observer {
            if (it?.name != null){
                val productArray = ArrayList<ProductTable>()
                productArray.add(it)
                productAdapter?.setProductData(productArray)
            }else{
                showDialog(currentQuery,"No Result Found")
            }
        })

    }


}
