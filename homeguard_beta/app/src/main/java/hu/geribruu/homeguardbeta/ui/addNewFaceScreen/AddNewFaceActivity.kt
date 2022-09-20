package hu.geribruu.homeguardbeta.ui.addNewFaceScreen

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
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
            recognitionInfo = tvRecognitionInfo,
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
//        cameraManager.isNewFaceAvailable()
//        when (cameraManager.isNewFaceAvailable()) {
//            is OkFace -> buildAlert()
//            is NoFace -> {
//                Toast.makeText(this, "No face detected!", Toast.LENGTH_SHORT).show()
//            }
//            is ExistingFace -> {
//                Toast.makeText(this, "This face is already existed", Toast.LENGTH_SHORT).show()
//            }
//        }
        buildAlert()
    }

    private fun buildAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Name")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton(
            "ADD"
        ) { _, _ ->
            cameraManager.setNewFace(input.text.toString())
            finish()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ ->
            finish()
            dialog.cancel()
        }
        builder.show()
    }
}

sealed class FaceState
object ExistingFace : FaceState()
object NoFace : FaceState()
object OkFace : FaceState()
