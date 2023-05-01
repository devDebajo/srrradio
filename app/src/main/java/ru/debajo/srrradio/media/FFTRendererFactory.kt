package ru.debajo.srrradio.media

import android.content.Context
import android.os.Handler
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.audio.AudioCapabilities
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.audio.AudioSink
import com.google.android.exoplayer2.audio.DefaultAudioSink
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector

internal class FFTRendererFactory(context: Context) : DefaultRenderersFactory(context), FFTAudioProcessor.FFTListener {

    private val listeners: MutableSet<FFTAudioProcessor.FFTListener> = mutableSetOf()

    fun addListener(listener: FFTAudioProcessor.FFTListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: FFTAudioProcessor.FFTListener) {
        listeners.remove(listener)
    }

    override fun buildAudioRenderers(
        context: Context,
        extensionRendererMode: Int,
        mediaCodecSelector: MediaCodecSelector,
        enableDecoderFallback: Boolean,
        audioSink: AudioSink,
        eventHandler: Handler,
        eventListener: AudioRendererEventListener,
        out: ArrayList<Renderer>
    ) {
        out.add(
            MediaCodecAudioRenderer(
                context,
                mediaCodecSelector,
                enableDecoderFallback,
                eventHandler,
                eventListener,
                DefaultAudioSink.Builder()
                    .setAudioCapabilities(AudioCapabilities.getCapabilities(context))
                    .setAudioProcessors(arrayOf(FFTAudioProcessor().also { it.listener = this }))
                    .build(),
            )
        )

        super.buildAudioRenderers(
            context,
            extensionRendererMode,
            mediaCodecSelector,
            enableDecoderFallback,
            audioSink,
            eventHandler,
            eventListener,
            out
        )
    }

    override fun onFFTReady(sampleRateHz: Int, channelCount: Int, fft: FloatArray) {
        if (listeners.isEmpty()) {
            return
        }

        for (listener in listeners) {
            listener.onFFTReady(sampleRateHz, channelCount, fft)
        }
    }
}
