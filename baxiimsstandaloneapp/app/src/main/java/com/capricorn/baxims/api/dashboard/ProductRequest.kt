package com.capricorn.baxims.api.dashboard


data class ProductRequest(
    var barcode: String?=null,
    var image: String?=null,
    var name: String?=null,
    var restock_level: Int?=null,
    var selling_price: Int?=null
)

//    val brand_id: Int?=null,
//    val category_id: List<String>,
