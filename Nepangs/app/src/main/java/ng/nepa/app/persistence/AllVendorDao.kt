package com.nepa.ng.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nepa.ng.model.AllVendorTable

@Dao
interface AllVendorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVendor(vendorTable: AllVendorTable)

    @Query("SELECT * FROM all_vendor_table")
    fun getAllVendors(): LiveData<List<AllVendorTable>>

}