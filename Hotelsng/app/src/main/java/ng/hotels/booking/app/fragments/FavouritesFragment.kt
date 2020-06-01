package ng.hotels.booking.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_favourites.*
import ng.hotels.booking.app.R
import ng.hotels.booking.app.adapters.FavouritesAdapter
import ng.hotels.booking.app.utils.TinyDB

class FavouritesFragment : Fragment() {

    lateinit var tinyDB: TinyDB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.activity_favourites, container, false)

        tinyDB = TinyDB(requireContext())

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var favList = tinyDB.getListString(TinyDB.FAVOURITES)

        if (favList.size > 0){
            empty_fav.visibility = View.GONE
            rv_fav.visibility = View.VISIBLE

            val adapterFav = FavouritesAdapter(favList, requireContext())

            val llm = LinearLayoutManager(requireContext())

            rv_fav.layoutManager = llm
            rv_fav.adapter = adapterFav
        }

    }
}
