package com.capricorn.baxims.ui.business

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import br.com.ilhasoft.support.validation.Validator

import com.capricorn.baxims.R
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.api.business.OutletList
import com.capricorn.baxims.api.business.OutletRequest
import com.capricorn.baxims.databinding.FragmentAddOutletBinding
import com.capricorn.baxims.utils.TinyDB
import com.capricorn.baxims.utils.isConnectedToTheInternet
import com.capricorn.baxims.utils.loadState
import com.capricorn.baxims.utils.showDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AddOutlet : Fragment() {
    lateinit var binding:FragmentAddOutletBinding
    lateinit var navController: NavController

    lateinit var providerFactory: ViewModelProvider.Factory

    var mApiService: ApiService? = null

    var tinyDB: TinyDB? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_add_outlet, container, false)
        navController=findNavController()
        tinyDB = TinyDB(activity!!)
        initializerRetrofit()
        val validator=Validator(binding)
        navigate(validator)
        return binding.root
    }



    private fun navigate(validator: Validator){

        binding.toolbar.setNavigationOnClickListener { navController.navigateUp() }

        binding.createOutlet.setOnClickListener {

            loadIndicator(0)
            val outletData = OutletRequest()

            outletData.name = binding.addOutletName.text.toString()
            outletData.address = binding.addOutletAddress.text.toString()
            outletData.city = binding.addOutletCity.text.toString()

            if (validator.validate()){
                if(activity!!.isConnectedToTheInternet()) {
                    subscribeObserver(outletData)
                }else{
                    val snack = Snackbar.make(it,"Internet Connection Required", Snackbar.LENGTH_LONG)
                    snack.show()
                    loadIndicator(1)
                }
            }
        }
    }



    fun initializerRetrofit(){

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.baxiims.com.ng/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mApiService = retrofit.create<ApiService>(ApiService::class.java)

    }


    private fun subscribeObserver(outletData: OutletRequest) {

        val call: Call<JsonObject> =
            mApiService!!.addOutletList(tinyDB!!.getString(TinyDB.Token), outletData, tinyDB!!.getString(TinyDB.Domain))

        call.enqueue(object : Callback<JsonObject>{
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(!response.isSuccessful){
                    loadIndicator(1)
                    Log.d("ADDOut", "Successfull: " + outletData.name + ", " + outletData.address
                            + ", " + outletData.city + ", " + tinyDB!!.getString(TinyDB.Domain))
                    if (response.message() == "null"){
                        return
                    }else{
                        activity?.showDialog("Please Try Again",
                            "Some data you entered are either not valid or already exist")
                    }
                }


                if(response.isSuccessful){
                    Log.d("ADDOut", "Successfull")
                    val action=AddOutletDirections.actionAddOutletToOutlet(tinyDB!!.getString(TinyDB.Domain))
                    findNavController().navigate(action)
                    loadIndicator(1)
                }
            }
        })
    }


        private fun loadIndicator(state:Int){
        when(state){
            0->{
                binding.progress.elevation=15F
                binding.createOutlet.loadState(0,"Loading")
            }
            1->{
                binding.progress.elevation=0.1F
                binding.createOutlet.loadState(1,"Continue")
            }
        }
    }
}
