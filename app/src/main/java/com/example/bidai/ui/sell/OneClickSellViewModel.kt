package com.example.bidai.ui.sell

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bidai.data.model.ProductDraft
import com.example.bidai.data.remote.AppResult
import com.example.bidai.data.repository.ProductRepository
import com.example.bidai.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OneClickSellUiState(
    val title: String = "",
    val price: String = "",
    val description: String = "",
    val category: String = "",
    val condition: String = "used",
    val imageUri: Uri? = null,
    val titleError: String? = null,
    val priceError: String? = null,
    val categoryError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val posted: Boolean = false,
)

class OneClickSellViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _state = MutableStateFlow(OneClickSellUiState())
    val state: StateFlow<OneClickSellUiState> = _state.asStateFlow()

    fun onTitleChange(value: String) = _state.update { it.copy(title = value, titleError = null, error = null) }

    fun onPriceChange(value: String) {
        //price digits
        if (value.count { it == '.' } > 1 || value.any { !it.isDigit() && it != '.' }) return
        _state.update { it.copy(price = value, priceError = null, error = null) }
    }

    fun onDescriptionChange(value: String) = _state.update { it.copy(description = value, error = null) }

    fun onCategoryChange(value: String) = _state.update { it.copy(category = value, categoryError = null, error = null) }

    fun onConditionChange(value: String) = _state.update { it.copy(condition = value) }

    fun onImagePicked(uri: Uri?) {
        if (uri != null) _state.update { it.copy(imageUri = uri) }
    }

    fun onImageRemoved() = _state.update { it.copy(imageUri = null) }

    fun submit() {
        val s = _state.value
        if (s.isLoading) return

        val titleError = Validators.title(s.title)
        val priceError = Validators.price(s.price)
        val categoryError = Validators.category(s.category)
        if (listOfNotNull(titleError, priceError, categoryError).isNotEmpty()) {
            _state.update { it.copy(titleError = titleError, priceError = priceError, categoryError = categoryError) }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val draft = ProductDraft(s.title, s.description, s.price, s.category, s.condition)
            when (val result = repository.createProduct(draft, listOfNotNull(s.imageUri))) {
                is AppResult.Success -> _state.update { it.copy(isLoading = false, posted = true) }
                is AppResult.Failure -> _state.update {
                    it.copy(
                        isLoading = false,
                        titleError = result.fieldErrors["title"],
                        priceError = result.fieldErrors["price"],
                        categoryError = result.fieldErrors["category"],
                        error = result.message,
                    )
                }
            }
        }
    }
}
