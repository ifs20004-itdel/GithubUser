package com.example.githubuser.data.remote.retrofit

import com.example.githubuser.data.remote.response.DetailResponse
import com.example.githubuser.FollowResponseItem
import com.example.githubuser.GithubResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("search/users")
    fun getUsers(
        @Query("q") username: String
    ): Call<GithubResponse>

    @GET("users/{username}")
    fun getDetailUser(
        @Path("username") username: String
    ): Call<DetailResponse>

    @GET("users/{username}/followers")
    fun getFollowers(@Path("username") username: String): Call<List<FollowResponseItem>>

    @GET("users/{username}/following")
    fun getFollowing(@Path("username") username: String): Call<List<FollowResponseItem>>
}