package com.capricorn.baxims.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capricorn.baxims.R
import com.capricorn.baxims.adapter.ProductAdapter
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.databinding.FragmentProductBinding
import com.capricorn.baxims.models.OutletTable
import com.capricorn.baxims.models.ProductTable
import com.capricorn.baxims.utils.TinyDB
import com.capricorn.baxims.utils.showDialog
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Product : Fragment() {


    lateinit var binding:FragmentProductBinding
    var count=0

    lateinit var viewmodel: DashboardViewModel

    var mApiService: ApiService? = null

    var tinyDB: TinyDB? = null

    var productAdapter: ProductAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_product, container, false)
        viewmodel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        tinyDB = TinyDB(activity!!)
        productAdapter = ProductAdapter(activity!!, viewmodel)
        binding.list.adapter = productAdapter
        binding.list.layoutManager = LinearLayoutManager(activity!!)
        viewmodel.selectProductsByOutlets(tinyDB!!.getString(TinyDB.OutletUID))?.observe(viewLifecycleOwner, Observer {
            productAdapter?.setProductData(it as ArrayList<ProductTable>)
        })
        initializerRetrofit()

        val outletTable = OutletTable()
        outletTable.id = 0
        outletTable.userId = tinyDB!!.getInt(TinyDB.UserID)
        outletTable.outletName = tinyDB!!.getString(TinyDB.OutletName)
        viewmodel.insertOutlet(outletTable)

        activity!!

        return binding.root
    }


    private fun initializerRetrofit(){

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.baxiims.com.ng/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mApiService = retrofit.create<ApiService>(ApiService::class.java)

        subScribeObserver()

    }


    private fun subScribeObserver(){

        val call: Call<JsonObject> = mApiService!!.getOutletProduct(tinyDB!!.getString(TinyDB.Token), tinyDB!!.getInt(TinyDB.OutletID), tinyDB!!.getString(TinyDB.Domain))

        call.enqueue(object : Callback<JsonObject>{
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (!response.isSuccessful){
                    Log.d("PRODUCTS", "Unsuccessful: " + response.body())
                    if (response.message() == "null"){
                        return
                    }else{
                        activity?.showDialog("Error","${response.message()}")
                    }
                }

                if (response.isSuccessful){
                    binding.roots.isRefreshing=false
                    Log.d("PRODUCTS", "Successful")
                    val getting = response.body()
                    val data = getting!!.get("data").asJsonArray

                    for(d in data){
                        val value = d.asJsonObject
                        val product = ProductTable()
                        product.my_outlet_name = tinyDB!!.getString(TinyDB.OutletName)
                        product.outlet_uid = tinyDB!!.getString(TinyDB.OutletUID)
                        if (value.get("barcode").toString() != "null"){
                            product.barcode = value.get("barcode").toString().replace("\"", "")
                        }else{
                            product.barcode = ""
                        }
                        //product.barcode = value.get("barcode").asString
                        product.buyingPrice = value.get("buying_price").asInt
                        product.sellingPrice = value.get("selling_price").asInt
                        product.restockLevel = value.get("restock_level").asInt
                        product.isActive = value.get("is_active").asBoolean
                        product.name = value.get("name").toString().replace("\"", "")
                        product.image = (value.get("image").toString()).replace("\"", "")
                        product.skuClient = (value.get("sku_client").toString()).replace("\"", "")
                        viewmodel.insertProduct(product)
                    }

                }
            }
        })

    }




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object{
        fun newInstance()= Product()
    }


    override fun onResume() {
        super.onResume()
    }

    private fun setUpNavigation(){
        binding.roots.setOnRefreshListener {
            binding.roots.isRefreshing=true
            subScribeObserver()
        }

    }

}
