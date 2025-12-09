package com.example.myapplication.domain.model

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val image: String?,
    val category: String?
)