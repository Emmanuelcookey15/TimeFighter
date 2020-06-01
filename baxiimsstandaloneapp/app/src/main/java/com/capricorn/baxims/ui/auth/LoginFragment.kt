package com.capricorn.baxims.ui.auth


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import br.com.ilhasoft.support.validation.Validator

import com.capricorn.baxims.R
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.api.auth.LoginRequest
import com.capricorn.baxims.databinding.FragmentLoginBinding
import com.capricorn.baxims.models.UserTable
import com.capricorn.baxims.ui.business.BusinessContainer
import com.capricorn.baxims.ui.dashboard.DashboardContainer
import com.capricorn.baxims.utils.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.regex.Pattern


class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    lateinit var navController: NavController
    lateinit var viewmodel: AuthViewModel
    lateinit var providerFactory: ViewModelProvider.Factory

    var tinyDB: TinyDB? = null

    var mApiService: ApiService? = null

    val emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$"
    val pattern = Pattern.compile(emailRegEx)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_login, container, false)
        viewmodel = ViewModelProvider(this).get(AuthViewModel::class.java)
        tinyDB = TinyDB(activity!!)
        navController=findNavController()
        initializerRetrofit()
        val validator=Validator(binding)
        navigate(validator)
        return binding.root
    }

    private fun navigate(validator: Validator){
        binding.login.setOnClickListener {
            val loginRequest = LoginRequest()

            loginRequest.username = binding.loginName.text.toString().trim()
            loginRequest.password = binding.loginPassword.text.toString().trim()
            if (validator.validate()){
                loadIndicator(0)
                if(activity!!.isConnectedToTheInternet()){
                    subscribeObserver(loginRequest)
                }else{
                    viewmodel.getUserNum().observe(activity!!, Observer<Int> { it ->
                        if (it > 0){
                            val matcher = pattern.matcher(loginRequest.username!!)
                            if(matcher.find()){// This means user logged in using his Email
                                viewmodel.getUserByEmail(loginRequest.username!!)?.observe(activity!!,
                                    Observer<UserTable?> { t ->
                                        if (t?.email != null){
                                            if(viewmodel.outLetCount() > 0){
                                                val newAccess = viewmodel.getUserOutLetAccess(t.id!!, tinyDB!!.getString(TinyDB.OutletName))
                                                if(newAccess?.outletName != null){
                                                    tinyDB!!.putInt(TinyDB.UserID, newAccess.userId!!)
                                                    activity?.navigateTo<DashboardContainer> {}
                                                }else{
                                                    activity?.showDialog("Sorry", "Invalid Email or Password")
                                                    loadIndicator(1)
                                                }
                                            }else{
                                                activity?.navigateTo<DashboardContainer> {  }
                                            }
                                        }else{
                                            activity?.showDialog("Error", "Invalid Email or Password")
                                            loadIndicator(1)
                                        }
                                    })

                            }else {
                                // This means user logged in using his Username
                                viewmodel.getUserByUserName(loginRequest.username!!)?.observe(activity!!,
                                    Observer<UserTable?> { t ->
                                        if (t?.username != null){
                                            if(viewmodel.outLetCount() > 0){
                                                val newAccess = viewmodel.getUserOutLetAccess(t.id!!, tinyDB!!.getString(TinyDB.OutletName))
                                                if(newAccess?.outletName != null){
                                                    activity?.navigateTo<DashboardContainer> {}
                                                }else{
                                                    activity?.showDialog("Sorry", "Invalid Email or Password")
                                                }
                                            }else{
                                                activity?.navigateTo<BusinessContainer> {  }
                                            }
                                        }else{
                                            activity?.showDialog("Error", "Invalid Email or Password")
                                            loadIndicator(1)
                                        }
                                    })
                            }

                        }else{
                            activity?.showDialog("Error",
                                "Internet Connection Required For First Time Login")
                            loadIndicator(1)
                        }
                    })
                }
            }
        }
        binding.GoToSignUp.setOnClickListener { navController.navigate(R.id.action_navigate_login_to_navigate_signup) }
    }


    fun initializerRetrofit(){

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.baxiims.com.ng/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mApiService = retrofit.create<ApiService>(ApiService::class.java)


    }

    private fun subscribeObserver(loginRequest: LoginRequest){


        val call: Call<JsonObject> = mApiService!!.login(loginRequest)
        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("INTER", "Failed")
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (!response.isSuccessful){
                    Log.d("INTER", "Unsuccessful: " + response.body())
                    loadIndicator(1)
                    if (response.message() == "null"){
                        return
                    }else{
                        activity?.showDialog("Please Try Again","Likely your username or password is incorrect")
                    }
                }

                if (response.isSuccessful){
                    val getting = response.body()
                    val data = getting!!.get("data").asJsonObject
                    val subData = data.get("user").asJsonObject
                    val matcher = pattern.matcher(loginRequest.username!!)
                    tinyDB!!.putString(TinyDB.Token, data.get("token").asString)
                    tinyDB!!.putInt(TinyDB.UserID,
                        subData.get("id").asInt)
                    val userTable = UserTable()
                    userTable.id = subData.get("id").asInt
                    userTable.first_name = subData.get("first_name").asString
                    userTable.last_name = subData.get("last_name").asString
                    userTable.username = subData.get("username").asString
                    userTable.isActive = true
                    userTable.password = loginRequest.password!!
                    if(matcher.find()){
                        userTable.email = loginRequest.username!!
                    }
                    viewmodel.insertModel(userTable)
                    activity?.navigateTo<BusinessContainer> {  }
                }
            }
        })
    }

    private fun loadIndicator(state:Int){
        when(state){
            0->{
                binding.progress.elevation=15F
                binding.login.loadState(0,"Loading")
            }
            1->{
                binding.progress.elevation=0.1F
                binding.login.loadState(1,"Continue to Login")
            }
        }
    }




}
