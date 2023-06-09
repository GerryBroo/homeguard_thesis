package hu.geri.homeguard.domain.analyzer.util

import android.graphics.*
import android.media.Image
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import java.io.ByteArrayOutputStream
import java.nio.ReadOnlyBufferException
import kotlin.experimental.inv

// region DETECTORS
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

fun customFaceDetector(): FaceDetector {
    val faceDetectorOption = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .enableTracking()
        .build()

    return FaceDetection.getClient(faceDetectorOption)
}
// endregion

// region FACE RECOGNITION UTILS

// IMPORTANT. If conversion not done ,the toBitmap conversion does not work on some devices.
private fun convertYUV420888toNV21(image: Image?): ByteArray {
    val width = image!!.width
    val height = image.height
    val ySize = width * height
    val uvSize = width * height / 4
    val nv21 = ByteArray(ySize + uvSize * 2)
    val yBuffer = image.planes[0].buffer // Y
    val uBuffer = image.planes[1].buffer // U
    val vBuffer = image.planes[2].buffer // V
    var rowStride = image.planes[0].rowStride
    assert(image.planes[0].pixelStride == 1)
    var pos = 0
    if (rowStride == width) { // likely
        yBuffer[nv21, 0, ySize]
        pos += ySize
    } else {
        var yBufferPos = -rowStride.toLong() // not an actual position
        while (pos < ySize) {
            yBufferPos += rowStride.toLong()
            yBuffer.position(yBufferPos.toInt())
            yBuffer[nv21, pos, width]
            pos += width
        }
    }
    rowStride = image.planes[2].rowStride
    val pixelStride = image.planes[2].pixelStride
    assert(rowStride == image.planes[1].rowStride)
    assert(pixelStride == image.planes[1].pixelStride)
    if (pixelStride == 2 && rowStride == width && uBuffer[0] == vBuffer[1]) {
        // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
        val savePixel = vBuffer[1]
        try {
            vBuffer.put(1, savePixel.inv() as Byte)
            if (uBuffer[0] == savePixel.inv() as Byte) {
                vBuffer.put(1, savePixel)
                vBuffer.position(0)
                uBuffer.position(0)
                vBuffer[nv21, ySize, 1]
                uBuffer[nv21, ySize + 1, uBuffer.remaining()]
                return nv21 // shortcut
            }
        } catch (ex: ReadOnlyBufferException) {
            println(ex)
        }

        vBuffer.put(1, savePixel)
    }

    for (row in 0 until height / 2) {
        for (col in 0 until width / 2) {
            val vuPos = col * pixelStride + row * rowStride
            nv21[pos++] = vBuffer[vuPos]
            nv21[pos++] = uBuffer[vuPos]
        }
    }
    return nv21
}

fun toBitmap(image: Image?): Bitmap {
    val nv21 = convertYUV420888toNV21(image)
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, image!!.width, image.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
    val imageBytes = out.toByteArray()

    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

fun rotateBitmap(
    bitmap: Bitmap?,
    rotationDegrees: Int
): Bitmap {
    val matrix = Matrix()

    // Rotate the image back to straight.
    matrix.postRotate(rotationDegrees.toFloat())

    // Mirror the image along the X or Y axis.
    matrix.postScale(1.0f, 1.0f)
    val rotatedBitmap =
        Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)

    // Recycle the old bitmap if it has changed.
    if (rotatedBitmap != bitmap) {
        bitmap.recycle()
    }
    return rotatedBitmap
}

fun getCropBitmapByCPU(source: Bitmap?, cropRectF: RectF): Bitmap {
    val resultBitmap = Bitmap.createBitmap(
        cropRectF.width().toInt(),
        cropRectF.height().toInt(), Bitmap.Config.ARGB_8888
    )
    val cavas = Canvas(resultBitmap)

    // draw background
    val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    paint.color = Color.WHITE
    cavas.drawRect(
        RectF(0f, 0f, cropRectF.width(), cropRectF.height()),
        paint
    )
    val matrix = Matrix()
    matrix.postTranslate(-cropRectF.left, -cropRectF.top)
    cavas.drawBitmap(source!!, matrix, paint)
    if (!source.isRecycled) {
        source.recycle()
    }
    return resultBitmap
}

fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
    val width = bm.width
    val height = bm.height
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    // CREATE A MATRIX FOR THE MANIPULATION
    val matrix = Matrix()
    // RESIZE THE BIT MAP
    matrix.postScale(scaleWidth, scaleHeight)

    // "RECREATE" THE NEW BITMAP
    val resizedBitmap = Bitmap.createBitmap(
        bm, 0, 0, width, height, matrix, false
    )
    bm.recycle()
    return resizedBitmap
}
// endregion