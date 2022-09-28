package hu.geribruu.homeguardbeta.domain.faceRecognition

import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

class CustomObjectDetector(tflite: String) {

    private val localModel: LocalModel = LocalModel.Builder()
        .setAssetFilePath(tflite)
        .build()
        get() = field

    private val customObjectDetectorOption = CustomObjectDetectorOptions.Builder(localModel)
        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .setClassificationConfidenceThreshold(0.8f)
        .setMaxPerObjectLabelCount(1)
        .build()

    val objectDetector = ObjectDetection.getClient(customObjectDetectorOption)
}
