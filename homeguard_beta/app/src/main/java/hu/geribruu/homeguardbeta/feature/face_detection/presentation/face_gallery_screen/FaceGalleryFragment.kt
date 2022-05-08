package hu.geribruu.homeguardbeta.feature.face_detection.presentation.face_gallery_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hu.geribruu.homeguardbeta.databinding.FragmentFaceGalleryBinding
import hu.geribruu.homeguardbeta.feature.face_detection.presentation.face_gallery_screen.adapter.FaceGalleryAdapter
import kotlinx.android.synthetic.main.fragment_face_gallery.*

@AndroidEntryPoint
class FaceGalleryFragment : Fragment(), FaceGalleryAdapter.FaceClickListener {

    private var _binding: FragmentFaceGalleryBinding? = null
    private val binding get() = _binding!!

    private val viewModel : FaceGalleryViewModel by viewModels()
    private lateinit var adapter : FaceGalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFaceGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = FaceGalleryAdapter(this)
        recyclerview_gallery.adapter = adapter
        recyclerview_gallery.layoutManager = LinearLayoutManager(context)

        activity?.let { activity ->
            viewModel.faces.observe(activity, Observer { birds ->
                birds.let { adapter.submitList(it) }
            } )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(id: Long) {
        return
    }
}