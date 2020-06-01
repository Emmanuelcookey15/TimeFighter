package ng.hotels.booking.app.activities

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_hotels_listing.*
import ng.hotels.booking.app.R
import ng.hotels.booking.app.adapters.SearchHotelsAdapter
import ng.hotels.booking.app.interfaces.HotelsngApiService
import ng.hotels.booking.app.utils.PreferenceUtils
import ng.hotels.booking.app.utils.TinyDB
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HotelsListingActivity : AppCompatActivity() {

    internal var hotelsngApiService: HotelsngApiService? = null

    lateinit var tinyDB: TinyDB
    lateinit var pref: PreferenceUtils
    val cacheSize = (5 * 1024 * 1024).toLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotels_listing)

        setSupportActionBar(find_hotels_listing_toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.title = ""

        tinyDB = TinyDB(this)


        // Spinner element
        val spinner = findViewById<Spinner>(R.id.filter_spinner)


        rl_filter.setOnClickListener {

        }

        // Spinner Drop down elements
        val categories = ArrayList<String>()
        categories.add(tinyDB.getString(TinyDB.SPINNERVALUE))
        categories.add("Luxury Hotels")
        categories.add("Cheap Hotels")
        categories.add("Expensive Hotel")
        categories.add("Most Popular")
        categories.add("All Hotels")

        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        spinner.adapter = dataAdapter

       // text_results_sort.text = tinyDB.getString(TinyDB.SORTED_VALUE)

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

                tinyDB.putString(TinyDB.SPINNERVALUE, "Top picks")
                tinyDB.putString(TinyDB.SORT_BY, "default")
                tinyDB.putString(TinyDB.SORTED_VALUE, "Filter")

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = parent!!.getItemAtPosition(position).toString()

                val selectedText = parent.getChildAt(0) as? TextView
                selectedText?.setTextColor(Color.parseColor("#084482"))
                when (item) {
                    "All Hotels" -> {
                        tinyDB.putString(TinyDB.SORT_BY, "default")
                        tinyDB.putString(TinyDB.SORTED_VALUE, "Filter")
                        tinyDB.putString(TinyDB.SPINNERVALUE, "Top picks")
                        finish()
                        startActivity(getIntent())
                    }
                    "Luxury Hotels" -> {
                        tinyDB.putString(TinyDB.SORT_BY, "price_desc")
                        tinyDB.putString(TinyDB.SORTED_VALUE, "Luxury Hotels")
                        tinyDB.putString(TinyDB.SPINNERVALUE, "Luxury")
                        finish();
                        startActivity(getIntent())

                    }
                    "Cheap Hotels" -> {
                        tinyDB.putString(TinyDB.SORT_BY, "price_asc")
                        tinyDB.putString(TinyDB.SORTED_VALUE, "Cheap Hotels")
                        tinyDB.putString(TinyDB.SPINNERVALUE, "Cheap")
                        finish();
                        startActivity(getIntent());
                    }
                    "Expensive Hotel" -> {
                        tinyDB.putString(TinyDB.SORT_BY, "price_desc")
                        tinyDB.putString(TinyDB.SORTED_VALUE, "Expensive Hotels")
                        tinyDB.putString(TinyDB.SPINNERVALUE, "Expensive")
                        finish();
                        startActivity(getIntent());
                    }
                    "Most Popular" -> {
                        tinyDB.putString(TinyDB.SORT_BY, "popular_first")
                        tinyDB.putString(TinyDB.SORTED_VALUE, "Most Popular")
                        tinyDB.putString(TinyDB.SPINNERVALUE, "Popular")
                        finish();
                        startActivity(getIntent());
                    }
                }
            }

        }


        val getSearchResult = intent.getStringExtra("SearchedResult")

        list.layoutManager = LinearLayoutManager(this)

        val myCache = Cache(this.cacheDir, cacheSize)


        val okHttpClient = OkHttpClient.Builder()
                .cache(myCache)
                .addInterceptor { chain ->
                    var request = chain.request()
                    var net = hasNetwork(this)
                    request = if (net == true) {
                        request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                    } else {
                        request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                    }
                    chain.proceed(request)
                }
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.timbu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        hotelsngApiService = retrofit.create<HotelsngApiService>(HotelsngApiService::class.java)
        pref = PreferenceUtils(this, tinyDB, hotelsngApiService!!)

        text_page_number.text = "Page" + tinyDB.getInt(TinyDB.PAGENUMBER)

        if (tinyDB.getInt(TinyDB.PAGENUMBER) <= 1){
            back_button.isEnabled = false
            back_button.setTextColor(ContextCompat.getColor(this, R.color.textLight))
        }


        back_button.setOnClickListener {
            tinyDB.putInt(TinyDB.PAGENUMBER, tinyDB.getInt(TinyDB.PAGENUMBER) - 1)
            finish()
            startActivity(intent)
        }

        next_button.setOnClickListener {
            tinyDB.putInt(TinyDB.PAGENUMBER, tinyDB.getInt(TinyDB.PAGENUMBER) + 1)
            finish()
            startActivity(intent)
        }


        when (getSearchResult) {
            "near" -> {
                find_hotels_listing_toolbar.title = "Hotels Near You"
                callHotelsNearYouAPI(list, progress_bar_search, tinyDB.getInt(TinyDB.PAGENUMBER))
            }
            "recommended" -> {
                find_hotels_listing_toolbar.title = "Recommended Hotels"
                getHotels(list, progress_bar_search, tinyDB.getInt(TinyDB.PAGENUMBER))
            }
            "search" -> {
                find_hotels_listing_toolbar.title = "Searched Results"
                val propertySearched = intent.getStringExtra("inputedItem")
                getSearch(list, progress_bar_search, tinyDB.getInt(TinyDB.PAGENUMBER), propertySearched)
            }
        }

    }

    private fun hasNetwork(ctx: Context?): Boolean {

        var isConnected: Boolean = false // Initial Value
        val connectivityManager : ConnectivityManager? = ctx?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager?.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }

    private fun callHotelsNearYouAPI(rv: RecyclerView, pb: ProgressBar, page: Int) : JsonArray {

        var data = JsonArray()

        val query = JSONObject()
        val filters = JSONObject()

        try {
            filters.put("per_page", Integer.valueOf(20))
            filters.put("page", Integer.valueOf(page))
            filters.put("agency_uuid", "a965ed6e-c292-4263-b4de-d01057984441")
            filters.put("sort_by", tinyDB.getString(TinyDB.SORT_BY))

            query.put("city", tinyDB.getString(TinyDB.LOCATION))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        pref.getToken()

        val call: Call<JsonObject> = hotelsngApiService!!.getHotelsNearYou(query.toString(),
                pref.getApiToken()!!, true, true,
                filters.toString(),  "property", "hotels")

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                if (t.message != null) {
                    Log.e("TokenFailure", t.message)
                }

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (!response.isSuccessful) {
                    //Toast.makeText(this@HotelsActivity, "Code: " + response.code(), Toast.LENGTH_LONG).show()
                    callHotelsNearYouAPI(rv, pb, page)
                    return
                }


                if (response.isSuccessful) {
                        pb.visibility = View.GONE
                        val getting = response.body()
                        //Toast.makeText(this@HotelsActivity, "Code: " + response.code(), Toast.LENGTH_LONG).show()

                        data = getting!!.get("data").asJsonArray

                        val value = getting.get("meta").asJsonObject.get("total_count")
                        text_results_count.text = value.asString + " results found"
                        if (tinyDB.getInt(TinyDB.PAGENUMBER) >= (value.asInt/20 + 1)){
                            next_button.isEnabled = false
                            next_button.setTextColor(ContextCompat.getColor(this@HotelsListingActivity, R.color.textLight))
                        }
                        val adapterH = SearchHotelsAdapter(data, this@HotelsListingActivity)
                        adapterH.notifyDataSetChanged()
                        rv.adapter = adapterH

                }

            }

        })

        return data
    }


    private fun getHotels(rv: RecyclerView, pb: ProgressBar, page: Int) : JsonArray {


        var data = JsonArray()

        val query = JSONObject()
        val filters = JSONObject()

        try {
            filters.put("per_page", Integer.valueOf(20))
            filters.put("page", Integer.valueOf(page))
            filters.put("agency_uuid", "a965ed6e-c292-4263-b4de-d01057984441")
            filters.put("sort_by", tinyDB.getString(TinyDB.SORT_BY))
            query.put("country", "Nigeria")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        pref.getToken()

        val call: Call<JsonObject> = hotelsngApiService!!.getRecommendedHotels(query.toString(),
                pref.getApiToken()!!, true, true,
                filters.toString(),  "property", "hotels")

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                if (t.message != null) {
                    Log.e("TokenFailure", t.message)
                }
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (!response.isSuccessful) {
                    //Toast.makeText(this@HotelsActivity, "Code: " + response.code(), Toast.LENGTH_LONG).show()
                    getHotels(rv, pb, page)
                    return
                }

                if (response.isSuccessful) {
                        pb.visibility = View.GONE
                        val getting = response.body()

                        data = getting!!.get("data").asJsonArray

                        val value = getting.get("meta").asJsonObject.get("total_count")
                        text_results_count.text = value.asString + " results found"
                        if (tinyDB.getInt(TinyDB.PAGENUMBER) >= (value.asInt/20 + 1)){
                            next_button.isEnabled = false
                            next_button.setTextColor(ContextCompat.getColor(this@HotelsListingActivity, R.color.textLight))
                        }
                        val adapterH = SearchHotelsAdapter(data, this@HotelsListingActivity)
                        adapterH.notifyDataSetChanged()
                        rv.adapter = adapterH
                }
            }
        })
        return data
    }


    private fun getSearch(rv: RecyclerView, pb: ProgressBar, page: Int, propertySearched: String) : JsonArray {

        tinyDB = TinyDB(this)
        pref = PreferenceUtils(this, tinyDB, hotelsngApiService!!)

        var data = JsonArray()

        val query = JSONObject()
        val filters = JSONObject()

        try {
            filters.put("per_page", Integer.valueOf(20))
            filters.put("page", Integer.valueOf(page))
            filters.put("agency_uuid", "a965ed6e-c292-4263-b4de-d01057984441")
            filters.put("sort_by", tinyDB.getString(TinyDB.SORT_BY))
            query.put("property_name", propertySearched)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        pref.getToken()

        val call: Call<JsonObject> = hotelsngApiService!!.getSearchHotels(query.toString(),
                pref.getApiToken()!!, true, true,
                filters.toString(),  "property", "hotels")

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                if (t.message != null) {
                    Log.e("TokenFailure", t.message)
                }
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (!response.isSuccessful) {
                    //Toast.makeText(this@HotelsActivity, "Code: " + response.code(), Toast.LENGTH_LONG).show()
                    getSearch(rv, pb, page, propertySearched)
                    return
                }

                if (response.isSuccessful) {
                    pb.visibility = View.GONE
                    val getting = response.body()


                    data = getting!!.get("data").asJsonArray



                    if (data.size() != 0){
                        var meta = getting.get("meta").asJsonObject
                        val value = meta.get("total_count")
                        text_results_count.text = value.asString + " results found"
                        if (tinyDB.getInt(TinyDB.PAGENUMBER) >= (value.asInt/20 + 1)){
                            next_button.isEnabled = false
                            next_button.setTextColor(ContextCompat.getColor(this@HotelsListingActivity, R.color.textLight))
                        }
                    }else if(data.size() == 0){
                        text_results_count.text = getting.get("message").asString
                    }

                    val adapterH = SearchHotelsAdapter(data, this@HotelsListingActivity)
                    adapterH.notifyDataSetChanged()
                    rv.adapter = adapterH
                }
            }

        })
        return data
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}
