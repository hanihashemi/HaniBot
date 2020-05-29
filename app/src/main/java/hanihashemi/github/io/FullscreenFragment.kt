package hanihashemi.github.io

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import hanihashemi.github.io.databinding.FragmentFullscreenBinding
import kotlinx.coroutines.*


class FullscreenFragment : Fragment() {
    lateinit var binding: FragmentFullscreenBinding
    private lateinit var textToSpeechSystem: TextToSpeech
    private var isTextToSpeechReady = false
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFullscreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("DEPRECATION")
    fun isAppInLockTaskMode(): Boolean {
        val activityManager: ActivityManager =
            getSystemService(requireContext(), ActivityManager::class.java) as ActivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (activityManager.lockTaskModeState
                    != ActivityManager.LOCK_TASK_MODE_NONE)
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activityManager.isInLockTaskMode
        } else false
    }

    private fun unlockDialog() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_unlock, null)
        val textField = view.findViewById<TextInputLayout>(R.id.textField)

        MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setTitle("Solve the problem to unlock")
            .setMessage("2 + 2 =")
            .setPositiveButton("OKAY") { _, _ ->
                if (textField.editText?.text.toString() == "4")
                    requireActivity().stopLockTask()
                (requireActivity() as FullscreenActivity).hide()
            }
            .setCancelable(true)
            .setOnCancelListener {
                (requireActivity() as FullscreenActivity).hide()
            }
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lock.setOnClickListener {
            if (isAppInLockTaskMode())
                unlockDialog()
            else requireActivity().startLockTask()
        }

        binding.greeting.setOnClickListener {
            greeting()
        }

        binding.countTo10.setOnClickListener {
            countTo10()
        }

        textToSpeechSystem = TextToSpeech(requireActivity(), TextToSpeech.OnInitListener { status ->
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
                        requireContext(),
                        "This device doesn't have English language!",
                        Toast.LENGTH_LONG
                    ).show()
                else {
                    greeting()
                    isTextToSpeechReady = true
                }
            } else Toast.makeText(
                requireContext(),
                "TextToSpeech is not available on this device!",
                Toast.LENGTH_LONG
            ).show()
        })
    }

    private fun greeting() {
        job?.cancel()
        job = lifecycleScope.launch(Dispatchers.IO) {
            speak("Hi Radein")
            delay(2000)
            speak("My name is Hani. How are you today?")
        }
    }

    private fun countTo10() {
        job?.cancel()
        job = lifecycleScope.launch(Dispatchers.IO) {
            for (index in 1..10) {
                if (!isActive) break
                speak("$index")
                delay(1700)
            }
        }
    }

    private suspend fun speak(text: String) = withContext(Dispatchers.Main) {
        if (isTextToSpeechReady)
            textToSpeechSystem.speak(
                text,
                TextToSpeech.QUEUE_ADD,
                null,
                null
            )
    }

}