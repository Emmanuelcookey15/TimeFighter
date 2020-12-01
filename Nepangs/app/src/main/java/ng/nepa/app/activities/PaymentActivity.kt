package ng.nepa.app.activities

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.flutterwave.raveandroid.RaveConstants
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RavePayManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonObject
import com.nepa.app.BuildConfig
import com.nepa.app.R
import com.nepa.app.databinding.ActivityPaymentBinding
import ng.nepa.app.fragments.electricitypayment.OrderConfirmedFragment
import ng.nepa.app.utils.TinyDB
import com.nepa.ng.api.ApiService
import com.teamapt.monnify.sdk.Monnify
import com.teamapt.monnify.sdk.MonnifyTransactionResponse
import com.teamapt.monnify.sdk.Status
import com.teamapt.monnify.sdk.data.model.TransactionDetails
import com.teamapt.monnify.sdk.rest.data.request.SubAccountDetails
import com.teamapt.monnify.sdk.service.ApplicationMode
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.math.BigDecimal
import java.util.*

class PaymentActivity : AppCompatActivity() {

    private val KEY_RESULT: String = "1000"
    private val INITIATE_PAYMENT_REQUEST_CODE: Int = 1000
    lateinit var binding: ActivityPaymentBinding
    private val READ_CONTACT_REQUEST_CODE = 10

    var tinyDB: TinyDB? = null


    private var discoId: String? = null
    private var phone: String? = null
    private var units: String? = null
    private var amount: String? = null
    private var meterNumber: String? = null
    private var paymentType: String? = null
    private var txRef:String? = null

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics


    var mApiService: ApiService? = null

    val token = "6kizWRqJlKHPpDtUcbZMNkIIZ7lAB5xK5YFiMqvVienZ1a6E108eCCHXbTzb"

    var monnify = Monnify.instance


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        tinyDB = TinyDB(this)

        setSupportActionBar(binding.paymentToolbar)
        supportActionBar?.title = "Payment Order"
        initializerRetrofit()

        paymentType = intent?.getStringExtra("paymentType")
        meterNumber = intent?.getStringExtra("meterNumber")
        amount = intent?.getStringExtra("amount")
        val email = intent?.getStringExtra("email")
        phone = intent?.getStringExtra("phone")
        val userName =  intent?.getStringExtra("userName")
        discoId =  intent?.getStringExtra("discoId")

        txRef = "TT" + UUID.randomUUID().toString().take(14)

        Log.d("TOKEN_TXREF", txRef!!)

        val narration = "payment for electricity bills"

        binding.monnifyPayment.setOnClickListener {

            monnify.setApiKey("MK_PROD_UKSW8QGCZC")
            monnify.setContractCode("441375123278")

            monnify.setApplicationMode(ApplicationMode.LIVE)


            val transaction = TransactionDetails.Builder()
                .amount(BigDecimal(amount))
                .currencyCode("NGN")
                .customerName(userName!!)
                .customerEmail(email!!)
                .paymentReference(txRef!!)
                .paymentDescription("Description of payment")
                .incomeSplitConfig(arrayListOf<SubAccountDetails>())
                .build()


            monnify.initializePayment(
                this@PaymentActivity,
                transaction,
                INITIATE_PAYMENT_REQUEST_CODE,
                KEY_RESULT)

        }



