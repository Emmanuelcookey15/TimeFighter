package com.capricorn.baxims.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import com.capricorn.baxims.api.business.BusinessCategoryResponse
import com.capricorn.baxims.models.UserTable


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserTable)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUser(user: UserTable)

    @Query("SELECT * FROM user_table")
    fun getLiveUser():LiveData<List<UserTable>>

    @Query("SELECT * FROM user_table")
    fun getUsers():List<UserTable>

    @Query("SELECT * FROM user_table WHERE id = :id")
    fun getUserById(id: Int):LiveData<UserTable>

    @Query("SELECT * FROM user_table WHERE username = :userName")
    fun getUserByUserName(userName: String):LiveData<UserTable>

    @Query("SELECT * FROM user_table WHERE email = :email")
    fun getUserByEmail(email: String):LiveData<UserTable>

    @Query("SELECT COUNT(*) from user_table")
    fun usersCount() : LiveData<Int>

}