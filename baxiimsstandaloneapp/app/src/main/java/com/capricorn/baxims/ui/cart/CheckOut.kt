package com.capricorn.baxims.ui.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capricorn.baxims.R
import com.capricorn.baxims.adapter.CheckOutAdapter
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.api.transaction.ProductTransaction
import com.capricorn.baxims.databinding.FragmentCheckOutBinding
import com.capricorn.baxims.models.CartTable
import com.capricorn.baxims.models.TransactionTable
import com.capricorn.baxims.utils.TinyDB
import com.capricorn.baxims.utils.showDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CheckOut : Fragment() {
    var transactionNumber: String? = null
    lateinit var binding:FragmentCheckOutBinding

    val transObject = JsonObject()
    val transCustomer = JsonObject()
    val transProductObj = JsonObject()
    val transProductArray = JsonArray()
    val transBrand = JsonObject()
    val transCategory = JsonObject()

    var transactionTable = TransactionTable()

    lateinit var viewmodel: CartViewModel

    lateinit var providerFactory: ViewModelProvider.Factory
    lateinit var navController: NavController
    var totalProductTrans= ArrayList<ProductTransaction>()

    var tinyDB: TinyDB? = null

    var mApiService: ApiService? = null

    var checkOutAdapter: CheckOutAdapter? = null

    var totalAmount = ""
    var amount = 0

    var changedDateOne: Date = Date()

    val sdf = SimpleDateFormat("MMM dd, yyyy | HH:mm:ss")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_check_out, container, false)
        viewmodel = ViewModelProvider(this).get(CartViewModel::class.java)
        tinyDB = TinyDB(activity!!)

        transactionNumber = "TT" + UUID.randomUUID().toString().take(12).random()

        var myCalendar = Calendar.getInstance()

        changedDateOne = Date()

        myCalendar.setTime(changedDateOne)

        binding.orderNumber.text = "#$transactionNumber"
        binding.orderDate.text = sdf.format(changedDateOne)

        checkOutAdapter = CheckOutAdapter(tinyDB!!, binding.totalItemAmount, binding.totaledAmount)
        binding.list.adapter = checkOutAdapter
        binding.list.layoutManager = LinearLayoutManager(activity!!)
        navController=findNavController()
        totalAmount = arguments?.getString("totalAmount")!!
        setUpNavigation(totalAmount)
        return binding.root
    }



    private fun setUpNavigation(totalAmount: String) {

        var productTrans= ProductTransaction()

        viewmodel.selectCart()?.observe(viewLifecycleOwner, Observer {

            checkOutAdapter?.setCheckOutData(it as ArrayList<CartTable>)
            for (cart in it){

                transProductObj.addProperty("name", cart.name)
                transProductObj.addProperty("barcode", cart.barcode)
                transProductObj.addProperty("quantity", cart.unit)
                transProductObj.addProperty("restock_level", cart.restockLevel)
                transProductObj.addProperty("selling_price", cart.sellingPrice)
                transProductObj.addProperty("sku_client", cart.skuClient)

                //Brand
                transBrand.addProperty("uid_client", "string")
                transBrand.addProperty("name", "string")

                //Categories
                transCategory.addProperty("uid_client", "string")
                transCategory.addProperty("name", "string")

                //Put the brand and category object inside the
                transProductObj.add("brand", transBrand)
                transProductObj.add("categories", transCategory)

                transProductArray.add(transProductObj)
            }

        })

        binding.toolbar.setNavigationOnClickListener { navController.navigateUp()}

        binding.cashPayment.setOnClickListener {
            transact("cash")
        }

        binding.posPayment.setOnClickListener {
            activity?.showDialog("Unavailable","Please try Cash Payment, " +
                    "this features will be included in future release")
        }
        binding.ussdPayment.setOnClickListener {
            activity?.showDialog("Unavailable","Please try Cash Payment, " +
                    "this features will be included in future release")
        }

        binding.bankTransferPayment.setOnClickListener {
            activity?.showDialog("Unavailable","Please try Cash Payment, " +
                    "this features will be included in future release")
        }
    }

    private fun transact(paymentMethod: String){

        viewmodel.getUserByID(tinyDB!!.getInt(TinyDB.UserID)).observe(viewLifecycleOwner, Observer {

            val totalAmount = tinyDB?.getInt(TinyDB.TotalTransactionPrice)

            transObject.addProperty("username", it.username)
            transObject.addProperty("outlet_uid", tinyDB!!.getString(TinyDB.OutletUID))
            transObject.addProperty("ref", transactionNumber)
            transObject.addProperty("type", "sales")
            transObject.addProperty("amount", totalAmount)
            transObject.addProperty("payment_method", paymentMethod)

            val timeStampLong = System.currentTimeMillis() / 1000
            val timeStamp = timeStampLong.toString()
            transObject.addProperty("processed_at", timeStamp)

            //Customer
            transCustomer.addProperty("phone_no", "string")
            transCustomer.addProperty("name", "string")
            transObject.add("customer", transCustomer)

            //Product
            transObject.add("products", transProductArray)

            transactionTable.my_outlet_name = tinyDB!!.getString(TinyDB.OutletName)
            transactionTable.outlet_uid = tinyDB!!.getString(TinyDB.OutletUID)
            transactionTable.transactions_json_string = transObject.toString()

            viewmodel.insertTransaction(transactionTable)


            navController.navigate(R.id.action_checkOut_to_process_payment)
        })




    }

}
