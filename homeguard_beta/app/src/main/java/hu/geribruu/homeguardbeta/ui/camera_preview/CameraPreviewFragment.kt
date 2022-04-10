package hu.geribruu.homeguardbeta.ui.camera_preview

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import hu.geribruu.homeguardbeta.databinding.FragmentCameraPreviewBinding
import hu.geribruu.homeguardbeta.feature.face_recognition.ImageAnalyzer
import java.util.concurrent.ExecutorService
import javax.inject.Inject

@AndroidEntryPoint
class CameraPreviewFragment : Fragment() {

    private var _binding: FragmentCameraPreviewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private val viewModel : CameraPreviewViewModel by viewModels()

    @Inject
    lateinit var imageAnalyzer: ImageAnalyzer
    @Inject
    lateinit var cameraExecutor: ExecutorService



    companion object {
        private const val TAG = "HomeGuardApp"
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraPreviewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                )
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        context?.let { context ->
            ContextCompat.checkSelfPermission(
                context, it
            )
        } == PackageManager.PERMISSION_GRANTED
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
                    context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                activity?.finish()
            }
        }
    }

    /** cameraX **/
    private fun startCamera() {
        Log.d("TAG", "START CAMERA")

        cameraProviderFuture = context?.let { ProcessCameraProvider.getInstance(it) } as ListenableFuture<ProcessCameraProvider>

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            bindPreview(cameraProvider = cameraProvider)

        }, ContextCompat.getMainExecutor(context!!))
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {

        val preview = Preview.Builder().build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        preview.setSurfaceProvider(binding.previewView.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context!!), imageAnalyzer)

        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview, imageAnalysis)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}