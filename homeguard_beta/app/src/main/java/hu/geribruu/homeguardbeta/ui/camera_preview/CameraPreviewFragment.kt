package hu.geribruu.homeguardbeta.ui.camera_preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import hu.geribruu.homeguardbeta.databinding.FragmentCameraPreviewBinding

class CameraPreviewFragment : Fragment() {

    private lateinit var cameraPreviewViewModel: CameraPreviewViewModel
    private var _binding: FragmentCameraPreviewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cameraPreviewViewModel =
            ViewModelProvider(this).get(CameraPreviewViewModel::class.java)

        _binding = FragmentCameraPreviewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        cameraPreviewViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}