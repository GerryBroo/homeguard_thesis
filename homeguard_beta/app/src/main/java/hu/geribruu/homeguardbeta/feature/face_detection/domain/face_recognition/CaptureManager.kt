package hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition

import android.graphics.Bitmap
import android.util.Log
import javax.inject.Inject

class CaptureManager @Inject constructor() {

    var previewBitmap: Bitmap? = null

    fun manageNewFace(bitmap: Bitmap) {

        previewBitmap = bitmap

        if (previewBitmap == null) {
            Log.d("ASD", "Null m;g mindig capturemaganerfeb")
        }
    }
}