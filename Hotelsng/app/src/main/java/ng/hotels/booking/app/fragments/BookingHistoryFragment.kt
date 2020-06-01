package ng.hotels.booking.app.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_booking_history.*
import ng.hotels.booking.app.R
import ng.hotels.booking.app.adapters.BookingHistoryAdapter
import ng.hotels.booking.app.utils.TinyDB

class BookingHistoryFragment : Fragment() {

    lateinit var tinyDB: TinyDB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.activity_booking_history, container, false)


        tinyDB = TinyDB(requireActivity())



        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bookingList = tinyDB.getListString(TinyDB.BookingsHistory)

        if (bookingList.size > 0){
            empty_booking_history.visibility = View.GONE
            rv_booking_history.visibility = View.VISIBLE

            val bookingHistoryAdapter = BookingHistoryAdapter(bookingList, requireContext())

            rv_booking_history.layoutManager = LinearLayoutManager(requireContext())

            rv_booking_history.adapter = bookingHistoryAdapter
        }


    }



}
