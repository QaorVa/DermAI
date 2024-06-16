package com.example.dermai.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.example.dermai.R
import com.example.dermai.databinding.ActivityCameraBinding
import com.example.dermai.ui.base.BaseActivity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.dermai.data.model.ResultResponse
import com.example.dermai.ui.collection.CollectionActivity
import com.example.dermai.ui.home.HomeActivity
import com.example.dermai.ui.result.ResultActivity
import com.example.dermai.ui.wishlist.WishlistActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.concurrent.ExecutionException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraActivity : BaseActivity<ActivityCameraBinding>() {
    private lateinit var viewModel: CameraViewModel
    private lateinit var result: ResultResponse

    private var cameraFacing = CameraSelector.LENS_FACING_FRONT
    private var capturedBitmap: Bitmap? = null

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result: Boolean ->
        if (result) {
            startCamera(cameraFacing)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data?.data
            selectedImageUri?.let {
                try {
                    val inputStream: InputStream? = contentResolver.openInputStream(it)
                    capturedBitmap = BitmapFactory.decodeStream(inputStream)
                    binding.ivSelectedImage.setImageBitmap(capturedBitmap)
                    cameraElementsVisibility(false)
                    Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getViewBinding(): ActivityCameraBinding {
        return ActivityCameraBinding.inflate(layoutInflater)
    }

    override fun setUI() {
        setupBottomNavigationView()
    }

    override fun setProcess() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera(cameraFacing)
        } else {
            activityResultLauncher.launch(Manifest.permission.CAMERA)
        }

        viewModel = ViewModelProvider(this)[CameraViewModel::class.java]
    }

    override fun setActions() {

        binding.apply {
            btFlip.setOnClickListener {
                flipCamera()
            }
            btGallery.setOnClickListener {
                openGallery()
            }
            btCameraCancel.setOnClickListener {
                cameraElementsVisibility(true)
            }
            btCameraConfirm.setOnClickListener {
                val imageFile = bitmapToFile(capturedBitmap!!, "captured_image.png")
                viewModel.setResult(imageFile)
            }
        }


    }

    override fun setObservers() {
        viewModel.isSuccess.observe(this) { isSuccess ->
            if(isSuccess) {
                moveToResultActivity()
            }
        }
        viewModel.isLoading.observe(this) {
            showLoading(it)
        }
        viewModel.isError.observe(this) { isError ->
            if(isError) {
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show()
            }
        }
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

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                capturedBitmap = imageProxyToBitmap(image)
                cameraElementsVisibility(false)
                image.close()
                runOnUiThread {
                    binding.ivSelectedImage.setImageBitmap(capturedBitmap)
                    cameraElementsVisibility(false)

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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun cameraElementsVisibility(isVisible: Boolean) {

        binding.btCamera.visibility = if (isVisible) VISIBLE else GONE
        binding.cameraView.visibility = if(isVisible) VISIBLE else GONE
        binding.btFlip.visibility = if (isVisible) VISIBLE else GONE
        binding.btFlash.visibility = if (isVisible) VISIBLE else GONE
        binding.btGallery.visibility = if (isVisible) VISIBLE else GONE
        binding.ivScan.visibility = if (isVisible) VISIBLE else GONE

        if(!isVisible) {
            binding.ivSelectedImage.visibility = VISIBLE
            binding.btCameraCancel.visibility = VISIBLE
            binding.btCameraConfirm.visibility = VISIBLE
        } else {
            binding.ivSelectedImage.visibility = GONE
            binding.btCameraCancel.visibility = GONE
            binding.btCameraConfirm.visibility = GONE
        }
    }

    private fun moveToResultActivity() {
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigationView.selectedItemId = R.id.camera
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    // Handle home click
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.camera -> {
                    // Handle camera click
                    true
                }
                R.id.wishlist -> {
                    // Handle collection click
                    val intent = Intent(this, WishlistActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap, fileName: String): File {
        // Create a file to save the image
        val file = File(applicationContext.filesDir, fileName)
        try {
            file.createNewFile()

            // Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapData = bos.toByteArray()

            // Write the byte array to file
            val fos = FileOutputStream(file)
            fos.write(bitmapData)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private fun showLoading(isLoading: Boolean) {
        if(isLoading) {
            binding.progressBar.visibility = VISIBLE
        } else {
            binding.progressBar.visibility = GONE
        }
    }
}