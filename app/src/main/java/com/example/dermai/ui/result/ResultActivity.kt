package com.example.dermai.ui.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.dermai.R
import com.example.dermai.databinding.ActivityResultBinding
import com.example.dermai.ui.base.BaseActivity

class ResultActivity : BaseActivity<ActivityResultBinding>() {
    override fun getViewBinding(): ActivityResultBinding {
        return ActivityResultBinding.inflate(layoutInflater)
    }

    override fun setUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.chevron_left)
        }
    }

    override fun setProcess() {

    }

    override fun setObservers() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}