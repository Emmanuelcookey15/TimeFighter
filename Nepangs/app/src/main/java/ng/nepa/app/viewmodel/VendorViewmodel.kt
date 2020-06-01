package com.nepa.ng.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.nepa.ng.model.AllVendorTable
import com.nepa.ng.repository.AppRepository

class VendorViewmodel constructor(application: Application): AndroidViewModel(application) {

    private val repository: AppRepository = AppRepository(application)


    fun insertVendor(vendorTable: AllVendorTable){
        repository.insertVendor(vendorTable)
    }

    fun getAllVendors(): LiveData<List<AllVendorTable>>? {
        return repository.getAllVendors()
    }

}