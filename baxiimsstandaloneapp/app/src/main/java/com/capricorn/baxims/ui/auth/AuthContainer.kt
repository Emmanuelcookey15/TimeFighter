package com.capricorn.baxims.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.capricorn.baxims.R
import com.capricorn.baxims.databinding.ActivityAuthContainerBinding


class AuthContainer : AppCompatActivity() {
    lateinit var binding: ActivityAuthContainerBinding
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_auth_container)
        navController = findNavController(R.id.nav_host_fragment)
    }

}
