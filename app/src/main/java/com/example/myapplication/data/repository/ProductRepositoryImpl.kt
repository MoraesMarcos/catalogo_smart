package com.example.myapplication.data.repository

import android.content.Context
import android.util.Log
import com.example.app_catalogo_produto.data.local.AppDatabase
import com.example.app_catalogo_produto.data.local.entity.toEntity
import com.example.myapplication.data.remote.ApiClient
import com.example.app_catalogo_produto.data.remote.mapper.toDomain
import com.example.app_catalogo_produto.domain.model.Product
import com.example.app_catalogo_produto.domain.repository.ProductRepository
import com.example.app_catalogo_produto.domain.util.RemoteResult
import retrofit2.HttpException
import java.io.IOException

class ProductRepositoryImpl(
    context: Context
) : ProductRepository {

    private val api = ApiClient.productApi
    private val dao = AppDatabase.getInstance(context).productDao()

    override suspend fun getProducts(): RemoteResult<List<Product>> {
        return try {

            val dtoList = api.getProducts()
            val products = dtoList.map { it.toDomain() }

            try {
                dao.clearAll()
                dao.insertAll(products.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e("Repository", "Erro ao salvar cache local", e)
            }

            RemoteResult.Success(products)

        } catch (e: Exception) {
            try {
                val localData = dao.getAll()
                if (localData.isNotEmpty()) {
                    RemoteResult.Success(localData.map { it.toDomain() })
                } else {
                    handleException(e)
                }
            } catch (dbError: Exception) {
                handleException(e)
            }
        }
    }

    override suspend fun getProductById(id: Int): RemoteResult<Product> {
        return try {
            val dto = api.getProductById(id)
            RemoteResult.Success(dto.toDomain())
        } catch (e: Exception) {
            try {
                val local = dao.getById(id)
                if (local != null) {
                    RemoteResult.Success(local.toDomain())
                } else {
                    handleException(e)
                }
            } catch (dbError: Exception) {
                handleException(e)
            }
        }
    }

    private fun <T> handleException(e: Exception): RemoteResult<T> {
        val message = when (e) {
            is IOException -> "Sem internet. Não foi possível carregar os dados."
            is HttpException -> "Erro ${e.code()}. Tente novamente."
            else -> "Erro inesperado. Tente novamente."
        }
        return RemoteResult.Error(message, e)
    }
}