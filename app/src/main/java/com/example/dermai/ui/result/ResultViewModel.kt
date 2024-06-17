package com.example.dermai.ui.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dermai.data.model.Product
import com.example.dermai.data.room.UserDao
import com.example.dermai.data.room.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultViewModel(application: Application): AndroidViewModel(application) {
    private var userDao: UserDao?
    private var userDb: UserDatabase?

    init {
        userDb = UserDatabase.getDatabase(application)
        userDao = userDb?.userDao()
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    fun insertWishlist(product: Product) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao?.insertWishlist(product)
        }
    }

    fun deleteWishlist(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao?.deleteWishlist(url)
        }
    }

    fun getAllWishlist(): LiveData<List<Product>> {
        return userDao?.getAllWishlist() ?: MutableLiveData<List<Product>>().apply { value = emptyList() }
    }
}