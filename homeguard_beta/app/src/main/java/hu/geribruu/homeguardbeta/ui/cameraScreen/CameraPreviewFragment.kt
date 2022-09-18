package hu.geribruu.homeguardbeta.ui.cameraScreen

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.AndroidEntryPoint
import hu.geribruu.homeguardbeta.databinding.FragmentCameraPreviewBinding
import hu.geribruu.homeguardbeta.domain.faceRecognition.CameraManager

@AndroidEntryPoint
class CameraPreviewFragment : Fragment() {

    companion object {
        private const val MY_CAMERA_REQUEST_CODE = 100
    }

    private var _binding: FragmentCameraPreviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraManager: CameraManager
    private var isLensFacingFront = true

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "camera permission granted", Toast.LENGTH_LONG).show()
                // TODO snack bar create
            } else {
                Toast.makeText(context, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCameraPreviewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initFunction()

        return root
    }

    private fun initFunction() {
        startCamera()
        controlCameraSelectCamera()
    }

    private fun startCamera() {
        cameraManager = CameraManager(
            owner = (this as LifecycleOwner),
            context = requireContext(),
            viewPreview = binding.previewViewCamera,
            recognitionInfo = binding.tvRecognitionInfo,
            facePreview = null
        )
        cameraManager.startCamera(onFrontCamera = true)
    }

    private fun controlCameraSelectCamera() {
        binding.btnFlipCamera.setOnClickListener {
            cameraManager.startCamera(onFrontCamera = changeCamera())
        }
    }

    private fun changeCamera(): Boolean {
        isLensFacingFront = !isLensFacingFront
        return isLensFacingFront
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.stop()
    }
}
