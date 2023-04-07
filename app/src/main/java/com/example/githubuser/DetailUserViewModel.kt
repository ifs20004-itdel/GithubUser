package com.example.githubuser

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubuser.data.remote.response.DetailResponse
import com.example.githubuser.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserViewModel: ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    fun getUserDetail(user: String, callback: (DetailResponse?)->Unit) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailUser(user)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ){
                _isLoading.value = false
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody !=null)
                    {
                        callback(responseBody)
                    }else{
                        Log.e(TAG,"onFailure: ${response.message()}")
                        callback(null)
                    }
                }
            }
            override fun onFailure(call: Call<DetailResponse>, t:Throwable){
                _isLoading.value = false
                callback(null)
                Log.e(TAG,"onFailure: ${t.message}")
            }
        })
    }
    companion object{
        private const val TAG = ".UserDetailViewModel"
    }
}