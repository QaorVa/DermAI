package com.example.dermai.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import com.example.dermai.R
import com.example.dermai.databinding.ActivityCameraBinding
import com.example.dermai.ui.base.BaseActivity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.nio.ByteBuffer
import java.util.concurrent.ExecutionException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraActivity : BaseActivity<ActivityCameraBinding>() {
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT
    private var capturedBitmap: Bitmap? = null

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera(cameraFacing)
        } else {
            activityResultLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    override fun setActions() {
        binding.btFlip.setOnClickListener {
            flipCamera()
        }
    }

    override fun setObservers() {

    }

    private fun startCamera(cameraFacing: Int) {
        val aspectRatio = aspectRatio(binding.cameraView.width, binding.cameraView.height)
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

                binding.btCamera.setOnClickListener {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                    takePicture(imageCapture)
                }

                binding.btFlash.setOnClickListener {
                    setFlashIcon(camera)
                }

                preview.setSurfaceProvider(binding.cameraView.surfaceProvider)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePicture(imageCapture: ImageCapture) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageUsingMediaStore(imageCapture)
            Log.d("CameraActivity", "Using MediaStore")
        } else {
            val file = File(getExternalFilesDir(null), "${System.currentTimeMillis()}.jpg")
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            saveImageToFile(imageCapture, outputFileOptions, file.path)
            Log.d("CameraActivity", "Using File")
        }*/

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                capturedBitmap = imageProxyToBitmap(image)
                image.close()
                runOnUiThread {
                    Toast.makeText(this@CameraActivity, "Image captured successfully", Toast.LENGTH_SHORT).show()
                }
                startCamera(cameraFacing)
            }

            override fun onError(exception: ImageCaptureException) {
                runOnUiThread {
                    Toast.makeText(this@CameraActivity, "Failed to capture image: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
                startCamera(cameraFacing)
            }
        })
    }

    private fun setFlashIcon(camera: Camera) {
        if (camera.cameraInfo.hasFlashUnit()) {
            val torchState = camera.cameraInfo.torchState.value
            if (torchState == TorchState.OFF) {
                camera.cameraControl.enableTorch(true)
                binding.btFlash.setImageResource(R.drawable.flash_off)
            } else {
                camera.cameraControl.enableTorch(false)
                binding.btFlash.setImageResource(R.drawable.flash_on)
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

    private fun flipCamera() {
        cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        startCamera(cameraFacing)
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        // Correct the orientation of the image if needed
        val rotationDegrees = image.imageInfo.rotationDegrees
        return if (rotationDegrees != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotationDegrees.toFloat())
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }
}