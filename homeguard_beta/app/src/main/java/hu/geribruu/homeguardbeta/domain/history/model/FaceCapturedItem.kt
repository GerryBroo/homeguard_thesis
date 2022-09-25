package hu.geribruu.homeguardbeta.domain.history.model

import android.os.CountDownTimer
import android.util.Log

class FaceCapturedItem(
    val name: String,
    private var remainingTime: Long = 5000L,
    private var countDownTime: Long = 15000L,
) {
    var countDownTimer: CountDownTimer? = null
    var isCounterRunning = false
    private var isCountDown = false
    var isDetected = false
    var isDetectable = true

    fun startTimer() {
        countDownTimer?.cancel()
        isCounterRunning = true
        countDownTimer = object : CountDownTimer(countDownTime, 1000) {
            override fun onTick(p0: Long) {
                if (isCountDown) {
                    remainingTime -= 1000L
                }

                if (remainingTime <= 0) {
                    isDetected = true
                }
            }

            override fun onFinish() {
                reset()
                isCounterRunning = false
            }
        }.start()
    }

    fun runTimer() {
        isCountDown = true
    }

    fun stopTimer() {
        isCountDown = false
    }

    fun reset() {
        countDownTimer?.cancel()
        isCountDown = false
        isDetected = false
        remainingTime = 5000L
    }

    fun startAvailableTimer() {
        isDetectable = false
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(120000L, 1000) {
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                isDetectable = true
            }
        }.start()
    }
}
