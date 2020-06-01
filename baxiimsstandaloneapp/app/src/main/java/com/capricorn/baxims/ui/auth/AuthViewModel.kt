package com.capricorn.baxims.ui.auth

import android.app.Application
import androidx.lifecycle.*
import com.capricorn.baxims.models.OutletTable
import com.capricorn.baxims.models.UserTable
import com.capricorn.baxims.repository.ImsRepository

class AuthViewModel constructor(application: Application): AndroidViewModel(application) {

    private val repository: ImsRepository = ImsRepository(application)

    fun insertModel(user: UserTable) {
        repository.insertUser(user)
    }

    fun updatesModel(user: UserTable) {
        repository.updates(user)
    }

    fun getAllUser(): LiveData<List<UserTable>>{
        var users = repository.getAllUser()
        return users
    }

    fun getUserByID(id: Int): LiveData<UserTable> {
        val user = repository.getUserByID(id)
        return user
    }

    fun getUserByUserName(userName: String): LiveData<UserTable>?{
        val user = repository.getUserByUserName(userName)
        return user
    }

    fun getUserByEmail(email: String): LiveData<UserTable>? {
        val user = repository.getUserByEmail(email)
        return user
    }

    fun getUserNum(): LiveData<Int>{
        val num = repository.getCountOfUser()
        return num
    }

    fun getUserOutLetAccess(userId: Int, outletName: String): OutletTable? {
        val outlet = repository.getUserOutLetAccess(userId, outletName)
        return outlet
    }

    fun outLetCount() : Int{
        return repository.outLetCount()
    }



}