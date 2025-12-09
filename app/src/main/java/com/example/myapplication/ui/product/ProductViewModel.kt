package com.example.myapplication.ui.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.collections.distinct
import kotlin.collections.filter
import kotlin.collections.mapNotNull
import kotlin.collections.sorted
import kotlin.collections.toMutableSet
import com.example.myapplication.domain.model.Product
import com.example.myapplication.data.repository.ProductRepositoryImpl
import com.example.myapplication.domain.repository.ProductRepository
import com.example.myapplication.domain.util.RemoteResult
import com.example.myapplication.ui.state.ProductUiState

class ProductViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: ProductRepository = ProductRepositoryImpl(application)

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading

            when (val result = repository.getProducts()) {
                is RemoteResult.Success -> {
                    val products = result.data
                    if (products.isNotEmpty()) {
                        _uiState.value = ProductUiState.Success(products)
                    } else {
                        _uiState.value = ProductUiState.Error("Nenhum produto encontrado.")
                    }
                }

                is RemoteResult.Error -> {
                    _uiState.value = ProductUiState.Error(result.message)
                }

                RemoteResult.Loading -> {
                    _uiState.value = ProductUiState.Loading
                }
            }
        }
    }

    fun toggleFavorite(productId: Int) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            current.add(productId)
        }
        _favoriteIds.value = current
    }

    fun getFavoriteProducts(): List<Product> {
        val state = _uiState.value
        val favIds = _favoriteIds.value
        return if (state is ProductUiState.Success) {
            state.products.filter { favIds.contains(it.id) }
        } else {
            emptyList()
        }
    }

    fun getCategories(): List<String> {
        val state = _uiState.value
        return if (state is ProductUiState.Success) {
            state.products.mapNotNull { it.category }.distinct().sorted()
        } else {
            emptyList()
        }
    }

    fun getProductsByCategory(category: String): List<Product> {
        val state = _uiState.value
        return if (state is ProductUiState.Success) {
            state.products.filter { it.category == category }
        } else {
            emptyList()
        }
    }
}
