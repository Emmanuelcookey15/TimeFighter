package ng.hotels.booking.app.fragments


import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_hotel_booking.*
import ng.hotels.booking.app.R
import ng.hotels.booking.app.adapters.ReviewAdapter
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

/**
 * A simple [Fragment] subclass.
 */
class ReviewsFragment : Fragment(), FragmentLifecycle {
    override fun onPauseFragment() {
        GetReviewsEndpoint().cancel(true)
    }

    override fun onResumeFragment() {
        Toast.makeText(activity!!, "got it", Toast.LENGTH_LONG).show()
    }

    internal var hotelsngApiService: HotelsngApiService? = null
    lateinit var rv: RecyclerView
    lateinit var pb_reviews: ProgressBar

    fun newInstance(): ReviewsFragment {
        return ReviewsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_reviews, container, false)
        rv = view.findViewById<RecyclerView>(R.id.rv_reviews_page)
        pb_reviews = view.findViewById<ProgressBar>(R.id.progress_bar_reviews)
        var selectRoom = view.findViewById<Button>(R.id.btn_reviews)


        selectRoom.setOnClickListener {
            activity!!.viewPager.setCurrentItem( 1, true)
        }



//        val getArgument = arguments!!.getString("data")
//
//        val jObj = JSONObject(getArgument)
//
//        val reviewsData = jObj.getJSONArray("reviews")
//
//        val adapterReview = ReviewAdapter(activity!!, reviewsData)
//
//        rv.adapter = adapterReview


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById<RecyclerView>(R.id.rv_reviews_page)
        pb_reviews = view.findViewById<ProgressBar>(R.id.progress_bar_reviews)

        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(activity!!)

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.timbu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        hotelsngApiService = retrofit.create<HotelsngApiService>(HotelsngApiService::class.java)

        GetReviewsEndpoint().execute()

    }

    private fun getHotelsReviews(rv:RecyclerView, pb: ProgressBar): JsonArray{

        val tinyDB = TinyDB(activity!!)
        val pref = PreferenceUtils(activity!!, tinyDB, hotelsngApiService!!)

        var data = JsonArray()

        val intent = activity!!.intent

        val jsonString = intent.getStringExtra("hotel_id")

        val jObj = JSONObject(jsonString)
        val id =  jObj.get("id").toString()

        pref.getToken()

        val call: Call<JsonObject> = hotelsngApiService!!.getHotelsReview(id, pref.getApiToken()!!)


        call.enqueue(object : Callback<JsonObject>{
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (!response.isSuccessful) {

                    getHotelsReviews(rv, pb)
                    return
                }

                if (response.isSuccessful) {

                    pb_reviews.visibility = View.GONE
                    val getting = response.body()

                    data = getting!!.get("data").asJsonArray

                    var adapterReview = ReviewAdapter(activity!!, data.asJsonArray)
                    adapterReview.notifyDataSetChanged()
                    rv.adapter = adapterReview

                }
            }

        })

        return data
    }


    internal inner class GetReviewsEndpoint : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            getHotelsReviews(rv, pb_reviews)
            return true
        }

        override fun onCancelled() {
            super.onCancelled()
        }

    }


    override fun onPause() {
        GetReviewsEndpoint().cancel(true)
        super.onPause()
    }

}
