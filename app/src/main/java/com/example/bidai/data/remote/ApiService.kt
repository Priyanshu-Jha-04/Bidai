package com.example.bidai.data.remote

import com.example.bidai.data.model.AuthResponse
import com.example.bidai.data.model.BrandsResponse
import com.example.bidai.data.model.ProductResponse
import com.example.bidai.data.model.ProductsResponse
import com.example.bidai.data.model.SignupRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/auth/signup")
    suspend fun signup(@Body body: SignupRequest): AuthResponse

    //profile
    @POST("api/auth/login")
    suspend fun login(): AuthResponse

    @GET("api/products")
    suspend fun products(
        @Query("search") search: String?,
        @Query("category") category: String?,
        @Query("brand") brand: String?,
        @Query("limit") limit: Int?,
    ): ProductsResponse

    @GET("api/products/brands")
    suspend fun brands(@Query("category") category: String): BrandsResponse

    @GET("api/products/mine")
    suspend fun myProducts(): ProductsResponse

    @PATCH("api/products/{id}/sold")
    suspend fun markSold(@Path("id") id: Int): ProductResponse

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("api/products")
    suspend fun createProduct(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("category") category: RequestBody,
        @Part("condition") condition: RequestBody,
        @Part("brand") brand: RequestBody,
        @Part("productType") productType: RequestBody,
        @Part("sellerType") sellerType: RequestBody,
        @Part("location") location: RequestBody,
        @Part images: List<MultipartBody.Part>,
    ): ProductResponse
}
