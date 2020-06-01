package com.capricorn.baxims.api.transaction

import com.capricorn.baxims.api.business.Category

data class FullTransaction(
    var transactions: List<Transactions>?= null
)

data class Transactions(

    var username: String?=null,
    var outlet_uid: String?=null,
    var ref: String?=null,
    var type: String?=null,
    var amount: Int?=null,
    var payment_method: String?=null,
    var processed_at: String?=null,
    var customer: Customer?=null,
    var products: List<ProductTransaction>?=null
)

data class Customer (
    var phone_no: String?=null,
    var name: String?=null
)

data class ProductTransaction (

    var sku_client: String?=null,
    var name: String?=null,
    var barcode: String?=null,
    var image: String?=null,
    var selling_price: Int?=null,
    var restock_level: Int?=null,
    var quantity:Int?=null,
    var brand:Brand?=null,
    var categories: List<Categories>?=null

)

data class Brand (
    var uid_client: String?=null,
    var name: String?=null
)

data class Categories (
    var uid_client: String?=null,
    var name: String?=null
)


