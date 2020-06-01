package ng.nepa.app.fragments.electricitypayment

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonObject

import com.nepa.app.R
import com.nepa.app.databinding.FragmentBuyElectricityBinding
import com.nepa.ng.api.ApiService
import ng.nepa.app.utils.TinyDB
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
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class BuyElectricityFragment : Fragment() {


    private var tinyDB: TinyDB? = null
    lateinit var binding: FragmentBuyElectricityBinding
    lateinit var navController: NavController


    var mApiService: ApiService? = null

    var discoId: String? = null
    var discoImage: String? = null

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    val token = "6kizWRqJlKHPpDtUcbZMNkIIZ7lAB5xK5YFiMqvVienZ1a6E108eCCHXbTzb"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_buy_electricity, container, false)
        navController = findNavController()
        tinyDB = TinyDB(requireActivity())
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
        initializerRetrofit()

        val activities = activity as AppCompatActivity
        activities.setSupportActionBar(binding.electricityToolbar)
        activities.supportActionBar?.title = "Buy Electricity"

        activities.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activities.supportActionBar?.setHomeButtonEnabled(true)
        activities.supportActionBar?.setDisplayShowHomeEnabled(true)

        if (tinyDB!!.getBoolean(TinyDB.Subscribe)){
            binding.subscribeElectricity.text = getString(R.string.subscription_false_status)
        }else{
            binding.subscribeElectricity.text = getString(R.string.subscription_true_status)
        }

        binding.subscribeElectricity.setOnClickListener {
            tinyDB!!.putBoolean(TinyDB.Subscribe, true)
            subscribeDialog()
        }

        binding.electricityToolbar.setNavigationOnClickListener {
            activity?.finish()
        }


        val listOfMeter = ArrayList<String>()
        val listOfAmount = ArrayList<String>()
        val listOfEmail = ArrayList<String>()
        val listOfPhoneNum = ArrayList<String>()

        for (str in tinyDB!!.getListString(TinyDB.MeterNumber)){
            listOfMeter.add(str)
        }

        for (str in tinyDB!!.getListString(TinyDB.Amount)){
            listOfAmount.add(str)
        }

        for (str in tinyDB!!.getListString(TinyDB.Email)){
            listOfEmail.add(str)
        }

        for (str in tinyDB!!.getListString(TinyDB.PhoneNumber)){
            listOfPhoneNum.add(str)
        }


        val arrayAdapterMeter = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, listOfMeter)
        val arrayAdapterAmount = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, listOfAmount)
        val arrayAdapterEmail = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, listOfEmail)
        val arrayAdapterPhone = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, listOfPhoneNum)

        Log.d("MeterNum", listOfMeter.toString())

        binding.payMeterNumber.setAdapter(arrayAdapterMeter)
        binding.payMeterNumber.threshold = 1

        binding.paymentEmail.setAdapter(arrayAdapterEmail)
        binding.paymentEmail.threshold = 0

        binding.paymentAmount.setAdapter(arrayAdapterAmount)
        binding.paymentAmount.threshold = 0

        binding.paymentPhoneNumber.setAdapter(arrayAdapterPhone)
        binding.paymentPhoneNumber.threshold = 0


        discoImage = activity?.intent?.getStringExtra("image")
        val discoSlug = activity?.intent?.getStringExtra("payment_type")
        discoId = activity?.intent?.getStringExtra("disco_id")

        Glide.with(requireActivity()).load(discoImage).into(binding.selectedVendorLogo)

        binding.paymentPhoneNumber.setOnEditorActionListener { view, i, keyEvent ->

            focusOnView()
            return@setOnEditorActionListener true
        }


        if (discoSlug!!.contains("postpaid")){
            binding.radioPrepaid.isChecked = true
        }else if(discoSlug.contains("prepaid")){
            binding.radioPrepaid.isChecked = true
        }

        binding.paymentSubmit.setOnClickListener {
            val selectedId = binding.radioGroup.checkedRadioButtonId
            when {
                TextUtils.isEmpty(binding.root.findViewById<RadioButton>(selectedId).text) -> {
                    binding.root.findViewById<RadioButton>(selectedId).error = "Please select a payment type"
                }
                TextUtils.isEmpty(binding.payMeterNumber.text) -> {
                    binding.payMeterNumber.error = "Please enter your Meter Number"
                }
                TextUtils.isEmpty(binding.paymentEmail.text) -> {
                    binding.paymentEmail.error = "Please enter Email"
                }
                TextUtils.isEmpty(binding.paymentAmount.text) -> {
                    binding.paymentAmount.error = "Please enter Amount"
                }
                TextUtils.isEmpty(binding.paymentPhoneNumber.text) -> {
                    binding.paymentPhoneNumber.error = "Phone number required"
                }
                else -> {

                    listOfMeter.clear()
                    listOfMeter.add(binding.payMeterNumber.text.toString())
                    tinyDB?.putListString(TinyDB.MeterNumber, listOfMeter)

                    listOfEmail.clear()
                    listOfEmail.add(binding.paymentEmail.text.toString())
                    tinyDB?.putListString(TinyDB.Email, listOfEmail)


                    listOfPhoneNum.clear()
                    listOfPhoneNum.add(binding.paymentPhoneNumber.text.toString())
                    tinyDB?.putListString(TinyDB.PhoneNumber, listOfPhoneNum)


                    listOfAmount.clear()
                    listOfAmount.add(binding.paymentAmount.text.toString())
                    tinyDB?.putListString(TinyDB.Amount, listOfAmount)


                    loadIndicator(0)
                    //Toast.makeText(activity!!, discoId, Toast.LENGTH_LONG).show()
                    subscribeObserver(discoId, discoImage!!)

                }

            }

        }

        binding.paymentCancel.setOnClickListener {
            activity?.finish()
        }

        return binding.root
    }


    private fun focusOnView(){
        binding.electricityNs.post { binding.electricityNs.scrollTo(0, binding.payMeterNumber.bottom) }
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

    }


    private fun subscribeObserver(discoId:String?, discoImage: String) {

        val selectedId = binding.radioGroup.checkedRadioButtonId
        val paymentType = binding.root.findViewById<RadioButton>(selectedId).text.toString()
        val meterNumber = binding.payMeterNumber.text.toString().trim()
        val amount = binding.paymentAmount.text.toString()
        val email = binding.paymentEmail.text.toString()
        val phone = binding.paymentPhoneNumber.text.toString()

        val call: Call<JsonObject> = mApiService!!.verifyMeterNumber("application/json", discoId!!, meterNumber)

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("VERIFY_DONE", "Failed")
                loadIndicator(1)
                showDialog("Something Went Wrong!",  "Please check your internet connection ${t.localizedMessage}")
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if(!response.isSuccessful){
                    loadIndicator(1)
                    Log.d("VERIFY_DONE", "Unsuccessful: " + response.message())
                    showDialog("Sorry!",  "${response.message()}"  + "Please verify your data and try again")

                }

                if(response.isSuccessful){

                    loadIndicator(1)
                    val getting = response.body()
                    val status = getting!!.get("status").asBoolean

                    if (status) {
                        val dataSource = getting.get("data").asJsonObject

                        val userName = dataSource.get("name").toString().replace("\"", "")
                        val userAddress = dataSource.get("address").toString().replace("\"", "")


                        val bundles = Bundle()
                        bundles.putString(
                            FirebaseAnalytics.Param.CONTENT_TYPE,
                            "Detail Verification"
                        )
                        bundles.putString(FirebaseAnalytics.Param.ITEM_ID, userName)
                        bundles.putString(FirebaseAnalytics.Param.ITEM_NAME, meterNumber)
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundles)

                        val bundle = Bundle()
                        bundle.putString("meterNumber", meterNumber)
                        bundle.putString("paymentType", paymentType)
                        bundle.putString("amount", amount)
                        bundle.putString("email", email)
                        bundle.putString("phone", phone)
                        bundle.putString("image", discoImage)
                        bundle.putString("userName", userName)
                        bundle.putString("userAddress", userAddress)
                        bundle.putString("discoId", discoId)
                        navController.navigate(
                            R.id.action_buyElectricityFragment_to_orderSummaryFragment,
                            bundle
                        )
                        Log.d("VERIFY_DONE", "Successful: $dataSource")

                    }else{
                        val error = getting.get("error").asJsonObject
                        val message = error.get("message").asString
                        showDialog("Error Occurred!",  "$message")
                    }
                }

            }
        })
    }


    private fun showDialog(title:String, message:String){
        MaterialDialog(requireActivity()).show {
            cornerRadius(5F)
            title(text = title)
            message(text = message)

            positiveButton(R.string.agree) {
                hide()
            }

            negativeButton {  }

        }

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


    private fun loadIndicator(state:Int){
        when(state){
            0->{
                //binding.progress.elevation = 15F
                binding.progress.visibility = View.VISIBLE
                binding.paymentSubmit.loadState(0,"Loading")
            }
            1->{
                binding.progress.visibility = View.GONE
                binding.paymentSubmit.loadState(1,"Submit")
            }
        }
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
                        subscribeIntializer()
                        dismiss()
                    }
                }
            }

        }

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

        subscribeMail()
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

}
