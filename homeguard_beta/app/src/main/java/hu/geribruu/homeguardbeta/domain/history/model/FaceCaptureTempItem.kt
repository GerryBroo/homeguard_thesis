package hu.geribruu.homeguardbeta.domain.history.model

import android.os.CountDownTimer
import android.util.Log

class FaceCaptureTempItem(
    val name: String,
    var isDetected: Boolean = true,
    private var remainingTime: Long = 5000L,
    private var mainRemainingTime: Long = 15000L,
) {
//    fun startTimer() {
//        isDetected = false
//        if()
//        object : CountDownTimer(mainRemainingTime, 1000) {
//            override fun onTick(p0: Long) {
//            }
//
//            override fun onFinish() {
//                isDetected = true
//            }
//        }.start()
//    }

    private var countDownTimer: CountDownTimer? = null
    private var isCountDown = false
    var isFinished = false

    init {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(mainRemainingTime, 1000) {
            override fun onTick(p0: Long) {
                if (isCountDown) {
                    remainingTime -= 1000L
                }

                if (remainingTime <= 0) {
                    isFinished = true
                }

                Log.d("asd", "$name Remain time: $p0")
                Log.d("asd", "$name time left: $remainingTime")
            }

            override fun onFinish() {
                mainRemainingTime = 15000L
                isFinished = false
                Log.d("asd", "$name finished")
            }
        }.start()
    }

    fun runTimer() {
        isCountDown = true
        Log.d("asd", "$isCountDown is count down")
    }

    fun stopTimer() {
        isCountDown = false
        Log.d("asd", "$isCountDown is count down")
    }
}
