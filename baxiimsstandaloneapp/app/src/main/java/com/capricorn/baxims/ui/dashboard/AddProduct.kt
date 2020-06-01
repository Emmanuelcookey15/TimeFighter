package com.capricorn.baxims.ui.dashboard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.ilhasoft.support.validation.Validator
import com.capricorn.baxims.R
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.api.dashboard.ProductRequest
import com.capricorn.baxims.databinding.ActivityAddProductBinding
import com.capricorn.baxims.models.ProductTable
import com.capricorn.baxims.utils.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


/**check the extending class out suspious extends*/
class AddProduct : AppCompatActivity() {
    private val ADD_NOTE_REQUEST: Int = 22
    lateinit var binding: ActivityAddProductBinding
    var productcategory: ArrayList<String> = arrayListOf()
    var productcategoryId: ArrayList<Int> = arrayListOf()
    val clickAdd:ArrayList<String> = arrayListOf()

    lateinit var viewmodel: DashboardViewModel

    private val RESULT_LOAD_IMG = 46
    private val CAMERA_REQUEST = 47

    var imageValue: String?= null
    var mApiService: ApiService? = null
    var tinyDB: TinyDB? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_product)
        viewmodel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        tinyDB = TinyDB(this)
        initializerRetrofit()
        binding.productPrize.setText("")
        binding.productQuantity.setText("")
        binding.productNameHolder.clearFocus()
        val validator= Validator(binding)
        selectAndConvertImage()
        setupNavigation(validator)

    }

    private fun setupNavigation(validator: Validator){

        binding.addProduct.setOnClickListener {

            val productRequest = ProductTable()
            val skuClient = "PP" + UUID.randomUUID().toString().take(10).toUpperCase()
            productRequest.barcode = binding.scanCodeText.text.toString()
            productRequest.name = binding.addedProductName.text.toString()
            productRequest.image = imageValue!!
            productRequest.quantiy_in_stock = binding.productQuantity.text.toString().toInt()
            productRequest.sellingPrice = binding.productPrize.text.toString().toInt()
            productRequest.skuClient = skuClient
            productRequest.my_outlet_name = tinyDB!!.getString(TinyDB.OutletName)
            productRequest.outlet_uid = tinyDB!!.getString(TinyDB.OutletUID)
            productRequest.restockLevel = 10


            if (validator.validate()){
                if (productRequest.image.isNullOrEmpty()){
                    showToast("select an image")
                    return@setOnClickListener
                }

                Log.d("THE-SKU", productRequest.skuClient)
                viewmodel.insertProduct(productRequest)
                finish()
            }
        }

        binding.productBarcodeHolder.setEndIconOnClickListener{

            var intent = Intent(this, ScannedBarcodeActivity::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
        productcategory.add(0,"Select Product Category")


    }

    private fun subscribeObserver(productRequest: ProductRequest) {

        val call: Call<JsonObject> =
            mApiService!!.AddProduct(tinyDB!!.getString(TinyDB.Token), tinyDB!!.getInt(TinyDB.OutletID), tinyDB!!.getString(TinyDB.Domain), productRequest)

        call.enqueue(object : Callback<JsonObject>{
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(!response.isSuccessful){
                    loadIndicator(1)
                    Log.d("ADDPRODUCT", "Unuccessful: ")
                    if (response.message() == "null"){
                        return
                    }else{
                        showDialog("Error","${response.message()}")
                    }
                }

                if(response.isSuccessful){
                    Log.d("ADDPRODUCT", "Unuccessful: ")
                    finish()
                }
            }
        })
    }


    fun initializerRetrofit(){

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.baxiims.com.ng/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mApiService = retrofit.create<ApiService>(ApiService::class.java)

    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun selectAndConvertImage(){
      binding.profileImage.setOnClickListener {

          if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
              == PackageManager.PERMISSION_GRANTED) {
          startDialog()
          }else {
              requestPermissions(
                  arrayOf(Manifest.permission.CAMERA),
                  CAMERA_REQUEST)
          }
      }

    }

    private fun loadIndicator(state:Int){
        when(state){
            0->{
                binding.progress.elevation=15F
                binding.addProduct.loadState(0,"Loading")
            }
            1->{
                binding.progress.elevation=0.1F
                binding.addProduct.loadState(1,"Continue to Login")
            }
        }
    }

    @Throws(IOException::class)
    private fun readBytes(context: Context, uri: Uri): ByteArray? =
        context.contentResolver.openInputStream(uri)?.buffered().use {
            it?.readBytes()

        }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            binding.scanCodeText.setText(data?.getStringExtra("barcode"), TextView.BufferType.EDITABLE)
        }

        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMG) {
            try {
                val imageUri = data!!.data

                val imageStream = contentResolver.openInputStream(imageUri!!)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                val nh = (selectedImage.height * (512.0 / selectedImage.width)).toInt()
                val scaled = Bitmap.createScaledBitmap(selectedImage, 512, nh, true)
                binding.profileImage.setImageBitmap(scaled)
                imageValue = imageUri.toString()
                BitMapToString(scaled)


            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            }

        } else if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            val theImage = data?.extras?.get("data") as Bitmap
            val nh = (theImage.height * (512.0 / theImage.width)).toInt()
            val scaled = Bitmap.createScaledBitmap(theImage, 512, nh, true)

            binding.profileImage.setImageBitmap(scaled)

            BitMapToString(scaled)

        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDialog()
            }

        }
    }


    private fun BitMapToString(bitmap: Bitmap){
        val baos =  ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos)
        val b= baos.toByteArray()
        val temp= android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT)
        imageValue = temp

    }


    private fun startDialog() {
        val myAlertDialog = androidx.appcompat.app.AlertDialog.Builder(
            this)
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery", DialogInterface.OnClickListener() {
                dialogInterface: DialogInterface, i: Int ->

            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)

        })

        myAlertDialog.setNegativeButton("Camera", DialogInterface.OnClickListener() {
                dialogInterface: DialogInterface, i: Int ->
            val intent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,
                CAMERA_REQUEST)
        })
        myAlertDialog.show()
    }

}
