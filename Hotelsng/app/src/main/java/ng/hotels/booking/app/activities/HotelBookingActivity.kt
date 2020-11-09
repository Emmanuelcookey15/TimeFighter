package ng.hotels.booking.app.activities

import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_hotel_booking.*
import ng.hotels.booking.app.R
import ng.hotels.booking.app.adapters.CustomPagerAdapter
import ng.hotels.booking.app.adapters.TabAdapter
import ng.hotels.booking.app.fragments.*
import ng.hotels.booking.app.interfaces.FragmentLifecycle
import ng.hotels.booking.app.interfaces.HotelsngApiService
import ng.hotels.booking.app.utils.PreferenceUtils
import ng.hotels.booking.app.utils.TinyDB
import ng.hotels.booking.app.utils.favouritehotelCheck
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HotelBookingActivity : AppCompatActivity() {

    internal var hotelsngApiService: HotelsngApiService? = null

    lateinit var tinyDB: TinyDB


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_booking)

        tinyDB = TinyDB(this@HotelBookingActivity)

        tinyDB.putListString(TinyDB.TOTALROOMINFO, ArrayList<String>())
        tinyDB.putListString(TinyDB.ROOMINFO, ArrayList<String>())
        tinyDB.putBoolean(TinyDB.BookingSuccess, false)
        tinyDB.putString(TinyDB.BookingResponse, "")

        var roomInfro = ArrayList<String>()
        tinyDB.putListString(TinyDB.ROOMINFO, roomInfro)

        tinyDB.putInt(TinyDB.ROOMPRICE, 0)

        val intent = intent

        val jsonString = intent.getStringExtra("hotel_id")

        val jObj = JSONObject(jsonString!!)

        val propertyName = jObj.getString("property_name")
        hotel_name_list.text = propertyName
        hotel_location_list.text = jObj.get("city_code").toString().replace("\"", "") + ", " + jObj.get("state_code").toString().replace("\"", "")

        var mCustomPagerAdapter = CustomPagerAdapter(this, jObj.getJSONArray("images"), jObj.getInt("num_of_images"))

        val mViewPager = findViewById<ViewPager>(R.id.blog_img);
        mViewPager.adapter = mCustomPagerAdapter
        indicator.setViewPager(mViewPager)

        setSupportActionBar(hotel_booking_toolbar)

        collapsing_toolbar.title = hotel_name_list.text
        collapsing_toolbar.setExpandedTitleTextAppearance(R.style.AppBarExpanded)
        collapsing_toolbar.setCollapsedTitleTextAppearance(R.style.AppBarCollapsed)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val alreadyAdded = favouritehotelCheck(propertyName, "property_name", tinyDB)

        if (alreadyAdded){
            single_page_fav_icon.setImageDrawable(resources.getDrawable(R.drawable.fav_select, applicationContext.theme))
        }

        single_page_fav_icon.setOnClickListener {

            if (tinyDB.getListString(TinyDB.FAVOURITES).size > 0){

                if (!alreadyAdded){
                    Toast.makeText(this, "added", Toast.LENGTH_SHORT).show()
                    val bookingHistory = tinyDB.getListString(TinyDB.FAVOURITES)
                    single_page_fav_icon.setImageDrawable(resources.getDrawable(R.drawable.fav_select, applicationContext.theme))
                    bookingHistory.add(jsonString)
                    tinyDB.putListString(TinyDB.FAVOURITES, bookingHistory)
                }else{
                    removeFromFavourite(propertyName)
                }
            }else{
                Toast.makeText(this, "added", Toast.LENGTH_SHORT).show()
                val bookingHistory = tinyDB.getListString(TinyDB.FAVOURITES)
                single_page_fav_icon.setImageDrawable(resources.getDrawable(R.drawable.fav_select, applicationContext.theme))
                bookingHistory.add(jsonString)
                tinyDB.putListString(TinyDB.FAVOURITES, bookingHistory)
            }

        }

        val rateAsDouble = jObj.get("rating").toString().toDouble()

        val rate = priceWithoutDecimal(rateAsDouble)

        hotel_rating_list.text = rate

        if (rateAsDouble <= 2){
            hotel_excellence_single.text = "Poor"
        }else if (rateAsDouble > 2 && rateAsDouble <= 4 ){
            hotel_excellence_single.text = "Fair"
        }else if(rateAsDouble > 4 && rateAsDouble <= 6 ){
            hotel_excellence_single.text = "Good"
        }else if(rateAsDouble > 6 && rateAsDouble <= 8 ){
            hotel_excellence_single.text = "Very Good"
        }else{
            hotel_excellence_single.text = "Excellent"
        }

        hotel_review_num.text = jObj.get("review_count").toString() + " Reviews"


        var viewPager = findViewById<ViewPager>(R.id.viewPager)
        var tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        var adapterTabing = TabAdapter(supportFragmentManager)
        adapterTabing.addFragment(FeaturesFragment(), "Features")
        adapterTabing.addFragment(RoomFragment(), "Rooms")
        adapterTabing.addFragment(LocationFragment(), "Location")
        adapterTabing.addFragment(GalleryFragment(), "Gallery")
        //adapterTabing.addFragment(ReviewsFragment(), "Reviews")

        viewPager.adapter = adapterTabing
        tabLayout.setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener( object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            var currentPosition = 0

            override fun onPageSelected(p0: Int) {

                val fragmentToShow = adapterTabing.getItem(p0) as FragmentLifecycle
                fragmentToShow.onResumeFragment()

                val fragmentToHide = adapterTabing.getItem(currentPosition) as FragmentLifecycle
                fragmentToHide.onPauseFragment()

                currentPosition = p0

                if (currentPosition == 0 || currentPosition == 2 || currentPosition == 3){
                    btn_select_bookings.visibility = View.VISIBLE
                }else{
                    btn_select_bookings.visibility = View.GONE
                }

            }

        })





        btn_select_bookings.setOnClickListener {

            viewPager.setCurrentItem( 1, true)
            btn_select_bookings.visibility = View.GONE

        }



