package ng.hotels.booking.app.fragments

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.fragment_hotels.*
import ng.hotels.booking.app.MyApp

import ng.hotels.booking.app.R
import ng.hotels.booking.app.activities.HotelsListingActivity
import ng.hotels.booking.app.adapters.AllHotelsAdapter
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
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class HotelsFragment : Fragment() {


    val TAG = "ng.hotels.booking.app"

    val PHONE_NUMBER = "+234 700 880 8800"
    val CALL_INTENT = 153

    internal var hotelsngApiService: HotelsngApiService? = null

    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    lateinit var rv: RecyclerView

    lateinit var tinyDB: TinyDB

    lateinit var adapterH: AllHotelsAdapter

    lateinit var alarmManager: AlarmManager

    lateinit var pref: PreferenceUtils

    val cacheSize = (5 * 1024 * 1024).toLong()

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics


    private var mTracker: Tracker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        tinyDB = TinyDB(activity!!)

        adapterH = AllHotelsAdapter(JsonArray(), activity!!)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity!!)

        alarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val myCache = Cache(context!!.cacheDir, cacheSize)
        val okHttpClient = OkHttpClient.Builder()
            .cache(myCache)
            .retryOnConnectionFailure(false)
            .addInterceptor { chain ->
                var request = chain.request()
                var net = hasNetwork(activity)
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
            .client(okHttpClient)
            .build()

        hotelsngApiService = retrofit.create<HotelsngApiService>(HotelsngApiService::class.java)

        pref = PreferenceUtils(activity!!, tinyDB, hotelsngApiService!!)

        val application =  activity!!.application as MyApp
        mTracker = application.getDefaultTracker()
        mTracker!!.send(
            HitBuilders.EventBuilder()
            .setCategory("Action")
            .setAction("Share")
            .build())
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_hotels, container, false)


        mSwipeRefreshLayout = v.findViewById(R.id.container_hotel_act)

        rv = v.findViewById<RecyclerView>(R.id.hotels_reco_act)




        if (tinyDB.getLong(TinyDB.UpdateTheHotelData, 0) <= System.currentTimeMillis()) {
            scheduleTheUpdate(activity!!)
        }

        mSwipeRefreshLayout.setOnRefreshListener {

            mSwipeRefreshLayout.isRefreshing = true

            if (hasNetwork(activity!!)) {
                GetDataFromEndpoint(activity!!).execute()
            }else {
                Toast.makeText(activity!!, "No Network connection", Toast.LENGTH_LONG).show()
                Handler().postDelayed({
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.isRefreshing = false
                        if(hasNetwork(activity)){
                            GetDataFromEndpoint(activity!!).execute()
                        }
                    }
                }, 2000)
            }
        }

        val searchEdit = v.findViewById<androidx.appcompat.widget.SearchView>(R.id.toolbar_hotel_search_new_activity)

        searchEdit.queryHint = "Search for hotels and places"

        searchEdit.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                tinyDB.putInt(TinyDB.PAGENUMBER, 1)
                val listingIntent = Intent(activity, HotelsListingActivity::class.java)
                tinyDB.putString(TinyDB.SPINNERVALUE, "Top picks")
                listingIntent.putExtra("SearchedResult", "search")
                listingIntent.putExtra("inputedItem", query)
                startActivity(listingIntent)

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        tinyDB.putString(TinyDB.SORTED_VALUE, "Filter")

        rv.layoutManager = GridLayoutManager(activity!!, 2, LinearLayoutManager.VERTICAL, false)


        if (hasNetwork(activity!!)){

            if (tinyDB.getString(TinyDB.HOTELS_DATA) != "") {
                tinyDB.getString(TinyDB.HOTELS_DATA)
                val gsonObject = JsonParser.parseString(tinyDB.getString(TinyDB.HOTELS_DATA)).asJsonObject
                adapterH = AllHotelsAdapter(gsonObject.get("data").asJsonArray, activity!!)
                rv.adapter = adapterH
            }else{
                if (tinyDB.getLong(TinyDB.UpdateTheHotelData, 0) <= System.currentTimeMillis()) {
                    mSwipeRefreshLayout.isRefreshing = true
                    GetDataFromEndpoint(activity!!).execute()
                }
            }

        }else{
            if (tinyDB.getString(TinyDB.HOTELS_DATA) != "" ){
                rv.visibility = View.VISIBLE
                tinyDB.getString(TinyDB.HOTELS_DATA)
                val gsonObject = JsonParser.parseString(tinyDB.getString(TinyDB.HOTELS_DATA)).asJsonObject
                adapterH = AllHotelsAdapter(gsonObject.get("data").asJsonArray, activity!!)
                adapterH.notifyDataSetChanged()
                rv.adapter = adapterH
            }else{
                mSwipeRefreshLayout.isRefreshing = true
                Toast.makeText(activity!!, "Please put on network connection to load hotels", Toast.LENGTH_LONG).show()

                Handler().postDelayed({
                    if(mSwipeRefreshLayout.isRefreshing()){
                        mSwipeRefreshLayout.isRefreshing = false
                        if(hasNetwork(activity)){
                            GetDataFromEndpoint(activity!!).execute()
                        }
                    }
                }, 3000)
            }
        }


        return v
    }


    private fun hasNetwork(ctx: Context?): Boolean {
        var isConnected: Boolean = false // Initial Value
        val connectivityManager : ConnectivityManager? = ctx?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager?.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }

    private fun scheduleTheUpdate(ctx: Context){
        val time = 43800 * 1000
        val intent = Intent(ctx, ReceiverHotelUpdate::class.java)
        val pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        see_all_recommended_act.setOnClickListener {
            tinyDB.putInt(TinyDB.PAGENUMBER, 1)
            val listingIntent = Intent(activity!!, HotelsListingActivity::class.java)
            listingIntent.putExtra("SearchedResult", "recommended")
            tinyDB.putString(TinyDB.SPINNERVALUE, "Top picks")
            tinyDB.putString(TinyDB.SORT_BY, "default")
            tinyDB.putString(TinyDB.SORTED_VALUE, "Filter")
            startActivity(listingIntent)
        }

    }




    private fun getHotels(ctx: Context) : JsonArray{
        var data = JsonArray()

        val query = JSONObject()
        val filters = JSONObject()

        try {
            filters.put("per_page", Integer.valueOf(20))
            filters.put("page", Integer.valueOf(1))
            filters.put("agency_uuid", "a965ed6e-c292-4263-b4de-d01057984441")
            filters.put("sort_by", "default")
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
                    getHotels(ctx)

                    return
                }

                if (response.isSuccessful) {
                    val getting = response.body()
                    tinyDB.putString(TinyDB.HOTELS_DATA, getting.toString())
                    Log.d("sucesss", response.message())

                    data = getting!!.get("data").asJsonArray


                    rv.visibility = View.VISIBLE

                    AllHotelsAdapter(data, ctx).notifyDataSetChanged()
                    adapterH = AllHotelsAdapter(data, ctx)
                    adapterH.notifyDataSetChanged()
                    rv.adapter = adapterH


                    Handler().postDelayed({
                        if(mSwipeRefreshLayout.isRefreshing()){
                            mSwipeRefreshLayout.isRefreshing = false
                            tinyDB.putLong(TinyDB.UpdateTheHotelData, System.currentTimeMillis() + 2000)
                        }

                    }, 2000)

                }

            }

        })

        return data
    }

    internal inner class GetDataFromEndpoint(ctxx: Context) : AsyncTask<Void, Void, Boolean>() {

        var ctx = ctxx
        override fun doInBackground(vararg params: Void?): Boolean {

            getHotels(ctx)
            return true
        }
    }



    class ReceiverHotelUpdate: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("DONE_ASSUREDLY", "done: " + Date().toString())
            val tinyDB = TinyDB(context!!)
            tinyDB.putLong(TinyDB.UpdateTheHotelData, 0)
        }

    }



}
