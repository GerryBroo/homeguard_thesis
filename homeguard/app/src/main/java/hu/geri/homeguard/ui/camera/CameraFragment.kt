package hu.geri.homeguard.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import hu.geri.homeguard.databinding.FragmentCameraBinding
import hu.geri.homeguard.domain.camera.CameraManager

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraManager: CameraManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val cameraViewModel =
            ViewModelProvider(this).get(CameraViewModel::class.java)

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    private fun initFunction() {
        startCamera()
        controlCameraSelectCamera()
        close()
    }

    private fun startCamera() {
        cameraManager = CameraManager(
            owner = viewLifecycleOwner,
            context = requireContext(),
            viewPreview = binding.pvViewCamera
        )
        cameraManager.startCamera(onFrontCamera = false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraManager.stop()
    }
}