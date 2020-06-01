package com.capricorn.baxims.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.capricorn.baxims.models.*


@Database(entities = [UserTable::class, OutletTable::class, ProductTable::class,
    ProductCategoryTable::class, CartTable::class, TransactionTable::class],version = 2)
abstract class ImsDatabase :RoomDatabase(){

//    abstract  fun productDao():ProductDao
    abstract  fun userDao():UserDao
    abstract fun outletDao():OutletDao
    abstract fun productDao():ProductDao
    abstract fun productCategoryDao(): ProductCategoryDao
    abstract fun cartDao(): CartDao
    abstract fun transactionDao(): TransactionDao

    companion object {

        @Volatile
        private var INSTANCE: ImsDatabase? = null

        fun getDatabase(context: Context): ImsDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ImsDatabase::class.java,
                        "Ims_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                }
                INSTANCE = instance
                return instance
            }
        }
    }


}