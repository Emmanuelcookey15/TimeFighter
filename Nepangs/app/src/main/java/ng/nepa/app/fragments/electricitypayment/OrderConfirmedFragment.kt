package ng.nepa.app.fragments.electricitypayment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonObject

import com.nepa.app.R
import ng.nepa.app.activities.MainActivity
import com.nepa.app.databinding.FragmentOrderConfirmedBinding
import com.nepa.ng.api.ApiService
import kotlinx.android.synthetic.main.activity_main.*
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
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class OrderConfirmedFragment : AppCompatActivity() {

    lateinit var binding: FragmentOrderConfirmedBinding

    lateinit var navController: NavController

    private var discoId: String? = null
    private var phone: String? = null
    private var units: String? = null
    private var amount: String? = null
    private var meterNumber: String? = null
    private var paymentType: String? = null
    private var txRef: String? = null

    var mApiService: ApiService? = null

    val token = "6kizWRqJlKHPpDtUcbZMNkIIZ7lAB5xK5YFiMqvVienZ1a6E108eCCHXbTzb"

    var tinyDB: TinyDB? = null

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    val sdf = SimpleDateFormat("dd/MM/yyyy")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
         binding = DataBindingUtil.setContentView(this, R.layout.fragment_order_confirmed)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        tinyDB = TinyDB(this)
        initializerRetrofit()

        setSupportActionBar(binding.orderConfirmedToolbar)
        supportActionBar?.title = "Order Confirmed"


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        binding.orderConfirmedToolbar.setNavigationOnClickListener {
            finish()
        }

        val changedDateOne = Date()

        paymentType = tinyDB?.getString("paymentType")
        meterNumber = tinyDB?.getString("meterNumber")
        amount = tinyDB?.getString("amount")
        units = tinyDB?.getString("units")
        phone = tinyDB?.getString("phone")
        discoId =  tinyDB?.getString("discoId")
        txRef = tinyDB?.getString("txRef")
        val tokenUnit = tinyDB?.getString("tokenUnit")

        if (tinyDB!!.getBoolean(TinyDB.Subscribe)){
            binding.subscribeComfirmed.text = getString(R.string.subscription_false_status)
        }else{
            binding.subscribeComfirmed.text = getString(R.string.subscription_true_status)
        }

        subscribeToken()

        val bundles = Bundle()
        bundles.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Successful Electricity Payment")
        bundles.putString(FirebaseAnalytics.Param.ITEM_ID, "$units($meterNumber)")
        bundles.putString(FirebaseAnalytics.Param.ITEM_NAME, meterNumber)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundles)


        binding.date.text = sdf.format(changedDateOne)
        binding.confirmPhone.text = phone
        binding.confirmedAmount.text = amount
        binding.confirmedUnit.text = units
        binding.confirmedMeterType.text = paymentType
        binding.confirmedMeterNumber.text = meterNumber
        binding.tokenUnit.text = tokenUnit


        binding.confirmedDone.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
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
            .baseUrl("https://nepa.ng/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        mApiService = retrofit.create<ApiService>(ApiService::class.java)

    }


    private fun subscribeToken(){

        val call: Call<JsonObject> = mApiService!!.sendToken(txRef!!, amount!!)

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("EMAIL_SENT", "Failed")
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if(!response.isSuccessful){
                    Log.d("EMAIL_SENT", "Unsuccessful: " + response.message() + txRef)

                }

                if(response.isSuccessful){
                    Log.d("EMAIL_SENT", "Successful: " + response.message() + txRef)
                }

            }
        })

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

}
