package hu.geri.homeguard.domain.analyzer

import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions


class CustomObjectDetector(tfLiteModel: String) {

    private val localModel: LocalModel = LocalModel.Builder()
        .setAssetFilePath(tfLiteModel)
        .build()

    private val options = CustomObjectDetectorOptions.Builder(localModel)
        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .setClassificationConfidenceThreshold(0.8f)
        .setMaxPerObjectLabelCount(1)
        .build()

    val objectDetector = ObjectDetection.getClient(options)


}

fun customObjectDetector(tfLiteModel: String): ObjectDetector {
    val localModel: LocalModel = LocalModel.Builder()
        .setAssetFilePath(tfLiteModel)
        .build()

    val options = CustomObjectDetectorOptions.Builder(localModel)
        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .setClassificationConfidenceThreshold(0.8f)
        .setMaxPerObjectLabelCount(1)
        .build()

    return ObjectDetection.getClient(options)
}