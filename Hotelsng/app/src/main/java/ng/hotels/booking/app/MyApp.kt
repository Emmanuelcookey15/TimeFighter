package ng.hotels.booking.app

import android.app.Application
import android.content.Context
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker

class MyApp: Application() {


    private var sAnalytics: GoogleAnalytics? = null
    private var sTracker: Tracker? = null

    override fun onCreate() {
        super.onCreate()
        sAnalytics = GoogleAnalytics.getInstance(this)
    }


    @Synchronized
    fun getDefaultTracker(): Tracker? { // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics?.newTracker(R.xml.global_tracker)
        }
        return sTracker
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}