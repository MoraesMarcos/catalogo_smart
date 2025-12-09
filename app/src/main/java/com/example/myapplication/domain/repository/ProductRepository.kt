package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Product
import com.example.myapplication.domain.util.RemoteResult

interface ProductRepository {
    suspend fun getProducts(): RemoteResult<List<Product>>
    suspend fun getProductById(id: Int): RemoteResult<Product>
}