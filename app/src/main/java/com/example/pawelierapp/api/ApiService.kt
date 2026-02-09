package com.example.pawelierapp.api

import com.example.pawelierapp.model.LoginRequest
import com.example.pawelierapp.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @POST("login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>
}
