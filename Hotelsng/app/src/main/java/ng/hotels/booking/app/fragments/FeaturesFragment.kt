package ng.hotels.booking.app.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.ms.square.android.expandabletextview.ExpandableTextView
import kotlinx.android.synthetic.main.activity_hotel_booking.*
import ng.hotels.booking.app.R
import ng.hotels.booking.app.interfaces.FragmentLifecycle
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class FeaturesFragment : Fragment(), FragmentLifecycle {
    override fun onPauseFragment() {

    }

    override fun onResumeFragment() {

    }

    lateinit var internet: RelativeLayout
    lateinit var pool:  RelativeLayout
    lateinit var bar:  RelativeLayout
    lateinit var gym:  RelativeLayout
    lateinit var restaurant:  RelativeLayout
    lateinit var electricity:  RelativeLayout
    lateinit var security:  RelativeLayout
    lateinit var parking:  RelativeLayout
    lateinit var airCondion:  RelativeLayout
    lateinit var laundry:  RelativeLayout
    lateinit var roomservice:  RelativeLayout
    lateinit var refrigerator:  RelativeLayout
    lateinit var television:  RelativeLayout
    lateinit var telephone:  RelativeLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_features, container, false)

        internet = v.findViewById(R.id.internet_layout)
        pool = v.findViewById(R.id.pool_layout)
        bar = v.findViewById(R.id.bar_layout)
        gym = v.findViewById(R.id.gym_layout)
        restaurant = v.findViewById(R.id.restaurant_layout)
        electricity = v.findViewById(R.id.electricity_layout)
        security = v.findViewById(R.id.security_layout)
        parking = v.findViewById(R.id.parking_layout)
        airCondion = v.findViewById(R.id.air_conditioning_layout)
        laundry = v.findViewById(R.id.laundry_layout)
        roomservice = v.findViewById(R.id.room_service_layout)
        refrigerator = v.findViewById(R.id.refrigerator_layout)
        television = v.findViewById(R.id.television_layout)
        telephone = v.findViewById(R.id.telephone_layout)
        var selectRoom = v.findViewById<Button>(R.id.btn_features)

        val description = v.findViewById<ExpandableTextView>(R.id.single_page_description)

        selectRoom.setOnClickListener {

            activity!!.viewPager.setCurrentItem( 1, true)
        }


        val intent = activity!!.intent

        val jsonString = intent.getStringExtra("hotel_id")

        val jObj = JSONObject(jsonString)

        description.text = removeHTMLTagsTest(jObj.get("description").toString())

        if (jObj.get("description").toString().isEmpty())
            description.text = ("No description for " + jObj.get("property_name").toString()).toString()
        else
            description.text = removeHTMLTagsTest(jObj.get("description").toString())

        val facility = jObj.get("facility_type_names").toString()

        setFacilities(facility.toLowerCase())

        val list = ArrayList<String>()
        val values = ArrayList<Int>()

        return v
    }

    fun removeHTMLTagsTest(string: String): String {
        val str = string.replace("\\<.*?\\>".toRegex(), "")
        val nextString = str.replace("&amp;","&")
        val newStr = nextString.replace("&nbsp;"," ")
        return newStr.replace("&rsquo;s"," ")
    }


    private fun setFacilities(facilities: String) {
        if (facilities.contains("internet") || facilities.contains("wifi")) {
            internet.visibility = View.VISIBLE
        } else {
            internet.visibility = View.GONE
        }

        if (facilities.contains("bar/lounge") || facilities.contains("bar")) {
            bar.visibility = View.VISIBLE
        } else {
            bar.visibility = View.GONE
        }

        if (facilities.contains("gym") || facilities.contains("fitness")) {
            gym.visibility = View.VISIBLE
        } else {
            gym.visibility = View.GONE
        }

        if (facilities.contains("restaurant")) {
            restaurant.visibility = View.VISIBLE
        } else {
            restaurant.visibility = View.GONE
        }

        if (facilities.contains("swimming pool") || facilities.contains("pool")) {
            pool.visibility = View.VISIBLE
        } else {
            pool.visibility = View.GONE
        }

        if (facilities.contains("24 hours electricity") || facilities.contains("electricity") ||
                facilities.contains("power")) {
            electricity.visibility = View.VISIBLE
        } else {
            electricity.visibility = View.GONE
        }


        if (facilities.contains("security")){
            security.visibility = View.VISIBLE
        }else {
            security.visibility = View.GONE
        }

        if (facilities.contains("parking")){
            parking.visibility = View.VISIBLE
        }else {
            parking.visibility = View.GONE
        }

        if (facilities.contains("air conditioning") || facilities.contains("a/c")){
            airCondion.visibility = View.VISIBLE
        }else {
            airCondion.visibility = View.GONE
        }

        if (facilities.contains("laundry")){
            laundry.visibility = View.VISIBLE
        }else {
            laundry.visibility = View.GONE
        }

        if (facilities.contains("room service") || facilities.contains("service")){
            roomservice.visibility = View.VISIBLE
        }else {
            roomservice.visibility = View.GONE
        }

        if (facilities.contains("refrigerator")){
            refrigerator.visibility = View.VISIBLE
        }else {
            refrigerator.visibility = View.GONE
        }

        if (facilities.contains("flatscreen tv") || facilities.contains("dstv") || facilities.contains("television")){
            television.visibility = View.VISIBLE
        }else {
            television.visibility = View.GONE
        }

        if (facilities.contains("telephone")){
            telephone.visibility = View.VISIBLE
        }else {
            telephone.visibility = View.GONE
        }
    }



}
