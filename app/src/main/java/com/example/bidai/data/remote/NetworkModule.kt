package com.example.bidai.data.remote

import com.example.bidai.BuildConfig
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//auth interceptor
private class FirebaseTokenInterceptor(private val auth: FirebaseAuth) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        val user = auth.currentUser ?: return chain.proceed(request)
        //token
        val token = runCatching { Tasks.await(user.getIdToken(false), 10, TimeUnit.SECONDS).token }.getOrNull()
            ?: return chain.proceed(request)
        return chain.proceed(request.newBuilder().header("Authorization", "Bearer $token").build())
    }
}

object NetworkModule {
    fun createApi(auth: FirebaseAuth): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            //logging
            level = HttpLoggingInterceptor.Level.BASIC
            redactHeader("Authorization")
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(FirebaseTokenInterceptor(auth))
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
