package com.example.myapplication.domain.repository

import com.example.myapplication.domain.util.RemoteResult

interface AuthRepository {
    suspend fun login(email: String, pass: String): RemoteResult<Boolean>
    suspend fun register(email: String, pass: String): RemoteResult<Boolean>
    fun logout()
    fun isUserLoggedIn(): Boolean
}

