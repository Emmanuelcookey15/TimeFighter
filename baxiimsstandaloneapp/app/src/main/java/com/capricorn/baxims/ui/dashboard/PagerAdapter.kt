package com.capricorn.baxims.ui.dashboard

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount()=4

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                Log.d("tagger","called home")
                Home()

            }
            1->{
                Log.d("tagger","called Product")
                Product()
            }
            2->{
                Log.d("tagger","Transaction")
                Transaction.newInstance()
            }
            3->{
                Settings()
            }

            else -> {Product()}
        }
    }


}