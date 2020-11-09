package ng.nepa.app.fragments.electricitypayment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject

import com.nepa.app.R
import ng.nepa.app.activities.PaymentActivity
import com.nepa.app.databinding.FragmentOrderSummaryBinding
import ng.nepa.app.utils.TinyDB
import com.nepa.ng.api.ApiService
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
class OrderSummaryFragment : Fragment() {

    private var discoId: String? = null
    private var phone: String? = null
    private var totalAmount: String? = null
    private var amount: String? = null
    private var meterNumber: String? = null
    private var paymentType: String? = null
    private var email: String? = null

    lateinit var binding: FragmentOrderSummaryBinding
    lateinit var navController: NavController

    var tinyDB: TinyDB? = null

    var mApiService: ApiService? = null

    val token = "6kizWRqJlKHPpDtUcbZMNkIIZ7lAB5xK5YFiMqvVienZ1a6E108eCCHXbTzb"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_summary, container, false)
        navController = findNavController()
        tinyDB = TinyDB(requireActivity())
        subscribeIntializer()

        val activities = activity as AppCompatActivity
        activities.setSupportActionBar(binding.orderSummaryToolbar)
        activities.supportActionBar?.title = "Order Summary"

        activities.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activities.supportActionBar?.setHomeButtonEnabled(true)
        activities.supportActionBar?.setDisplayShowHomeEnabled(true)

        if (tinyDB!!.getBoolean(TinyDB.Subscribe)){
            binding.subscribeOrderSummary.text = getString(R.string.subscription_false_status)
        }else{
            binding.subscribeOrderSummary.text = getString(R.string.subscription_true_status)
        }

        binding.subscribeOrderSummary.setOnClickListener {
            tinyDB!!.putBoolean(TinyDB.Subscribe, true)
            subscribeDialog()
        }

        binding.orderSummaryToolbar.setNavigationOnClickListener {
            navController.navigate(R.id.action_orderSummaryFragment_to_buyElectricityFragment)
        }

        paymentType = arguments?.getString("paymentType")
        meterNumber = arguments?.getString("meterNumber")
        amount = arguments?.getString("amount")
        email = arguments?.getString("email")
        phone = arguments?.getString("phone")
        val image = arguments?.getString("image")
        val userName =  arguments?.getString("userName")
        val userAddress =  arguments?.getString("userAddress")
        discoId =  arguments?.getString("discoId")


        binding.userName.text = userName
        binding.userAddress.text = userAddress

        Glide.with(requireActivity()).load(image).into(binding.summaryVendorLogo)
        confirmRequest()

        binding.summaryMeterNumber.text = meterNumber
        binding.summaryMeterType.text = paymentType
        binding.summaryAmount.text = (amount!!.toInt() + 100).toString()
        //binding.summaryUnit.text = totalAmount

        binding.summaryCancel.setOnClickListener {
            activity?.finish()
        }

        binding.sumPayCreditCardRadio.setOnClickListener {
            binding.sumPayCreditCardRadio.isChecked = true
            binding.sumPayTransferRadio.isChecked = false
        }

        binding.sumPayTransferRadio.setOnClickListener {
            binding.sumPayCreditCardRadio.isChecked = false
            binding.sumPayTransferRadio.isChecked = true
        }

        binding.btnGetUnit.setOnClickListener {
            if (binding.sumPayCreditCardRadio.isChecked){

                if (totalAmount == null){
                    totalAmount = (amount!!.toInt() + 100).toString()
                }

                val paymentIntent = Intent(requireActivity(), PaymentActivity::class.java)
                paymentIntent.putExtra("userName", userName)
                paymentIntent.putExtra("email", email)
                paymentIntent.putExtra("amount", totalAmount)
                paymentIntent.putExtra("discoId", discoId)
                paymentIntent.putExtra("meterNumber", meterNumber)
                paymentIntent.putExtra("phone", phone)
                paymentIntent.putExtra("paymentType", paymentType)
                startActivity(paymentIntent)
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            }else if(binding.sumPayTransferRadio.isChecked){
                Snackbar.make(it, "This Feature will be available in Future Release",Snackbar.LENGTH_LONG).show()
            }else{
                Snackbar.make(it, "Please Select a payment method",Snackbar.LENGTH_LONG).show()
            }
        }

        return binding.root
    }



    private fun subscribeIntializer(){
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
            .baseUrl("https://nepa.ng/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        mApiService = retrofit.create<ApiService>(ApiService::class.java)
    }


    private fun confirmRequest(){

        val call: Call<JsonObject> = mApiService!!.confirmTokenRequest(email!!, phone!!,
            paymentType!!, meterNumber!!, amount!!, discoId!!)

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("CONFIRM", "Failed")
            }
            

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if(!response.isSuccessful){
                    Log.d("CONFIRM", "Unsuccessful: " + response.message())


                }

                if(response.isSuccessful){
                    Log.d("CONFIRM", "Successful: " + response.message())

                    val getting = response.body()
                    totalAmount = getting!!.get("total_amount").asString
                    binding.summaryAmount.text = totalAmount
                }

            }
        })

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun subscribeDialog(){
        MaterialDialog(requireContext()).show {
            cornerRadius(5F)
            customView(R.layout.item_subscribe_layout)


            val subBtn = this.view.findViewById<Button>(R.id.btn_subscribe)
            val subName = this.view.findViewById<AutoCompleteTextView>(R.id.subscribe_name)
            val subEmail = this.view.findViewById<AutoCompleteTextView>(R.id.subscribe_email)

            subBtn.setOnClickListener {
                when {
                    TextUtils.isEmpty(subName.text) -> {
                        subName.error = "Name cannot be empty"
                    }
                    TextUtils.isEmpty(subEmail.text) -> {
                        subEmail.error = "Please enter your email"
                    }
                    else -> {
                        subBtn.loadState(0, "Please wait ...")
                        tinyDB?.putString(TinyDB.SubscribeName, subName.text.toString())
                        tinyDB?.putString(TinyDB.SubscribeEmail, subEmail.text.toString())
                        subscribeMail()
                        dismiss()
                    }
                }
            }

        }

    }



    fun subscribeMail(){

        val call: Call<JsonObject> = mApiService!!.subscribeMail(tinyDB!!.getString(TinyDB.SubscribeName), tinyDB!!.getString(TinyDB.SubscribeEmail))

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("SUBSCRIBE", "Failed")
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if(!response.isSuccessful){
                    Log.d("SUBSCRIBE", "Unsuccessful: " + response.message())

                }

                if(response.isSuccessful){
                    Log.d("SUBSCRIBE", "Successful: " + response.message() + tinyDB!!.getString(TinyDB.SubscribeName))
                }

            }
        })

    }

    private fun Button.loadState(state:Int, message: String){
        this.apply {
            when(state){
                0->{
                    alpha=0.6f
                    text=message
                    isClickable=false
                }
                1->{
                    alpha=1.0f
                    text=message
                    isClickable=true
                }
            }

        }
    }




}
