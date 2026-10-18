package com.example.bidai.data.model

import androidx.annotation.DrawableRes
import com.example.bidai.R

data class User(val id: Int, val name: String, val email: String, val phone: String?)

data class Seller(val id: Int, val name: String)

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val condition: String,
    val brand: String?,
    val productType: String?,
    val sellerType: String?,
    val location: String?,
    //original price
    val originalPrice: Double?,
    //eco score
    val ecoScore: Int?,
    val verified: Boolean,
    val imageUrl: String?,
    val images: List<String>,
    val status: String,
    val createdAt: String,
    val seller: Seller,
)

data class SignupRequest(val name: String, val phone: String)

data class AuthResponse(val user: User)
data class ProductsResponse(val products: List<Product>)
data class ProductResponse(val product: Product)
data class BrandsResponse(val brands: List<String>)

//error body
data class ApiError(val error: String?, val fields: Map<String, String>?)

//listing draft
data class ProductDraft(
    val title: String,
    val description: String,
    val price: String,
    val category: String,
    val condition: String = "used",
    val brand: String = "",
    val productType: String = "",
    val sellerType: String = Catalog.SELLER_INDIVIDUAL,
    val location: String = "",
)

data class Category(val name: String, @DrawableRes val icon: Int)

object Catalog {
    const val SELLER_VENDOR = "vendor"
    const val SELLER_INDIVIDUAL = "individual"
    const val MAX_PHOTOS = 5
    const val MIN_PHOTOS = 2

    //category order
    val categories = listOf(
        Category("Cars", R.drawable.cat_cars),
        Category("Real Estate", R.drawable.cat_real_estate),
        Category("Mobiles", R.drawable.cat_mobiles),
        Category("Jobs", R.drawable.cat_jobs),
        Category("Bikes", R.drawable.cat_bikes),
        Category("Electronics", R.drawable.cat_electronics),
        Category("Home & Garden", R.drawable.cat_home_garden),
        Category("Beauty", R.drawable.cat_beauty),
        Category("Clothing", R.drawable.cat_clothing),
        Category("Books", R.drawable.cat_books),
        Category("Arts and Crafts", R.drawable.cat_arts_crafts),
        Category("Services", R.drawable.cat_services),
        Category("Yodha", R.drawable.cat_yodha),
        Category("Agriculture", R.drawable.cat_agriculture),
        Category("General Service", R.drawable.cat_general_service),
    )

    val categoryNames = categories.map { it.name }

    //labels
    val conditions = linkedMapOf("new" to "New", "like_new" to "Like new", "used" to "Used")

    fun conditionLabel(value: String) = conditions[value] ?: value
}
