package ng.hotels.booking.app.fragments


import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ng.hotels.booking.app.R
import ng.hotels.booking.app.activities.SecureBookingActivity
import ng.hotels.booking.app.adapters.RoomsAdapter
import ng.hotels.booking.app.interfaces.FragmentLifecycle
import ng.hotels.booking.app.interfaces.HotelsngApiService
import ng.hotels.booking.app.utils.PreferenceUtils
import ng.hotels.booking.app.utils.TinyDB
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class RoomFragment : Fragment(), FragmentLifecycle {


    override fun onPauseFragment() {

    }

    override fun onResumeFragment() {

//        totalPrice.text = tinyDB.getInt(TinyDB.ROOMPRICE).toString()

    }


    lateinit var tinyDB: TinyDB

    lateinit var hotelsngApiService: HotelsngApiService

    lateinit var pref: PreferenceUtils

    lateinit var rv: RecyclerView
    lateinit var pbRooms: ProgressBar
    lateinit var totalPrice: TextView

    lateinit var todayCheckIn : TextView

    lateinit var tomorrowCheckOut : TextView


    val sdf = SimpleDateFormat("EEE, MMM dd yyyy")

    lateinit var roomsAdapter: RoomsAdapter

    var jsonString : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tinyDB = TinyDB(activity!!)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_room, container, false)
        totalPrice = v.findViewById<TextView>(R.id.total_room_price)
        roomsAdapter = RoomsAdapter(activity!!, JsonArray(), totalPrice)

        var myCalendar = Calendar.getInstance()

        val intent = activity!!.intent

        jsonString = intent.getStringExtra("hotel_id")

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById<RecyclerView>(R.id.rv_rooms_booking)
        pbRooms = view.findViewById<ProgressBar>(R.id.pb_rooms)
        totalPrice = view.findViewById<TextView>(R.id.total_room_price)
        val btnCheckOut = view.findViewById<Button>(R.id.btn_final_check_out)

        totalPrice.text = tinyDB.getInt(TinyDB.ROOMPRICE).toString()

        btnCheckOut.setOnClickListener {

            val jObj = JSONObject(jsonString)
            val id =  jObj.get("id").toString()
            val hotel_name = jObj.get("property_name").toString()
            val hotel_address = jObj.get("city_code").toString().replace("\"", "") + ", " + jObj.get("state_code").toString().replace("\"", "")
            val numOfImage = jObj.getString("num_of_images")
            val images = jObj.getJSONArray("images").toString()
            val rating = priceWithoutDecimal(jObj.get("rating").toString().toDouble())

            val priceIntent = Intent(activity!!, SecureBookingActivity::class.java)
            priceIntent.putExtra("hotel_Id", id)
            priceIntent.putExtra("hotel_name", hotel_name)
            priceIntent.putExtra("hotel_address", hotel_address)
            priceIntent.putExtra("numOfImage", numOfImage)
            priceIntent.putExtra("images", images)
            priceIntent.putExtra("rating", rating)
            priceIntent.putExtra("hotelPrice", totalPrice.text.toString())
            priceIntent.putExtra("roomsInvoled", tinyDB.getListString(TinyDB.ROOMINFO))
            priceIntent.putExtra("totalRoomData", tinyDB.getListString(TinyDB.TOTALROOMINFO))
            startActivity(priceIntent)

        }


        totalPrice.text = tinyDB.getInt(TinyDB.ROOMPRICE).toString()

//        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(activity!!)


        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.timbu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        hotelsngApiService = retrofit.create<HotelsngApiService>(HotelsngApiService::class.java)

        pref = PreferenceUtils(activity!!, tinyDB, hotelsngApiService)

        GetSinglePageRoomFromendpoint(activity!!).execute()
    }



    private fun priceWithoutDecimal(number: Double): String {
        val number3digits:Double = Math.round(number * 1000.0) / 1000.0
        val number2digits:Double = Math.round(number3digits * 100.0) / 100.0
        val solution:Double = Math.round(number2digits * 10.0) / 10.0
        val result = NumberFormat.getNumberInstance(Locale.US).format(solution)
        //val result = solu.toString()
        return result
    }



    private fun getHotelsReviews(ctx: Context) : JsonObject {


        val jObj = JSONObject(jsonString)
        val id =  jObj.get("id").toString()

        var data = JsonObject()

        var date = Date()

        val c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1)
        date = c.getTime()

        val t = Date()
        val df = SimpleDateFormat("EEE, MMM dd yyyy")
        val formattedDate = df.format(t)
        val nextFormatted = df.format(date)

        pref.getToken()

        val call: Call<JsonObject> = hotelsngApiService.getAllSinglePageDetails(id, pref.getApiToken()!!, "NGN", formattedDate, nextFormatted, "1", "1", "1")

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (!response.isSuccessful) {

                    Log.d("thedata", response.message())

                    getHotelsReviews(ctx)
                    return
                }

                if (response.isSuccessful) {

                    Log.d("thedatas", response.message())

                    val getting = response.body()

                    data = getting!!.get("data").asJsonObject
                    if(roomsAdapter != null) {
                        roomsAdapter = RoomsAdapter(ctx, data.get("rooms").asJsonArray, totalPrice)
                        rv.adapter = roomsAdapter
                    }

                }
            }

        })
        return data
    }


    internal inner class GetSinglePageRoomFromendpoint(ctxx: Context) : AsyncTask<Void, Void, Boolean>() {

        var data = JsonObject()

        var ctx = ctxx

        override fun onPreExecute() {
            super.onPreExecute()
            pbRooms.visibility = View.VISIBLE
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun doInBackground(vararg params: Void?): Boolean {

            data = getHotelsReviews(ctx)
            return true
        }


        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            pbRooms.visibility = View.GONE
        }

        override fun onCancelled() {
            super.onCancelled()
        }

    }


    override fun onPause() {
        GetSinglePageRoomFromendpoint(activity!!).cancel(true)
        super.onPause()
    }

}
