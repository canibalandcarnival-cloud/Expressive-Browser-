package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Real-time procedural audio synthesis engine and haptic feedback manager.
 * Generates crisp, expressive UI sounds (pops, clicks, swooshes, chords) without external audio assets.
 */
class InteractionFeedback(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)

    var sfxEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    // Pre-synthesized PCM buffers for instant zero-latency playback
    private val clickBuffer: ShortArray by lazy { synthesizeClick(sampleRate = 44100, durationMs = 16, startFreq = 1600.0, endFreq = 900.0) }
    private val popBuffer: ShortArray by lazy { synthesizePop(sampleRate = 44100, durationMs = 36, lowFreq = 260.0, peakFreq = 540.0) }
    private val swooshBuffer: ShortArray by lazy { synthesizeSwoosh(sampleRate = 44100, durationMs = 70) }
    private val chimeBuffer: ShortArray by lazy { synthesizeChimeChord(sampleRate = 44100, durationMs = 120, freqs = doubleArrayOf(523.25, 659.25, 783.99)) }
    private val dingBuffer: ShortArray by lazy { synthesizeDing(sampleRate = 44100, durationMs = 75, freq = 880.0) }

    fun playClick(view: View? = null) {
        if (sfxEnabled) {
            playRawBuffer(clickBuffer, 44100)
        }
        triggerHapticLight(view)
    }

    fun playPop(view: View? = null) {
        if (sfxEnabled) {
            playRawBuffer(popBuffer, 44100)
        }
        triggerHapticMedium(view)
    }

    fun playSwoosh(view: View? = null) {
        if (sfxEnabled) {
            playRawBuffer(swooshBuffer, 44100)
        }
        triggerHapticLight(view)
    }

    fun playTabSwitch(view: View? = null) {
        if (sfxEnabled) {
            playRawBuffer(dingBuffer, 44100)
        }
        triggerHapticMedium(view)
    }

    fun playSuccess(view: View? = null) {
        if (sfxEnabled) {
            playRawBuffer(chimeBuffer, 44100)
        }
        triggerHapticSuccess()
    }

    private fun playRawBuffer(buffer: ShortArray, sampleRate: Int) {
        scope.launch {
            try {
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = maxOf(buffer.size * 2, minBufferSize)
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                
                // Allow completion then release
                val sleepDuration = (buffer.size * 1000L / sampleRate) + 20L
                kotlinx.coroutines.delay(sleepDuration)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {
                // Silently fallback if audio output is unavailable
            }
        }
    }

    // Haptics
    private fun triggerHapticLight(view: View?) {
        if (!hapticsEnabled) return
        try {
            if (view != null && view.isHapticFeedbackEnabled) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator?.hasVibrator() == true) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(10)
            }
        } catch (_: Exception) {}
    }

    private fun triggerHapticMedium(view: View?) {
        if (!hapticsEnabled) return
        try {
            if (view != null && view.isHapticFeedbackEnabled) {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator?.hasVibrator() == true) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(25)
            }
        } catch (_: Exception) {}
    }

    private fun triggerHapticSuccess() {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator?.hasVibrator() == true) {
                val timings = longArrayOf(0, 15, 60, 25)
                val amplitudes = intArrayOf(0, 120, 0, 200)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40)
            }
        } catch (_: Exception) {}
    }

    companion object {
        // Synthesis utilities for warm, pleasant UI micro-sounds
        private fun synthesizeClick(sampleRate: Int, durationMs: Int, startFreq: Double, endFreq: Double): ShortArray {
            val totalSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(totalSamples)
            var phase = 0.0
            for (i in 0 until totalSamples) {
                val t = i.toDouble() / totalSamples
                val currentFreq = startFreq + (endFreq - startFreq) * t
                val deltaPhase = 2.0 * PI * currentFreq / sampleRate
                phase += deltaPhase
                // Fast decaying envelope
                val envelope = (1.0 - t) * (1.0 - t)
                val sampleVal = sin(phase) * envelope * 0.45 * Short.MAX_VALUE
                samples[i] = sampleVal.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            return samples
        }

        private fun synthesizePop(sampleRate: Int, durationMs: Int, lowFreq: Double, peakFreq: Double): ShortArray {
            val totalSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(totalSamples)
            var phase = 0.0
            for (i in 0 until totalSamples) {
                val t = i.toDouble() / totalSamples
                // Smooth parabola frequency curve for a bubbly bounce
                val freq = lowFreq + (peakFreq - lowFreq) * (4.0 * t * (1.0 - t))
                val deltaPhase = 2.0 * PI * freq / sampleRate
                phase += deltaPhase
                // Punchy decay envelope
                val envelope = sin(PI * (1.0 - t)) * (1.0 - t * 0.7)
                val sampleVal = sin(phase) * envelope * 0.55 * Short.MAX_VALUE
                samples[i] = sampleVal.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            return samples
        }

        private fun synthesizeSwoosh(sampleRate: Int, durationMs: Int): ShortArray {
            val totalSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(totalSamples)
            var phase = 0.0
            for (i in 0 until totalSamples) {
                val t = i.toDouble() / totalSamples
                val freq = 450.0 - 280.0 * t
                phase += 2.0 * PI * freq / sampleRate
                val envelope = sin(PI * t) * 0.28
                val sampleVal = sin(phase) * envelope * Short.MAX_VALUE
                samples[i] = sampleVal.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            return samples
        }

        private fun synthesizeDing(sampleRate: Int, durationMs: Int, freq: Double): ShortArray {
            val totalSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(totalSamples)
            for (i in 0 until totalSamples) {
                val t = i.toDouble() / totalSamples
                val envelope = kotlin.math.exp(-5.0 * t)
                val sampleVal = sin(2.0 * PI * freq * i / sampleRate) * envelope * 0.4 * Short.MAX_VALUE
                samples[i] = sampleVal.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            return samples
        }

        private fun synthesizeChimeChord(sampleRate: Int, durationMs: Int, freqs: DoubleArray): ShortArray {
            val totalSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(totalSamples)
            for (i in 0 until totalSamples) {
                val t = i.toDouble() / totalSamples
                val envelope = kotlin.math.exp(-4.0 * t)
                var sum = 0.0
                for (f in freqs) {
                    sum += sin(2.0 * PI * f * i / sampleRate)
                }
                val sampleVal = (sum / freqs.size) * envelope * 0.5 * Short.MAX_VALUE
                samples[i] = sampleVal.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            return samples
        }
    }
}
