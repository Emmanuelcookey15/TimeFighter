package com.capricorn.baxims.ui.dashboard

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import androidx.room.Transaction
import androidx.room.util.DBUtil
import com.capricorn.baxims.api.business.BusinessList
import com.capricorn.baxims.api.dashboard.*
import com.capricorn.baxims.models.*
import com.capricorn.baxims.repository.ImsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel constructor(application: Application):AndroidViewModel(application) {

    private val repository: ImsRepository = ImsRepository(application)

    fun getUserByID(id: Int): LiveData<UserTable> {
        val user = repository.getUserByID(id)
        return user
    }









    fun insertOutlet(outletTable: OutletTable) {
        repository.insertOutlet(outletTable)
    }









    fun insertProduct(productResponse: ProductTable){
        repository.insertProduct(productResponse)
    }


    fun updateUnit(unit: Int, client: String){
        repository.updateUnit(unit, client)
    }


    fun updatePrize(prize: Int, client: String){
        repository.updatePrize(prize, client)
    }

    fun updateName(name: String, client: String){
        repository.updateName(name, client)
    }

    fun updateBarcode(barcode: String, client: String){
        repository.updateBarcode(barcode, client)
    }

    fun updateImage(image: String, client: String){
        repository.updateImage(image, client)
    }



    fun getAllProducts(): LiveData<List<ProductTable>>{
        return repository.getAllProducts()
    }

    fun selectProductsByOutlets(outletuid: String): LiveData<List<ProductTable>>?{
         return repository.selectProductsByOutlets(outletuid)
    }

    fun selectProductById(id: Int): ProductTable {
        return repository.selectProductById(id)
    }

    fun selectProductByName(name: String): LiveData<ProductTable>? {
        return repository.selectProductByName(name)
    }

    fun selectProductByClient(name: String): LiveData<ProductTable>? {
        return repository.selectProductByClient(name)
    }

    fun nukeOutLetProductTable(outletname: String){
        repository.nukeOutLetProductTable(outletname)
    }









    fun insertProductCategorys(category: ProductCategoryTable){
        repository.insertProductCategorys(category)
    }


    fun getProductCategorys(): LiveData<List<ProductCategoryTable>>?{
        return repository.getProductCategorys()
    }







    fun insertCart(cartTable: CartTable){
        repository.insertCart(cartTable)
    }

    fun SelectAllCart():LiveData<List<CartTable>>?{
        return repository.SelectAllCart()
    }


    fun updateUnitCart(unit:Int,id:Int){
        repository.updateUnitCart(unit, id)
    }

    fun selectCartById(id:Int): CartTable?{
        return repository.selectCartById(id)
    }




    //Transaction


    fun getTransactionByOutletUID(outletuid: String):LiveData<List<TransactionTable>>?{
        return repository.getTransactionByOutletUID(outletuid)
    }



}