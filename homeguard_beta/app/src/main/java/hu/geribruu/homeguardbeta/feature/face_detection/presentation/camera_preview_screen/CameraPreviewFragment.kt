package hu.geribruu.homeguardbeta.feature.face_detection.presentation.camera_preview_screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Pair
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector
import dagger.hilt.android.AndroidEntryPoint
import hu.geribruu.homeguardbeta.R
import hu.geribruu.homeguardbeta.databinding.FragmentCameraPreviewBinding
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.FaceCaptureManager
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.ImageAnalyzer
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.SimilarityClassifier
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.util.getCropBitmapByCPU
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.util.getResizedBitmap
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.util.rotateBitmap
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.util.toBitmap
import hu.geribruu.homeguardbeta.ui.MainActivity
import kotlinx.coroutines.flow.collectLatest
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class CameraPreviewFragment : Fragment() {

    private var _binding: FragmentCameraPreviewBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var imageAnalyzer: ImageAnalyzer
    @Inject
    lateinit var faceDetector: FaceDetector
    @Inject
    lateinit var faceCaptureManager: FaceCaptureManager
    @Inject
    lateinit var imageCapture: ImageCapture


    private val viewModel: CameraPreviewViewModel by viewModels()

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var previewView: PreviewView? = null
    private lateinit var facePreview: ImageView
    private lateinit var recoName: TextView
    private lateinit var textAbovePreview: TextView
    private lateinit var recognize: Button
    private lateinit var cameraSwitch: Button
    private lateinit var addFaceBtn: ImageButton

    private var start = true
    private var flipX = false
    private var cameraDirection = CameraSelector.LENS_FACING_FRONT //Default FRONT Camera

    private lateinit var embeedings: Array<FloatArray>
    private lateinit var cameraProvider: ProcessCameraProvider
    private var registered: HashMap<String?, SimilarityClassifier.Recognition> =
        HashMap<String?, SimilarityClassifier.Recognition>() //saved Faces

    var inputSize = 112 //Input size for model

    var IMAGE_MEAN = 128.0f
    var IMAGE_STD = 128.0f
    var OUTPUT_SIZE = 192 //Output size of model

    var isModelQuantized = false
    lateinit var intValues: IntArray

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraPreviewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        facePreview = binding.imageView
        recoName = binding.textView
        textAbovePreview = binding.textAbovePreview
        addFaceBtn = binding.imageButton
        addFaceBtn.visibility = View.INVISIBLE

        facePreview.visibility = View.INVISIBLE
        recognize = binding.button3
        cameraSwitch = binding.btnFlipCamera
        textAbovePreview.text = getString(R.string.camera_recognized_faces)

        //Camera Permission
        if (activity!!.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_REQUEST_CODE)
        }

        //On-screen switch to toggle between Cameras.
        cameraSwitch.setOnClickListener {
            if (cameraDirection == CameraSelector.LENS_FACING_BACK) {
                cameraDirection = CameraSelector.LENS_FACING_FRONT
                flipX = true
            } else {
                cameraDirection = CameraSelector.LENS_FACING_BACK
                flipX = false
            }
            cameraProvider.unbindAll()
            cameraBind()
        }
        addFaceBtn.setOnClickListener { addFace() }
        recognize.setOnClickListener {
            if (recognize.text.toString() == "Recognize") {
                start = true
                textAbovePreview.text = getString(R.string.camera_recognized_faces)
                recognize.text = getString(R.string.camera_add_faces)
                addFaceBtn.visibility = View.INVISIBLE
                recoName.visibility = View.VISIBLE
                facePreview.visibility = View.INVISIBLE
                //preview_info.setVisibility(View.INVISIBLE);
            } else {
                textAbovePreview.text = getString(R.string.camera_recognized_faces)
                recognize.text = getString(R.string.camera_recognize)
                addFaceBtn.visibility = View.VISIBLE
                recoName.visibility = View.INVISIBLE
                facePreview.visibility = View.VISIBLE
            }
        }

