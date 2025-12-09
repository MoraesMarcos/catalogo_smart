package com.example.app_catalogo_produto.data.remote.mapper

import com.example.app_catalogo_produto.data.remote.dto.ProductDto
import com.example.app_catalogo_produto.domain.model.Product

fun ProductDto.toDomain() = Product(
    id = id,
    name = title,
    price = price,
    image = image,
    category = category
)
