package ng.hotels.booking.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import ng.hotels.booking.app.fragments.BookingHistoryFragment
import ng.hotels.booking.app.fragments.FavouritesFragment
import ng.hotels.booking.app.fragments.HotelsFragment
import ng.hotels.booking.app.interfaces.HotelsngApiService
import ng.hotels.booking.app.utils.TinyDB
import ng.hotels.telexlibraries.TelexManager

class MainActivity : AppCompatActivity() {


    val TAG = "ng.hotels.booking.app"

    val PHONE_NUMBER = "+234 700 880 8800"
    val CALL_INTENT = 153


    internal var hotelsngApiService: HotelsngApiService? = null


    lateinit var tinyDB: TinyDB


    private lateinit var mFirebaseAnalytics: FirebaseAnalytics


    private var mTracker: Tracker? = null

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tinyDB = TinyDB(this)

        setSupportActionBar(find_hotels_toolbar)


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


        bottom_nav.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.hotels_item -> {
                    supportActionBar?.title = "Hotels"
                    changeFragmentViews(HotelsFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.bookings_item -> {
                    supportActionBar?.title = "Bookings"
                    changeFragmentViews(BookingHistoryFragment())
                    return@OnNavigationItemSelectedListener true
                }


                R.id.fav_item -> {
                    supportActionBar?.title = "Favourites"
                    changeFragmentViews(FavouritesFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.support_item -> {
                    TelexManager(this)
                        .setDevelopersEmail("emmanuelcookey744@gmail.com")
                        .setFrameLayout(R.id.fragment_container)
                        .initializeAsFragment()
                }


            }
            false
        })


        bottom_nav.selectedItemId = R.id.hotels_item

    }




    fun changeFragmentViews(fragment: Fragment)  {

        val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
                .commitNow()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CALL_INTENT && requestCode == RESULT_OK) {
            callSupport()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_find_hotels, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_call -> {
                callSupport()
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }


    private fun callSupport() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$PHONE_NUMBER"))

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), CALL_INTENT)
        } else {
            startActivity(intent)
        }
    }



}
