package com.example.androidautoplayground

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.annotation.RawRes
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.IOException
import java.util.*
import kotlin.collections.HashSet

@SuppressLint("NewApi")
class PlaygroundScreen(carContext: CarContext) : Screen(carContext),
    DefaultLifecycleObserver,
    TextToSpeech.OnInitListener {

    private val audioManager by lazy { carContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private var tts: TextToSpeech? = null

    private val activeUtteranceIds = HashSet<String>()

    private val audioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
        .build()

    private val audioFocusRequest: AudioFocusRequest =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(audioAttributes)
            .build()

    init {
        lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        tts = TextToSpeech(carContext, this)
    }

    override fun onGetTemplate(): Template = MessageTemplate.Builder("Play music on AA(Spotify) and change volume while app speaks. With TTS - it changes audio volume, with MediaPlayer prompt volume")
        .addAction(
            Action.Builder()
                .setOnClickListener { speakUsingTTS() }
                .setTitle("TTS")
                .build()
        )
        .addAction(
            Action.Builder()
                .setOnClickListener { speakUsingMediaPlayer(R.raw.turn_right) }
                .setTitle("MediaPlayer")
                .build()
        )
        .setTitle("TTS sound test")
        .build()

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        tts?.shutdown()
    }

    override fun onInit(initStatus: Int) {
        if (initStatus != TextToSpeech.SUCCESS) {
            throw IllegalStateException("Tts is not working")
        }
        tts?.language = Locale.ENGLISH
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {}
            override fun onDone(utteranceId: String) = notifyTtsEnd(utteranceId)
            override fun onError(utteranceId: String) = notifyTtsEnd(utteranceId)
            override fun onStop(utteranceId: String, interrupted: Boolean) = notifyTtsEnd(utteranceId)

            private fun notifyTtsEnd(utteranceId: String) {
                if (activeUtteranceIds.remove(utteranceId) && activeUtteranceIds.isEmpty()) {
                    audioManager.abandonAudioFocusRequest(audioFocusRequest)
                }
            }
        })
    }

    private fun speakUsingTTS() {
        val params = Bundle().apply {
            putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
        }

        if (audioManager.requestAudioFocus(audioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            val id = System.currentTimeMillis().toString()
            activeUtteranceIds.add(id)
            tts?.speak(
                "Let's talk and change volume",
                TextToSpeech.QUEUE_FLUSH,
                params,
                id
            )
        } else {
            Log.e("Play", "focus not granted")
        }
    }

    private fun speakUsingMediaPlayer(@RawRes resourceId: Int) {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(audioAttributes)

        mediaPlayer.setOnCompletionListener { player: MediaPlayer ->
            try {
                player.stop()
                player.release()
            } finally {
                audioManager.abandonAudioFocusRequest(audioFocusRequest)
            }
        }

        if (audioManager.requestAudioFocus(audioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            try {
                val afd = carContext.resources.openRawResourceFd(resourceId)
                mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                mediaPlayer.prepare()
            } catch (e: IOException) {
                Log.e("Play", "Failure loading audio resource", e)
                audioManager.abandonAudioFocusRequest(audioFocusRequest)
            }

            // Start the audio playback.
            mediaPlayer.start()
        }
    }

}