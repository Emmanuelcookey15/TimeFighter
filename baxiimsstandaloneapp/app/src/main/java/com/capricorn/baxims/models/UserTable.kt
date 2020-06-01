package com.capricorn.baxims.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_table")
class UserTable() {


    @PrimaryKey
    var id: Int?=null
    var first_name: String=""
    var last_name: String=""
    var username: String=""
    var phone_no: String=""
    var email: String=""
    var gender: String=""
    var password: String=""
    var isAdmin: Boolean? =null
    var roleId: Int? =null
    var isActive: Boolean?=null
    var rememberToken: String=""


}