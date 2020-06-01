package com.capricorn.baxims.ui.business


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.ViewHolder
import com.capricorn.baxims.adapter.OutLetAdapter
import com.capricorn.baxims.R
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.databinding.FragmentOutletBinding
import com.capricorn.baxims.utils.TinyDB
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Outlet : Fragment() {

    lateinit var binding:FragmentOutletBinding
    lateinit var navController: NavController

    var mApiService: ApiService? = null

    var tinyDB: TinyDB? = null
    var outletAdapter: OutLetAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_outlet, container, false)
        navController=findNavController()
        tinyDB = TinyDB(activity!!)
        outletAdapter = OutLetAdapter(activity!!)
        binding.list.layoutManager = LinearLayoutManager(activity!!)
        initializerRetrofit()
        setupNavigation()
        return binding.root
    }



    private fun initializerRetrofit(){

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.baxiims.com.ng/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mApiService = retrofit.create<ApiService>(ApiService::class.java)
        subscribeObserver()

    }



    private fun subscribeObserver(){

        val call: Call<JsonObject> = mApiService!!.getOutletList(tinyDB!!.getString(TinyDB.Token), tinyDB!!.getString(TinyDB.Domain))

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if(response.isSuccessful){
                    binding.swipe.isRefreshing=true
                }

                if(response.isSuccessful){

                    Log.d("Domain",  tinyDB!!.getString(TinyDB.Domain))
                    binding.swipe.isRefreshing=false
                    binding.emptystates.visibility = View.GONE

                    val getting = response.body()

                    val dataSource = getting!!.get("data").asJsonArray
                    if (dataSource.size() > 0) {
                        outletAdapter!!.setOutLetData(dataSource)
                        binding.list.adapter = outletAdapter
                    }
                }

            }
        })

    }



    private fun setupNavigation(){
        binding.swipe.setOnRefreshListener {
            binding.swipe.isRefreshing=true
            subscribeObserver()
        }

        binding.addOutlet.setOnClickListener {
            val action=OutletDirections.actionOutletToAddOutlet(tinyDB!!.getString(TinyDB.Domain))
            findNavController().navigate(action)
        }
        binding.toolbar.setNavigationOnClickListener {
            navController.navigate(R.id.action_outlet_to_business)
        }
    }


    class OutletBusiness(itemview:View):ViewHolder(itemview){
        val outletName: TextView =itemview.findViewById(R.id.outlet_name)
        val outletAddress: TextView =itemview.findViewById(R.id.outlet_address)
        val outletCredentials: TextView =itemview.findViewById(R.id.outlet_credentials)
        val outletFullItemView: ConstraintLayout = itemview.findViewById(R.id.outlet_item_full_view)

    }



}
