package com.capricorn.baxims.ui.cart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.capricorn.baxims.R
import com.capricorn.baxims.databinding.ActivityCartContainerBinding

class CartContainer : AppCompatActivity() {
    lateinit var navController:NavController
    lateinit var binding:ActivityCartContainerBinding
    lateinit var viewmodel: CartViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_cart_container)
        viewmodel = ViewModelProvider(this).get(CartViewModel::class.java)
        navController=findNavController(R.id.nav_host_fragment)
    }

    override fun onBackPressed() = when (navController.graph.startDestination) {
        navController.currentDestination?.id -> finish()
        else -> goUp()
    }

    private fun goUp(){
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
        navController.navigateUp()
    }
}
