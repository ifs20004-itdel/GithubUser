package com.example.githubuser.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.githubuser.BuildConfig
import com.example.githubuser.FollowResponseItem
import com.example.githubuser.GithubResponse
import com.example.githubuser.data.local.entity.FavoriteUser
import com.example.githubuser.data.local.room.FavoriteUserDao
import com.example.githubuser.data.remote.retrofit.ApiConfig
import com.example.githubuser.data.remote.retrofit.ApiService
import com.example.githubuser.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteUserRepository private constructor(
    private val apiService: ApiService,
    private val fUserDao: FavoriteUserDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Result<List<FavoriteUser>>>()
    private val resultFollowing = MediatorLiveData<Result<List<FavoriteUser>>>()

    fun getUser(): LiveData<Result<List<FavoriteUser>>> {
        result.value = Result.Loading
        val client = apiService.getUsers(USERNAME)
        client.enqueue(object : Callback<GithubResponse> {
            override fun onResponse(
                call: Call<GithubResponse>,
                response: Response<GithubResponse>
            ) {
                if (response.isSuccessful) {
                    val items = response.body()?.items
                    val favorites = ArrayList<FavoriteUser>()
                    appExecutors.diskIO.execute {
                        items?.forEach { item ->
                            val isFavorite = fUserDao.isfUserBookmarked(item.login)
                            val favoriteUser = FavoriteUser(
                                item.login,
                                item.avatarUrl,
                                isFavorite
                            )
                            favorites.add(favoriteUser)
                        }
                        fUserDao.deleteAll()
                        fUserDao.insertList(favorites)
                    }
                }
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })

        val localData = fUserDao.getUser()
        result.addSource(localData) { newData: List<FavoriteUser> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun getUserByUsername(username: String): LiveData<Result<List<FavoriteUser>>> {
        result.value = Result.Loading
        val client = apiService.getUsers(username)
        client.enqueue(object : Callback<GithubResponse> {
            override fun onResponse(
                call: Call<GithubResponse>,
                response: Response<GithubResponse>
            ) {
                if (response.isSuccessful) {
                    val items = response.body()?.items
                    val favorites = ArrayList<FavoriteUser>()
                    appExecutors.diskIO.execute {
                        items?.forEach { item ->
                            val isFavorite = fUserDao.isfUserBookmarked(item.login)
                            val favoriteUser = FavoriteUser(
                                item.login,
                                item.avatarUrl,
                                isFavorite
                            )
                            favorites.add(favoriteUser)
                        }
                        fUserDao.deleteAll()
                        fUserDao.insertList(favorites)
                    }
                }
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })

        val localData = fUserDao.getUserByUsername(username)
        result.addSource(localData) { newData: List<FavoriteUser> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun getUserFollowing(username: String): LiveData<Result<List<FavoriteUser>>> {
        resultFollowing.value = Result.Loading
        val client = apiService.getFollowing(username)
        client.enqueue(object : Callback<List<FollowResponseItem>> {
            override fun onResponse(
                call: Call<List<FollowResponseItem>>,
                response: Response<List<FollowResponseItem>>
            ) {
                if (response.isSuccessful) {
                    val items = response.body()
                    val favorites = ArrayList<FavoriteUser>()
                    appExecutors.diskIO.execute {
                        items?.forEach { item ->
                            val isFavorite = fUserDao.isfUserBookmarked(item.login)
                            val favoriteUser = FavoriteUser(
                                item.login,
                                item.avatarUrl,
                                isFavorite
                            )
                            favorites.add(favoriteUser)
                        }
                        fUserDao.deleteAll()
                        fUserDao.insertList(favorites)
                    }
                }
            }

            override fun onFailure(call: Call<List<FollowResponseItem>>, t: Throwable) {
                resultFollowing.value = Result.Error(t.message.toString())
            }
        })
        val localData = fUserDao.getUser()
        resultFollowing.addSource(localData) { newData: List<FavoriteUser> ->
            resultFollowing.value = Result.Success(newData)
        }
        return resultFollowing
    }

    fun getUserFollower(username: String): LiveData<Result<List<FavoriteUser>>> {
        result.value = Result.Loading
        val client = apiService.getFollowers(username)
        client.enqueue(object : Callback<List<FollowResponseItem>> {
            override fun onResponse(
                call: Call<List<FollowResponseItem>>,
                response: Response<List<FollowResponseItem>>
            ) {
                if (response.isSuccessful) {
                    val items = response.body()
                    val favorites = ArrayList<FavoriteUser>()
                    appExecutors.diskIO.execute {
                        items?.forEach { item ->
                            val isFavorite = fUserDao.isfUserBookmarked(item.login)
                            val favoriteUser = FavoriteUser(
                                item.login,
                                item.avatarUrl,
                                isFavorite
                            )
                            favorites.add(favoriteUser)
                        }
                        fUserDao.deleteAll()
                        fUserDao.insertList(favorites)
                    }
                }
            }

            override fun onFailure(call: Call<List<FollowResponseItem>>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
//        val localData = fUserDao.getUser()
//        result.addSource(localData) { newData: List<FavoriteUser> ->
//            result.value = Result.Success(newData)
//        }
        return result
    }

    fun searchBookmarkedByUsername(user: String): LiveData<Boolean> {
        return fUserDao.searchUserBookmarkedByUsername(user)
    }

    fun getBookmarkedUser(): LiveData<List<FavoriteUser>> {
        return fUserDao.getAllFavoriteUser()
    }

    fun setBookmarkedFUser(user: FavoriteUser, bookmarkState: Boolean) {
        appExecutors.diskIO.execute {
            user.isBookmarked = bookmarkState
            fUserDao.update(user)
        }
    }

    companion object {
        var USERNAME = "type:username"

        @Volatile
        private var instance: FavoriteUserRepository? = null
        fun getInstance(
            apiService: ApiService,
            fUserDao: FavoriteUserDao,
            appExecutors: AppExecutors,
        ): FavoriteUserRepository =
            instance ?: synchronized(this) {
                instance ?: FavoriteUserRepository(apiService, fUserDao, appExecutors)
            }.also { instance = it }
    }
}