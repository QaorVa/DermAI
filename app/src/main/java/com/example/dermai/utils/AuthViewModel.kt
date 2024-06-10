package com.example.dermai.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.ktx.userProfileChangeRequest

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                } else {
                    _error.value = task.exception?.message
                }
            }
    }

    fun registerWithEmail(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.updateProfile(userProfileChangeRequest {
                        displayName = name
                    })?.addOnCompleteListener {
                        _user.value = auth.currentUser
                    }
                } else {
                    _error.value = task.exception?.message
                }
            }
    }

    fun loginWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                } else {
                    _error.value = task.exception?.message
                }
            }
    }

    fun logout() {
        auth.signOut()
        _user.value = null
    }
}
