package com.example.bidai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bidai.ui.auth.LoginScreen
import com.example.bidai.ui.auth.SignupScreen
import com.example.bidai.ui.chat.ChatScreen
import com.example.bidai.ui.components.DockTab
import com.example.bidai.ui.home.HomeScreen
import com.example.bidai.ui.items.YourItemsScreen
import com.example.bidai.ui.sell.OneClickSellScreen
import com.example.bidai.ui.sell.SellScreen

object Routes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val HOME = "home"
    const val SELL = "sell"
    const val ONE_CLICK_SELL = "one_click_sell"
    const val CHAT = "chat"
    const val YOUR_ITEMS = "your_items"

    //refresh key
    const val REFRESH_KEY = "refresh"

    fun of(tab: DockTab) = when (tab) {
        DockTab.Home -> HOME
        DockTab.Sell -> SELL
        DockTab.OneClickSell -> ONE_CLICK_SELL
        DockTab.Chat -> CHAT
        DockTab.YourItems -> YOUR_ITEMS
    }
}

@Composable
fun BidaiNavHost(
    startDestination: String,
    navController: NavHostController = rememberNavController(),
) {
    //auth reset
    fun NavHostController.resetTo(route: String) = navigate(route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }

    //dock tabs
    val onTab: (DockTab) -> Unit = { tab ->
        navController.navigate(Routes.of(tab)) {
            popUpTo(Routes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    //after post
    val onPosted: () -> Unit = {
        runCatching { navController.getBackStackEntry(Routes.HOME).savedStateHandle[Routes.REFRESH_KEY] = true }
        onTab(DockTab.Home)
    }

    NavHost(navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoggedIn = { navController.resetTo(Routes.HOME) },
                onSignupClick = { navController.navigate(Routes.SIGNUP) { launchSingleTop = true } },
            )
        }
        composable(Routes.SIGNUP) {
            SignupScreen(
                onSignedUp = { navController.resetTo(Routes.HOME) },
                onLoginClick = { navController.popBackStack() },
            )
        }
        composable(Routes.HOME) { entry ->
            val refreshRequested by entry.savedStateHandle
                .getStateFlow(Routes.REFRESH_KEY, false)
                .collectAsStateWithLifecycle()
            HomeScreen(
                onTabSelected = onTab,
                onLoggedOut = { navController.resetTo(Routes.LOGIN) },
                refreshRequested = refreshRequested,
                onRefreshHandled = { entry.savedStateHandle[Routes.REFRESH_KEY] = false },
            )
        }
        composable(Routes.SELL) {
            SellScreen(onTabSelected = onTab, onBackHome = { onTab(DockTab.Home) }, onPosted = onPosted)
        }
        composable(Routes.ONE_CLICK_SELL) {
            OneClickSellScreen(onTabSelected = onTab, onPosted = onPosted)
        }
        composable(Routes.CHAT) { ChatScreen(onTabSelected = onTab) }
        composable(Routes.YOUR_ITEMS) { YourItemsScreen(onTabSelected = onTab) }
    }
}
