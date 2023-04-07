package com.example.githubuser

import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.githubuser.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowViewModel: ViewModel() {
    var username = ""

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _followList = MutableLiveData<List<FollowResponseItem>>()
    val followList: LiveData<List<FollowResponseItem>> = _followList

    fun getFollowingList(key: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFollowing(key)

        client.enqueue(object : Callback <List<FollowResponseItem>> {
            override fun onResponse(
                call: Call<List<FollowResponseItem>>,
                response: Response<List<FollowResponseItem>>
            ){
                _isLoading.value = false
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody !=null){
                        _followList.value = response.body()
                    }else{
                        Log.e(TAG,"onFailure: ${response.message()}")
                    }
                }
            }
            override fun onFailure(call: Call<List<FollowResponseItem>>, t: Throwable) {
                Log.e(TAG,"onFailure: ${t.message}")
            }
        }
        )
    }

    fun getFollowerList(key: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFollowers(key)

        client.enqueue(object : Callback <List<FollowResponseItem>> {
            override fun onResponse(
                call: Call<List<FollowResponseItem>>,
                response: Response<List<FollowResponseItem>>
            ){
                _isLoading.value = false
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody !=null){
                        _followList.value = response.body()
                    }else{
                        Log.e(TAG,"onFailure: ${response.message()}")
                    }
                }
            }
            override fun onFailure(call: Call<List<FollowResponseItem>>, t: Throwable) {
                Log.e(TAG,"onFailure: ${t.message}")
            }
        }
        )
    }

    companion object{
        private const val TAG = ".FollowViewModel"
    }

}
