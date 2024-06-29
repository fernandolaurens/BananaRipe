package com.laurens.bananaripe

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.laurens.bananaripe.ml.Model
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.laurens.utils.GPSTracker
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.*

class DeteksiActivity : AppCompatActivity() {
    private lateinit var result: TextView
    private lateinit var demoText: TextView
    private lateinit var classified: TextView
//    private lateinit var clickHere: TextView
    private lateinit var confidences: TextView
    private lateinit var confidencesText: TextView
    private lateinit var imageView: ImageView
    private lateinit var arrowImage: ImageView
    private lateinit var arrowImage2: ImageView
    private lateinit var camera: LottieAnimationView
    private lateinit var gallery: LottieAnimationView
    private lateinit var tvLocation: TextView
    private lateinit var tvDateTime: TextView
    private lateinit var gpsTracker: GPSTracker
    private val imageSize = 224 // default image size

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deteksi)
        initializeViews()
        setInitialVisibility()
        camera.setOnClickListener { requestCameraPermission() }
        gallery.setOnClickListener { openGallery() }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_detection

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_beranda -> {
                    startActivity(Intent(this@DeteksiActivity, MainActivity::class.java))
                    true
                }
                R.id.bottom_information -> {
                    startActivity(Intent(this@DeteksiActivity, InformasiActivity::class.java))
                    true
                }
                R.id.bottom_profile -> {
                    startActivity(Intent(this@DeteksiActivity, ProfileActivity::class.java))
                    true
                }

                else -> false
            }
        }

        gpsTracker = GPSTracker(this)
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
        private const val REQUEST_CAMERA_IMAGE = 1
        private const val REQUEST_GALLERY_IMAGE = 2
    }

    private fun initializeViews() {
        result = findViewById(R.id.result)
        imageView = findViewById(R.id.imageView)
        gallery = findViewById(R.id.btnGallery)
        confidences = findViewById(R.id.confidences)
        camera = findViewById(R.id.btnCamera)
        demoText = findViewById(R.id.demoText)
//        clickHere = findViewById(R.id.click_here)
        arrowImage = findViewById(R.id.demoArrow)
        arrowImage2 = findViewById(R.id.demoArrow2)
        confidencesText = findViewById(R.id.confidencesText)
        classified = findViewById(R.id.classified)
        tvLocation = findViewById(R.id.tvLocation)
        tvDateTime = findViewById(R.id.tvDateTime)
    }

    private fun setInitialVisibility() {
        demoText.visibility = View.VISIBLE
//        clickHere.visibility = View.GONE
        arrowImage.visibility = View.VISIBLE
        arrowImage2.visibility = View.VISIBLE
        classified.visibility = View.GONE
        result.visibility = View.GONE
        confidencesText.visibility = View.GONE
        confidences.visibility = View.GONE
        tvLocation.visibility = View.GONE
        tvDateTime.visibility = View.GONE
    }

    private fun requestCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    private fun launchCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CAMERA_IMAGE)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_GALLERY_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_IMAGE && data != null) {
                handleCameraImage(data)
                updateVisibility(true)
                updateLocationAndTime()
            } else if (requestCode == REQUEST_GALLERY_IMAGE && data != null) {
                handleGalleryImage(data)
                updateVisibility(false)
            }
        }
    }

    private fun handleCameraImage(data: Intent) {
        val image = data.extras?.get("data") as Bitmap?
        processAndDisplayImage(image)
    }

    private fun handleGalleryImage(data: Intent) {
        val uri = data.data
        var image: Bitmap? = null
        try {
            image = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        processAndDisplayImage(image)
    }

    private fun processAndDisplayImage(image: Bitmap?) {
        image?.let {
            imageView.setImageBitmap(it)
            val scaledImage = Bitmap.createScaledBitmap(it, imageSize, imageSize, false)
            classifyImage(scaledImage)
        }
    }

    private fun classifyImage(image: Bitmap) {
        try {
            val model = Model.newInstance(applicationContext)

            // Create input tensor buffer
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            // Get 1D array of pixels in the image
            val intValue = IntArray(imageSize * imageSize)
            image.getPixels(
                intValue,
                0,
                image.width,
                0,
                0,
                image.width,
                image.height
            )

            // Iterate over pixels and extract R, G, B values and add to byte buffer
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val pixelValue = intValue[i * imageSize + j] // RGB
                    byteBuffer.putFloat((pixelValue shr 16 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((pixelValue shr 8 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((pixelValue and 0xFF) * (1f / 255f))
                }
            }
            inputFeature0.loadBuffer(byteBuffer)

            // Run model inference and get result
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.getOutputFeature0AsTensorBuffer()
            val confidence = outputFeature0.floatArray

            // Find the index of the class with the highest confidence
            var maxPos = 0
            var maxConfidence = 0f
            confidence.forEachIndexed { index, value ->
                if (value > maxConfidence) {
                    maxConfidence = value
                    maxPos = index
                }
            }

            // Define classes
            val classes = arrayOf("Pisang Hijau (Underripe Banana)", "Pisang Kuning (Ripe Banana)", "Pisang Bercak Cokelat (Very Ripe) ", "Pisang yang Sangat Matang (Over Ripe Banana)", "unknown object")

            // Set result text and confidence
            result!!.text = classes[maxPos]
            var s = ""
            for (i in classes.indices) {
                s += String.format("%s: %.1f%%\n", classes[i], confidence[i] * 100)
            }
            confidences!!.text = s

//            // Set click listener to open Google search with result text
//            result.setOnClickListener {
//                startActivity(
//                    Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse("https://www.google.com/search?q=${result.text}")
//                    )
//                )
//            }
            model.close()
        } catch (e: IOException) {
            // Handle the exception
        }
    }

    private fun updateVisibility(fromCamera: Boolean) {
        demoText.visibility = View.GONE
//        clickHere.visibility = View.VISIBLE
        arrowImage.visibility = View.GONE
        arrowImage2.visibility = View.GONE
        classified.visibility = View.VISIBLE
        result.visibility = View.VISIBLE
        confidencesText.visibility = View.VISIBLE
        confidences.visibility = View.VISIBLE
        tvLocation.visibility = if (fromCamera) View.VISIBLE else View.GONE
        tvDateTime.visibility = if (fromCamera) View.VISIBLE else View.GONE
    }

    private fun updateLocationAndTime() {
        val lokasiGambar: String? = gpsTracker.getAddressLine(this)
        tvLocation.text = lokasiGambar ?: "Lokasi tidak tersedia"

        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
        tvDateTime.text = dateFormat.format(currentTime)
    }
}