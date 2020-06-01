package com.capricorn.baxims.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import com.capricorn.baxims.models.CartTable

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cartTable: CartTable)

    @Query("SELECT * From cartTable")
    fun SelectAllCart():LiveData<List<CartTable>>

    @Query("SELECT * From cartTable")
    fun SelectAllCarts():LiveData<CartTable>

    @Query("UPDATE cartTable set unit=:unit where ProductId=:id")
    fun updateUnitCart(unit:Int,id:Int)


    @Query("Select * from cartTable where ProductId=:id")
    fun selectCartById(id:Int):CartTable

    @Query("DELETE FROM cartTable")
    fun nukeCartTable()

}