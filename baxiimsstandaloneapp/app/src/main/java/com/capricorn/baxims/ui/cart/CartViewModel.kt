package com.capricorn.baxims.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.capricorn.baxims.models.CartTable
import com.capricorn.baxims.models.ProductTable
import com.capricorn.baxims.models.TransactionTable
import com.capricorn.baxims.models.UserTable
import com.capricorn.baxims.repository.ImsRepository

class CartViewModel constructor(application: Application): AndroidViewModel(application){

    private val repository: ImsRepository = ImsRepository(application)



    fun selectCart(): LiveData<List<CartTable>>?{
        return repository.SelectAllCart()
    }

    fun selectProductByClient(name: String): LiveData<ProductTable>?{
        return repository.selectProductByClient(name)
    }

    //For Product restock
    fun updateUnit(unit: Int, client: String){
        repository.updateUnit(unit, client)
    }


    fun selectCartById(id:Int):CartTable?{
        return repository.selectCartById(id)
    }

    fun updateUnitCart(unit:Int,id:Int){
        return repository.updateUnitCart(unit, id)
    }

    fun getUserByID(id: Int): LiveData<UserTable>{
        return repository.getUserByID(id)
    }

    fun nukeCartTable(){
        return repository.nukeCartTable()
    }



    //Transaction

    fun insertTransaction(transactionTable: TransactionTable){
        repository.insertTransaction(transactionTable)
    }

    fun getTransactionByOutletUID(outletuid: String):LiveData<List<TransactionTable>>?{
        return repository.getTransactionByOutletUID(outletuid)
    }


}