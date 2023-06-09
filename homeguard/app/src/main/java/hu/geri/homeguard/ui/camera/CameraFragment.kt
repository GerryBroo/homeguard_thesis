package hu.geri.homeguard.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.geri.homeguard.databinding.FragmentCameraBinding
import hu.geri.homeguard.domain.camera.CameraManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private val cameraViewModel: CameraViewModel by viewModel()

    private lateinit var cameraManager: CameraManager
    private var isLensFacingFront = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        initFunctions()
        setupView()

        return binding.root
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
        cameraManager.startCamera(true)
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

    private fun setupView() {
        cameraViewModel.recognizedObjectText.observe(viewLifecycleOwner) { str ->
            binding.txtRecognizedObject.text = str
        }
        cameraViewModel.recognizedFaceText.observe(viewLifecycleOwner) { str ->
            binding.txtRecognizedFace.text = str
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraManager.stop()
    }
}