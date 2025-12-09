package com.example.myapplication.data.remote.mapper

import com.example.myapplication.data.remote.dto.ProductDto
import com.example.myapplication.domain.model.Product

fun ProductDto.toDomain() = Product(
    id = id,
    name = title,
    price = price,
    image = image,
    category = category
)
