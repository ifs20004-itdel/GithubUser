package com.example.githubuser.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubuser.GithubResponse
import com.example.githubuser.ItemsItem
import com.example.githubuser.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {

    private val _user = MutableLiveData<List<ItemsItem>>()
    val user : LiveData<List<ItemsItem>> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    init {
        findUser(USERNAME)
    }

    private fun findUser(key: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUsers(key)
        client.enqueue(object : Callback<GithubResponse> {
            override fun onResponse(
                call: Call<GithubResponse>,
                response: Response<GithubResponse>
            ){
                _isLoading.value = false
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody !=null)
                    {
                        _user.value = response.body()?.items
                    }else{
                        Log.e(TAG,"onFailure: ${response.message()}")
                    }
                }
            }
            override fun onFailure(call: Call<GithubResponse>, t:Throwable){
                _isLoading.value = false
                Log.e(TAG,"onFailure: ${t.message}")
            }
        })
    }
    fun searchUser(key: String){
        findUser(key)
    }

    companion object{
        private const val TAG = ".MainViewModel"
        var USERNAME = "type:username"
    }
}