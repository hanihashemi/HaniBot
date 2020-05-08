package hanihashemi.github.io

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import hanihashemi.github.io.databinding.ActivityFullscreenBinding
import kotlinx.android.synthetic.main.activity_fullscreen.*


class FullscreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var textToSpeechSystem: TextToSpeech


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fullscreen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.hide()


    }

    override fun onStart() {
        super.onStart()
        textToSpeechSystem = TextToSpeech(this, OnInitListener { status ->
            val language = textToSpeechSystem.availableLanguages.find { it.country == "GB" }
            val voice = textToSpeechSystem
                .voices
                .filter { it.locale.country == "GB" }
                .filter { !it.isNetworkConnectionRequired }
                .first { it.name.contains("male") }

            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeechSystem.setLanguage(language)
                textToSpeechSystem.voice = voice
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    Toast.makeText(
                        this,
                        "This device doesn't have English language!",
                        Toast.LENGTH_LONG
                    ).show()
                else textToSpeechSystem.speak(
                    "Hi Radin, My name is Hani. How are you today?",
                    TextToSpeech.QUEUE_ADD,
                    null,
                    null
                )
            } else Toast.makeText(
                this,
                "TextToSpeech is not available on this device!",
                Toast.LENGTH_LONG
            ).show()
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        hide()
    }

    private fun hide() {
        fullscreen_content.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
}