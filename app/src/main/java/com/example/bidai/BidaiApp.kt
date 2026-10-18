package com.example.bidai

import android.app.Application
import android.content.Context
import com.example.bidai.data.remote.NetworkModule
import com.example.bidai.data.repository.AuthRepository
import com.example.bidai.data.repository.ProductRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

//container
class AppContainer(context: Context) {
    private val auth = Firebase.auth
    private val api = NetworkModule.createApi(auth)

    val authRepository = AuthRepository(auth, api)
    val productRepository = ProductRepository(api, context.contentResolver)
}

class BidaiApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
