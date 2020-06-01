package com.capricorn.baxims.ui.business

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import br.com.ilhasoft.support.validation.Validator
import com.capricorn.baxims.R
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.api.business.BusinessList
import com.capricorn.baxims.api.business.BusinessRequest
import com.capricorn.baxims.databinding.FragmentAddBusinessBinding
import com.capricorn.baxims.ui.auth.SignupCategory
import com.capricorn.baxims.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddBusiness : Fragment() {
    lateinit var binding:FragmentAddBusinessBinding
    lateinit var navController: NavController

    var category: ArrayList<String> = arrayListOf()
    var categoryId: ArrayList<Int> = arrayListOf()

    var mApiService: ApiService? = null

    var tinyDB: TinyDB? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_add_business, container, false)
        navController=findNavController()
        tinyDB = TinyDB(activity!!)
        binding.sd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text=binding.sd.text
                binding.subd.helperText="$text.baxiims.com.ng"
            }
        })

        initializerRetrofit()
        //val validator=Validator(binding)
        navigate()
        return binding.root
    }


    private fun navigate(){
        binding.createBusiness.setOnClickListener {
            loadIndicator(0)
            val businessData = BusinessRequest()

            businessData.name = binding.buisinessName.text.toString()
            businessData.subdomain = binding.sd.text.toString() + ".baxiims.com.ng"
            val string = binding.businessCategory.selectedItem.toString()
            when(string){
                "Select Category"->{
                    businessData.business_category_id="0"
                }
                "Supermarket"->{businessData.business_category_id="1"

                }
                "Minimart"->{businessData.business_category_id="2"

                }
                "Spare Parts"->{businessData.business_category_id="3"
                }
                "Groceries"->{businessData.business_category_id="4"
                }
            }



            if(activity!!.isConnectedToTheInternet()) {
                subscribeObserver(businessData)
            }else{
                val snack = Snackbar.make(it,"Internet Connection Required", Snackbar.LENGTH_LONG)
                snack.show()
                loadIndicator(1)
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

    private fun subscribeObserver(businessData: BusinessRequest) {

        val call: Call<JsonObject> = mApiService!!.addBusiness(tinyDB!!.getString(TinyDB.Token), businessData)

        call.enqueue(object : Callback<JsonObject>{
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if(!response.isSuccessful){
                    loadIndicator(1)
                    Log.d("ADDBUS", "Unsuccessful: " + businessData.subdomain)
                    if (response.message() == "null"){
                        return
                    }else{
                        activity?.showDialog("Please Try Again",
                            "Some data you entered are either not valid or already exist")
                    }
                }

                if(response.isSuccessful){
                    Log.d("ADDBUS", "Successful: " + businessData.business_category_id)
                    tinyDB!!.putInt(TinyDB.Business_ID, businessData.business_category_id!!.toInt())
                    tinyDB!!.putString(TinyDB.Domain, businessData.subdomain!!)
                    val intentCategory = Intent(activity, SignupCategory::class.java)
                    startActivity(intentCategory)
                    activity?.overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                    loadIndicator(1)
                }
            }

        })

    }

    private fun loadIndicator(state:Int){
        when(state){
            0->{
                binding.progress.elevation=15F
                binding.createBusiness.loadState(0,"Loading")
            }
            1->{
                binding.progress.elevation=0.1F
                binding.createBusiness.loadState(1,"Continue")
            }
        }
    }

}
