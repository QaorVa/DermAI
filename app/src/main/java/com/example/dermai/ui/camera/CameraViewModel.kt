package com.example.dermai.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dermai.data.model.ResultResponse
import com.example.dermai.data.retrofit.ApiConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CameraViewModel: ViewModel() {
    val result = MutableLiveData<ResultResponse>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    fun setResult(imageFile: File) {
        _isLoading.value = true

        val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

        val client = ApiConfig.getApiService().uploadImage(requestFile)
        client.enqueue(object: retrofit2.Callback<ResultResponse> {
            override fun onResponse(call: retrofit2.Call<ResultResponse>, response: retrofit2.Response<ResultResponse>) {
                _isLoading.value = false
                if(response.isSuccessful) {
                    _isSuccess.value = true
                    result.postValue(response.body())
                }
            }

            override fun onFailure(call: retrofit2.Call<ResultResponse>, t: Throwable) {
                _isError.value = true
                _isSuccess.value = false
                _isLoading.value = false
            }
        })
    }

    fun getResult(): LiveData<ResultResponse> {
        return result
    }
}