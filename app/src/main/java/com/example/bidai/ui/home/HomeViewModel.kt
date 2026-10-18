package com.example.bidai.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bidai.data.model.Product
import com.example.bidai.data.remote.AppResult
import com.example.bidai.data.repository.AuthRepository
import com.example.bidai.data.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//brand row
data class ProductSection(
    val category: String,
    val brands: List<String> = emptyList(),
    val selectedBrand: String? = null,
    val products: List<Product> = emptyList(),
)

data class HomeUiState(
    val userName: String = "",
    val location: String = DEFAULT_LOCATION,
    val search: String = "",
    //selected category
    val category: String? = null,
    val cars: ProductSection = ProductSection("Cars"),
    val bikes: ProductSection = ProductSection("Bikes"),
    val recommendations: List<Product> = emptyList(),
    //results
    val results: List<Product> = emptyList(),
    val favourites: Set<Int> = emptySet(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selected: Product? = null,
) {
    val isFiltering get() = search.isNotBlank() || category != null
}

//location
private const val DEFAULT_LOCATION = "Taj Retreat, Bengaluru"

class HomeViewModel(
    private val auth: AuthRepository,
    private val products: ProductRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState(userName = auth.userName))
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun onSearchChange(value: String) {
        _state.update { it.copy(search = value) }
        //debounce
        load(debounceMs = SEARCH_DEBOUNCE_MS)
    }

    fun onCategorySelected(category: String?) {
        _state.update { it.copy(category = if (it.category == category) null else category) }
        load()
    }

    fun clearFilters() {
        _state.update { it.copy(search = "", category = null) }
        load()
    }

    fun onBrandSelected(category: String, brand: String) {
        _state.update { s ->
            fun ProductSection.toggle() = copy(selectedBrand = if (selectedBrand == brand) null else brand)
            when (category) {
                s.cars.category -> s.copy(cars = s.cars.toggle())
                else -> s.copy(bikes = s.bikes.toggle())
            }
        }
        viewModelScope.launch {
            val section = _state.value.let { if (category == it.cars.category) it.cars else it.bikes }
            val result = products.getProducts(category = category, brand = section.selectedBrand, limit = SECTION_LIMIT)
            if (result is AppResult.Success) {
                _state.update { s ->
                    if (category == s.cars.category) s.copy(cars = s.cars.copy(products = result.data))
                    else s.copy(bikes = s.bikes.copy(products = result.data))
                }
            }
        }
    }

    fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        load()
    }

    fun onProductSelected(product: Product?) = _state.update { it.copy(selected = product) }

    fun toggleFavourite(id: Int) = _state.update {
        it.copy(favourites = if (id in it.favourites) it.favourites - id else it.favourites + id)
    }

    fun logout() = auth.logout()

    private fun load(debounceMs: Long = 0) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (debounceMs > 0) delay(debounceMs)
            _state.update { it.copy(isLoading = !it.isRefreshing, error = null) }
            if (_state.value.isFiltering) loadResults() else loadSections()
        }
    }

    private suspend fun loadResults() {
        val s = _state.value
        when (val result = products.getProducts(search = s.search, category = s.category)) {
            is AppResult.Success -> _state.update { it.copy(results = result.data, isLoading = false, isRefreshing = false) }
            is AppResult.Failure -> _state.update { it.copy(isLoading = false, isRefreshing = false, error = result.message) }
        }
    }

    private suspend fun loadSections() = coroutineScope {
        val s = _state.value
        val cars = async { products.getProducts(category = s.cars.category, brand = s.cars.selectedBrand, limit = SECTION_LIMIT) }
        val bikes = async { products.getProducts(category = s.bikes.category, brand = s.bikes.selectedBrand, limit = SECTION_LIMIT) }
        val recommended = async { products.getProducts(limit = RECOMMENDED_LIMIT) }
        val carBrands = async { products.getBrands(s.cars.category) }
        val bikeBrands = async { products.getBrands(s.bikes.category) }

        val results = listOf(cars.await(), bikes.await(), recommended.await())
        val failure = results.firstOrNull { it is AppResult.Failure } as? AppResult.Failure
        _state.update { current ->
            current.copy(
                cars = current.cars.copy(
                    products = (cars.await() as? AppResult.Success)?.data ?: current.cars.products,
                    brands = (carBrands.await() as? AppResult.Success)?.data ?: current.cars.brands,
                ),
                bikes = current.bikes.copy(
                    products = (bikes.await() as? AppResult.Success)?.data ?: current.bikes.products,
                    brands = (bikeBrands.await() as? AppResult.Success)?.data ?: current.bikes.brands,
                ),
                recommendations = (recommended.await() as? AppResult.Success)?.data ?: current.recommendations,
                isLoading = false,
                isRefreshing = false,
                //error
                error = failure?.message?.takeIf { results.all { r -> r is AppResult.Failure } },
            )
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 350L
        const val SECTION_LIMIT = 10
        const val RECOMMENDED_LIMIT = 4
    }
}
