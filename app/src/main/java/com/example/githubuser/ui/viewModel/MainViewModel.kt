package com.example.githubuser.ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubuser.GithubResponse
import com.example.githubuser.ItemsItem
import com.example.githubuser.data.UserRepository
import com.example.githubuser.data.local.entity.FavoriteUser
import com.example.githubuser.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val userRepository: UserRepository) :
    ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _user = MutableLiveData<List<ItemsItem>>()
    val user: LiveData<List<ItemsItem>> = _user

    init {
        getUser(USERNAME)
    }

    fun getUser(login: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUsers(login)
        client.enqueue(object : Callback<GithubResponse> {
            override fun onResponse(
                call: Call<GithubResponse>,
                response: Response<GithubResponse>
            ) {
                Log.e("test", "les")
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _user.value = response.body()?.items
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }
            }
            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        }
        )
    }

    fun getFavoriteUser() = userRepository.getFavoriteUser()

    fun searchFavoriteByUsername(user: String) =
        userRepository.searchFavoriteByUsername(user)

    fun saveFUser(fUser: FavoriteUser) {
        userRepository.setFavoriteUser(fUser)
    }

    fun deleteFUser(fUser: FavoriteUser) {
        userRepository.deleteFavoriteUser(fUser)
    }

    companion object {
        private const val TAG = ".MainViewModel"
        var USERNAME = "username"
    }
}