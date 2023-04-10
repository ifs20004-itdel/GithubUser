package com.example.githubuser

import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.githubuser.data.FavoriteUserRepository
import com.example.githubuser.data.local.entity.FavoriteUser
import com.example.githubuser.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.githubuser.data.Result

class FollowViewModel(private val favoriteUserRepository: FavoriteUserRepository) : ViewModel() {
    var username = ""

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getFollowingList(key: String) = favoriteUserRepository.getUserFollowing(key)

    fun getFollowerList(key: String) = favoriteUserRepository.getUserFollower(key)

    companion object {
        private const val TAG = ".FollowViewModel"
    }

}
