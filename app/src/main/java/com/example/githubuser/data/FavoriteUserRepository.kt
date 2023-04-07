package com.example.githubuser.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.githubuser.BuildConfig
import com.example.githubuser.GithubResponse
import com.example.githubuser.data.local.entity.FavoriteUser
import com.example.githubuser.data.local.room.FavoriteUserDao
import com.example.githubuser.data.remote.retrofit.ApiService
import com.example.githubuser.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteUserRepository private constructor(
    private val apiService: ApiService,
    private val fUserDao : FavoriteUserDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Result<List<FavoriteUser>>>()

    fun getUser(): LiveData<Result<List<FavoriteUser>>>{
        result.value = Result.Loading
        val client = apiService.getUsers(BuildConfig.KEY)
        client.enqueue(object : Callback<GithubResponse>{
            override fun onResponse(
                call: Call<GithubResponse>,
                response: Response<GithubResponse>
            ) {
                if(response.isSuccessful){
                    val items = response.body()?.items
                    val favorites = ArrayList<FavoriteUser>()
                    appExecutors.diskIO.execute{
                        items?.forEach{
                            item ->
                            val isFavorite = fUserDao.isfUserBookmarked(item.login)
                            val favoriteUser = FavoriteUser(
                                item.login,
                                item.avatarUrl,
                                isFavorite
                            )
                            favorites.add(favoriteUser)
                        }
                        fUserDao.deleteAll()
                        fUserDao.insertFavorite(favorites)
                    }
                }
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
        val localData = fUserDao.getAllFavoriteUser()
        result.addSource(localData){
            newData : List<FavoriteUser> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    companion object{
        @Volatile
        private var instance : FavoriteUserRepository? = null
        fun getInstance(
            apiService: ApiService,
            fUserDao: FavoriteUserDao,
            appExecutors: AppExecutors,
        ): FavoriteUserRepository =
            instance?: synchronized(this){
                instance?: FavoriteUserRepository(apiService, fUserDao, appExecutors)
            }.also { instance = it }
    }
}