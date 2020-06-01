package com.capricorn.baxims.ui.dashboard

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.capricorn.baxims.R
import com.capricorn.baxims.databinding.ActivityEditBinding
import com.capricorn.baxims.models.ProductTable
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class EditActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditBinding

    private val ADD_NOTE_REQUEST: Int = 22

    private val RESULT_LOAD_IMG = 46
    private val CAMERA_REQUEST = 47

    lateinit var viewmodel: DashboardViewModel

    var productTable = ProductTable()
    var skuValue = ""
    var quantityInStock = 0

    var imageValue: String?= null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_edit)
        viewmodel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        skuValue = intent.getStringExtra("skuClient")
        quantityInStock = intent.getIntExtra("quantityInStock", 0)
        loadProductDetails(skuValue)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadProductDetails(value: String) {
        binding.editProduct.setOnClickListener {

            if(binding.editedProductName.text!!.isNotEmpty()){
                viewmodel.updateName(binding.editedProductName.text.toString(), value)
            }
            if(binding.editedScanCodeText.text!!.isNotEmpty()){
                viewmodel.updateBarcode(binding.editedScanCodeText.text.toString(), value)
            }
            if (binding.editedProductPrize.text!!.isNotEmpty()){
                viewmodel.updatePrize(binding.editedProductPrize.text.toString().toInt(), value)
            }
            if (binding.editedProductQuantity.text!!.isNotEmpty()){
                val addStock = quantityInStock + binding.editedProductQuantity.text.toString().toInt()
                viewmodel.updateUnit(addStock, value)
            }
            if (imageValue != null){
                viewmodel.updateImage(imageValue!!, value)
            }

            finish()

        }

        selectAndConvertImage()

        binding.productBarcodeHolder.setEndIconOnClickListener{

            val intent = Intent(this, ScannedBarcodeActivity::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST);
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun selectAndConvertImage(){
        binding.editedProfileImage.setOnClickListener {

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            binding.editedScanCodeText.setText(
                data?.getStringExtra("email_address"),
                TextView.BufferType.EDITABLE
            )
        }

        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMG) {
            try {
                val imageUri = data!!.data

                val imageStream = contentResolver.openInputStream(imageUri!!)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                val nh = (selectedImage.height * (512.0 / selectedImage.width)).toInt()
                val scaled = Bitmap.createScaledBitmap(selectedImage, 512, nh, true)
                binding.editedProfileImage.setImageBitmap(scaled)
                imageValue = imageUri.toString()
                BitMapToString(scaled)


            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }

        if(resultCode != RESULT_CANCELED) {
            if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
                val theImage = data?.extras?.get("data") as Bitmap
                val sizeAdjustment = (theImage.height * (512.0 / theImage.width)).toInt()
                val scaled = Bitmap.createScaledBitmap(theImage, 512, sizeAdjustment, true)

                binding.editedProfileImage.setImageBitmap(scaled)

                BitMapToString(scaled)

            }
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
        myAlertDialog.setTitle("Upload Pictures Option")
        myAlertDialog.setMessage("How do you want to set your picture?")

        myAlertDialog.setPositiveButton("Gallery", DialogInterface.OnClickListener() {
                dialogInterface: DialogInterface, i: Int ->

            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)

        });

        myAlertDialog.setNegativeButton("Camera", DialogInterface.OnClickListener() {
                dialogInterface: DialogInterface, i: Int ->
            val intent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,
                CAMERA_REQUEST)
        });
        myAlertDialog.show();
    }

}
