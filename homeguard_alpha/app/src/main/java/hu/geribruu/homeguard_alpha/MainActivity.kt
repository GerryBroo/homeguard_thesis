package hu.geribruu.homeguard_alpha

import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.net.Uri
import java.io.File

import android.graphics.Bitmap
import android.media.Image
import hu.geribruu.homeguard_alpha.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import android.R.attr.angle




class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    private var rec: Recognation = Recognation(null, null, null)

    private lateinit var lastCaptureUrl: String

    private val faceBmp = Bitmap.createBitmap(112, 112, Bitmap.Config.ARGB_8888)

    private val TF_OD_API_MODEL_FILE = "mobile_face_net.tflite"
    private val TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt"


    val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        outputFileUri = getOutputDirectory()

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { lastCaptureUrl = takePhoto() }
        viewBinding.imageDialog.setOnClickListener {
            runOnUiThread {
                showDialog()

            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun getOutputDirectory(): String {

        val mediaDir = externalMediaDirs?.firstOrNull().let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir.exists())
            mediaDir.toString() else filesDir.toString()
    }

    fun takePhoto(): String {

        val outputDirectory = File(outputFileUri)

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return ""

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )


        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(applicationContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })

        Log.d("ASD", photoFile.absolutePath)

        return photoFile.absolutePath
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer(this, viewBinding, rec))
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun showDialog() {

        val builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = getLayoutInflater()
        val dialogLayout: View = inflater.inflate(R.layout.image_edit_dialog, null)
        val ivFace = dialogLayout.findViewById<ImageView>(R.id.dlg_image)
        val tvTitle = dialogLayout.findViewById<TextView>(R.id.dlg_title)
        val etName = dialogLayout.findViewById<EditText>(R.id.dlg_input)

        if (rec.bitmap != null) {
            ivFace.setImageBitmap(rec.cropped)
        }



        tvTitle.text = "ivFace is not null"
        etName.hint = "Input name"

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dlg, i ->

        })
        builder.setView(dialogLayout)
        builder.show()
    }

    fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val vuBuffer = planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        lateinit var outputFileUri: String

        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}

class ImageAnalyzer(
    private var context: Context,
    private var binding: ActivityMainBinding,
    private var rec: Recognation
) : ImageAnalysis.Analyzer {

    private val faceDetectorOption = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .enableTracking()
        .build()

    private val faceDetector = FaceDetection.getClient(faceDetectorOption)

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val processImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

            faceDetector.process(processImage)
                .addOnSuccessListener { faces ->

                    for (face in faces) {

                        val bound = face.boundingBox
                        rec.bitmap = bound
                        rec.face = face
                        //showToast(faces.size, face)

                        val targetWidth = 1728
                        val targetHeight = 2304

//                        val previewSize = overlayView.width.toFloat()
//                        val newLeft = if (isFrontCamera) previewSize - (rect.right * scaleX) else rect.left * scaleX
//                        val newRight = if (isFrontCamera) previewSize - (rect.left * scaleX) else rect.right * scaleX

//                        val preview = binding.viewFinder
//
//                        Log.d("ASD", "preview w: ${preview.width}")
//                        Log.d("ASD", "preview h: ${preview.height}")
//
////                        val _scaleY = preview.height.toFloat() / binding.mainlayout.height.toFloat()
////                        val _scaleX = preview.width.toFloat() / binding.mainlayout.width.toFloat()
//
//                        val _scaleY = preview.height.toFloat() / 2304
//                        val _scaleX = preview.width.toFloat() / 1728
//
//                        Log.d("ASD", "scale w: ${_scaleY}")
//                        Log.d("ASD", "scale h: ${_scaleX}")
//
////                        val _scaleY = 112f / preview.height.toFloat()
////                        val _scaleX = 112f / preview.width.toFloat()
//
//                        fun translateX(x: Float): Float = x * _scaleX
//                        fun translateY(y: Float): Float = y * _scaleY
//
//                        fun translateRect(rect: Rect) = RectF(
//                            translateX(rect.left.toFloat()),
//                            translateY(rect.top.toFloat()),
//                            translateX(rect.right.toFloat()),
//                            translateY(rect.bottom.toFloat())
//                        )
//
//                        val rect = translateRect(bound)

                        val bitmap = mediaImage.toBitmap()
//                        var cropped = Bitmap.createBitmap(bitmap, bound.left, bound.top, bound.width(), bound.height())


                        val matrix = Matrix()
                        matrix.postRotate(-90f)
                        val cropped = Bitmap.createBitmap(
                            bitmap, bound.top, bound.left, bound.height(), bound.width(), matrix, true)


                        rec.cropped = cropped


//                        Log.d("ASD", "LEFT: ${rect.left}")
//                        Log.d("ASD", "TOP: ${rect.top}")
//                        Log.d("ASD", "Right: ${rect.right}")
//                        Log.d("ASD", "Bottom: ${rect.bottom}")
                        Log.d("ASD", "====================")
                        Log.d("ASD", "BoundBox Left: ${bound.left}")
                        Log.d("ASD", "BoundBox Top: ${bound.top}")
                        Log.d("ASD", "BoundBox Right: ${bound.right}")
                        Log.d("ASD", "BoundBox Bottom: ${bound.bottom}")
                        Log.d("ASD", "====================")

                        val element = Draw(
                            context = context,
                            rect = bound,
                            text = "Jól nézzel ki!")
                        binding.mainlayout.addView(element,1)

                        val handler = Handler()
                        handler.postDelayed(Runnable { binding.mainlayout.removeViewAt(1) }, 100)

                    }

                }
                .addOnFailureListener {
                    Log.v("MainActivity", "Error - ${it.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val vuBuffer = planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun showToast(faceCount: Int, face: Face) {
        val mToastToShow = Toast.makeText(
            context,
            "Face Count: $faceCount \n TrackingId: ${face.trackingId} \n Smiling: ${face.smilingProbability} ",
            Toast.LENGTH_SHORT
        )
        mToastToShow.show()
        val handler = Handler()
        handler.postDelayed(Runnable { mToastToShow.cancel() }, 100)
    }
}

data class Recognation(
    var bitmap: Rect?,
    var face: Face?,
    var cropped: Bitmap?
)