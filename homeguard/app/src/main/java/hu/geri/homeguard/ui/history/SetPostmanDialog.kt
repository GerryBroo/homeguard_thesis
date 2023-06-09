package hu.geri.homeguard.ui.history

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hu.geri.homeguard.R
import kotlinx.android.synthetic.main.dialog_set_postman.view.*

class SetPostmanDialog(
    private val listener: SetPostmanListener,
    private val facePreview: Bitmap
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.dialog_set_postman, container, false)

        rootView.facePreviewPostman.setImageBitmap(facePreview)

        rootView.setPostmanDialogCancelBtn.setOnClickListener {
            dismiss()
        }

        rootView.setPostmanDialogSubmitBtn.setOnClickListener {
            listener.onDialogSubmit()
            dismiss()
        }

        return rootView
    }

    interface SetPostmanListener {
        fun onDialogSubmit()
    }
}