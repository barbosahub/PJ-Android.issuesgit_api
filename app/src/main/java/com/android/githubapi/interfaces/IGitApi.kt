package com.android.githubapi.interfaces

import com.android.githubapi.models.GithubApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface IGitApi {
    @Headers("Content-type: application/json")
    @GET("repos/JetBrains/kotlin/issues")
    fun findList(): Call<List<GithubApi>>
}