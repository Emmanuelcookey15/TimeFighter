package com.capricorn.baxims.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.capricorn.baxims.models.CartTable
import com.capricorn.baxims.models.TransactionTable

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transactionTable: TransactionTable)

    @Query("SELECT * From transactions_table WHERE outlet_uid=:outletuid")
    fun getTransactionByOutletUID(outletuid: String): LiveData<List<TransactionTable>>

}