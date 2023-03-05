package hu.geri.homeguard.ui.facegallery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import hu.geri.homeguard.databinding.FragmentFaceGalleryBinding
import hu.geri.homeguard.domain.face.model.RecognizedFace
import hu.geri.homeguard.ui.addface.AddFaceActivity
import kotlinx.android.synthetic.main.fragment_face_gallery.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FaceGalleryFragment : Fragment(), FaceGalleryAdapter.FaceClickListener {

    private var _binding: FragmentFaceGalleryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FaceGalleryViewModel by viewModel()
    private lateinit var adapter: FaceGalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFaceGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        btnAddFace.setOnClickListener {
            val intent = Intent(activity, AddFaceActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFaces()
    }

    private fun setupView() {
        adapter = FaceGalleryAdapter(this)
        recyclerview_gallery.adapter = adapter
        recyclerview_gallery.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    uiState.faces.let {
                        adapter.submitList(it)
                    }
                }
            }
        }
    }

    override fun onClick(id: Long) {
        return
    }

    override fun onDelete(face: RecognizedFace) {
        viewModel.deleteFace(face)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}