package com.capricorn.baxims.ui.business

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.recyclical.datasource.emptyDataSource
import com.capricorn.baxims.adapter.BusinessAdapter

import com.capricorn.baxims.R
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.databinding.FragmentBusinessBinding
import com.capricorn.baxims.ui.auth.AuthContainer
import com.capricorn.baxims.utils.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Business : Fragment() {
    lateinit var binding: FragmentBusinessBinding
    lateinit var navController: NavController
    private var dataSource = emptyDataSource()

    lateinit var providerFactory: ViewModelProvider.Factory
    lateinit var sharedPreferences: SharedPreferences

    var mApiService: ApiService? = null

    var tinyDB: TinyDB? = null

    var businessAdapter: BusinessAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_business, container, false)
        tinyDB = TinyDB(activity!!)
        navController = findNavController()
        businessAdapter = BusinessAdapter(
            activity!!,
            navController
        )
        binding.list.layoutManager = LinearLayoutManager(activity!!)
        initializerRetrofit()
        setUpNavigation()
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

        val call: Call<JsonObject> = mApiService!!.getBusinessList(tinyDB!!.getString(TinyDB.Token))

        call.enqueue(object : Callback<JsonObject>{
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if(!response.isSuccessful){
                    Log.d("BUSINESS", "Unsuccessful")
                    binding.swipe.isRefreshing=true
                }

                if(response.isSuccessful){
                    Log.d("BUSINESS", "Successful")
                    binding.swipe.isRefreshing=false
                    binding.emptystate.visibility = View.GONE

                    val getting = response.body()
                    val dataSource = getting!!.get("data").asJsonObject.get("my_own_businesses").asJsonArray
                    if (dataSource.size() > 0) {
                        businessAdapter!!.setBusinessData(dataSource)
                        binding.list.adapter = businessAdapter
                    }else{
                        binding.emptystate.visibility = View.VISIBLE
                        binding.textView12.text = "No business listed. Create a business"
                    }
                }

            }
        })

    }


    class BusinessViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){
        val businessName: TextView =itemview.findViewById(R.id.business_name)
        val businessCategory: TextView =itemview.findViewById(R.id.business_address)
        val businessCredential: TextView =itemview.findViewById(R.id.business_credentials)
        val businessFullItemView: ConstraintLayout = itemview.findViewById(R.id.business_item_fullview)

    }
    private fun setUpNavigation(){
        binding.swipe.setOnRefreshListener {
            binding.swipe.isRefreshing=true
            subscribeObserver()
        }
        binding.toolbar.setNavigationOnClickListener { activity?.navigateTo<AuthContainer>() }
        binding.addBiz.setOnClickListener { navController.navigate(R.id.action_business_to_addBusiness)

        }
    }

}
