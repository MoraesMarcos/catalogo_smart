package com.example.myapplication.domain.repository

import com.example.app_catalogo_produto.domain.model.Product
import com.example.app_catalogo_produto.domain.util.RemoteResult

interface ProductRepository {
    suspend fun getProducts(): RemoteResult<List<Product>>
    suspend fun getProductById(id: Int): RemoteResult<Product>
}