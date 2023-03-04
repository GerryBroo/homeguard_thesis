package hu.geri.homeguard.ui.addface

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hu.geri.homeguard.R
import kotlinx.android.synthetic.main.add_face_dialog.view.*

class AddFaceDialog(
    private val addFaceListener: AddFaceListener
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.add_face_dialog, container, false)

        rootView.addFaceDialogCancelBtn.setOnClickListener {
            dismiss()
            addFaceListener.onDialogCancel()
        }

        rootView.addFaceDialogSubmitBtn.setOnClickListener {
            addFaceListener.onDialogSubmit(rootView.nameEditText.text.toString())
        }

        return rootView
    }

    interface AddFaceListener {
        fun onDialogSubmit(name: String)
        fun onDialogCancel()
    }
}