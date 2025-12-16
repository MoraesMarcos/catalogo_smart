package com.example.myapplication.data.repository

import android.content.Context
import android.util.Log
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.entity.toDomain
import com.example.myapplication.data.local.entity.toEntity
import com.example.myapplication.data.remote.ApiClient
import com.example.myapplication.data.remote.mapper.toDomain
import com.example.myapplication.domain.model.Product
import com.example.myapplication.domain.repository.ProductRepository
import com.example.myapplication.domain.util.RemoteResult
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

class ProductRepositoryImpl(
    context: Context
) : ProductRepository {

    private val api = ApiClient.productApi
    private val dao = AppDatabase.getInstance(context).productDao()

    override suspend fun getProducts(): RemoteResult<List<Product>> {
        return fetchAndSave(
            networkCall = { api.getProducts().map { it.toDomain() } },
            saveToLocal = { products ->
                dao.clearAll()
                dao.insertAll(products.map { it.toEntity() })
            },
            fetchFromLocal = { dao.getAll().map { it.toDomain() } }
        )
    }

    override suspend fun getProductById(id: Int): RemoteResult<Product> {

        val localProduct = dao.getById(id)?.toDomain()

        if (localProduct != null) {
            return RemoteResult.Success(localProduct)
        }

        return try {
            val remoteProduct = api.getProductById(id).toDomain()
            RemoteResult.Success(remoteProduct)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private suspend fun <T> fetchAndSave(
        networkCall: suspend () -> T,
        saveToLocal: suspend (T) -> Unit,
        fetchFromLocal: suspend () -> T
    ): RemoteResult<T> {
        return try {
            val data = networkCall()

            try {
                saveToLocal(data)
            } catch (e: Exception) {
                Log.e("Repository", "Falha ao salvar cache", e)
            }

            RemoteResult.Success(data)

        } catch (e: Exception) {

            try {

                delay(800)

                val localData = fetchFromLocal()

                if (localData is List<*> && localData.isNotEmpty()) {
                    RemoteResult.Success(localData)
                } else if (localData != null && localData !is List<*>) {
                    RemoteResult.Success(localData)
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
            is IOException -> "Sem conexão com a internet. Verifique seu Wifi/Dados."
            is HttpException -> "Erro no servidor (Código: ${e.code()})."
            else -> "Ocorreu um erro inesperado."
        }
        return RemoteResult.Error(message, e)
    }
}