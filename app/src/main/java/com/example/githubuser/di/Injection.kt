package com.example.githubuser.di

import android.content.Context
import com.example.githubuser.data.UserRepository
import com.example.githubuser.data.local.room.FavoriteUserRoomDatabase
import com.example.githubuser.data.remote.retrofit.ApiConfig
import com.example.githubuser.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val database = FavoriteUserRoomDatabase.getDatabase(context)
        val dao = database.favoriteUserDao()
        val appExecutors = AppExecutors()
        return UserRepository.getInstance(dao, appExecutors)
    }
}