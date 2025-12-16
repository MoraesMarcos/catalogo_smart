package com.example.myapplication.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Product
import com.example.myapplication.domain.repository.ProductRepository
import com.example.myapplication.domain.util.RemoteResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface ProductDetailUiState {
    object Loading : ProductDetailUiState
    data class Success(val product: Product) : ProductDetailUiState
    data class Error(val message: String) : ProductDetailUiState
}

class ProductDetailViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState

    fun loadProduct(productId: Int) {
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading

            val result = repository.getProductById(productId)

            _uiState.value = when (result) {
                is RemoteResult.Success -> {
                    if (result.data != null) {
                        ProductDetailUiState.Success(result.data)
                    } else {
                        ProductDetailUiState.Error("Produto não encontrado.")
                    }
                }
                is RemoteResult.Error -> {
                    ProductDetailUiState.Error(result.message ?: "Erro desconhecido")
                }
                is RemoteResult.Loading -> {
                    ProductDetailUiState.Loading
                }
            }
        }
    }
}

class ProductDetailViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}