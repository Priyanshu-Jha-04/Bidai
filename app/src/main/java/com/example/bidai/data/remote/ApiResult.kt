package com.example.bidai.data.remote

import com.example.bidai.BuildConfig
import com.google.gson.Gson
import com.example.bidai.data.model.ApiError
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(
        val message: String,
        val fieldErrors: Map<String, String> = emptyMap(),
    ) : AppResult<Nothing>
}

private val gson = Gson()

//safe call
suspend fun <T> safeApiCall(block: suspend () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    e.toFailure()
}

fun Exception.toFailure(): AppResult.Failure = when (this) {
    is HttpException -> {
        val body = runCatching { gson.fromJson(response()?.errorBody()?.string(), ApiError::class.java) }.getOrNull()
        AppResult.Failure(
            message = body?.error ?: "Server error (${code()})",
            fieldErrors = body?.fields.orEmpty(),
        )
    }
    //failure message
    is IOException -> AppResult.Failure("Can't reach the server at ${BuildConfig.API_BASE_URL}. Check your connection and that the backend is running.")
    else -> AppResult.Failure(message ?: "Something went wrong")
}
