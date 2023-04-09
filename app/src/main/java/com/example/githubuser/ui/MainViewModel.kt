package com.example.githubuser.ui

import androidx.lifecycle.ViewModel
import com.example.githubuser.data.FavoriteUserRepository
import com.example.githubuser.data.local.entity.FavoriteUser

class MainViewModel(private val favoriteUserRepository: FavoriteUserRepository) :
    ViewModel() {
    fun getUser() = favoriteUserRepository.getUser()

    fun getUserByUsername(username: String) = favoriteUserRepository.getUserByUsername(username)

    fun getFBookmarkedUser() = favoriteUserRepository.getBookmarkedUser()

    fun searchBookmarkedByUsername(user: String) =
        favoriteUserRepository.searchBookmarkedByUsername(user)

    fun saveFUser(fUser: FavoriteUser) {
        favoriteUserRepository.setBookmarkedFUser(fUser, true)
    }

    fun deleteFUser(fUser: FavoriteUser) {
        favoriteUserRepository.setBookmarkedFUser(fUser, false)
    }

}