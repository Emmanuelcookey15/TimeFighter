package com.capricorn.baxims.ui.auth


import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import br.com.ilhasoft.support.validation.Validator

import com.capricorn.baxims.R
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.api.auth.SignUpRequest
import com.capricorn.baxims.databinding.FragmentSignupBinding
import com.capricorn.baxims.models.UserTable
import com.capricorn.baxims.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_signup_category.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SignupFragment : Fragment() {
    lateinit var binding: FragmentSignupBinding
    lateinit var navController: NavController

    lateinit var providerFactory: ViewModelProvider.Factory
    lateinit var sharedPreferences: SharedPreferences
    lateinit var viewmodel: AuthViewModel

    var mApiService: ApiService? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_signup, container, false)
        viewmodel = ViewModelProvider(this).get(AuthViewModel::class.java)
        navController=findNavController()
        initializerRetrofit()
        val validator=Validator(binding)
        navigate(validator)
        return binding.root
    }


    fun initializerRetrofit(){

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.baxiims.com.ng/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mApiService = retrofit.create<ApiService>(ApiService::class.java)

    }


    private fun navigate(validator: Validator){
        binding.Next.setOnClickListener {
            loadIndicator(0)
            val signUpData = SignUpRequest()

            signUpData.first_name = binding.firstName.text.toString()
            signUpData.last_name = binding.lastName.text.toString()
            signUpData.email = binding.email.text.toString()
            signUpData.phone_no = binding.phoneNo.text.toString()
            signUpData.username = binding.userName.text.toString()
            signUpData.password = binding.password.text.toString()

            val string = binding.gender.selectedItem.toString()
            when(string){
                "Select Gender"->{
                    signUpData.gender=null
                }
                "Male"->{
                    signUpData.gender=string

                }
                "Female"->{
                    signUpData.gender=string
                }
            }

            if (validator.validate()){
                if(activity!!.isConnectedToTheInternet()) {
                    if(signUpData.gender != null) {
                        subscribeObserver(signUpData)
                    }else{
                        val snack = Snackbar.make(it,"Select Gender", Snackbar.LENGTH_LONG)
                        snack.show()
                        loadIndicator(1)
                    }
                }else{
                    val snack = Snackbar.make(it,"Internet Connection Required", Snackbar.LENGTH_LONG)
                    snack.show()
                    loadIndicator(1)
                }
            }


        }
    }



    private fun subscribeObserver(signUpData: SignUpRequest){

        val call: Call<JsonObject> = mApiService!!.signUp(signUpData)

        call.enqueue(object : Callback<JsonObject>{
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(!response.isSuccessful){
                    loadIndicator(1)
                    if (response.message() == "null"){
                        return
                    }else{
                        activity?.showDialog("Please Try Again",
                            "Some data you entered are not valid or already exist")
                    }
                }

                if(response.isSuccessful){

                    val getting = response.body()
                    val data = getting!!.get("data").asJsonObject
                    val userTable = UserTable()
                    userTable.id = data.get("id").asInt
                    userTable.first_name = data.get("first_name").asString
                    userTable.last_name = data.get("last_name").asString
                    userTable.username = data.get("username").asString
                    userTable.phone_no = data.get("phone_no").asString
                    userTable.email = data.get("email").asString
                    userTable.gender = signUpData.gender.toString()
                    userTable.isActive = true
                    userTable.password = signUpData.password!!
                    viewmodel.insertModel(userTable)
                    loadIndicator(1)
                    navController.navigate(R.id.action_navigate_signup_to_authSuccessfull)
                }
            }
        })

    }

    /**refer to ExtentionFunction class to get more info on this extensions*/
    private fun loadIndicator(state:Int){
       when(state){
           0->{
               binding.progress.elevation=15F
               binding.Next.loadState(0,"Loading")
           }
           1->{
               binding.progress.elevation=0F
               binding.Next.loadState(1,"Next")
           }
       }
    }



}
