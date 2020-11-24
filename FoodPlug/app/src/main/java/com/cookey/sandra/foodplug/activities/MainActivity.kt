package com.cookey.sandra.foodplug.activities

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.cookey.sandra.foodplug.R
import com.cookey.sandra.foodplug.adapter.TodayMenuAdapter
import com.cookey.sandra.foodplug.adapter.ViewPagerAdapter
import com.cookey.sandra.foodplug.data.FoodItem
import com.cookey.sandra.foodplug.fragments.*
import com.cookey.sandra.foodplug.utils.FirebaseUtils
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var foodItemList: ArrayList<FoodItem>
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        drawer = findViewById(R.id.drawer_layout)

        toggle = ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        foodItemList = arrayListOf()

        foodItemList = FirebaseUtils.getFirebaseFoodDays("Monday", foodItemList)

        val foodAdapter = TodayMenuAdapter(foodItemList, applicationContext)

        rv_menu_list.layoutManager = LinearLayoutManager(this)

        rv_menu_list.adapter = foodAdapter


        viewPager = findViewById<ViewPager2>(R.id.view_pager)
        tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        viewPager.adapter = createCardAdapter()
        TabLayoutMediator(tabLayout, viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = "Tab " + (position + 1)

                tab.text = createCardAdapter().getPageTitle(position)

//                when (position) {
//                    0 -> {
//                        tab.text = "Monday"
//                    }
//                    1 -> {
//                        tab.text = "Tab " + (position + 1)
//                    }
//                    2 -> {
//                        tab.text = "Tab " + (position + 1)
//                    }
//                    3 -> {
//                        tab.text = "Tab " + (position + 1)
//                    }
//                    4 -> {
//                        tab.text = "Tab " + (position + 1)
//                    }
//                }

            }).attach()



    }

    private fun createCardAdapter(): ViewPagerAdapter {
        val foodPagerAdapter = ViewPagerAdapter(this)

        foodPagerAdapter.addFragment(MondayFragment(), "Monday")
        foodPagerAdapter.addFragment(TuesdayFragment(), "Tuesday")
        foodPagerAdapter.addFragment(WednesdayFragment(), "Wednesday")
        foodPagerAdapter.addFragment(ThursdayFragment(), "Thursday")
        foodPagerAdapter.addFragment(FridayFragment(), "Friday")
        return foodPagerAdapter
    }



    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_one -> Toast.makeText(this, "Clicked item one", Toast.LENGTH_SHORT).show()
            R.id.nav_item_two -> Toast.makeText(this, "Clicked item two", Toast.LENGTH_SHORT).show()
            R.id.nav_item_three -> Toast.makeText(this, "Clicked item three", Toast.LENGTH_SHORT).show()
            R.id.nav_item_four -> Toast.makeText(this, "Clicked item four", Toast.LENGTH_SHORT).show()
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


}
