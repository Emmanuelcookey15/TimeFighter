package com.capricorn.baxims.ui.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.capricorn.baxims.R
import com.capricorn.baxims.adapter.ProductAdapter
import com.capricorn.baxims.databinding.ActivityDashboardContainerBinding
import com.capricorn.baxims.models.ProductTable
import com.capricorn.baxims.models.UserTable
import com.capricorn.baxims.ui.cart.CartContainer
import com.capricorn.baxims.ui.users.UserContainer
import com.capricorn.baxims.utils.TinyDB
import com.capricorn.baxims.utils.navigateTo
import com.capricorn.baxims.utils.showDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class DashboardContainer : AppCompatActivity() {
    lateinit var binding: ActivityDashboardContainerBinding
    lateinit var viewmodel: DashboardViewModel
    lateinit var smsCountTxt:TextView

    private val ADD_NOTE_REQUEST: Int = 22

    lateinit var sharedPreferences: SharedPreferences

    var tinyDB: TinyDB? = null
    var productAdapter: ProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard_container)
        viewmodel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        tinyDB = TinyDB(this)
        productAdapter = ProductAdapter(this, viewmodel)
        setUpNavigation()
        initView()

        Log.d(
            "tagger",
            "====================number of times container is called=================="
        )
    }

    @SuppressLint("RestrictedApi")
    private fun setUpNavigation() {
        binding.imageView.setOnClickListener { navigateTo<UserContainer> ()}
        binding.fab.hide()
        binding.pager.adapter = PagerAdapter(this)
        TabLayoutMediator(
            binding.tab,
            binding.pager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "Home";tab.setIcon(R.drawable.ic_home);
                    }
                    1 -> {
                        tab.text = "Product";tab.setIcon(R.drawable.ic_products);
                    }
                    2 -> {
                        tab.text = "Transaction";tab.setIcon(R.drawable.ic_transaction)
                    }
                    3 -> {
                        tab.text = "Settings";tab.setIcon(R.drawable.ic_settings)
                    }
                }
            }).attach()
        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.fab.hide();binding.title.text = getString(R.string.Inventory)
                    }
                    1 -> {
                        binding.fab.show();binding.title.text = "Product";
                    }
                    2 -> {
                        binding.fab.hide();binding.title.text = "Transaction"
                    }
                    3 -> {
                        binding.fab.hide();binding.title.text = "Settings"
                    }
                }
            }

        })
        binding.fab.setOnClickListener { navigateTo<AddProduct> { } }
    }

    private fun initView() {
        viewmodel.getUserByID(tinyDB!!.getInt(TinyDB.UserID)).observe(this, Observer<UserTable> {  t ->
            binding.usersname.text = t.first_name
        })
            binding.floatingSearchView.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.cartsearch->{
                    navigateTo<CartContainer> ()}
                R.id.barcodesearch->{
                    val intent = Intent(this, ScannedBarcodeActivity::class.java)
                    startActivityForResult(intent, ADD_NOTE_REQUEST)
                }
            }
        }

        binding.floatingSearchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener{
            override fun onSearchAction(currentQuery: String?) {
                val intentSearched = Intent(this@DashboardContainer, SearchedResultActivity::class.java)
                intentSearched.putExtra("searched_result", currentQuery)
                startActivity(intentSearched)
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }

            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
            }
        })

    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            binding.floatingSearchView.setSearchText(
                data?.getStringExtra("barcode")
            )
        }

    }

}
