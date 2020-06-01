package com.capricorn.baxims.ui.dashboard


import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog

import com.capricorn.baxims.R
import com.capricorn.baxims.databinding.FragmentSettingsBinding
import com.capricorn.baxims.ui.auth.AuthContainer
import com.capricorn.baxims.ui.users.UserContainer
import com.capricorn.baxims.utils.navigateTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Settings : Fragment() {
    lateinit var binding:FragmentSettingsBinding

    lateinit var providerFactory: ViewModelProvider.Factory

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_settings, container, false)
        setupNavigation()
        return binding.root
    }

    private fun  setupNavigation(){
        binding.profile.setOnClickListener { activity?.navigateTo<UserContainer> {  } }
        binding.apply {
            logout.setOnClickListener {
                MaterialDialog(context!!).show {
                    cornerRadius(5F)
                    title(text = "Confirm Logout")
                    message(text = "Are you sure you want to logout")

                    positiveButton(R.string.agree) { dialog ->
                        CoroutineScope(IO).launch{
//                            viewmodels.nukeProductTable
//                            sharedPreferences.all.clear()
                            withContext(Main){
                                activity?.navigateTo<AuthContainer> {  }
                            }
                        }
                    }
                    negativeButton { hide() }
                }
            }

        }
    }

    companion object{
        fun newInstance()= Settings()
    }
}
