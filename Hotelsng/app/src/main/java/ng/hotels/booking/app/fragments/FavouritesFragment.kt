package ng.hotels.booking.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_favourites.*
import ng.hotels.booking.app.R
import ng.hotels.booking.app.adapters.FavouritesAdapter
import ng.hotels.booking.app.utils.TinyDB
import java.util.ArrayList

class FavouritesFragment : Fragment() {

    private var adapterFav: FavouritesAdapter? = null
    lateinit var tinyDB: TinyDB

    lateinit var favRv : RecyclerView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.activity_favourites, container, false)

        tinyDB = TinyDB(requireContext())

        favRv = v.findViewById(R.id.rv_fav)

        val llm = LinearLayoutManager(requireContext())

        adapterFav = FavouritesAdapter(requireContext())

        favRv.layoutManager = llm

        favRv.adapter = adapterFav

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val favList = tinyDB.getListString(TinyDB.FAVOURITES)

        adapterFav!!.setData(favList)

        adapterFav!!.notifyDataSetChanged()

        if (favList.size > 0){
            empty_fav.visibility = View.GONE
            rv_fav.visibility = View.VISIBLE
        }

    }
}
