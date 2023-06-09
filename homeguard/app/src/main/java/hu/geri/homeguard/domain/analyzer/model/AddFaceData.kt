package hu.geri.homeguard.domain.analyzer.model

import android.graphics.Bitmap
import hu.geri.homeguard.data.history.model.HistoryEnum

data class AddFaceData(
    var bitmap: Bitmap? = null,
    var embeedings: Array<FloatArray>? = null,
    var type: HistoryEnum
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddFaceData

        if (bitmap != other.bitmap) return false
        if (embeedings != null) {
            if (other.embeedings == null) return false
            if (!embeedings.contentDeepEquals(other.embeedings)) return false
        } else if (other.embeedings != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bitmap?.hashCode() ?: 0
        result = 31 * result + (embeedings?.contentDeepHashCode() ?: 0)
        return result
    }
}
