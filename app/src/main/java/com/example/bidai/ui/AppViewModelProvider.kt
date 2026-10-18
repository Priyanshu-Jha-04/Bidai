package com.example.bidai.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bidai.BidaiApp
import com.example.bidai.ui.auth.LoginViewModel
import com.example.bidai.ui.auth.SignupViewModel
import com.example.bidai.ui.home.HomeViewModel
import com.example.bidai.ui.items.YourItemsViewModel
import com.example.bidai.ui.sell.OneClickSellViewModel
import com.example.bidai.ui.sell.SellViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { LoginViewModel(container().authRepository) }
        initializer { SignupViewModel(container().authRepository) }
        initializer { HomeViewModel(container().authRepository, container().productRepository) }
        initializer { SellViewModel(container().productRepository) }
        initializer { OneClickSellViewModel(container().productRepository) }
        initializer { YourItemsViewModel(container().productRepository) }
    }

    private fun CreationExtras.container() = (this[APPLICATION_KEY] as BidaiApp).container
}
