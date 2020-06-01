package com.capricorn.baxims.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.capricorn.baxims.R
import com.capricorn.baxims.databinding.FragmentAuthSuccessfullBinding
import com.capricorn.baxims.databinding.FragmentCartBinding
import com.capricorn.baxims.ui.dashboard.DashboardContainer
import com.capricorn.baxims.utils.navigateTo


class AuthSuccessfull : Fragment() {
    lateinit var binding: FragmentAuthSuccessfullBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_auth_successfull, container, false)
        setUpNavigation()
        return binding.root
    }

    private fun setUpNavigation() {
        binding.goToLogin.setOnClickListener {
            activity?.navigateTo<AuthContainer> {  }
        }
    }

}
