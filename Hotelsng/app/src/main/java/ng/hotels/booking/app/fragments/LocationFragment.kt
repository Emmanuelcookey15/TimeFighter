package ng.hotels.booking.app.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_hotel_booking.*
import ng.hotels.booking.app.R
import ng.hotels.booking.app.interfaces.FragmentLifecycle
import org.json.JSONObject
import kotlin.properties.Delegates

/**
 * A simple [Fragment] subclass.
 */
class LocationFragment : Fragment(), OnMapReadyCallback, FragmentLifecycle {
    override fun onPauseFragment() {
    }

    override fun onResumeFragment() {
    }

    lateinit var mMapView: MapView
    private lateinit var mGoogleMap: GoogleMap

    var g by Delegates.notNull<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       g = MapsInitializer.initialize(activity!!)
    }


    override fun onMapReady(p0: GoogleMap?) {

        g
        val intent = activity!!.intent

        val jsonString = intent.getStringExtra("hotel_id")

        val jObj = JSONObject(jsonString)

        var lat = jObj.get("latitude").toString().toDouble()
        var long = jObj.get("longitude").toString().toDouble()

        if (lat.isNaN() || long.isNaN()){
            lat = 0.00000
            long = 0.00000
        }



        mGoogleMap = p0 as GoogleMap
        p0.mapType = GoogleMap.MAP_TYPE_NORMAL

        p0.setMinZoomPreference(12f)
        p0.isIndoorEnabled = true
        val uiSettings = p0.uiSettings
        uiSettings.isIndoorLevelPickerEnabled = true
        uiSettings.isMyLocationButtonEnabled = true
        uiSettings.isMapToolbarEnabled = true
        uiSettings.isCompassEnabled = true
        uiSettings.isZoomControlsEnabled = true
        uiSettings.isZoomGesturesEnabled = true
        uiSettings.setAllGesturesEnabled(true)

        val sydney = LatLng(lat, long)

        p0.addMarker(MarkerOptions().position(sydney)).title = jObj.get("property_name").toString()
        p0.addMarker(MarkerOptions().position(sydney)).hideInfoWindow()
        val Liberty = CameraPosition.builder().target(LatLng(lat, long)).build()

        p0.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_location, container, false)

        var selectRoom = v.findViewById<Button>(R.id.btn_location)


        selectRoom.setOnClickListener {

            activity!!.viewPager.setCurrentItem( 1, true)
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMapView = view.findViewById(R.id.maps)

        if(mMapView != null){
            mMapView.onCreate(null)
            mMapView.onResume()
            mMapView.getMapAsync(this)
        }

    }


}
