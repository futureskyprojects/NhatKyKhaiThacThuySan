package vn.vistark.nkktts.utils

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.*

class TtsUtils(val context: Context, val langCode: String) : AsyncTask<String, Int, Uri?>() {
    private var mediaPlayer = MediaPlayer()
    lateinit var audioManager: AudioManager
    private var defaultMsg = ""
    private var previousStreamVolume = -1

    // Default TTS
    private var tts: TextToSpeech? = null

    override fun onPreExecute() {
        super.onPreExecute()
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private fun defaultTTS(msg: String) {
        tts = TextToSpeech(context, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                // Lệnh khi thành công
                tts!!.language = Locale(langCode)
                tts!!.setPitch(1.1F)
                tts!!.setSpeechRate(1.2F)
                tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onDone(utteranceId: String?) {
                        makeDefaultVolume()
                    }

                    override fun onError(utteranceId: String?) {
                        makeDefaultVolume()
                    }

                    override fun onStart(utteranceId: String?) {
                        makeMaxVolume()
                    }
                })
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //vi-vn-x-vif-local (Giọng nam Bắc - nói offline)
                    //vi-vn-x-vid-network (Giọng nam Bắc - off)
                    //vi-vn-x-vic-network (Giọng nữ Bắc - off)
                    //vi-vn-x-vif-network (Giọng nam Nam - off)
                    //vi-vn-x-vie-local (Giọng nữ Nam - off)
                    //vi-vn-x-gft-local (Giọng nữ mặc định- offline - dớ tệ hại)
                    //vi-vn-x-vic-local (Giọng nữ Bắc - offline - ngữ điệu - HAY)
                    //vi-vn-x-vid-local (Giọng nam Nam - offline - khá dở)
                    //vi-vn-x-vie-network (Giọng nữ Nam - offline - khá dở)
                    //vi-vn-x-gft-network (Giọng nữ Nam - offline - dở)
                    //vi-VN-language (Mặc định)

                    val name = "vi-vn-x-vic-local"
                    if (!tts!!.defaultVoice.name.contains(name)) {
                        for (v in tts!!.voices) {
                            if (v.name.contains(name)) {
                                tts!!.voice = v
                                break
                            }
                        }
                    }
                    tts!!.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    tts!!.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
                }


            }
        })
    }

    fun makeMaxVolume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!audioManager.isStreamMute(AudioManager.STREAM_MUSIC)) {
                previousStreamVolume =
                    audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    AudioManager.FLAG_PLAY_SOUND
                )
            }
        }
    }

    fun makeDefaultVolume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!audioManager.isStreamMute(AudioManager.STREAM_MUSIC)) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    previousStreamVolume,
                    AudioManager.FLAG_PLAY_SOUND
                )
            }
        }
    }

    // WaveNet TTS
    override fun doInBackground(vararg params: String?): Uri? {
        defaultMsg = params[0]!!
        return null
    }

    override fun onPostExecute(result: Uri?) {
        super.onPostExecute(result)
        if (result != null) {
            // Nếu đang nói thì ngưng
            if (isTalking()) {
                stop()
            }
        } else {
            // Nếu đang nói thì ngưng
            if (isTalking()) {
                stop()
            }
            // Thực hiện nói mới
            defaultTTS(defaultMsg)
        }
    }

    //
    private fun isTalking(): Boolean {
        return tts?.isSpeaking ?: false || mediaPlayer.isPlaying
    }

    fun stop() {
        if (tts?.isSpeaking == true) {
            tts?.stop()
        }

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
            mediaPlayer.release()
        }
    }
}