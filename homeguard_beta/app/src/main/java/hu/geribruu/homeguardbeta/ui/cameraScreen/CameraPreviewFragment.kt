package hu.geribruu.homeguardbeta.ui.cameraScreen

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.AndroidEntryPoint
import hu.geribruu.homeguardbeta.R
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

    private lateinit var facePreview: ImageView
    private lateinit var recognationName: TextView
    private lateinit var textAbovePreview: TextView
    private lateinit var recognize: Button
    private lateinit var cameraSwitch: Button
    private lateinit var addFaceBtn: ImageButton

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

    private fun initFunction() {
        initUI()
        startCamera()
        controlCameraSelectCamera()
        controlAddFace()
    }

    private fun initUI() {
        facePreview = binding.imageView
        recognationName = binding.tvRecognitionInfo
        textAbovePreview = binding.textAbovePreview
        addFaceBtn = binding.btnAddFace
        addFaceBtn.visibility = View.INVISIBLE

        facePreview.visibility = View.INVISIBLE
        recognize = binding.button3
        cameraSwitch = binding.btnFlipCamera
        textAbovePreview.text = getString(R.string.camera_recognized_faces)

        recognize.setOnClickListener { // TODO Add face delete and add to faceGallery
            if (recognize.text.toString() == "Recognize") {
                textAbovePreview.text = getString(R.string.camera_recognized_faces)
                recognize.text = getString(R.string.camera_add_faces)
                addFaceBtn.visibility = View.INVISIBLE
                recognationName.visibility = View.VISIBLE
                facePreview.visibility = View.INVISIBLE
            } else {
                textAbovePreview.text = getString(R.string.camera_recognized_faces)
                recognize.text = getString(R.string.camera_recognize)
                addFaceBtn.visibility = View.VISIBLE
                recognationName.visibility = View.INVISIBLE
                facePreview.visibility = View.VISIBLE
            }
        }
    }

    private fun startCamera() {
        cameraManager = CameraManager(
            owner = (this as LifecycleOwner),
            context = context!!,
            viewPreview = binding.previewView,
            recognationName = binding.tvRecognitionInfo,
            facePreview = facePreview
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

    private fun controlAddFace() {
        binding.btnAddFace.setOnClickListener { addFace() }
    }

    private fun addFace() {
        cameraManager.addFace()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.stop()
    }
}
