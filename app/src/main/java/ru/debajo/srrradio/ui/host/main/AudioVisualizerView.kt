package ru.debajo.srrradio.ui.host.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.LinearGradientShader
import java.lang.System.arraycopy
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow
import ru.debajo.srrradio.common.utils.inject
import ru.debajo.srrradio.media.FFTAudioProcessor
import ru.debajo.srrradio.media.MediaController

/**
 * Adopted copy of https://github.com/dzolnai/ExoVisualizer/blob/master/app/src/main/java/com/egeniq/exovisualizer/FFTBandView.kt
 */
class AudioVisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), FFTAudioProcessor.FFTListener {

    var visualizeEnabled: Boolean = true
        set(value) {
            field = value
            if (value) {
                invalidate()
            }
        }

    private val mediaController: MediaController by inject()

    private val bands: Int = FREQUENCY_BAND_LIMITS.size
    private val size: Int = FFTAudioProcessor.SAMPLE_SIZE / 2
    private val maxConst: Int = 25_000

    private val fft: FloatArray = FloatArray(size)
    private val paintBandsFill: Paint = Paint()

    private val smoothingFactor: Int = 10
    private val previousValues: FloatArray = FloatArray(bands * smoothingFactor)

    private var startedAt: Long = 0

    init {
        paintBandsFill.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        paintBandsFill.shader = LinearGradientShader(
            from = Offset(0f, 0f),
            to = Offset(0f, measuredHeight.toFloat()),
            colors = listOf(
                androidx.compose.ui.graphics.Color.Transparent,
                androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.3f),
            )
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mediaController.addFftListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mediaController.removeFftListener(this)
        startedAt = 0L
    }

    override fun onFFTReady(sampleRateHz: Int, channelCount: Int, fft: FloatArray) {
        if (!visualizeEnabled) {
            return
        }

        synchronized(this.fft) {
            if (startedAt == 0L) {
                startedAt = System.currentTimeMillis()
            }
            arraycopy(fft, 2, this.fft, 0, size)
            invalidate()
        }
    }

    private fun drawAudio(canvas: Canvas) {
        var currentFftPosition = 0
        var currentFrequencyBandLimitIndex = 0
        var currentAverage = 0f

        while (currentFftPosition < size) {
            var accum = 0f
            val nextLimitAtPosition = floor(FREQUENCY_BAND_LIMITS[currentFrequencyBandLimitIndex] / 20_000.toFloat() * size).toInt()

            synchronized(fft) {
                for (j in 0 until (nextLimitAtPosition - currentFftPosition) step 2) {
                    val raw = (fft[currentFftPosition + j].toDouble().pow(2.0) +
                            fft[currentFftPosition + j + 1].toDouble().pow(2.0)).toFloat()
                    val m = bands / 2
                    val windowed = raw * (0.54f - 0.46f * cos(2 * Math.PI * currentFrequencyBandLimitIndex / (m + 1))).toFloat()
                    accum += windowed
                }
            }
            if (nextLimitAtPosition - currentFftPosition != 0) {
                accum /= (nextLimitAtPosition - currentFftPosition)
            } else {
                accum = 0.0f
            }
            currentFftPosition = nextLimitAtPosition

            var smoothedAccum = accum
            for (i in 0 until smoothingFactor) {
                smoothedAccum += previousValues[i * bands + currentFrequencyBandLimitIndex]
                if (i != smoothingFactor - 1) {
                    previousValues[i * bands + currentFrequencyBandLimitIndex] =
                        previousValues[(i + 1) * bands + currentFrequencyBandLimitIndex]
                } else {
                    previousValues[i * bands + currentFrequencyBandLimitIndex] = accum
                }
            }
            smoothedAccum /= (smoothingFactor + 1)
            currentAverage += smoothedAccum / bands

            val leftX = width * (currentFrequencyBandLimitIndex / bands.toFloat())
            val rightX = leftX + width / bands.toFloat()

            val barHeight = (height * (smoothedAccum / maxConst.toDouble()).coerceAtMost(1.0).toFloat())
            val top = height - barHeight

            canvas.drawRect(leftX, top, rightX, height.toFloat(), paintBandsFill)
            currentFrequencyBandLimitIndex++
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (visualizeEnabled) {
            drawAudio(canvas)
            invalidate()
        }
    }

    private companion object {
        private val FREQUENCY_BAND_LIMITS: IntArray = intArrayOf(
            20, 25, 32, 40, 50, 63, 80, 100, 125, 160, 200, 250, 315, 400, 500, 630,
            800, 1000, 1250, 1600, 2000, 2500, 3150, 4000, 5000, 6300, 8000, 10000,
            12500, 16000, 20000
        )
    }
}
