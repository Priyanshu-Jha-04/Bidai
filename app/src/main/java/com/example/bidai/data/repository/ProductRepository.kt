package com.example.bidai.data.repository

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import com.example.bidai.data.model.Product
import com.example.bidai.data.model.ProductDraft
import com.example.bidai.data.remote.ApiService
import com.example.bidai.data.remote.AppResult
import com.example.bidai.data.remote.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import kotlin.math.max

class ProductRepository(
    private val api: ApiService,
    private val contentResolver: ContentResolver,
) {
    suspend fun getProducts(
        search: String? = null,
        category: String? = null,
        brand: String? = null,
        limit: Int? = null,
    ): AppResult<List<Product>> =
        safeApiCall {
            api.products(
                search = search?.trim()?.takeIf { it.isNotEmpty() },
                category = category?.takeIf { it != ALL },
                brand = brand,
                limit = limit,
            ).products
        }

    suspend fun getBrands(category: String): AppResult<List<String>> = safeApiCall { api.brands(category).brands }

    suspend fun myProducts(): AppResult<List<Product>> = safeApiCall { api.myProducts().products }

    suspend fun markSold(id: Int): AppResult<Product> = safeApiCall { api.markSold(id).product }

    suspend fun delete(id: Int): AppResult<Unit> = safeApiCall {
        val response = api.deleteProduct(id)
        if (!response.isSuccessful) throw retrofit2.HttpException(response)
    }

    suspend fun createProduct(draft: ProductDraft, imageUris: List<Uri>): AppResult<Product> = safeApiCall {
        val text = "text/plain".toMediaType()
        fun String.part(): RequestBody = trim().toRequestBody(text)

        val images = imageUris.mapIndexed { index, uri ->
            val bytes = withContext(Dispatchers.IO) {
                //bad photo
                runCatching { compress(uri) }.getOrElse { throw IllegalStateException("Couldn't read that photo. Try a different one.") }
            }
            MultipartBody.Part.createFormData("images", "photo$index.jpg", bytes.toRequestBody("image/jpeg".toMediaType()))
        }
        api.createProduct(
            title = draft.title.part(),
            description = draft.description.part(),
            price = draft.price.part(),
            category = draft.category.part(),
            condition = draft.condition.part(),
            brand = draft.brand.part(),
            productType = draft.productType.part(),
            sellerType = draft.sellerType.part(),
            location = draft.location.part(),
            images = images,
        ).product
    }

    //downscale
    private fun compress(uri: Uri): ByteArray {
        val source = ImageDecoder.createSource(contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val longest = max(info.size.width, info.size.height)
            if (longest > MAX_SIDE) {
                val scale = MAX_SIDE.toFloat() / longest
                decoder.setTargetSize((info.size.width * scale).toInt(), (info.size.height * scale).toInt())
            }
        }
        return ByteArrayOutputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 82, out)
            out.toByteArray()
        }
    }

    companion object {
        const val ALL = "All"
        private const val MAX_SIDE = 1280
    }
}
