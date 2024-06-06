package com.example.dermai.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dermai.R
import com.example.dermai.ui.camera.CameraActivity
import com.example.dermai.ui.details.DetailsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intent = Intent(this, CameraActivity::class.java)
        /*intent.putExtra(DetailsActivity.EXTRA_SELECT, "acne_level")*/
        startActivity(intent)
    }
}