package com.example.githubuser.data

import androidx.lifecycle.LiveData
import com.example.githubuser.data.local.entity.FavoriteUser
import com.example.githubuser.data.local.room.FavoriteUserDao
import com.example.githubuser.data.remote.retrofit.ApiService
import com.example.githubuser.utils.AppExecutors

class UserRepository private constructor(
    private val apiService: ApiService,
    private val fUserDao: FavoriteUserDao,
    private val appExecutors: AppExecutors
) {
    fun getFavoriteUser(): LiveData<List<FavoriteUser>> {
        return fUserDao.getAllFavoriteUser()
    }

    fun searchFavoriteByUsername(user: String): LiveData<Boolean> {
        return fUserDao.searchFavoriteUserByUsername(user)
    }

    fun setFavoriteUser(user: FavoriteUser) {
        appExecutors.diskIO.execute {
            fUserDao.insert(user)
        }
    }

    fun deleteFavoriteUser(user: FavoriteUser) {
        appExecutors.diskIO.execute {
            fUserDao.delete(user)
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            fUserDao: FavoriteUserDao,
            appExecutors: AppExecutors,
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, fUserDao, appExecutors)
            }.also { instance = it }
    }
}