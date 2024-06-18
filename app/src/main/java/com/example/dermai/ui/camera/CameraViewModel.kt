package com.example.dermai.ui.camera

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dermai.data.model.BadRequestResponse
import com.example.dermai.data.model.ResultResponse
import com.example.dermai.data.retrofit.ApiConfig
import com.example.dermai.data.room.UserDao
import com.example.dermai.data.room.UserDatabase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CameraViewModel(application: Application): AndroidViewModel(application) {
    val result = MutableLiveData<ResultResponse>()

    private var userDao: UserDao?
    private var userDb: UserDatabase?

    init {
        userDb = UserDatabase.getDatabase(application)
        userDao = userDb?.userDao()
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _networkStatus = MutableLiveData<String>()
    val networkStatus: LiveData<String> = _networkStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun setResult(imageFile: File) {
        _isLoading.value = true

        val requestFile = imageFile.asRequestBody("image/jpg".toMediaType())
        val photoPart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        val client = ApiConfig.getApiService().uploadImage(photoPart)
        client.enqueue(object: retrofit2.Callback<ResultResponse> {
            override fun onResponse(call: retrofit2.Call<ResultResponse>, response: retrofit2.Response<ResultResponse>) {
                _isLoading.value = false
                if(response.isSuccessful) {
                    result.postValue(response.body())
                    _isSuccess.value = true
                } else {
                    try {
                        val errorMessage: String = when (response.code()) {
                            // Handle specific HTTP error codes and their corresponding error messages
                            400 -> "Bad request: ${response.message()}"
                            401 -> "Unauthorized: ${response.message()}"
                            413 -> "Request entity too large: ${response.message()}"
                            // Add more cases as needed
                            else -> response.message() ?: "Unknown error"
                        }
                        _errorMessage.value = errorMessage
                    } catch (e: Exception) {
                        Log.e("Register", "Error parsing error response body: ${e.message}")
                    }
                    _networkStatus.value = response.message()
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

    fun insertResult(result: ResultResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao?.deleteResult()
            userDao?.insertResult(result)
        }
    }
}