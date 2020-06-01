package com.capricorn.baxims.ui.dashboard

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.capricorn.baxims.R
import com.capricorn.baxims.databinding.ActivityProductDetailsBinding
import com.capricorn.baxims.models.CartTable
import com.capricorn.baxims.models.ProductTable
import com.capricorn.baxims.utils.TinyDB
import com.capricorn.baxims.utils.showToast
import kotlinx.coroutines.*


class ProductDetails : AppCompatActivity() {
    lateinit var binding:ActivityProductDetailsBinding

    lateinit var providerFactory: ViewModelProvider.Factory
    lateinit var viewmodel: DashboardViewModel
    var tinyDB: TinyDB? = null
    var count = 0
    var productTable = ProductTable()
    var skuValue = ""
    var quantityInStock= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_product_details)
        viewmodel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        binding.toolbar.title = "Product Detail"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        skuValue = intent.getStringExtra("skuClient")
        loadProductDetails(skuValue)
        setUpNavigation()
    }

    private fun setUpNavigation() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.fab.setOnClickListener {

            count=0
            MaterialDialog(this).show {
                cornerRadius(5F)
                customView(R.layout.item_add_to_cart)
                val pImage = view.findViewById<ImageView>(R.id.productImg)
                val pName = view.findViewById<TextView>(R.id.productName)
                val pPrize = view.findViewById<TextView>(R.id.amount)
                val pAdd: ImageView = view.findViewById(R.id.add)
                val pMinus: ImageView = view.findViewById(R.id.minus)
                val pUnits: TextView = view.findViewById(R.id.units)

                val imageTwo = StringToBitMap(productTable.image)
                if (imageTwo != null) {
                    pImage.setImageBitmap(imageTwo)
                } else {
                    Glide.with(this@ProductDetails).load(productTable.image).into(pImage)
                }

                pName.text = productTable.name
                pPrize.text = "₦${productTable.sellingPrice}"
                pAdd.setOnClickListener {
                    pMinus.alpha = 1F
                    if (count < productTable.quantiy_in_stock!!) {
                        count++
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(200)
                            pUnits.text = "$count"
                        }
                    } else {
                        pAdd.alpha = 0.6F
                        return@setOnClickListener
                    }
                }

                pMinus.setOnClickListener {
                    pAdd.alpha = 1F
                    if (count > 0) {
                        count--
                        pUnits.text = "$count"
                    } else {
                        pMinus.alpha = 0.6F
                        return@setOnClickListener
                    }
                }

                this.view.findViewById<Button>(R.id.addToCarts).setOnClickListener {
                    if (count == 0) {
                        context.showToast("can't add an empty cart")
                        return@setOnClickListener
                    }
                    val cart = CartTable(
                        ProductId = productTable.id,
                        unit = count,
                        sellingPrice = productTable.sellingPrice,
                        restockLevel = productTable.restockLevel,
                        quantiy_in_stock = productTable.quantiy_in_stock,
                        name = productTable.name,
                        image = productTable.image,
                        skuClient = productTable.skuClient
                    )

                    CoroutineScope(Dispatchers.IO).launch {

                        withContext(Dispatchers.Main) {
                            viewmodel.insertCart(cart)
                        }
                    }
                    showToast("${cart.unit} item added to cart")
                    dismiss()
                    finish()
                }

                this.view.findViewById<Button>(R.id.dismiss).setOnClickListener {
                    dismiss()
                }
            }

            //navigateTo<AddProduct> ()
        }
    }

    private fun loadProductDetails(value: String) {
        val product = viewmodel.selectProductByClient(value)?.observe(this, Observer {

            productTable=it
            binding.productName.text = it?.name
            binding.Productbarcode.text = "Product Barcode: ${it?.barcode}"
            binding.productAmount.text = "₦ ${it?.sellingPrice}"
            binding.productUnits.text = "Product In Stock ${it?.quantiy_in_stock} Units"
            quantityInStock = it.quantiy_in_stock!!
            binding.productImage
            val image = StringToBitMap(it!!.image)
            if (image != null){
                binding.productImage.setImageBitmap(image)
            }else {
                Glide.with(this).load(it.image).into(binding.productImage)
            }

        })
    }


    private fun StringToBitMap(encodedString: String): Bitmap? {
        try {
            val encodeByte = android.util.Base64.decode(encodedString, android.util.Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            return null
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.update_product -> {
                editProduct()
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }

    private fun editProduct() {
        val editIntent = Intent(this, EditActivity::class.java)
        editIntent.putExtra("skuClient", skuValue)
        editIntent.putExtra("quantityInStock", quantityInStock)
        startActivity(editIntent)
    }

}
