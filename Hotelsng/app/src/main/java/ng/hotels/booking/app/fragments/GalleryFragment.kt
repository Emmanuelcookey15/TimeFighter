package ng.hotels.booking.app.fragments


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_hotel_booking.*
import ng.hotels.booking.app.R
import ng.hotels.booking.app.adapters.GalleryAdapter
import ng.hotels.booking.app.interfaces.FragmentLifecycle
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : Fragment(), FragmentLifecycle {
    override fun onPauseFragment() {

    }

    override fun onResumeFragment() {

    }

    var myDialog : Dialog? = null

    lateinit var mRelativeLayout: RelativeLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)

        val selectRoom = view.findViewById<Button>(R.id.btn_gallery)

        activity!!.btn_select_bookings.visibility = View.VISIBLE

        myDialog =  Dialog(activity!!)

        val vv = R.layout.full_gallery_image
        myDialog!!.setContentView(vv)
        myDialog!!.setCancelable(true)
        myDialog!!.setCanceledOnTouchOutside(true)


        selectRoom.setOnClickListener {
            activity!!.viewPager.setCurrentItem( 1, true)

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent = activity!!.intent

        val jsonString = intent.getStringExtra("hotel_id")

        val jObj = JSONObject(jsonString)
        val pics =  jObj.getJSONArray("images")

        val images = view.findViewById<RecyclerView>(R.id.rv_gallery)

        val coll = calculateNoOfColumns(activity!!, 135f)

        mRelativeLayout = view.findViewById(R.id.rl)

        images.layoutManager = GridLayoutManager(activity!!, 3, LinearLayoutManager.VERTICAL, false)
        images.adapter = GalleryAdapter(activity!!, pics, jObj, myDialog!!, mRelativeLayout)
    }

    fun calculateNoOfColumns(ctx: Context, columnWidthDp: Float): Int { // For example columnWidthdp=180
        val displayMetrics = ctx.getResources().getDisplayMetrics()
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }


}
