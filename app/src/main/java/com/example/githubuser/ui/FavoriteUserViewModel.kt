package com.example.githubuser.ui

import androidx.lifecycle.ViewModel
import com.example.githubuser.data.FavoriteUserRepository

class FavoriteUserViewModel(private val favoriteUserRepository: FavoriteUserRepository): ViewModel() {
    fun getfUser() = favoriteUserRepository.getUser()
}