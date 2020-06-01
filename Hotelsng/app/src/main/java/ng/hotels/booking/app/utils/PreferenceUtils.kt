package ng.hotels.booking.app.utils

import android.content.Context
import android.util.Log
import ng.hotels.booking.app.BuildConfig
import ng.hotels.booking.app.interfaces.HotelsngApiService
import ng.hotels.booking.app.models.Token
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit

class PreferenceUtils(private val ctx: Context, private var tinydb : TinyDB, var hotelsngApiService: HotelsngApiService) {


    val GRANT_TYPE =  BuildConfig.GRANT_TYPE
    val CLIENT_ID = BuildConfig.CLIENT_ID
    val CLIENT_SECRET = BuildConfig.CLIENT_SECRET
    val SCOPE = BuildConfig.SCOPE

    val publicKey = BuildConfig.Rave_PublicKey

    val encryptionKey = BuildConfig.Rave_EncryptionKey



    val city: String?
        get() = tinydb!!.getString(TinyDB.LOCATION)

    val lastTimeRecommendedHotelsWasStored: Long
        get() = tinydb!!.getLong(TinyDB.RECOMMENDED_HOTELS_DATA_WAS_STORED, 0)

    val lastTimeHotelsNearYouWasStored: Long
        get() = tinydb!!.getLong(TinyDB.HOTELS_NEAR_YOU_DATA_WAS_STORED, 0)


    val isThisUserFirstTime: Boolean

        get() = tinydb!!.getBoolean(TinyDB.USER_FIRST_TIME)

    val isThisUserFirstTimeForBooking: Boolean
        get() = tinydb!!.getBoolean(TinyDB.USER_FIRST_TIME_BOOKING)



    fun getToken() {
        val tinydb = TinyDB(ctx)


        if (tokenIsExpired(ctx, tinydb)) {
            Log.e("TAG Emem", "Token has expired fetch again ")
            //TokenServiceUtils.startTokenService(context); // removed calls to token service
            // fetch token again this code may never be called just a precaution
            fetchTokenFromServer()
        } else {
            //+
            if (getApiToken() == null) {
                fetchTokenFromServer()
            } else {

            }
        }

    }

    private fun fetchTokenFromServer() {
        val tinydb = TinyDB(ctx)
        hotelsngApiService.getToken(GRANT_TYPE, CLIENT_ID, CLIENT_SECRET, SCOPE).enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (response.isSuccessful()) {

                    Log.d("well", response.message().toString())
                    if (response.raw().cacheResponse != null) {
                        onFailure(call, Throwable("Token from cache"))
                    } else {
                        storeAPIToken(response.body()!!.token)
                    }

                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                /*new PreferenceUtils(context)
                        .storeAPIToken(null, 0 );*/
                if (t.message != null) {
                    Log.e("TokenFailure", t.message)
                }
            }
        })

    }


    private fun storeAPIToken(token: String?) {

        tinydb.putString(TinyDB.API_TOKEN, token!!)

        tinydb.putLong(TinyDB.EXPIRY_TIME, Calendar.getInstance().timeInMillis)
    }

    fun getApiToken(): String? {
        return tinydb.getString(TinyDB.API_TOKEN)
    }

    private fun tokenIsExpired(context: Context, tinydb : TinyDB): Boolean {
        val lastFetched = tinydb.getLong(TinyDB.EXPIRY_TIME, 0)
        val timeout = TimeUnit.MINUTES.toMillis(2) + lastFetched
        Log.e("TAG", ("" + lastFetched + "---" + timeout + "---" + Calendar.getInstance().timeInMillis))
        return Calendar.getInstance().timeInMillis > timeout
    }


    fun storeCity(city: String) {
        tinydb!!.putString(TinyDB.LOCATION, city)
    }

    fun storeTimeRecommendedHotelsDataIsStored() {
        tinydb!!.putLong(TinyDB.RECOMMENDED_HOTELS_DATA_WAS_STORED, Calendar.getInstance().timeInMillis)
    }

    fun storeTimeHotelsNearYouDataIsStored() {
        tinydb!!.putLong(TinyDB.HOTELS_NEAR_YOU_DATA_WAS_STORED, Calendar.getInstance().timeInMillis)
    }

    fun storeUserFirstTime() {
        tinydb!!.putBoolean(TinyDB.USER_FIRST_TIME, false)
    }

    fun storeUserFirstTimeBooking() {
        tinydb!!.putBoolean(TinyDB.USER_FIRST_TIME_BOOKING, false)
    }


    fun saveLocationStateUnstable(context: Context, state: Boolean) {
        tinydb!!.putBoolean("location_state", state)
    }

    //get User data from SharedPreference
    fun isLocationStateUnstable(context: Context): Boolean {
        return tinydb!!.getBoolean("location_state")
    }

    fun saveOnHomeActivityOnce(context: Context, state: Boolean) {
        tinydb!!.putBoolean("home", state)
    }

    //get User data from SharedPreference
    fun isInHomeActivityOnce(context: Context): Boolean {
        return tinydb!!.getBoolean("home")
    }

    fun ReadLocation(context: Context): String {
        val locationKey = "ng.hotels.app.location"
        val pref = context.getSharedPreferences(locationKey, Context.MODE_PRIVATE)
        val location = pref.getString("location", "")//AppController.getInstance()
        Log.e("Location", "From SharedPreferences " + location!!)
        return if (location === "")
            "lagos"
        else
            location
    }



}