package com.capricorn.baxims.api.business

data class BusinessList(
    val data: Data,
    val message: String,
    val success: Boolean
)

data class Data(
//    val my_other_businesses: List<Any>,
    val my_own_businesses: List<MyOwnBusinesse>
)

data class Category(
    val businesses_count: Int?=null,
    val id: Int?=null,
    val name: String?=null
)

data class MyOwnBusinesse(
    val business_category_id: Int?=null,
    val category: Category?=null,
    val domain: String?=null,
    val id: Int?=null,
    val is_active: Boolean,
    val logo: String?=null,
    val name: String?=null,
    val user_id: Int?=null,
    val website_id: Int?=null
)