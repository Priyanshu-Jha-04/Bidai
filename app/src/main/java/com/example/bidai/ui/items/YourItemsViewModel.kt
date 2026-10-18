package com.example.bidai.ui.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bidai.data.model.Product
import com.example.bidai.data.remote.AppResult
import com.example.bidai.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class YourItemsUiState(
    val items: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class YourItemsViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _state = MutableStateFlow(YourItemsUiState())
    val state: StateFlow<YourItemsUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = it.items.isEmpty(), error = null) }
            when (val result = repository.myProducts()) {
                is AppResult.Success -> _state.update { it.copy(items = result.data, isLoading = false) }
                is AppResult.Failure -> _state.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    fun markSold(product: Product) = viewModelScope.launch {
        when (val result = repository.markSold(product.id)) {
            is AppResult.Success -> _state.update { s -> s.copy(items = s.items.map { if (it.id == product.id) result.data else it }) }
            is AppResult.Failure -> _state.update { it.copy(error = result.message) }
        }
    }

    fun delete(product: Product) = viewModelScope.launch {
        when (val result = repository.delete(product.id)) {
            is AppResult.Success -> _state.update { s -> s.copy(items = s.items.filterNot { it.id == product.id }) }
            is AppResult.Failure -> _state.update { it.copy(error = result.message) }
        }
    }
}
