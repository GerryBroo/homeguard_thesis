package hu.geri.homeguard.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import hu.geri.homeguard.databinding.FragmentCameraBinding
import hu.geri.homeguard.domain.camera.CameraManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private val cameraViewModel: CameraViewModel by viewModel()

    private lateinit var cameraManager: CameraManager
    private var changeCamera = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        initFunctions()

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Toast.makeText(context, "camera permission granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun initFunctions() {
        startCamera()
        controlCameraSelectCamera()
    }

    private fun startCamera() {
        cameraManager = CameraManager(
            owner = viewLifecycleOwner,
            context = requireContext(),
            viewPreview = binding.previewCamera
        )
        cameraManager.startCamera(onFrontCamera = false)
    }

    private fun controlCameraSelectCamera() {
        changeCamera = !changeCamera
        binding.btnFlipCamera.setOnClickListener {
            cameraManager.startCamera(onFrontCamera = changeCamera)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraManager.stop()
    }

    companion object {
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