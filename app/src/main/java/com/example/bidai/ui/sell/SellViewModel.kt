package com.example.bidai.ui.sell

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bidai.data.model.Catalog
import com.example.bidai.data.model.ProductDraft
import com.example.bidai.data.remote.AppResult
import com.example.bidai.data.repository.ProductRepository
import com.example.bidai.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//sell state
enum class SellStep { Category, Details }

data class SellUiState(
    val step: SellStep = SellStep.Category,
    val category: String? = null,
    val photos: List<Uri> = emptyList(),
    val sellerType: String = Catalog.SELLER_VENDOR,
    val brand: String = "",
    val productType: String = "",
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val location: String = "",
    val categoryError: String? = null,
    val photoError: String? = null,
    val titleError: String? = null,
    val priceError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val posted: Boolean = false,
)

class SellViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _state = MutableStateFlow(SellUiState())
    val state: StateFlow<SellUiState> = _state.asStateFlow()

    fun onCategorySelected(name: String) = _state.update { it.copy(category = name, categoryError = null) }

    //photos
    fun canPickPhotos(): Boolean {
        val ok = _state.value.category != null
        if (!ok) _state.update { it.copy(categoryError = "Choose a category first") }
        return ok
    }

    fun onPhotosPicked(uris: List<Uri>) {
        if (uris.isEmpty()) return
        _state.update {
            val photos = (it.photos + uris).distinct().take(Catalog.MAX_PHOTOS)
            it.copy(photos = photos, photoError = null, error = null, step = SellStep.Details)
        }
    }

    fun onPhotoRemoved(uri: Uri) = _state.update { it.copy(photos = it.photos - uri) }

    //back
    fun onBack(): Boolean {
        if (_state.value.step == SellStep.Category) return false
        _state.update { it.copy(step = SellStep.Category) }
        return true
    }

    //reset
    fun reset() { _state.value = SellUiState() }

    fun onSellerTypeChange(value: String) = _state.update { it.copy(sellerType = value) }
    fun onBrandChange(value: String) = _state.update { it.copy(brand = value) }
    fun onProductTypeChange(value: String) = _state.update { it.copy(productType = value) }
    fun onTitleChange(value: String) = _state.update { it.copy(title = value, titleError = null, error = null) }
    fun onDescriptionChange(value: String) = _state.update { it.copy(description = value) }
    fun onLocationChange(value: String) = _state.update { it.copy(location = value) }

    fun onPriceChange(value: String) {
        //price digits
        if (value.count { it == '.' } > 1 || value.any { !it.isDigit() && it != '.' }) return
        _state.update { it.copy(price = value, priceError = null, error = null) }
    }

    fun submit() {
        val s = _state.value
        val category = s.category
        if (s.isLoading || category == null) return

        val photoError = if (s.photos.size < Catalog.MIN_PHOTOS) "Add at least ${Catalog.MIN_PHOTOS} photos" else null
        val titleError = Validators.title(s.title)
        val priceError = Validators.price(s.price)
        if (listOfNotNull(photoError, titleError, priceError).isNotEmpty()) {
            _state.update { it.copy(photoError = photoError, titleError = titleError, priceError = priceError) }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val draft = ProductDraft(
                title = s.title,
                description = s.description,
                price = s.price,
                category = category,
                brand = s.brand,
                productType = s.productType,
                sellerType = s.sellerType,
                location = s.location,
            )
            when (val result = repository.createProduct(draft, s.photos)) {
                is AppResult.Success -> _state.update { it.copy(isLoading = false, posted = true) }
                is AppResult.Failure -> _state.update {
                    it.copy(
                        isLoading = false,
                        titleError = result.fieldErrors["title"],
                        priceError = result.fieldErrors["price"],
                        error = result.message,
                    )
                }
            }
        }
    }
}
