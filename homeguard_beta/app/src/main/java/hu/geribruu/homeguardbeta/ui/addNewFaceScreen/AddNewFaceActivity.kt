package hu.geribruu.homeguardbeta.ui.addNewFaceScreen

import android.hardware.camera2.CameraManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import hu.geribruu.homeguardbeta.R
import hu.geribruu.homeguardbeta.databinding.ActivityAddNewFaceBinding

@AndroidEntryPoint
class AddNewFaceActivity : AppCompatActivity() {

    private var _binding: ActivityAddNewFaceBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraManager: CameraManager
    private var isLensFacingFront = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_face)
    }
}
