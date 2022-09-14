package hu.geribruu.homeguardbeta.ui.addNewFaceScreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.AndroidEntryPoint
import hu.geribruu.homeguardbeta.R
import hu.geribruu.homeguardbeta.domain.faceRecognition.CameraManager
import kotlinx.android.synthetic.main.activity_add_new_face.btnAddNewFace
import kotlinx.android.synthetic.main.activity_add_new_face.btnFlipCamera
import kotlinx.android.synthetic.main.activity_add_new_face.facePreview
import kotlinx.android.synthetic.main.activity_add_new_face.previewView
import kotlinx.android.synthetic.main.activity_add_new_face.tvRecognitionInfo

@AndroidEntryPoint
class AddNewFaceActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private var isLensFacingFront = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_face)

        initFunctions()
    }

    private fun initFunctions() {
        startCamera()
        controlCameraSelectCamera()
        controlAddFace()
    }

    private fun startCamera() {
        cameraManager = CameraManager(
            owner = (this as LifecycleOwner),
            context = this,
            viewPreview = previewView,
            recognationName = tvRecognitionInfo,
            facePreview = facePreview
        )
        cameraManager.startCamera(onFrontCamera = true)
    }

    private fun controlCameraSelectCamera() {
        btnFlipCamera.setOnClickListener {
            cameraManager.startCamera(onFrontCamera = changeCamera())
        }
    }

    private fun changeCamera(): Boolean {
        isLensFacingFront = !isLensFacingFront
        return isLensFacingFront
    }

    private fun controlAddFace() {
        btnAddNewFace.setOnClickListener { addFace() }
    }

    private fun addFace() {
        cameraManager.addFace()
    }
}
