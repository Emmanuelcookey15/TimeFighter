package com.capricorn.baxims.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.capricorn.baxims.api.ApiService
import com.capricorn.baxims.models.*
import com.capricorn.baxims.persistence.ImsDatabase
import com.capricorn.baxims.persistence.UserDao
import com.capricorn.baxims.utils.isConnectedToTheInternet
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

class ImsRepository(val application: Application) {

    private var parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)


    //Repository Section For User Table Of the IMS Dataase
    private val userDaos = ImsDatabase.getDatabase(application).userDao()


    fun insertUser(user: UserTable) {
            scope.launch(Dispatchers.IO)  {
                userDaos.insertUser(user)
            }
    }

    fun updates(user: UserTable) {
        scope.launch(Dispatchers.IO) {
                userDaos.updateUser(user)
        }
    }

    fun getAllUser(): LiveData<List<UserTable>>{
        val users = userDaos.getLiveUser()
        return users
    }

    fun getUserByID(id: Int): LiveData<UserTable>{

        val user = userDaos.getUserById(id)
        return user
    }

    fun getUserByUserName(userName: String): LiveData<UserTable>?{
        val user = userDaos.getUserByUserName(userName)
        return user
    }

    fun getUserByEmail(email: String): LiveData<UserTable>? {
        val user = userDaos.getUserByEmail(email)
        return user
    }

    fun getCountOfUser(): LiveData<Int>{
        val num = userDaos.usersCount()
        return num
    }









    //Repository Section For Outlet Table Of the IMS Dataase
    private val outletDaos = ImsDatabase.getDatabase(application).outletDao()


    fun insertOutlet(outletTable: OutletTable) {
        scope.launch(Dispatchers.IO)  {
            outletDaos.insertOutlet(outletTable)
        }
    }

    fun getUserOutLetAccess(userId: Int, outletName: String):OutletTable?{
        var outlet = OutletTable()
        scope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                outlet = outletDaos.getUserOutLetAccess(userId, outletName)
            }
        }
        return outlet
    }

    fun outLetCount() : Int{
        var num = 0
        scope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                num = outletDaos.outLetCount()
            }
        }
        return num
    }









    //Repository Section For Product Table Of the IMS Dataase
    private val productDaos = ImsDatabase.getDatabase(application).productDao()


    fun insertProduct(productResponse: ProductTable){
        scope.launch(Dispatchers.IO)  {
            productDaos.insertProduct(productResponse)
        }
    }


    fun updateUnit(unit: Int, client: String){
        scope.launch(Dispatchers.IO)  {
            productDaos.updateUnit(unit, client)
        }
    }

    fun updatePrize(prize: Int, client: String){
        scope.launch(Dispatchers.IO)  {
            productDaos.updatePrize(prize, client)
        }
    }

    fun updateName(name: String, client: String){
        scope.launch(Dispatchers.IO)  {
            productDaos.updateName(name, client)
        }
    }

    fun updateBarcode(barcode: String, client: String){
        scope.launch(Dispatchers.IO)  {
            productDaos.updateBarcode(barcode, client)
        }
    }

    fun updateImage(image: String, client: String){
        scope.launch(Dispatchers.IO)  {
            productDaos.updateImage(image, client)
        }
    }



    fun getAllProducts(): LiveData<List<ProductTable>>{
        val productTable = productDaos.getAllProducts()
        return productTable
    }

    fun selectProductsByOutlets(outletuid: String): LiveData<List<ProductTable>>?{
        val productTable = productDaos.selectProductsByOutlets(outletuid)
        return productTable
    }

    fun selectProductById(id: Int): ProductTable{
        var productTable = ProductTable()
        scope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                productTable = productDaos.selectProductById(id)
            }
        }
        return productTable
    }

    fun selectProductByName(name: String): LiveData<ProductTable>?{
        val productTable = productDaos.selectProductByName(name)
        return productTable
    }

    fun selectProductByClient(name: String): LiveData<ProductTable>?{
        val productTable = productDaos.selectProductByClient(name)

        return productTable
    }

    fun nukeOutLetProductTable(outletname: String){
        scope.launch(Dispatchers.IO)  {
            productDaos.nukeOutLetProductTable(outletname)
        }
    }









    //Repository Section For Product Category Table Of the IMS Dataase
    private val productCategoryDaos = ImsDatabase.getDatabase(application).productCategoryDao()


    fun insertProductCategorys(category: ProductCategoryTable){
        scope.launch(Dispatchers.IO)  {
            productCategoryDaos.insertProductCategorys(category)
        }
    }


    fun getProductCategorys(): LiveData<List<ProductCategoryTable>>?{
        val productCategoryTable = productCategoryDaos.getProductCategorys()
        return productCategoryTable
    }



    //Repository Section For Product Category Table Of the IMS Database
    private val cartDaos = ImsDatabase.getDatabase(application).cartDao()


    fun insertCart(cartTable: CartTable){
        scope.launch(Dispatchers.IO)  {
            cartDaos.insertCart(cartTable)
        }
    }

    fun SelectAllCart():LiveData<List<CartTable>>?{
        val cartTable = cartDaos.SelectAllCart()
        return cartTable
    }


    fun updateUnitCart(unit:Int,id:Int){
        scope.launch(Dispatchers.IO)  {
            cartDaos.updateUnitCart(unit, id)
        }
    }

    fun selectCartById(id:Int):CartTable?{
        var cartTable = CartTable()
        scope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                cartTable = cartDaos.selectCartById(id)
            }
        }
        return cartTable
    }


    fun nukeCartTable(){
        scope.launch(Dispatchers.IO)  {
            cartDaos.nukeCartTable()
        }
    }






    //Repository Section For Product Category Table Of the IMS Database
    private val transactionDaos = ImsDatabase.getDatabase(application).transactionDao()

    fun insertTransaction(transactionTable: TransactionTable){
        scope.launch(Dispatchers.IO)  {
            transactionDaos.insertTransaction(transactionTable)
        }
    }


    fun getTransactionByOutletUID(outletuid: String):LiveData<List<TransactionTable>>?{
        val transactionTable = transactionDaos.getTransactionByOutletUID(outletuid)
        return transactionTable
    }


}