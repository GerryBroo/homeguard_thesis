package hu.geribruu.homeguardbeta.ui.camera_preview

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Pair
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector
import dagger.hilt.android.AndroidEntryPoint
import hu.geribruu.homeguardbeta.databinding.FragmentCameraPreviewBinding
import hu.geribruu.homeguardbeta.feature.face_recognition.ImageAnalyzer
import hu.geribruu.homeguardbeta.feature.face_recognition.SimilarityClassifier
import hu.geribruu.homeguardbeta.feature.face_recognition.util.getCropBitmapByCPU
import hu.geribruu.homeguardbeta.feature.face_recognition.util.getResizedBitmap
import hu.geribruu.homeguardbeta.feature.face_recognition.util.rotateBitmap
import hu.geribruu.homeguardbeta.feature.face_recognition.util.toBitmap
import hu.geribruu.homeguardbeta.ui.MainActivity
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
//
//    @Inject
//    lateinit var imageAnalyzer: ImageAnalyzer

    @Inject
    lateinit var detector: FaceDetector


    var inputSize = 112 //Input size for model

    var IMAGE_MEAN = 128.0f
    var IMAGE_STD = 128.0f
    var OUTPUT_SIZE = 192 //Output size of model
    var isModelQuantized = false // todo constans
    lateinit var intValues: IntArray  // todo nem v'gom miert lateniat

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    var previewView: PreviewView? = null
    lateinit var face_preview: ImageView
    lateinit var reco_name: TextView
    lateinit var textAbove_preview: TextView
    lateinit var recognize: Button
    lateinit var camera_switch: Button
    lateinit var add_face: ImageButton
    var cameraSelector: CameraSelector? = null
    var distance = 1.0f
    var start = true
    var flipX = false
    var cam_face = CameraSelector.LENS_FACING_BACK //Default Back Camera
    lateinit var embeedings: Array<FloatArray>
    lateinit var cameraProvider: ProcessCameraProvider
    private var registered: HashMap<String?, SimilarityClassifier.Recognition> =
        HashMap<String?, SimilarityClassifier.Recognition>() //saved Faces

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraPreviewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        face_preview = binding.imageView
        reco_name = binding.textView
        textAbove_preview = binding.textAbovePreview
        add_face = binding.imageButton
        add_face.setVisibility(View.INVISIBLE)
        val sharedPref = activity!!.getSharedPreferences("Distance", AppCompatActivity.MODE_PRIVATE)
        distance = sharedPref.getFloat("distance", 1.00f)
        face_preview.setVisibility(View.INVISIBLE)
        recognize = binding.button3
        camera_switch = binding.btnFlipCamera
        textAbove_preview.setText("Recognized Face:")
        //Camera Permission
        if (activity!!.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_REQUEST_CODE)
        }


        //On-screen switch to toggle between Cameras.
        camera_switch.setOnClickListener(View.OnClickListener {
            if (cam_face == CameraSelector.LENS_FACING_BACK) {
                cam_face = CameraSelector.LENS_FACING_FRONT
                flipX = true
            } else {
                cam_face = CameraSelector.LENS_FACING_BACK
                flipX = false
            }
            cameraProvider!!.unbindAll()
            cameraBind()
        })
        add_face.setOnClickListener(View.OnClickListener { addFace() })
        recognize.setOnClickListener(View.OnClickListener {
            if (recognize.getText().toString() == "Recognize") {
                start = true
                textAbove_preview.setText("Recognized Face:")
                recognize.setText("Add Face")
                add_face.setVisibility(View.INVISIBLE)
                reco_name.setVisibility(View.VISIBLE)
                face_preview.setVisibility(View.INVISIBLE)
                //preview_info.setVisibility(View.INVISIBLE);
            } else {
                textAbove_preview.setText("Face Preview: ")
                recognize.setText("Recognize")
                add_face.setVisibility(View.VISIBLE)
                reco_name.setVisibility(View.INVISIBLE)
                face_preview.setVisibility(View.VISIBLE)
            }
        })



        cameraBind()

        return root
    }

    fun recognizeImage(bitmap: Bitmap) {

        // set Face to Preview
        face_preview!!.setImageBitmap(bitmap)

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

                if (distance_local < distance) //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                    reco_name!!.text = name else reco_name!!.text = "Unknown"
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
            ) { dialog, which -> //Toast.makeText(context, input.getText().toString(), Toast.LENGTH_SHORT).show();

                //Create and Initialize new object with Face embeddings and Name.
                val result = SimilarityClassifier.Recognition(
                    "0", "", -1f
                )
                result.extra = embeedings
                registered[input.text.toString()] = result
                start = true
            }
            builder.setNegativeButton(
                "Cancel"
            ) { dialog, which ->
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
        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(cam_face)
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
            val result = detector!!.process(image)
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
                        if (registered.isEmpty()) reco_name!!.text =
                            "Add Face" else reco_name!!.text =
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
            cameraSelector!!, imageAnalysis, preview
        )
    }


    companion object {
        private const val MY_CAMERA_REQUEST_CODE = 100
    }


//    private var _binding: FragmentCameraPreviewBinding? = null
//
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
//
//    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
//    private val viewModel: CameraPreviewViewModel by viewModels()
//
//    @Inject
//    lateinit var imageAnalyzer: ImageAnalyzer
//
//    @Inject
//    lateinit var cameraExecutor: ExecutorService
//
//
//    companion object {
//        private const val TAG = "HomeGuardApp"
//        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
//        private const val REQUEST_CODE_PERMISSIONS = 10
//        private val REQUIRED_PERMISSIONS =
//            mutableListOf(
//                Manifest.permission.CAMERA,
//                Manifest.permission.RECORD_AUDIO
//            ).apply {
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                }
//            }.toTypedArray()
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentCameraPreviewBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        // Request camera permissions
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            activity?.let {
//                ActivityCompat.requestPermissions(
//                    it, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
//                )
//            }
//        }
//
//        return root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        context?.let { context ->
//            ContextCompat.checkSelfPermission(
//                context, it
//            )
//        } == PackageManager.PERMISSION_GRANTED
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String>, grantResults:
//        IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted()) {
//                startCamera()
//            } else {
//                Toast.makeText(
//                    context,
//                    "Permissions not granted by the user.",
//                    Toast.LENGTH_SHORT
//                ).show()
//                activity?.finish()
//            }
//        }
//    }
//
//    /** cameraX **/
//    private fun startCamera() {
//        Log.d("TAG", "START CAMERA")
//
//        cameraProviderFuture =
//            context?.let { ProcessCameraProvider.getInstance(it) } as ListenableFuture<ProcessCameraProvider>
//
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//
//            bindPreview(cameraProvider = cameraProvider)
//
//        }, ContextCompat.getMainExecutor(context!!))
//    }
//
//    @SuppressLint("UnsafeExperimentalUsageError")
//    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
//
//        val preview = Preview.Builder().build()
//
//        val cameraSelector = CameraSelector.Builder()
//            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
//            .build()
//
//        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
//
//        val imageAnalysis = ImageAnalysis.Builder()
//            .setTargetResolution(Size(1280, 720))
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .build()
//
//        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context!!), imageAnalyzer)
//
//        cameraProvider.bindToLifecycle(
//            this as LifecycleOwner,
//            cameraSelector,
//            preview,
//            imageAnalysis
//        )
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        cameraExecutor.shutdown()
//    }
}