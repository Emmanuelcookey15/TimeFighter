package com.capricorn.baxims.ui.business

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.capricorn.baxims.R


class BusinessContainer : AppCompatActivity() {

    lateinit var navController:NavController
    lateinit var providerFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_container)
        navController=findNavController(R.id.nav_host_fragment)
    }

    override fun onBackPressed() = when (navController.graph.startDestination) {
        navController.currentDestination?.id -> finishAffinity()
        else -> goUp()
    }


    private fun goUp(){
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
        navController.navigateUp()
    }
}
