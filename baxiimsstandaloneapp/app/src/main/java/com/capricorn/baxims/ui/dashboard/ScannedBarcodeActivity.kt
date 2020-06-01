package com.capricorn.baxims.ui.dashboard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.capricorn.baxims.R
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_scanned_barcode.*
import java.io.IOException

class ScannedBarcodeActivity : AppCompatActivity() {


    lateinit var surfaceView: SurfaceView
    lateinit var txtBarcodeValue: TextView
    lateinit var btnAction: Button

    lateinit var barcodeDetector: BarcodeDetector
    lateinit var cameraSource: CameraSource
    val REQUEST_CAMERA_PERMISSION = 201;
    var intentData = "";
    var isEmail = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned_barcode)

        initViews()
    }


    private fun initViews() {
        txtBarcodeValue = findViewById<TextView>(R.id.txtBarcodeValue)
        surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
        btnAction = findViewById<Button>(R.id.btnAction)
        btnAction.setOnClickListener{
                if (intentData.length > 0) {
                    if (isEmail) {
                        val data = Intent().putExtra("barcode", intentData)
                        setResult(Activity.RESULT_OK, data)
                        finish()
                    } else {
                        val data = Intent().putExtra("barcode", intentData)
                        setResult(Activity.RESULT_OK, data)
                        finish()
                    }
                }
        }
    }



    private fun initialiseDetectorsAndSources() {
        Toast.makeText(applicationContext, "Barcode scanner started", Toast.LENGTH_SHORT)
            .show()
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
        surfaceView.getHolder().addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@ScannedBarcodeActivity,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource.start(surfaceView.getHolder())
                    } else {
                        ActivityCompat.requestPermissions(
                            this@ScannedBarcodeActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_CAMERA_PERMISSION
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(
                    applicationContext,
                    "To prevent memory leaks barcode scanner has been stopped",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(Runnable {
                        if (barcodes.valueAt(0).email != null) {
                            txtBarcodeValue.removeCallbacks(null)
                            intentData = barcodes.valueAt(0).email.address
                            txtBarcodeValue.setText(intentData)
                            isEmail = true
                            btnAction.setText("SCANNING")
                        } else {
                            isEmail = false
                            btnAction.setText("BARCODE SCANNED")
                            intentData = barcodes.valueAt(0).displayValue
                            txtBarcodeValue.setText(intentData)
                        }
                    })
                }
            }
        })
    }


    override fun onPause() {
        super.onPause()
        cameraSource.release()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }


}
