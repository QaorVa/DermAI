package com.example.dermai.utils

fun String.isEmailCorrect(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}