package hu.geri.homeguard.ui.addface

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import hu.geri.homeguard.R
import hu.geri.homeguard.domain.camera.CameraManager
import kotlinx.android.synthetic.main.activity_add_face.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddFaceActivity : AppCompatActivity(), AddFaceDialog.AddFaceListener {

    private val viewModel: AddNewFaceViewModel by viewModel()

    private lateinit var cameraManager: CameraManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_face)
        supportActionBar?.hide()

        initFunctions()
        setupView()
    }

    private fun initFunctions() {
        startCamera()
        controlAddFace()
    }

    private fun startCamera() {
        cameraManager = CameraManager(
            owner =  (this as LifecycleOwner),
            context = this,
            viewPreview = addFacePreview
        )
        cameraManager.startCamera(true)
    }

    private fun setupView() {
        viewModel.recognizedFaceText.observe(this) { str ->
            recognitionInfoText.text = str
        }
    }

    private fun controlAddFace() {
        btnAddNewFace.setOnClickListener { addFace() }
    }

    private fun addFace() {
        showAddFaceDialog()
    }

    private fun showAddFaceDialog() {
        val dialog = AddFaceDialog(this)
        dialog.show(supportFragmentManager, "ADD_FACE_DIALOG")
    }

    override fun onDialogSubmit(name: String) {
        viewModel.setNewFace(name)
        finish()
    }

    override fun onDialogCancel() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.stop()
    }
}