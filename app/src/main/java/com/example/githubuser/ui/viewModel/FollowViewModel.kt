package com.example.githubuser.ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubuser.FollowResponseItem
import com.example.githubuser.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowViewModel : ViewModel() {
    var username = ""

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _followList = MutableLiveData<List<FollowResponseItem>>()
    val followList: LiveData<List<FollowResponseItem>> = _followList

    fun getFollow(key: String, isFollower: Boolean) {
        _isLoading.value = true
        val client = if (isFollower) ApiConfig.getApiService()
            .getFollowers(key) else ApiConfig.getApiService().getFollowing(key)

        client.enqueue(object : Callback<List<FollowResponseItem>> {
            override fun onResponse(
                call: Call<List<FollowResponseItem>>,
                response: Response<List<FollowResponseItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _followList.value = response.body()
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<List<FollowResponseItem>>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        }
        )
    }

    companion object {
        private const val TAG = ".FollowViewModel"
    }

}