//        lifecycleScope.launch {
//            viewModel.uiState.collect { uiState ->
//                if (uiState.previewBitmap != null) {
//                    face_preview.setImageBitmap(uiState.previewBitmap)
//                } else {
//                    Log.d("ASD", "Null a fragmentben")
//                }
//            }
//        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { uiState ->
                if (uiState.previewBitmap != null) {
                    facePreview.setImageBitmap(uiState.previewBitmap)
                } else {
                    Log.d("ASD", "Null a fragmentben")
                }
            }
        }

        cameraBind()

        return root
    }

    private fun addFace() {

        run {
            start = false
            val builder =
                AlertDialog.Builder(context!!)
            builder.setTitle("Enter Name")

            // Set up the input
            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            // Set up the buttons
            builder.setPositiveButton(
                "ADD"
            ) { _, _ -> //Toast.makeText(context, input.getText().toString(), Toast.LENGTH_SHORT).show();

                //Create and Initialize new object with Face embeddings and Name.
                val result = SimilarityClassifier.Recognition(
                    "0", "", -1f
                )
                val name = input.text.toString()

                result.extra = embeedings
                registered[name] = result
                start = true

                faceCaptureManager.manageNewFace(name)
            }
            builder.setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                start = true
                dialog.cancel()
            }
            builder.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "camera permission granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    //Bind camera and preview view
    private fun cameraBind() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context!!)
        previewView = binding.previewView
        cameraProviderFuture!!.addListener({
            try {
                cameraProvider = cameraProviderFuture!!.get()
                bindPreview(cameraProvider)
            } catch (e: ExecutionException) {
                // No errors need to be handled for this in Future.
                // This should never be reached.
            } catch (e: InterruptedException) {
            }
        }, ContextCompat.getMainExecutor(context!!))
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder()
            .build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraDirection)
            .build()
        preview.setSurfaceProvider(previewView!!.surfaceProvider)
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 480))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //Latest frame is shown
            .build()
        val executor: Executor = Executors.newSingleThreadExecutor()
        imageAnalysis.setAnalyzer(executor, { imageProxy ->
            try {
                Thread.sleep(0) //Camera preview refreshed every 10 millisec(adjust as required)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            var image: InputImage? = null
            @SuppressLint("UnsafeExperimentalUsageError") val mediaImage// Camera Feed-->Analyzer-->ImageProxy-->mediaImage-->InputImage(needed for ML kit face detection)
                    = imageProxy.image
            if (mediaImage != null) {
                image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                //                    System.out.println("Rotation "+imageProxy.getImageInfo().getRotationDegrees());
            }

            //                System.out.println("ANALYSIS");

            //Process acquired image to detect faces
            val result = faceDetector!!.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.size != 0) {
                        val face = faces[0] //Get first face from detected faces
                        //                                                    System.out.println(face);

                        //mediaImage to Bitmap
                        val frame_bmp = toBitmap(mediaImage)
                        val rot = imageProxy.imageInfo.rotationDegrees

                        //Adjust orientation of Face
                        val frame_bmp1 =
                            rotateBitmap(frame_bmp, rot, false, false)


                        //Get bounding box of face
                        val boundingBox = RectF(face.boundingBox)

                        //Crop out bounding box from whole Bitmap(image)
                        var cropped_face =
                            getCropBitmapByCPU(frame_bmp1, boundingBox)
                        if (flipX) cropped_face =
                            rotateBitmap(cropped_face, 0, flipX, false)
                        //Scale the acquired Face to 112*112 which is required input for model
                        val scaled = getResizedBitmap(cropped_face, 112, 112)
                        if (start) recognizeImage(scaled) //Send scaled bitmap to create face embeddings.
                        //                                                    System.out.println(boundingBox);
                    } else {
                        if (registered.isEmpty()) recoName.text =
                            "Add Face" else recoName.text =
                            "No Face Detected!"
                    }
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }
                .addOnCompleteListener {
                    imageProxy.close() //v.important to acquire next frame for analysis
                }
        })
        cameraProvider.bindToLifecycle(
            (this as LifecycleOwner),
            cameraSelector!!, imageAnalysis, preview, imageCapture
        )
    }


    /*
    @SuppressLint("UnsafeOptInUsageError")
    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraSelector)
            .build()
        preview.setSurfaceProvider(previewView!!.surfaceProvider)
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 480))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //Latest frame is shown
            .build()
        val executor: Executor = Executors.newSingleThreadExecutor()
        imageAnalysis.setAnalyzer(executor, imageAnalyzer)

        cameraProvider.bindToLifecycle(
            (this as LifecycleOwner),
            cameraSelector, imageAnalysis, preview
        )
    }
    */


    companion object {
        private const val MY_CAMERA_REQUEST_CODE = 100
    }

    fun recognizeImage(bitmap: Bitmap) {

        // set Face to Preview
        facePreview.setImageBitmap(bitmap)

        //Create ByteBuffer to store normalized image
        val imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
        imgData.order(ByteOrder.nativeOrder())
        intValues = IntArray(inputSize * inputSize)

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        imgData.rewind()
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixelValue = intValues[i * inputSize + j]
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((pixelValue shr 16 and 0xFF).toByte())
                    imgData.put((pixelValue shr 8 and 0xFF).toByte())
                    imgData.put((pixelValue and 0xFF).toByte())
                } else { // Float model
                    imgData.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                }
            }
        }
        //imgData is input to our model
        val inputArray = arrayOf<Any>(imgData)
        val outputMap: MutableMap<Int, Any> = HashMap()
        embeedings =
            Array(1) { FloatArray(OUTPUT_SIZE) } //output of model will be stored in this variable
        outputMap[0] = embeedings
        MainActivity.tfLite.runForMultipleInputsOutputs(inputArray, outputMap) //Run model
        var distance_local = Float.MAX_VALUE
        val id = "0"
        val label = "?"

        //Compare new face with saved Faces.
        if (registered.size > 0) {
            val nearest = findNearest(embeedings[0]) //Find 2 closest matching face
            if (nearest[0] != null) {
                val name = nearest[0]!!.first //get name and distance of closest matching face
                // label = name;
                distance_local = nearest[0]!!.second

                if (distance_local < 1.0f) //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                    recoName.text = name else recoName.text = "Unknown"
                //                    System.out.println("nearest: " + name + " - distance: " + distance_local);

            }
        }
    }

    //Compare Faces by distance between face embeddings
    private fun findNearest(emb: FloatArray): List<Pair<String, Float>?> {
        val neighbour_list: MutableList<Pair<String, Float>?> = ArrayList()
        var ret: Pair<String, Float>? = null //to get closest match
        var prev_ret: Pair<String, Float>? = null //to get second closest match
        for ((name, value) in registered) {
            val knownEmb: FloatArray = ((value.extra) as Array<*>)[0] as FloatArray
            var distance = 0f
            for (i in emb.indices) {
                val diff = emb[i] - knownEmb[i]
                distance += diff * diff
            }
            distance = Math.sqrt(distance.toDouble()).toFloat()
            if (ret == null || distance < ret.second) {
                prev_ret = ret
                ret = Pair(name, distance)
            }
        }
        if (prev_ret == null) prev_ret = ret
        neighbour_list.add(ret)
        neighbour_list.add(prev_ret)
        return neighbour_list
    }
}