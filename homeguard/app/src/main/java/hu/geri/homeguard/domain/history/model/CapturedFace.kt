package hu.geri.homeguard.domain.history.model

import android.os.CountDownTimer

class CapturedFace(
    val name: String
) {
    private var countDownTimer: CountDownTimer? = null
    private var onScreenTime = ON_SCREEN_TIME

    var isOnScreen = false
    var isDetected = false
    var isDetectable = true
    var isDeletable = false

    fun startOnScreenTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(ON_SCREEN_DETECTION_TIME, 1000) {
            override fun onTick(p0: Long) {
                if (isOnScreen) {
                    onScreenTime -= 1000L
                }

                if (onScreenTime <= 0) {
                    isDetected = true
                }
            }

            override fun onFinish() {}
        }.start()
    }

    fun startCaptureTimer() {
        isDetectable = false
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(CAPTURE_TIME, 1000) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                isDeletable = true
            }
        }.start()
    }

    companion object {
        const val ON_SCREEN_DETECTION_TIME = 15000L
        const val ON_SCREEN_TIME = 3000L
        const val CAPTURE_TIME = 120000L
    }
}