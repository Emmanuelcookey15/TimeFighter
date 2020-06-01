package com.capricorn.baxims.ui.auth


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.recyclical.ViewHolder
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.itemdefinition.onChildViewClick
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.capricorn.baxims.R
import com.capricorn.baxims.adapter.ProductCategoryAdapter
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.api.business.CategoryRequest
import com.capricorn.baxims.databinding.FragmentSignupCategoryBinding
import com.capricorn.baxims.models.ProductCategoryTable
import com.capricorn.baxims.ui.business.BusinessContainer
import com.capricorn.baxims.ui.business.Outlet
import com.capricorn.baxims.ui.dashboard.DashboardContainer
import com.capricorn.baxims.ui.dashboard.DashboardViewModel
import com.capricorn.baxims.utils.TinyDB
import com.capricorn.baxims.utils.navigateTo
import com.capricorn.baxims.utils.showDialog
import com.google.android.material.card.MaterialCardView
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SignupCategory : AppCompatActivity() {
    lateinit var binding:FragmentSignupCategoryBinding
    lateinit var navController: NavController


    var productCategoryAdapter: ProductCategoryAdapter? = null
    var productCategoryList = ArrayList<String>()

    var mApiService: ApiService? = null
    lateinit var viewmodel: DashboardViewModel
    var productCategoryTableList = ArrayList<ProductCategoryTable>()

    var tinyDB: TinyDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.fragment_signup_category)
        viewmodel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        tinyDB = TinyDB(this)
        productCategoryAdapter = ProductCategoryAdapter(productCategoryList, productCategoryTableList)
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = productCategoryAdapter
        initializerRetrofit()
        subScribeObserver()
        viewmodel.getProductCategorys()?.observe(this, Observer {
            productCategoryAdapter?.setProductCategoryData(it as ArrayList<ProductCategoryTable>)
        })
        setupNavigation()
    }




    private fun initializerRetrofit(){

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.baxiims.com.ng/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mApiService = retrofit.create<ApiService>(ApiService::class.java)


    }


    private fun subScribeObserver(){
        val call: Call<JsonObject> = mApiService!!.getProductCategory(tinyDB!!.getInt(TinyDB.Business_ID))

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (!response.isSuccessful){
                    Log.d("PRODUCTS", "Unsuccessful: " + response.body())
                    if (response.message() == "null"){
                        return
                    }else{
                        showDialog("Error","${response.message()}")
                    }
                }

                if (response.isSuccessful){
                    Log.d("PRODUCTS", "Successful")
                    val getting = response.body()
                    val data = getting!!.get("data").asJsonArray
                    var pp = ArrayList<ProductCategoryTable>()

                    for(d in data){
                        val value = d.asJsonObject
                        val product = ProductCategoryTable()
                        product.id = value.get("id").asInt
                        product.isActive = value.get("is_active").asBoolean
                        product.name = value.get("name").asString
                        product.products_count = value.get("products_count").asInt
                        product.uid = value.get("uid").asString
                        product.createdAt = value.get("created_at").asString
                        product.updatedAt = value.get("updated_at").asString
                        pp.add(product)
                        viewmodel.insertProductCategorys(product)
                    }

                }
            }
        })

    }





    private val dataSource = dataSourceTypedOf(
        CardModel(R.drawable.ic_makeup, "Beauty"),
        CardModel(R.drawable.ic_food_and_restaurant, "Food & Resturant"),
        CardModel(R.drawable.ic_shoe, "Shoes and Bag"),
        CardModel(R.drawable.ic_tools_and_utensils, "Clothing"),
        CardModel(R.drawable.ic_cake, "Cake & Confectioneries"),
        CardModel(R.drawable.ic_hands_free, "Tech & Gadgets")

        )

    private fun setupCategorySelection(){
        binding.list.setup {
        withDataSource(dataSource)
            withLayoutManager(GridLayoutManager(this@SignupCategory,2))
            withItem<CardModel,CardViewHolder>(R.layout.card_category_item){
                onBind(::CardViewHolder){ _, item->
                    name.text=item.name
                    image.setImageResource(item.image!!)

                }
                 onChildViewClick(CardViewHolder::cardView){index, view ->
                  when(index){
                      0->{(view as MaterialCardView).toggle()}
                      1->{(view as MaterialCardView).toggle()}
                      2->{(view as MaterialCardView).toggle()}
                      3->{(view as MaterialCardView).toggle()}
                      4->{(view as MaterialCardView).toggle()}
                      5->{(view as MaterialCardView).toggle()}
                  }
                }
            }

        }

    }
    private fun setupNavigation(){
       binding.signup.setOnClickListener {
           val categoryRequest = CategoryRequest()
           categoryRequest.categories = productCategoryList
           if (!categoryRequest.categories.isNullOrEmpty()){
               Log.d("CATLIST", categoryRequest.categories.toString())
               scribeObserver(categoryRequest)
           }
       }
    }


    private fun scribeObserver(categoryRequest: CategoryRequest){
        val call: Call<JsonObject> = mApiService!!.saveProductCategory(tinyDB!!.getString(TinyDB.Token), tinyDB!!.getString(TinyDB.Domain), categoryRequest)

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (!response.isSuccessful){
                    Log.d("PRODUCTS", "Unsuccessful: " + response.body())
                    if (response.message() == "null"){
                        return
                    }else{
                        showDialog("Error","${response.message()}")
                    }
                }

                if (response.isSuccessful){
                    val intent = Intent(this@SignupCategory, BusinessContainer::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
            }
        })

    }




    data class CardModel(
        var image:Int?=null,
        var name:String?=null

    )
    class CardViewHolder(itemView:View):ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.Description)
        val image:ImageView=itemView.findViewById(R.id.image)
        val cardView:CardView=itemView.findViewById(R.id.root)
    }




}