        binding.flutterPayment.setOnClickListener {
            /*
            Create instance of RavePayManager
             */
            RavePayManager(this).setAmount(amount!!.toDouble())
            .setCountry("NG")
            .setCurrency("NGN")
            .setEmail(email)
            .setfName(userName)
            .setlName("")
            .setNarration(narration)
            .setPublicKey(BuildConfig.Rave_PublicKey)
            .setEncryptionKey(BuildConfig.Rave_EncryptionKey)
            .setTxRef(txRef)
            .acceptAccountPayments(false)
            .acceptCardPayments(true)
            .acceptMpesaPayments(false)
            .acceptGHMobileMoneyPayments(false)
            .onStagingEnv(false)
            .allowSaveCardFeature(false)
            .withTheme(R.style.DefaultTheme)
            .initialize()
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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            val message = data.getStringExtra("response")
            when (resultCode) {
                RavePayActivity.RESULT_SUCCESS -> {
                    binding.containerSwipe.isRefreshing = true
                    Toast.makeText(this, "Paid Successfully", Toast.LENGTH_SHORT).show()
                    val bundles = Bundle()
                    bundles.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Electricity Payment Method")
                    bundles.putString(FirebaseAnalytics.Param.ITEM_ID, "Flutterwave")
                    bundles.putString(FirebaseAnalytics.Param.ITEM_NAME, meterNumber)
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundles)
                    flutterWaveResponse()
                }
                RavePayActivity.RESULT_ERROR -> {
                    Toast.makeText(this, "ERROR $message", Toast.LENGTH_LONG).show()
                }
                RavePayActivity.RESULT_CANCELLED -> {
                    Toast.makeText(this, "CANCELLED $message", Toast.LENGTH_SHORT).show()
                }
            }

        }

        if (requestCode == INITIATE_PAYMENT_REQUEST_CODE && data != null){
            val monnifyTransactionResponse = data?.getParcelableExtra(KEY_RESULT) as MonnifyTransactionResponse

            val message = when(monnifyTransactionResponse.status) {
                Status.PENDING -> "Transaction not paid"
                Status.PAID -> "Customer paid exact amount"
                Status.OVERPAID -> "Customer paid more than expected amount."
                Status.PARTIALLY_PAID -> "Customer paid less than expected amount."
                Status.FAILED -> "Transaction completed unsuccessfully. This means no payment came in for Account Transfer method or attempt to charge card failed."
                Status.PAYMENT_GATEWAY_ERROR -> "Payment gateway error"
                else -> ""
            }

            if (message == "Customer paid exact amount") {
                binding.containerSwipe.isRefreshing = true
                val bundles = Bundle()
                bundles.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Electricity Payment Method")
                bundles.putString(FirebaseAnalytics.Param.ITEM_ID, "Monnify")
                bundles.putString(FirebaseAnalytics.Param.ITEM_NAME, meterNumber)
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundles)
                monnifyResponse()
            }

            Toast.makeText(this@PaymentActivity, message, Toast.LENGTH_LONG).show()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun flutterWaveResponse() {


        val call: Call<JsonObject> = mApiService!!.sendToken(txRef!!, amount!!)

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("EMAIL_SENT", "Failed")
                showDialog("Something Went Wrong!",  "Please check your internet connection")
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if(!response.isSuccessful){
                    Log.d("EMAIL_SENT", "Unsuccessful: " + response.message() + txRef)
                    showDialog("Error!",  response.message() + "Please verify and try again")

                }

                if(response.isSuccessful){
                    Log.d("EMAIL_SENT", "Successful: " + response.message() + txRef)


                    tinyDB!!.putString("meterNumber", meterNumber!!)
                    tinyDB!!.putString("paymentType", paymentType!!)
                    tinyDB!!.putString("amount", amount!!)
                    tinyDB!!.putString("units", units!!)
                    tinyDB!!.putString("phone", phone!!)
                    tinyDB!!.putString("discoId", discoId!!)
                    tinyDB!!.putString("txRef", txRef!!)


                    val orderIntent = Intent(this@PaymentActivity, OrderConfirmedFragment::class.java)
                    startActivity(orderIntent)

                    Log.d("ORDER_DONE", "Successful")
                }

            }
        })
    }


    private fun monnifyResponse(){

        val call: Call<JsonObject> = mApiService!!.sendTokenMonnify(txRef!!)

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("EMAIL_SENT", "Failed")
                showDialog("Something Went Wrong!",  "Please check your internet connection")
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if(!response.isSuccessful){
                    Log.d("EMAIL_SENT", "Unsuccessful: " + response.message() + txRef)
                    showDialog("Error!",  response.message() + "Please verify and try again")

                }

                if(response.isSuccessful){
                    Log.d("EMAIL_SENT", "Successful: " + response.message() + txRef)


                    tinyDB!!.putString("meterNumber", meterNumber!!)
                    tinyDB!!.putString("paymentType", paymentType!!)
                    tinyDB!!.putString("amount", amount!!)
                    tinyDB!!.putString("units", units!!)
                    tinyDB!!.putString("phone", phone!!)
                    tinyDB!!.putString("discoId", discoId!!)
                    tinyDB!!.putString("txRef", txRef!!)


                    val orderIntent = Intent(this@PaymentActivity, OrderConfirmedFragment::class.java)
                    startActivity(orderIntent)

                    Log.d("ORDER_DONE", "Successful")
                }

            }
        })

    }



    private fun showDialog(title:String, message:String){
        MaterialDialog(this).show {
            cornerRadius(5F)
            title(text = title)
            message(text = message)

            positiveButton(R.string.agree) {
                hide()
            }

            negativeButton {  }

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_CONTACT_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }

        }

    }




}
