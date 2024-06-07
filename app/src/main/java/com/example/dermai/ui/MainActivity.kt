package com.example.dermai.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dermai.R
import com.example.dermai.ui.camera.CameraActivity
import com.example.dermai.ui.details.DetailsActivity
import com.example.dermai.ui.result.ResultActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)*/
    }
}