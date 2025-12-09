package com.example.app_catalogo_produto.data.remote.dto

data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val image: String,
    val category: String
)
