package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.app_catalogo_produto.domain.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val price: Double,
    val image: String?,
    val category: String?
)

fun ProductEntity.toDomain() = Product(
    id = id,
    name = name,
    price = price,
    image = image,
    category = category
)

fun Product.toEntity() = ProductEntity(
    id = id,
    name = name,
    price = price,
    image = image,
    category = category
)