//        val retrofit = Retrofit.Builder()
//                .baseUrl("https://api.timbu.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//
//        hotelsngApiService = retrofit.create<HotelsngApiService>(HotelsngApiService::class.java)

        //GetSinglePageDataFromendpoint().execute()

    }



    fun removeFromFavourite(propertyName: String){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure, You want to remove from Favourite?")
        alertDialogBuilder.setPositiveButton("yes", DialogInterface.OnClickListener { dialogInterface, i ->
            if (tinyDB.getListString(TinyDB.FAVOURITES).size > 0) {
                for (value in tinyDB.getListString(TinyDB.FAVOURITES)) {
                    val jsonFormat = JSONObject(value)
                    val name = jsonFormat.getString("property_name")
                    if (name == propertyName) {
                        Toast.makeText(this, "Done!!!", Toast.LENGTH_SHORT).show()
                        val listFav = tinyDB.getListString(TinyDB.FAVOURITES)
                        listFav.remove(value)
                        tinyDB.putListString(TinyDB.FAVOURITES, listFav)

                    }
                }
            }
        })

        alertDialogBuilder.setNegativeButton("No",DialogInterface.OnClickListener() {dialogInterface, i ->
            alertDialogBuilder.create().dismiss()
        });

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show();
    }

    private fun priceWithoutDecimal(number: Double): String {
        val number3digits:Double = Math.round(number * 1000.0) / 1000.0
        val number2digits:Double = Math.round(number3digits * 100.0) / 100.0
        val solution:Double = Math.round(number2digits * 10.0) / 10.0
        val result = solution.toString()
        return result
    }

    private fun getHotelsReviews() : JsonObject{

        val pref = PreferenceUtils(this, tinyDB, hotelsngApiService!!)

        val intent = intent


        val jsonString = intent.getStringExtra("hotel_id")

        val jObj = JSONObject(jsonString)
        val id =  jObj.get("id").toString()

        var data = JsonObject()

        var date = Date()

        val c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        date = c.getTime()

        val t = Date()
        val df = SimpleDateFormat("MMM dd yyyy")
        val formattedDate = df.format(t)
        val nextFormatted = df.format(date)



        pref.getToken()

        val call: Call<JsonObject> = hotelsngApiService!!.getAllSinglePageDetails(id, pref.getApiToken()!!, "NGN", formattedDate, nextFormatted, "1", "1", "1")

        call.enqueue(object : Callback<JsonObject>{
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (!response.isSuccessful) {

                    getHotelsReviews()
                    return
                }

                if (response.isSuccessful) {

                    val getting = response.body()

                    data = getting!!.get("data").asJsonObject


                }
            }

        })
        return data
    }





    internal inner class GetSinglePageDataFromendpoint : AsyncTask<Void, Void, Boolean>() {

        var data = JsonObject()

        @RequiresApi(Build.VERSION_CODES.O)
        override fun doInBackground(vararg params: Void?): Boolean {

            data = getHotelsReviews()
            return true
        }


        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            val  argumentFragment = ReviewsFragment();//Get Fragment Instance
            val infro = Bundle();//Use bundle to pass data
            infro.putString("data", data.toString());//put string, int, etc in bundle with a key value
            argumentFragment.arguments = infro;
        }

        override fun onCancelled() {
            super.onCancelled()
        }

    }

    override fun onPause() {
        GetSinglePageDataFromendpoint().cancel(true)
        super.onPause()
    }


    override fun onResume() {
        super.onResume()

//        var roomInfro = ArrayList<String>()
//        tinyDB.putListString(TinyDB.ROOMINFO, roomInfro)

//        tinyDB.putInt(TinyDB.ROOMPRICE, 0)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return (when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        })
    }


}
