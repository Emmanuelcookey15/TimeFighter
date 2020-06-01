package com.nepa.ng.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.nepa.ng.model.AllVendorTable
import com.nepa.ng.persistence.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AppRepository(val application: Application) {

    private var parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)

    //Repository Section For User Table Of the IMS Dataase
    private val vendorDao = AppDatabase.getDatabase(application).allVendorDao()


    fun insertVendor(vendorTable: AllVendorTable){
        scope.launch(Dispatchers.IO)  {
            vendorDao.insertVendor(vendorTable)
        }
    }


    fun getAllVendors(): LiveData<List<AllVendorTable>>? {
        return vendorDao.getAllVendors()
    }

}