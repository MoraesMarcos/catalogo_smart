package com.example.myapplication.ui.product

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlowimport kotlin.collections.firstOrNull

class ProductDetailViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState

    private var cachedProducts: List<Product> = emptyList()

    fun setProducts(products: List<Product>) {
        cachedProducts = products
    }

    fun loadProduct(productId: Int) {
        _uiState.value = ProductDetailUiState.Loading

        val product = cachedProducts.firstOrNull { it.id == productId }

        _uiState.value = if (product != null) {
            ProductDetailUiState.Success(product)
        } else {
            ProductDetailUiState.Error("Produto não encontrado.")
        }
    }
}