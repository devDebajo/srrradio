package ru.debajo.srrradio.ui.host.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.lang.System.arraycopy
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow
import ru.debajo.srrradio.common.utils.inject
import ru.debajo.srrradio.media.FFTAudioProcessor
import ru.debajo.srrradio.media.MediaController

/**
 * Adopted copy of https://github.com/dzolnai/ExoVisualizer/blob/master/app/src/main/java/com/egeniq/exovisualizer/FFTBandView.kt
 *
 * Based on FFTBandView by Pär Amsen:
 * https://github.com/paramsen/noise/blob/master/sample/src/main/java/com/paramsen/noise/sample/view/FFTBandView.kt
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
    private val maxConst: Int = 25_000 // Reference max value for accum magnitude

    private val fft: FloatArray = FloatArray(size)
    private val paintBandsFill: Paint = Paint()

    // We average out the values over 3 occurences (plus the current one), so big jumps are smoothed out
    private val smoothingFactor: Int = 3
    private val previousValues: FloatArray = FloatArray(bands * smoothingFactor)

    private var startedAt: Long = 0

    init {
        paintBandsFill.color = Color.parseColor("#20FFFFFF")
        paintBandsFill.style = Paint.Style.FILL
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mediaController.fftListener = this
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mediaController.fftListener = null
    }

    override fun onFFTReady(sampleRateHz: Int, channelCount: Int, fft: FloatArray) {
        if (!visualizeEnabled) {
            return
        }

        synchronized(this.fft) {
            if (startedAt == 0L) {
                startedAt = System.currentTimeMillis()
            }
            // The resulting graph is mirrored, because we are using real numbers instead of imaginary
            // Explanations: https://www.mathworks.com/matlabcentral/answers/338408-why-are-fft-diagrams-mirrored
            // https://dsp.stackexchange.com/questions/4825/why-is-the-fft-mirrored/4827#4827
            // So what we do here, we only check the left part of the graph.
            arraycopy(fft, 2, this.fft, 0, size)
            // By calling invalidate, we request a redraw.
            invalidate()
        }
    }

    private fun drawAudio(canvas: Canvas) {
        // Clear the previous drawing on the screen
        canvas.drawColor(Color.TRANSPARENT)

        // Set up counters and widgets
        var currentFftPosition = 0
        var currentFrequencyBandLimitIndex = 0
        var currentAverage = 0f

        // Iterate over the entire FFT result array
        while (currentFftPosition < size) {
            var accum = 0f

            // We divide the bands by frequency.
            // Check until which index we need to stop for the current band
            val nextLimitAtPosition =
                floor(FREQUENCY_BAND_LIMITS[currentFrequencyBandLimitIndex] / 20_000.toFloat() * size).toInt()

            synchronized(fft) {
                // Here we iterate within this single band
                for (j in 0 until (nextLimitAtPosition - currentFftPosition) step 2) {
                    // Convert real and imaginary part to get energy
                    val raw = (fft[currentFftPosition + j].toDouble().pow(2.0) +
                            fft[currentFftPosition + j + 1].toDouble().pow(2.0)).toFloat()

                    // Hamming window (by frequency band instead of frequency, otherwise it would prefer 10kHz, which is too high)
                    // The window mutes down the very high and the very low frequencies, usually not hearable by the human ear
                    val m = bands / 2
                    val windowed = raw * (0.54f - 0.46f * cos(2 * Math.PI * currentFrequencyBandLimitIndex / (m + 1))).toFloat()
                    accum += windowed
                }
            }
            // A window might be empty which would result in a 0 division
            if (nextLimitAtPosition - currentFftPosition != 0) {
                accum /= (nextLimitAtPosition - currentFftPosition)
            } else {
                accum = 0.0f
            }
            currentFftPosition = nextLimitAtPosition

            // Here we do the smoothing
            // If you increase the smoothing factor, the high shoots will be toned down, but the
            // 'movement' in general will decrease too
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
            smoothedAccum /= (smoothingFactor + 1) // +1 because it also includes the current value

            // We display the average amplitude with a vertical line
            currentAverage += smoothedAccum / bands


            val leftX = width * (currentFrequencyBandLimitIndex / bands.toFloat())
            val rightX = leftX + width / bands.toFloat()

            val barHeight =
                (height * (smoothedAccum / maxConst.toDouble()).coerceAtMost(1.0).toFloat())
            val top = height - barHeight

            canvas.drawRect(
                leftX,
                top,
                rightX,
                height.toFloat(),
                paintBandsFill
            )

            currentFrequencyBandLimitIndex++
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (visualizeEnabled) {
            drawAudio(canvas)
            // By calling invalidate, we request a redraw. See https://github.com/dzolnai/ExoVisualizer/issues/2
            invalidate()
        }
    }

    private companion object {
        // Taken from: https://en.wikipedia.org/wiki/Preferred_number#Audio_frequencies
        private val FREQUENCY_BAND_LIMITS = arrayOf(
            20, 25, 32, 40, 50, 63, 80, 100, 125, 160, 200, 250, 315, 400, 500, 630,
            800, 1000, 1250, 1600, 2000, 2500, 3150, 4000, 5000, 6300, 8000, 10000,
            12500, 16000, 20000
        )
    }
}
