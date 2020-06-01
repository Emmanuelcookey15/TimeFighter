package com.capricorn.baxims.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import com.capricorn.baxims.models.ProductTable

@Dao
interface ProductDao {


 /** get all product **/
 @Insert(onConflict = OnConflictStrategy.REPLACE)
 fun insertProduct(productResponse: ProductTable)

// /** get all product **/
  @Query("SELECT * FROM products_data_list WHERE id=:id")
  fun selectProductById(id:Int): ProductTable


 @Query("SELECT * FROM products_data_list WHERE name LIKE :name OR barcode LIKE :name ORDER BY name")
 fun selectProductByName(name:String): LiveData<ProductTable>

 @Query("SELECT * FROM products_data_list WHERE skuClient=:name")
 fun selectProductByClient(name:String): LiveData<ProductTable>

 @Query("UPDATE products_data_list set quantiy_in_stock=:unit where skuClient=:client")
 fun updateUnit(unit:Int, client: String)

 @Query("UPDATE products_data_list set sellingPrice=:prize where skuClient=:client")
 fun updatePrize(prize:Int, client: String)

 @Query("UPDATE products_data_list set name=:name where skuClient=:client")
 fun updateName(name:String, client: String)

 @Query("UPDATE products_data_list set barcode=:barcode where skuClient=:client")
 fun updateBarcode(barcode:String, client: String)

 @Query("UPDATE products_data_list set image=:image where skuClient=:client")
 fun updateImage(image:String, client: String)


 // /** get all product **/
 @Query("SELECT * FROM  products_data_list")
 fun getAllProducts(): LiveData<List<ProductTable>>

 @Query("SELECT * FROM products_data_list WHERE outlet_uid=:outletuid")
 fun selectProductsByOutlets(outletuid: String): LiveData<List<ProductTable>>

 @Query("DELETE FROM products_data_list WHERE my_outlet_name=:outletname")
 fun nukeOutLetProductTable(outletname: String)


}