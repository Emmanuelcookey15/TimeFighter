package com.capricorn.baxims.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import com.capricorn.baxims.models.OutletTable
import com.capricorn.baxims.models.UserTable

@Dao
interface OutletDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutlet(outletTable: OutletTable)

    @Query("SELECT * FROM outlet_table")
    fun getLiveOutlet(): LiveData<List<OutletTable>>

    @Query("SELECT * FROM outlet_table")
    fun getOutlet():List<OutletTable>

    @Query("SELECT * FROM outlet_table WHERE userId = :userId AND outletName = :outletName")
    fun getUserOutLetAccess(userId: Int, outletName: String):OutletTable

    @Query("SELECT COUNT(*) from outlet_table")
    fun outLetCount() : Int

}