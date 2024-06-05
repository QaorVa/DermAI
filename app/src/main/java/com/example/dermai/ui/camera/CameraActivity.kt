package com.example.dermai.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import com.example.dermai.R
import com.example.dermai.databinding.ActivityCameraBinding
import com.example.dermai.ui.base.BaseActivity
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraActivity : BaseActivity<ActivityCameraBinding>() {
    private lateinit var capture: ImageButton
    private lateinit var toggleFlash: ImageButton
    private lateinit var flipCamera: ImageButton
    private lateinit var previewView: PreviewView
    private var cameraFacing = CameraSelector.LENS_FACING_BACK

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result: Boolean ->
        if (result) {
            startCamera(cameraFacing)
        }
    }

    override fun getViewBinding(): ActivityCameraBinding {
        return ActivityCameraBinding.inflate(layoutInflater)
    }

    override fun setUI() {

    }

    override fun setProcess() {

    }

    override fun setObservers() {

    }

    private fun startCamera(cameraFacing: Int) {
        val aspectRatio = aspectRatio(previewView.width, previewView.height)
        val listenableFuture = ProcessCameraProvider.getInstance(this)

        listenableFuture.addListener({
            try {
                val cameraProvider = listenableFuture.get()
                val preview = Preview.Builder()
                    .setTargetAspectRatio(aspectRatio)
                    .build()

                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetRotation(windowManager.defaultDisplay.rotation)
                    .build()

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraFacing)
                    .build()

                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

                capture.setOnClickListener {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                    takePicture(imageCapture)
                }

                toggleFlash.setOnClickListener {
                    setFlashIcon(camera)
                }

                preview.setSurfaceProvider(previewView.surfaceProvider)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePicture(imageCapture: ImageCapture) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageUsingMediaStore(imageCapture)
        } else {
            val file = File(getExternalFilesDir(null), "${System.currentTimeMillis()}.jpg")
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            saveImageToFile(imageCapture, outputFileOptions, file.path)
        }
    }

    private fun setFlashIcon(camera: Camera) {
        if (camera.cameraInfo.hasFlashUnit()) {
            val torchState = camera.cameraInfo.torchState.value
            if (torchState == TorchState.OFF) {
                camera.cameraControl.enableTorch(true)
                toggleFlash.setImageResource(R.drawable.flash_off)
            } else {
                camera.cameraControl.enableTorch(false)
                toggleFlash.setImageResource(R.drawable.flash_on)
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Flash is not available currently", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        return if (abs(previewRatio - 4.0 / 3.0) <= abs(previewRatio - 16.0 / 9.0)) {
            AspectRatio.RATIO_4_3
        } else {
            AspectRatio.RATIO_16_9
        }
    }

    private fun saveImageUsingMediaStore(imageCapture: ImageCapture) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/CameraXExample")
        }
        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(resolver, uri!!, contentValues).build()

        saveImageToFile(imageCapture, outputFileOptions, uri.toString())
    }

    private fun saveImageToFile(imageCapture: ImageCapture, outputFileOptions: ImageCapture.OutputFileOptions, path: String) {
        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                runOnUiThread {
                    Toast.makeText(this@CameraActivity, "Image saved at: $path", Toast.LENGTH_SHORT).show()
                }
                startCamera(cameraFacing)
            }

            override fun onError(exception: ImageCaptureException) {
                runOnUiThread {
                    Toast.makeText(this@CameraActivity, "Failed to save: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
                startCamera(cameraFacing)
            }
        })
    }
}