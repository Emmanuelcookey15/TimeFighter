package ng.hotels.booking.app.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.flutterwave.raveandroid.RaveConstants
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RavePayManager
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_secure_booking.*
import ng.hotels.booking.app.BuildConfig
import ng.hotels.booking.app.MyApp
import ng.hotels.booking.app.R
import ng.hotels.booking.app.interfaces.HotelsngApiService
import ng.hotels.booking.app.models.Bookings
import ng.hotels.booking.app.utils.PreferenceUtils
import ng.hotels.booking.app.utils.TinyDB
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SecureBookingActivity : AppCompatActivity() {


    private val READ_CONTACT_REQUEST_CODE = 10
    lateinit var tinyDB: TinyDB

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    lateinit var hotelsngApiService: HotelsngApiService

    lateinit var pref: PreferenceUtils

    var builder: AlertDialog.Builder? = null

    lateinit var todayDate : Date
    lateinit var tomorrowDate : Date
    var changedDateOne: Date = Date()
    var changedDateTwo: Date = Date()

    var theBooking = Bookings()

    var propertyId : String? = null
    var hotel_name: String? = null
    var address_hotel: String? = null
    var images_room: String? = null
    var images_room_num: String? = null
    var rating: String? = null

    var they : ArrayList<HashMap<String, String>>? = null

    var diff: Long = 0

    val sdf = SimpleDateFormat("EEE, MMM dd, yyyy")

    var bookingsIntent : Intent? = null

    private var mTracker: Tracker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secure_booking)


        val application =  application as MyApp
        mTracker = application.getDefaultTracker()
        mTracker!!.send(
            HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build())

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        setSupportActionBar(secure_hotels_bookings_toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.title = "Secure Booking"

        tinyDB = TinyDB(this)

        edit_booking_details.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val price = ((intent.getStringExtra("hotelPrice")).replace(",", "")).toInt()

        propertyId = intent.getStringExtra("hotel_Id")

        hotel_name = intent.getStringExtra("hotel_name")

        address_hotel = intent.getStringExtra("hotel_address")

        images_room = intent.getStringExtra("images")

        images_room_num = intent.getStringExtra("numOfImage")

        rating = intent.getStringExtra("rating")

        val roomInvolved = intent.getStringArrayListExtra("roomsInvoled")


        val roomsBooked = intent.getStringArrayListExtra("totalRoomData")
        they = totalRoomFilter(roomsBooked)

        bookings_hotel_name.text = hotel_name

        bookings_hotel_room_name.text =  address_hotel


        val titles = ArrayList<String>()
        titles.add("Mr.")
        titles.add("Mrs.")
        titles.add("Miss.")
        titles.add("Chief.")
        titles.add("Dr.")
        titles.add("Engr.")
        titles.add("Dr.Mrs.")
        titles.add("HRH.")
        titles.add("Senator.")
        titles.add("Arch.")
        titles.add("Barr.")
        titles.add("Prof.")
        titles.add("Pastor")
        titles.add("Rev.")
        titles.add("Hon.")
        titles.add("Amb.")

        val titleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, titles)

        titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        user_booking_title_name.adapter = titleAdapter

        var myCalendar = Calendar.getInstance()

        changedDateOne = Date()

        myCalendar.setTime(changedDateOne)

        changedDateOne = myCalendar.time
        myCalendar.add(Calendar.DATE, 1)
        changedDateTwo = myCalendar.time


        main_check_in_date.text = sdf.format(changedDateOne)
        main_check_out_date.text = sdf.format(changedDateTwo)

        room_check_in_date.setOnClickListener {

            DatePickerDialog(this, datePickerTextView(main_check_in_date, myCalendar), myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show()


        }

        room_check_out_date.setOnClickListener {

            DatePickerDialog(this, datePickerTextView(main_check_out_date, myCalendar), myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show()

        }

        diff = (changedDateTwo.time - changedDateOne.time)/(24 * 60 * 60 * 1000)

        val solu = NumberFormat.getNumberInstance(Locale.US).format((diff * price))
        val result = solu.toString()

        sum_total_bookings.text = "₦ $result"

        val rooms = ff(roomInvolved)

        room_and_night_details.text = rooms


        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.timbu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        hotelsngApiService = retrofit.create<HotelsngApiService>(HotelsngApiService::class.java)

        pref = PreferenceUtils(this, tinyDB, hotelsngApiService)

        booking_reservation_by_who_check.setOnClickListener {

            if (booking_reservation_by_who_check.isChecked){
                bookings_on_behalf.visibility = View.VISIBLE

            }else{
                bookings_on_behalf.visibility = View.GONE
            }

        }

        builder = AlertDialog.Builder(this);

        btn_pay_now.setOnClickListener {

            when {
                TextUtils.isEmpty(user_booking_first_name.text) -> {
                    user_booking_first_name.error = "First name is required!"

                }
                TextUtils.isEmpty(user_booking_last_name.text) -> {
                    user_booking_last_name.error = "Last name is required!"

                }
                TextUtils.isEmpty(user_booking_email.text) -> {
                    user_booking_email.error = "email is required!\n\nBooking ID will be sent to this email"
                }
                TextUtils.isEmpty(user_booking_phone_num.text) -> {
                    user_booking_email.error = "Phone number is required!\n\nBooking ID will be sent to this email"
                }
                ((diff * price).toDouble()) <= 0 -> {
                    Toast.makeText(this, "No Room was booked please select room", Toast.LENGTH_LONG).show()
                }
                else -> {

                    val txRef = user_booking_email.text.toString() + " " + UUID.randomUUID().toString()

                    val narration = "payment for bookings at $hotel_name"
                    /*
                    Create instance of RavePayManager
                     */RavePayManager(this).setAmount((diff * price).toDouble())
                            .setCountry("NG")
                            .setCurrency("NGN")
                            .setEmail(user_booking_email.text.toString())
                            .setfName(user_booking_first_name.text.toString())
                            .setlName(user_booking_last_name.text.toString())
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

        }




        btn_pay_at_hotel.setOnClickListener {

            Toast.makeText(this, "Your booking is being processed. Please wait...", Toast.LENGTH_SHORT).show()

            when {
                TextUtils.isEmpty(user_booking_first_name.text) -> {
                    user_booking_first_name.error = "First name is required!"

                }
                TextUtils.isEmpty(user_booking_last_name.text) -> {
                    user_booking_last_name.error = "Last name is required!"

                }
                TextUtils.isEmpty(user_booking_email.text) -> {
                    user_booking_email.error = "email is required!\n\nBooking ID will be sent to this email"
                }
                TextUtils.isEmpty(user_booking_phone_num.text) -> {
                    user_booking_email.error = "Phone number is required!\n\nBooking ID will be sent to this email"
                }
                ((diff * price).toDouble()) <= 0 -> {
                    Toast.makeText(this, "No Room was booked please select room", Toast.LENGTH_LONG).show()
                }
                else -> {
                    theBooking.country = "157"
                    theBooking.currency_code = "NGN"
                    theBooking.title = user_booking_title_name.selectedItem.toString()
                    theBooking.fname = user_booking_first_name.text.toString()
                    theBooking.lname = user_booking_last_name.text.toString()
                    theBooking.email = user_booking_email.text.toString()
                    theBooking.phone = user_booking_phone_num.text.toString()
                    theBooking.address = "n/a"
                    val df = SimpleDateFormat("yyyy-MM-dd")
                    theBooking.checkin = df.format(changedDateOne)
                    theBooking.checkout = df.format(changedDateTwo)
                    theBooking.ptype = "hotel"
                    theBooking.property = propertyId
                    if (booking_additional_comment.text.toString().isEmpty()) {
                        theBooking.additional_info = ""
                    }else{
                        theBooking.additional_info = booking_additional_comment.text.toString()
                    }
                    theBooking.payment_type = "post-payment"
                    theBooking.agent = ""
                    theBooking.provider = "HNG App"
                    theBooking.rooms = they

                    if (booking_reservation_by_who_check.isChecked){

                        theBooking.on_behalf_of_fname = user_booking_onbehalf_first_name.text.toString()
                        theBooking.on_behalf_of_lname = user_booking_onbehalf_last_name.text.toString()
                        theBooking.on_behalf_of_email = user_booking_onbehalf_email.text.toString()
                        theBooking.on_behalf_of_phone = user_booking_onbehalf_phone_num.text.toString()

                    }

                    btn_pay_at_hotel.loadState(0, "Please Wait...")
                    BookRoom().execute()

                    bookingsIntent = Intent(this, YourBookingsActivity::class.java)

                    val dftwo = SimpleDateFormat("dd, MMM yyyy")
                    bookingsIntent?.putExtra("hotel_Id", propertyId)
                    bookingsIntent?.putExtra("hotel_name", hotel_name)
                    bookingsIntent?.putExtra("hotel_address", address_hotel)
                    bookingsIntent?.putExtra("numOfImage", images_room_num)
                    bookingsIntent?.putExtra("images", images_room)
                    bookingsIntent?.putExtra("rating", rating)
                    bookingsIntent?.putExtra("checkin", dftwo.format(changedDateOne))
                    bookingsIntent?.putExtra("checkout", dftwo.format(changedDateTwo))

                }
            }

        }


    }


    private fun datePickerTextView(tv: TextView, myCalendar: Calendar): DatePickerDialog.OnDateSetListener{
        val date = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val price = ((intent.getStringExtra("hotelPrice")).replace(",", "")).toInt()

                var changedDate = myCalendar.time

                tv.text = sdf.format(changedDate)

                try {
                    changedDateOne = sdf.parse(main_check_in_date.text.toString())

                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                try {
                    changedDateTwo = sdf.parse(main_check_out_date.text.toString())

                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                diff = (changedDateTwo.time - changedDateOne.time)/(24 * 60 * 60 * 1000)

                val solu = NumberFormat.getNumberInstance(Locale.US).format((diff * price))
                val result = solu.toString()

                sum_total_bookings.text = "₦ $result"
            }

        }

        return date
    }


    fun ff(numbers: ArrayList<String>): String{

        var roomsBooked = StringBuilder()
        val map = HashMap<String, Int>()
        for (key in numbers) {
            if (map.containsKey(key)) {
                var occurrence = map.get(key)!!
                occurrence++
                map.put(key, occurrence)
            } else {
                map.put(key, 1)
            }
        }

        for (key in map.keys) {
            val occurrence = map.get(key)!!
            var value = key + " " + occurrence + "\n"
            roomsBooked.append(value)
        }

        return roomsBooked.toString()
    }

    fun totalRoomFilter(roomData: ArrayList<String>): ArrayList<HashMap<String, String>>{

        var allRooms = ArrayList<HashMap<String, String>>()


        val df = SimpleDateFormat("yyyy-MM-dd")
        val date = df.format(changedDateOne)
//        room_id
        for(item in roomData){
            var rooms = HashMap<String, String>()
            val room = JSONObject()
            val jObj = JSONObject(item)
            val hh = jObj.getJSONObject("availability").getJSONObject("rates")
            var count = 0
            hh.keys().forEach{
                if (count == 0) {
                    rooms.put("room_id", jObj.get("room_id").toString())
                    rooms.put("rate_id",(hh.getJSONObject(it).get("id").toString()))
                    rooms.put("s_rate", hh.getJSONObject(it).get("selling_price").toString())
                    //rooms.add(room)
                    count++
                }
            }
            allRooms.add(rooms)
        }

        Log.d("drt", allRooms.toString())
        return allRooms
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


    fun bookHotels(){

        pref.getToken()

        theBooking.access_token = pref.getApiToken()

        val call: Call<JsonObject> = hotelsngApiService.bookingHotel(theBooking)


        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("bookingData", t.message)
                btn_pay_at_hotel.loadState(1, "Pay at hotel")
                Toast.makeText(this@SecureBookingActivity,
                        "Your bookings failed. Please check your network connection or try again later.", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (!response.isSuccessful){
                    Log.d("bookingData", response.message() + " : " + response.code().toString())
                    tinyDB.putBoolean(TinyDB.BookingSuccess, false)
                    btn_pay_at_hotel.loadState(1, "Pay at hotel")
                    Toast.makeText(this@SecureBookingActivity,
                            "Your booking failed. Please check your network connection or try again later.", Toast.LENGTH_LONG).show()
                }



                if (response.isSuccessful) {
                    Log.d("bookingData", response.body().toString())

                    btn_pay_at_hotel.loadState(1, "Pay at hotel")
                    tinyDB.putString(TinyDB.BookingResponse, response.body().toString())
                    tinyDB.putBoolean(TinyDB.BookingSuccess, true)
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Bookings")
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Bookings: Pay At Hotels")
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

                    bookingsIntent?.putExtra("bookings", tinyDB.getString(TinyDB.BookingResponse))
                    startActivity(bookingsIntent)

                }

            }
        })

    }



    internal inner class BookRoom() : AsyncTask<Void, Void, Boolean>(){
        override fun doInBackground(vararg params: Void?): Boolean {
            bookHotels()
            return true
        }


    }

    override fun onResume() {
        super.onResume()

        val name = "SecureBookingActivity"
        Log.i("WHICH_FRAGMENT", "Setting screen name: $name");
        mTracker!!.setScreenName("Image~$name");
        mTracker!!.send(HitBuilders.ScreenViewBuilder().build())

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            val message = data.getStringExtra("response")
            when (resultCode) {
                RavePayActivity.RESULT_SUCCESS -> {
                    Toast.makeText(this, "SUCCESS $message", Toast.LENGTH_SHORT).show()
                    theBooking.country = "157"
                    theBooking.currency_code = "NGN"
                    theBooking.title = user_booking_title_name.selectedItem.toString()
                    theBooking.fname = user_booking_first_name.text.toString()
                    theBooking.lname = user_booking_last_name.text.toString()
                    theBooking.email = user_booking_email.text.toString()
                    theBooking.phone = user_booking_phone_num.text.toString()
                    theBooking.address = "n/a"
                    val df = SimpleDateFormat("yyyy-MM-dd")
                    theBooking.checkin = df.format(changedDateOne)
                    theBooking.checkout = df.format(changedDateTwo)
                    theBooking.ptype = "hotel"
                    theBooking.property = propertyId
                    if (booking_additional_comment.text.toString().isEmpty()) {
                        theBooking.additional_info = ""
                    }else{
                        theBooking.additional_info = booking_additional_comment.text.toString()
                    }
                    theBooking.payment_type = "pre-payment"
                    theBooking.agent = ""
                    theBooking.provider = "hotelsng"
                    theBooking.rooms = they

                    if (booking_reservation_by_who_check.isChecked){

                        theBooking.on_behalf_of_fname = user_booking_onbehalf_first_name.text.toString()
                        theBooking.on_behalf_of_lname = user_booking_onbehalf_last_name.text.toString()
                        theBooking.on_behalf_of_email = user_booking_onbehalf_email.text.toString()
                        theBooking.on_behalf_of_phone = user_booking_onbehalf_phone_num.text.toString()

                    }

                    BookRoom().execute()

                    bookingsIntent = Intent(this, YourBookingsActivity::class.java)

                    val dftwo = SimpleDateFormat("dd, MMM yyyy")
                    bookingsIntent?.putExtra("hotel_Id", propertyId)
                    bookingsIntent?.putExtra("hotel_name", hotel_name)
                    bookingsIntent?.putExtra("hotel_address", address_hotel)
                    bookingsIntent?.putExtra("numOfImage", images_room_num)
                    bookingsIntent?.putExtra("images", images_room)
                    bookingsIntent?.putExtra("rating", rating)
                    bookingsIntent?.putExtra("checkin", dftwo.format(changedDateOne))
                    bookingsIntent?.putExtra("checkout", dftwo.format(changedDateTwo))
                }
                RavePayActivity.RESULT_ERROR -> {
                    Toast.makeText(this, "ERROR $message", Toast.LENGTH_SHORT).show()
                }
                RavePayActivity.RESULT_CANCELLED -> {
                    Toast.makeText(this, "CANCELLED $message", Toast.LENGTH_SHORT).show()
                }
            }
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
