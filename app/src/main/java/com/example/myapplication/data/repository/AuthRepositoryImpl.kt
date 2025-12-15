package com.example.myapplication.data.repository

import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.util.RemoteResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    override suspend fun login(email: String, pass: String): RemoteResult<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            RemoteResult.Success(true)
        } catch (e: Exception) {
            RemoteResult.Error(e.message ?: "Erro ao fazer login")
        }
    }

    override suspend fun register(email: String, pass: String): RemoteResult<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, pass).await()
            RemoteResult.Success(true)
        } catch (e: Exception) {
            RemoteResult.Error(e.message ?: "Erro ao registrar")
        }
    }

    override fun logout() = auth.signOut()

    override fun isUserLoggedIn(): Boolean = auth.currentUser != null
}