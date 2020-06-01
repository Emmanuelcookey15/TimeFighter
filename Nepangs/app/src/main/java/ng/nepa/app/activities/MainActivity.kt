package ng.nepa.app.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nepa.app.R
import com.nepa.ng.adapters.AllVendorsAdapter
import com.nepa.ng.api.ApiService
import com.nepa.ng.model.AllVendorTable
import com.nepa.ng.viewmodel.VendorViewmodel
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

class MainActivity : AppCompatActivity() {

    var mApiService: ApiService? = null

    var tinyDB: TinyDB? = null

    lateinit var viewmodel: VendorViewmodel

    val token = "6kizWRqJlKHPpDtUcbZMNkIIZ7lAB5xK5YFiMqvVienZ1a6E108eCCHXbTzb"

    var vendorAdapter: AllVendorsAdapter? = null

    val TAG = "ng.nepa.app"


    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_home)

        viewmodel = ViewModelProvider(this).get(VendorViewmodel::class.java)

        tinyDB = TinyDB(this)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d(TAG, msg)
            })

        val topupHeaderFirstPart = "Top Up Your "
        val topupHeaderSecondPart = "<font color='#1FA7F4'>Nepa </font>"

        top_up_text_header.text = (HtmlCompat.fromHtml(
            "$topupHeaderFirstPart$topupHeaderSecondPart", HtmlCompat.FROM_HTML_MODE_LEGACY))

        val search: AutoCompleteTextView = findViewById(R.id.search_location)

        val lists = ArrayList<String>()
        lists.add("Lagos")
        lists.add("Abuja")
        lists.add("Port Harcourt")
        lists.add("Oyo")
        lists.add("Ogun")
        lists.add("Imo")
        lists.add("Kano")

        val arrayAdapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, lists)
        search.setAdapter(arrayAdapter)
        search.threshold = 1


        if (tinyDB!!.getBoolean(TinyDB.Subscribe)){
            subscribe_main.text = getString(R.string.subscription_false_status)
        }else{
            subscribe_main.text = getString(R.string.subscription_true_status)
        }

        subscribe_main.setOnClickListener {
            tinyDB!!.putBoolean(TinyDB.Subscribe, true)
            subscribeDialog()
        }


        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                vendorAdapter?.filter?.filter(p0.toString())

                focusOnView()

            }
        })

        vendorAdapter = AllVendorsAdapter(this)
        list.adapter = vendorAdapter
        //list.layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)

        list.layoutManager = LinearLayoutManager(this)


        initializerRetrofit()

        subscribeObserver()

        viewmodel.getAllVendors()?.observe(this, Observer {

            vendorAdapter?.setDataForVendors(it as ArrayList<AllVendorTable>)

        })
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

    private fun focusOnView(){
        ns.post { ns.scrollTo(0, search_location.bottom) }
    }

    private fun subscribeObserver() {

        val call: Call<JsonArray> = mApiService!!.getPrepaidPowerVendor("application/json")

        call.enqueue(object : Callback<JsonArray> {
            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.d("DONE", "Failed")
            }

            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {

                if(!response.isSuccessful){
                    Log.d("DONE", "Unsuccessful: " + response.message())

                }

                if(response.isSuccessful){

                    val getting = response.body()
                    val dataSource = getting!![0].asJsonArray

                    for (data in dataSource){
                        val value = data.asJsonObject
                        val vendor = AllVendorTable()
                        vendor.id = value.get("id").asInt
                        vendor.name = value.get("name").asString
                        vendor.logo = value.get("logo").asString
                        vendor.slug = value.get("slug").asString
                        vendor.cities = value.get("cities").asString
                        vendor.disco_name = value.get("disco_name").asString
                        viewmodel.insertVendor(vendor)
                    }

                    Log.d("DONE", "Successful: $dataSource")
                }

            }
        })

    }


    private fun subscribeDialog(){
        MaterialDialog(this).show {
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
