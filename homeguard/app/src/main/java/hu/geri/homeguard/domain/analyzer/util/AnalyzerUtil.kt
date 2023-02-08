package hu.geri.homeguard.domain.analyzer.util

import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

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