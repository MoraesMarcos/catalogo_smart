package com.example.myapplication.ui.state

import com.example.myapplication.domain.model.Product

sealed interface ProductUiState {
    object Loading : ProductUiState
    data class Success(val products: List<Product>) : ProductUiState
    data class Error(val message: String) : ProductUiState
}
