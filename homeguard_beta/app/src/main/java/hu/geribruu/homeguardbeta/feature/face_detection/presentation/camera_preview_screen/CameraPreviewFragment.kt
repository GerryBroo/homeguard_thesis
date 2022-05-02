package hu.geribruu.homeguardbeta.feature.face_detection.presentation.camera_preview_screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import hu.geribruu.homeguardbeta.R
import hu.geribruu.homeguardbeta.databinding.FragmentCameraPreviewBinding
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.ImageAnalyzer
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.SimilarityClassifier
import kotlinx.coroutines.flow.collectLatest
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
    private var cameraSelector = CameraSelector.LENS_FACING_FRONT //Default FRONT Camera

    private lateinit var embeedings: Array<FloatArray>
    private lateinit var cameraProvider: ProcessCameraProvider
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
            if (cameraSelector == CameraSelector.LENS_FACING_BACK) {
                cameraSelector = CameraSelector.LENS_FACING_FRONT
                flipX = true
            } else {
                cameraSelector = CameraSelector.LENS_FACING_BACK
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
                result.extra = embeedings
                registered[input.text.toString()] = result
                start = true
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

    companion object {
        private const val MY_CAMERA_REQUEST_CODE = 100
    }
}