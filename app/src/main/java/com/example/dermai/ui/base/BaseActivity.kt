package com.example.dermai.ui.base

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.example.dermai.R

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(_binding?.root)

        setIntent()
        setUI()
        setAdapter()
        setActions()
        setProcess()
        setObservers()
    }

    abstract fun getViewBinding(): VB

    abstract fun setUI()

    abstract fun setProcess()

    abstract fun setObservers()

    protected open fun setIntent() {}

    protected open fun setAdapter() {}

    protected open fun setActions() {}

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    internal fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    open fun showErrorDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(getString(R.string.error_dialog))
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    open fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    open fun setActivityLabel(label: String) {
        title = label
    }

    open val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("Permission", "Permission granted")
            } else {
                Log.d("Permission", "Permission denied")
            }
        }


    companion object {
        internal const val REQUIRED_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    }
}