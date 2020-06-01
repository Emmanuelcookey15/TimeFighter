package com.capricorn.baxims.ui.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.capricorn.baxims.R
import com.capricorn.baxims.databinding.FragmentProcessPaymentBinding
import com.capricorn.baxims.models.CartTable
import com.capricorn.baxims.ui.dashboard.DashboardContainer
import com.capricorn.baxims.utils.TinyDB
import com.capricorn.baxims.utils.navigateTo


class ProcessPayment : Fragment() {

    lateinit var binding: FragmentProcessPaymentBinding
    lateinit var navController: NavController

    lateinit var viewmodel: CartViewModel

    var newRestockvalue = 0

    var tinyDB: TinyDB? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_process_payment, container, false)
        viewmodel = ViewModelProvider(this).get(CartViewModel::class.java)
        tinyDB = TinyDB(activity!!)
        navController=findNavController()

        viewmodel.selectCart()?.observe(viewLifecycleOwner, Observer { list ->

           for (li in list){
               newRestockvalue = li.quantiy_in_stock!! - li.unit!!
               viewmodel.updateUnit(newRestockvalue, li.skuClient)
           }
            viewmodel.nukeCartTable()

        })

        binding.returnHome.setOnClickListener {
            tinyDB!!.putInt(TinyDB.TotalTransactionPrice, 0)
            activity?.navigateTo<DashboardContainer> ()
        }

        binding.toolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }

        return binding.root
    }

}
