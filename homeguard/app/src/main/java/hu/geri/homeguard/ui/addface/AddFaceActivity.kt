package hu.geri.homeguard.ui.addface

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import hu.geri.homeguard.R
import hu.geri.homeguard.domain.camera.CameraManager
import hu.geri.homeguard.ui.camera.CameraViewModel
import kotlinx.android.synthetic.main.activity_add_face.*
import kotlinx.android.synthetic.main.fragment_camera.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddFaceActivity : AppCompatActivity() {

    private val viewModel: AddNewFaceViewModel by viewModel()

    private lateinit var cameraManager: CameraManager
    private var changeCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_face)
        supportActionBar?.hide()

        initFunctions()
        setupView()
    }

    private fun initFunctions() {
        startCamera()
        controlCameraSelectCamera()
        controlAddFace()
    }

    private fun startCamera() {
        cameraManager = CameraManager(
            owner =  (this as LifecycleOwner),
            context = this,
            viewPreview = previewViewActivity
        )
        cameraManager.startCamera(onFrontCamera = false)
    }

    private fun controlCameraSelectCamera() {
        changeCamera = !changeCamera
        btnFlipCameraActivity.setOnClickListener {
            cameraManager.startCamera(onFrontCamera = changeCamera)
        }
    }

    private fun setupView() {
//        cameraViewModel.recognizedObjectText.observe(viewLifecycleOwner) { str ->
//            binding.txtRecognizedObject.text = str
//        }
        viewModel.recognizedFaceText.observe(this) { str ->
            tvRecognitionInfo.text = str
        }
    }

    private fun controlAddFace() {
        btnAddNewFace.setOnClickListener { addFace() }
    }

    private fun addFace() {
        buildAlert()
//        when (viewModel.isNewFaceAvailable()) {
//            is OkFace -> buildAlert()
//            is NoFace -> {
//                Toast.makeText(this, "No face detected!", Toast.LENGTH_SHORT).show()
//            }
//            is ExistingFace -> {
//                Toast.makeText(this, "This face is already existed", Toast.LENGTH_SHORT).show()
//            }
//        }
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
//            viewModel.setNewFace(input.text.toString())
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

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.stop()
    }
}