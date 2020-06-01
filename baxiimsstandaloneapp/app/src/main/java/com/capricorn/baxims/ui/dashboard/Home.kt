package com.capricorn.baxims.ui.dashboard


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.capricorn.baxims.R
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.databinding.FragmentHomeBinding
import com.capricorn.baxims.utils.TinyDB
import com.capricorn.baxims.utils.navigateTo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class Home : Fragment() {
    lateinit var binding:FragmentHomeBinding

    val sdf = SimpleDateFormat("MMM dd yyyy")
    var tinyDB: TinyDB? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false)
        tinyDB = TinyDB(activity!!)
        var currentDate = Date()
        val myCalendar = Calendar.getInstance()
        myCalendar.setTime(currentDate)
        currentDate = myCalendar.time
        binding.textView9.text = sdf.format(currentDate)
        binding.profitMarginSales.text = sdf.format(currentDate)
        binding.restockTime.text = sdf.format(currentDate)
        binding.textView6.text = "₦${tinyDB!!.getInt(TinyDB.OverallTransactionPrice)}"
        binding.inventorysalesAmount.text = "₦${tinyDB!!.getInt(TinyDB.OverallTransactionPrice)}"


        setupNavigations()
        return binding.root
    }

    private fun setupNavigations() {
        binding.addProduct.setOnClickListener { activity?.navigateTo<AddProduct> {  } }
    }

    companion object{
        fun newInstance()= Home()
    }
}
