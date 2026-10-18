package com.example.bidai

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bidai.ui.navigation.BidaiNavHost
import com.example.bidai.ui.navigation.Routes
import com.example.bidai.ui.theme.BidaiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //dark icons
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        val container = (application as BidaiApp).container
        val start = if (container.authRepository.isLoggedIn) Routes.HOME else Routes.LOGIN
        setContent {
            BidaiTheme {
                BidaiNavHost(startDestination = start)
            }
        }
    }
}