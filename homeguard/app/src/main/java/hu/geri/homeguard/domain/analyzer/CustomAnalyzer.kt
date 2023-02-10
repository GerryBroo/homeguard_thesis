package hu.geri.homeguard.domain.analyzer

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.RectF
import android.media.Image
import android.provider.Settings.System.getString
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.objects.DetectedObject
import hu.geri.homeguard.R
import hu.geri.homeguard.domain.analyzer.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder

class CustomAnalyzer(
) : ImageAnalysis.Analyzer {

    private val objectDetector = customObjectDetector("homeguard.tflite")
    private val faceDetector = customFaceDetector()

    val recognizedObject = MutableStateFlow("Undefined")
    val recognizedFace = MutableStateFlow("Undefined")

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val processImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)


            // TODO REFACTOR
            objectDetector.process(processImage)
                .addOnSuccessListener { objects ->
                    processObjects(objects)

                }
                .addOnFailureListener {
                    Log.v("ImageAnalyzer", "Error - ${it.message}")
                }
                .addOnCompleteListener {

                    // TODO I don't know i need this
//                    val planes = mediaImage.planes
//                    if (planes.size >= 3) {
//                        // Reset buffer position for each plane's buffer.
//                        for (plane in planes) {
//                            plane.buffer.rewind()
//                        }
//                    }

                    faceDetector.process(processImage)
                        .addOnSuccessListener { faces ->
                            processFaces(mediaImage, rotationDegrees, faces)
                        }
                        .addOnFailureListener {
                            Log.v("ImageAnalyzer", "Error - ${it.message}")
                        }.addOnCompleteListener {
                            imageProxy.close()
                        }

                }
        }
    }

    private fun processObjects(objects: List<DetectedObject>) {
        for (detectedObject in objects) {
            recognizedObject.value = detectedObject.labels.firstOrNull()?.text ?: "Undefined"
        }
    }


    private fun processFaces(mediaImage: Image, rotationDegree: Int, faces: List<Face>) {
        if(faces.isNotEmpty()) {
            val frameBMP = toBitmap(mediaImage)
            val rotatedFace = rotateBitmap(frameBMP, rotationDegree)

            val face = faces[0]
            val boundingBox = RectF(face.boundingBox)

            val croppedFace = getCropBitmapByCPU(rotatedFace, boundingBox)
            val scaledFace = getResizedBitmap(croppedFace, CROPPED_BITMAP_SIZE, CROPPED_BITMAP_SIZE)

            Log.d("asd", "scaled Face $scaledFace")
            Log.d("asd", "faces size ${faces.size}")
            recognizedFace.value = faces.size.toString()
//            recognizeImage(scaledFace)
        } else {
            recognizedFace.value = "No face detected!"
        }
    }

//    fun recognizeImage(bitmap: Bitmap) {
//
//        // set Face to Preview
//        facePreview?.setImageBitmap(bitmap)
//
//        // Create ByteBuffer to store normalized image
//        val imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
//        imgData.order(ByteOrder.nativeOrder())
//        intValues = IntArray(inputSize * inputSize)
//
//        // get pixel values from Bitmap to normalize
//        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
//        imgData.rewind()
//        for (i in 0 until inputSize) {
//            for (j in 0 until inputSize) {
//                val pixelValue = intValues[i * inputSize + j]
//                if (isModelQuantized) {
//                    // Quantized model
//                    imgData.put((pixelValue shr 16 and 0xFF).toByte())
//                    imgData.put((pixelValue shr 8 and 0xFF).toByte())
//                    imgData.put((pixelValue and 0xFF).toByte())
//                } else { // Float model
//                    imgData.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                    imgData.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                    imgData.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                }
//            }
//        }
//        // imgData is input to our model
//        val inputArray = arrayOf<Any>(imgData)
//        val outputMap: MutableMap<Int, Any> = HashMap()
//        // output of model will be stored in this variable
//        embeedings = Array(1) { FloatArray(OUTPUT_SIZE) }
//        outputMap[0] = embeedings
//        MainActivity.tfLiteFace.runForMultipleInputsOutputs(inputArray, outputMap) // Run model
//
//        var distance_local = Float.MAX_VALUE
//        val id = "0"
//        val label = "?"
//
//        // Compare new face with saved Faces.
//        if (registered.size > 0) {
//            val nearest = findNearest(embeedings[0]) // Find 2 closest matching face
//            if (nearest[0] != null) {
//                val name = nearest[0]!!.first // get name and distance of closest matching face
//                distance_local = nearest[0]!!.second
//
//                if (distance_local < 1.0f) {
//                    // If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
//                    recognitionInfo.text = name
//                } else {
//                    recognitionInfo.text = "Unknown"
//                }
//            }
//        }
//
//        faceManager.manageFace(recognitionInfo.text.toString())
//    }

    companion object {
        const val CROPPED_BITMAP_SIZE = 112
    }
}