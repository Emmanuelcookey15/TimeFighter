package com.capricorn.baxims.ui.dashboard


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.capricorn.baxims.R
import com.capricorn.baxims.adapter.TransactionAdapter
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.api.transaction.Customer
import com.capricorn.baxims.api.transaction.FullTransaction
import com.capricorn.baxims.api.transaction.ProductTransaction
import com.capricorn.baxims.api.transaction.Transactions
import com.capricorn.baxims.databinding.FragmentTransactionBinding
import com.capricorn.baxims.utils.TinyDB
import com.capricorn.baxims.utils.isConnectedToTheInternet
import com.capricorn.baxims.utils.showDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass.
 */
class Transaction : Fragment() {

    lateinit var binding: FragmentTransactionBinding

    lateinit var viewmodel: DashboardViewModel

    var mApiService: ApiService? = null

    var tinyDB: TinyDB? = null

    var transactionAdapter: TransactionAdapter? = null

    val transactionNew = ArrayList<String>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_transaction, container, false)
        viewmodel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        tinyDB = TinyDB(activity!!)
        initializerRetrofit()


        transactionAdapter = TransactionAdapter(activity!!)
        binding.list.adapter = transactionAdapter
        binding.list.layoutManager = LinearLayoutManager(activity!!)

        viewmodel.getTransactionByOutletUID(tinyDB!!.getString(TinyDB.OutletUID))?.observe(viewLifecycleOwner, Observer {
            for (tran in it){
                transactionNew.add(tran.transactions_json_string)
            }

            transactionAdapter!!.setTransactionData(transactionNew)

            upload(transactionNew)

        })

        //transactionAdapter!!.setTransactionData(transaction)

        return binding.root
    }


    fun initializerRetrofit(){

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.baxiims.com.ng/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mApiService = retrofit.create<ApiService>(ApiService::class.java)

    }


    private fun upload(transactioArray: ArrayList<String>){

        val transactionFull = FullTransaction()
        val tList = ArrayList<Transactions>()
        val t = Transactions()
        val proList = ArrayList<ProductTransaction>()
        val cus = Customer()
        val pro = ProductTransaction()


        for(trans in transactioArray){
            val item = JsonParser.parseString(trans).asJsonObject
            t.username = item.get("username").asString
            t.outlet_uid = item.get("outlet_uid").asString
            t.ref = item.get("ref").asString
            t.type = item.get("type").asString
            t.amount = item.get("amount").asInt
            t.payment_method = item.get("payment_method").asString
            t.processed_at = item.get("processed_at").asString
            //
            cus.name = item.get("customer").asJsonObject.get("name").asString
            cus.phone_no = item.get("customer").asJsonObject.get("phone_no").asString
            t.customer = cus

            var itemProduct = item.get("products").asJsonArray
            for (p in itemProduct){
                val itemvalue = p.asJsonObject
                pro.sku_client = itemvalue.get("sku_client").asString
                pro.name = itemvalue.get("name").asString
                if (itemvalue.get("barcode").asString != null){
                    pro.barcode = itemvalue.get("barcode").asString
                }else{
                    pro.barcode = "noBarcode"
                }
                pro.selling_price = itemvalue.get("selling_price").asInt
                pro.restock_level = itemvalue.get("restock_level").asInt
                pro.quantity = itemvalue.get("quantity").asInt
                proList.add(pro)
            }

            t.products = proList

            tList.add(t)
        }

        transactionFull.transactions = tList


        if(activity!!.isConnectedToTheInternet()) {
            Log.d("TransTTT", transactionFull.transactions.toString())
            subscribeObserver(transactionFull)
        }

    }

    private fun subscribeObserver(transactionFull: FullTransaction) {

        val call: Call<JsonObject> = mApiService!!.postTransactionHistory(
            tinyDB!!.getString(TinyDB.Token), tinyDB!!.getString(TinyDB.Domain), transactionFull)

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (!response.isSuccessful){
                    Log.d("TransactionPosted", "Unsuccessful: " + response.errorBody().toString())
                    if (response.message() == "null"){
                        return
                    }else{
                        activity?.showDialog("Error","${response.raw().message}")
                    }
                }

                if (response.isSuccessful){
                    Log.d("TransactionPosted","Very Sucessfull")
                }
            }
        })
    }

    companion object{
        fun newInstance()= Transaction()
    }
}
