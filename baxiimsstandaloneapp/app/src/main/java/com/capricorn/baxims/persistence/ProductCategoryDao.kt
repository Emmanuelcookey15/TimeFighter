package com.capricorn.baxims.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.capricorn.baxims.models.ProductCategoryTable

@Dao
interface ProductCategoryDao {


    /** get Product category data **/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductCategorys(category: ProductCategoryTable)


    /** select all from Product category data **/
    @Query("SELECT * FROM product_category_data")
    fun getProductCategorys(): LiveData<List<ProductCategoryTable>>
}