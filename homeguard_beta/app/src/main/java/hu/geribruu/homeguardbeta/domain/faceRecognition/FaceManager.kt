package hu.geribruu.homeguardbeta.domain.faceRecognition

import android.content.Context
import android.util.Log
import hu.geribruu.homeguardbeta.data.face.disk.FaceDiskDataSource
import hu.geribruu.homeguardbeta.data.history.HistoryRepository
import hu.geribruu.homeguardbeta.domain.faceRecognition.model.RecognizedFace
import hu.geribruu.homeguardbeta.domain.faceRecognition.model.SimilarityClassifier
import hu.geribruu.homeguardbeta.domain.faceRecognition.util.insertToSP
import hu.geribruu.homeguardbeta.domain.history.model.FaceCaptureTempItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class FaceManager @Inject constructor(
    private var context: Context,
    private val photoCapture: PhotoCapture,
    private val faceDiskDataSource: FaceDiskDataSource,
    private val repositoryHistory: HistoryRepository,
) {

    private var detectedFace: MutableList<String> = mutableListOf()
    private var faceCaptureTempItems: MutableList<FaceCaptureTempItem> = mutableListOf()

    fun manageNewFace(
        registered: HashMap<String?, SimilarityClassifier.Recognition>,
        name: String,
    ) {
        insertToSP(context, registered)

        val date = SimpleDateFormat(
            PhotoCapture.FILENAME_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis())
        val url = photoCapture.takePhoto()

        GlobalScope.launch {
            faceDiskDataSource.insertFace(RecognizedFace(0, name, date, url))
        }
    }

    fun manageFace(name: String) {
        val date = SimpleDateFormat(
            PhotoCapture.FILENAME_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis())

        if (name !in detectedFace && name != "Unknown" && name != "No Face Detected!") {
            detectedFace.add(name)
            faceCaptureTempItems.add(FaceCaptureTempItem(name))
            Log.d("asd", "$name is add to detect face")
            faceCaptureTempItems.forEach { face ->
                Log.d("asd", "${face.name} is elemnt of facecaptuere")
            }
        }

        if (name in detectedFace && name != "Unknown" && name != "No Face Detected!") {
            Log.d("asd", "$name start timer in facemanager")

            faceCaptureTempItems.forEach { face ->
                if (face.name == name) {
                    Log.d("asd", "$name set run or stop timer")
                    face.runTimer()
                }
            }
        } else {
            Log.d("asd", "$name stop timer in facemanager")
            faceCaptureTempItems.forEach { face ->
                face.stopTimer()
            }
        }

        faceCaptureTempItems.forEach { face ->
            if (face.isFinished) {
                Log.d("asd", "${face.name} VÉGE VÉGZET")
            }
        }

        // ----------------------

//        if (name !in detectedFace && name != "Unknown" && name != "No Face Detected") {
//            detectedFace[name] = 3000
//
//            object : CountDownTimer(15000, 1000) {
//                override fun onTick(p0: Long) {
//                    Log.d("asd", "$name Remain time: $p0")
//                }
//
//                override fun onFinish() {
//                    Log.d("asd", "$name finished")
//                }
//            }.start()
//        }
//
//        if (name in detectedFace && name != "Unknown" && name != "No Face Detected") {
//
//        }

//        if (name !in detectedFace && name != "Unknown" && name != "No Face Detected") {
//            detectedFace[name] = 3000
//
//            object : CountDownTimer(15000, 1000) {
//                override fun onTick(p0: Long) {
//                    Log.d("asd", "$name Remain time: $p0")
//                    if (name in detectedFace) {
//                        detectedFace[name] = detectedFace[name]?.minus(1000) ?: 3000
//                    }
//
//                    detectedFace.forEach { face ->
//                        Log.d("asd", "${face.key} timer left: ${face.value}")
//                    }
//                }
//
//                override fun onFinish() {
//                    Log.d("asd", "$name finished")
//                }
//            }.start()
//        } else {
//            Log.d("asd", "Delete: $name")
//            detectedFace.remove(name)
//        }

//        detectedFace.forEach { face ->
//            Log.d("asd", "${face.key} timer left: ${face.value}")
//        }

//        if (name !in detectedFace) {
//            detectedFace.put(name, 3000)
//            val asd = object : CountDownTimer(15000, 1000) {
//                override fun onTick(p0: Long) {
//                    Log.d("asd", "$name Remain time: $p0")
//                }
//
//                override fun onFinish() {
//                    Log.d("asd", "$name finished")
//                }
//            }.start()
//        }

//        GlobalScope.launch {
//            repositoryHistory.insertHistory(
//                RoomHistoryItem(0, name, date)
//            )
//        }
    }
}
