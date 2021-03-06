package com.cookey.sandra.foodplug.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.cookey.sandra.foodplug.R

/**
 * A simple [Fragment] subclass.
 */
class WednesdayFragment : Fragment() {


    private var ARG_COUNT = "param1"
    private var day: String? = null

    fun newInstance(dayValue: String?): MondayFragment? {
        val fragment = MondayFragment()
        val args = Bundle()
        args.putString(ARG_COUNT, dayValue!!)
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            day = it.getString(ARG_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wednesday, container, false)
    }

}
