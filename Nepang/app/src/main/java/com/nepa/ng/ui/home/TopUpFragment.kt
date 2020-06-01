package com.nepa.ng.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nepa.ng.R
import com.nepa.ng.adapters.AllVendorsAdapter
import com.nepa.ng.api.ApiService
import com.nepa.ng.databinding.FragmentTopUpBinding
import com.nepa.ng.model.AllVendorTable
import com.nepa.ng.viewmodel.VendorViewmodel
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 */
class TopUpFragment : Fragment() {

    lateinit var binding: FragmentTopUpBinding

    var mApiService: ApiService? = null

    lateinit var viewmodel: VendorViewmodel

    val token = "6kizWRqJlKHPpDtUcbZMNkIIZ7lAB5xK5YFiMqvVienZ1a6E108eCCHXbTzb"

    var vendorAdapter: AllVendorsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_up, container, false)
        viewmodel = ViewModelProvider(this).get(VendorViewmodel::class.java)


        val topupHeaderFirstPart = "Top Up Your "
        val topupHeaderSecondPart = "<font color='#1FA7F4'>Nepa </font>"

        binding.topUpTextHeader.text = (HtmlCompat.fromHtml(
            "$topupHeaderFirstPart$topupHeaderSecondPart", HtmlCompat.FROM_HTML_MODE_LEGACY))

        initializerRetrofit()

        vendorAdapter = AllVendorsAdapter()
        binding.list.adapter = vendorAdapter
        binding.list.layoutManager = LinearLayoutManager(activity!!)

        viewmodel.getAllVendors()?.observe(activity!!, Observer {

            vendorAdapter?.setDataForVendors(it as ArrayList<AllVendorTable>)

        })


        return  binding.root
    }



    private fun initializerRetrofit(){

        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val request: Request =
                    chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
                return chain.proceed(request)
            }
        })

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nepa.ng/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        mApiService = retrofit.create<ApiService>(ApiService::class.java)
        subscribeObserver()

    }

    private fun subscribeObserver() {

        val call: Call<JsonArray> = mApiService!!.getAllPowerVendor(token)

        call.enqueue(object : Callback<JsonArray> {
            override fun onFailure(call: Call<JsonArray>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {

                if(!response.isSuccessful){
                    Log.d("DONE", "Unsucessfull")
                }

                if(response.isSuccessful){

                    val getting = response.body()
                    val dataSource = getting!![0].asJsonArray

                    for (data in dataSource){
                        val value = data.asJsonObject
                        val vendor = AllVendorTable()
                        vendor.id = value.get("id").asInt
                        vendor.name = value.get("name").asString
                        vendor.logo = value.get("logo").asString
                        vendor.slug = value.get("slug").asString
                        vendor.cities = value.get("cities").asString
                        vendor.disco_name = value.get("disco_name").asString
                        viewmodel.insertVendor(vendor)
                    }

                    Log.d("DONE", "Sucessfull: " + dataSource.toString())

                }

            }
        })

    }

}
