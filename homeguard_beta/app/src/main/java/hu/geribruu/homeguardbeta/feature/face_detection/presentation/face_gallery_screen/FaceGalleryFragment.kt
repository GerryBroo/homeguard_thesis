package hu.geribruu.homeguardbeta.feature.face_detection.presentation.face_gallery_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import hu.geribruu.homeguardbeta.databinding.FragmentFaceGalleryBinding

class FaceGalleryFragment : Fragment() {

    private lateinit var faceGalleryViewModel: FaceGalleryViewModel
    private var _binding: FragmentFaceGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        faceGalleryViewModel =
            ViewModelProvider(this).get(FaceGalleryViewModel::class.java)

        _binding = FragmentFaceGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        faceGalleryViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}