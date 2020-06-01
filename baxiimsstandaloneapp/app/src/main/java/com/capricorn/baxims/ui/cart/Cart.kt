package com.capricorn.baxims.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capricorn.baxims.R
import com.capricorn.baxims.adapter.CartAdapter
import com.capricorn.baxims.databinding.FragmentCartBinding
import com.capricorn.baxims.models.CartTable
import com.capricorn.baxims.models.ProductTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class Cart : Fragment() {
    lateinit var binding: FragmentCartBinding
    lateinit var navController: NavController
    val dataProduct:ArrayList<ProductTable> = arrayListOf()
    val dataCart:ArrayList<CartTable> = arrayListOf()
    var totalamount= arrayListOf<Int>()

    var cartAdapter: CartAdapter? = null

    lateinit var viewmodel: CartViewModel

    lateinit var providerFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_cart, container, false)
        viewmodel = ViewModelProvider(this).get(CartViewModel::class.java)
        navController=findNavController()
        cartAdapter = CartAdapter(activity!!, totalamount, binding.checkout, viewmodel)
        binding.list.adapter = cartAdapter
        binding.list.layoutManager = LinearLayoutManager(activity!!)
        setUpNavigation()
        return binding.root
    }

    private fun setUpNavigation() {
        viewmodel.selectCart()?.observe(viewLifecycleOwner, Observer { list ->

            cartAdapter?.setCartData(list as ArrayList<CartTable>)

        })


        binding.toolbar.setNavigationOnClickListener { activity?.finish()}
        binding.checkout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("totalAmount", totalamount.sum().toString())
            navController.navigate(R.id.action_cart_to_checkOut, bundle)

        }


    }


}
