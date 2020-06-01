package com.capricorn.baxims.api.dashboard

data class AddProductData(
    val barcode: String?=null,
    val buying_price: Int?=null,
    val id: Int?=null,
    val image: String?=null,
    val is_active: Boolean?=null,
    val my_brand_id: Int?=null,
    val name: String?=null,
    val quantiy_in_stock: Int?=null,
    val restock_level: Int?=null,
    val selling_price: Int?=null,
    val sku_client: String?=null,
    val sku_server: String?=null,
    val synced_at: Any,
    val updated_at: String?=null
)