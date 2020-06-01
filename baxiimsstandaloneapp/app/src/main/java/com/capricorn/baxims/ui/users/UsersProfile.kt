package com.capricorn.baxims.ui.users

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import br.com.ilhasoft.support.validation.Validator

import com.capricorn.baxims.R
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.api.auth.SignUpRequest
import com.capricorn.baxims.api.auth.UpdateRequest
import com.capricorn.baxims.databinding.FragmentUsersProfileBinding
import com.capricorn.baxims.utils.isConnectedToTheInternet
import com.capricorn.baxims.utils.showToast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class UsersProfile : Fragment() {
    lateinit var binding:FragmentUsersProfileBinding
    lateinit var navController: NavController

    var mApiService: ApiService? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_users_profile, container, false)
        initializerRetrofit()
        navController=findNavController()
        val validator=Validator(binding)
        setUpNavigaion(validator)
        return binding.root
    }

    private fun setUpNavigaion(validator: Validator) {
        binding.update.setOnClickListener {

            val signUpData = UpdateRequest()

            signUpData.first_name = binding.firstNameProfile.text.toString()
            signUpData.last_name = binding.lastNameProfile.text.toString()
            signUpData.email = binding.profileEmail.text.toString()
            signUpData.username = binding.profileUsername.text.toString()
                context?.showToast("in progress")
                if (validator.validate()){
                    if(activity!!.isConnectedToTheInternet()) {
                        subscribeObserver(signUpData)
                    }else{
                        val snack = Snackbar.make(it,"Internet Connection Required", Snackbar.LENGTH_LONG)
                        snack.show()
                    }
                }


        }

        binding.toolbar.setNavigationOnClickListener { activity?.finish() }
    }


    fun initializerRetrofit(){

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.baxiims.com.ng/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mApiService = retrofit.create<ApiService>(ApiService::class.java)

    }


    private fun subscribeObserver(signUpData: UpdateRequest){

        val call: Call<JsonObject> = mApiService!!.update(signUpData)

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if(!response.isSuccessful){
                    Log.d("PROFILE", "Unsuccessful")
                }

                if(response.isSuccessful){
                    Log.d("PROFILE", "Success")

                    activity?.finish()
                }
            }
        })

    }


}
