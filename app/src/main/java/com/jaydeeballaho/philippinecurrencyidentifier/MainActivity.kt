package com.jaydeeballaho.philippinecurrencyidentifier

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale


class MainActivity : AppCompatActivity() {

    lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Element variables
        val continueBtn = findViewById<Button>(R.id.btnContinue)
        val termsMessage = getString(R.string.app_terms_conditions);

        val speechMessage = "Welcome to Philippine Currency Identifier. $termsMessage"
        val uniqueId = "welcoming"

        // Check if the license has expired
        if (isLicenseExpired()) {
            // License expired, show message and disable continue button
            val expiredMessage = "Your license to use this application has expired. Contact the developer for support."
            val txtTerms = findViewById<TextView>(R.id.txtTerms)
            txtTerms.text = expiredMessage
            continueBtn.text = "Close"

            continueBtn.setOnClickListener {
                // Close the app when the button is clicked
                finish()
            }
        }else{
            tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
                if (it == TextToSpeech.SUCCESS) {
                    val speechListener = object : UtteranceProgressListener() {
                        override fun onDone(utteranceId: String?) {
                        }

                        override fun onError(utteranceId: String?) {
                        }

                        override fun onStart(utteranceId: String?) {
                        }
                    }

                    speakNow(speechMessage, uniqueId, speechListener)
                }
            })

            continueBtn.setOnClickListener {
                val intent = Intent(this, ScanActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // Function to check if the license has expired
    private fun isLicenseExpired(): Boolean {
        // Here you would implement your logic to check if the license has expired.
        // For this example, let's assume the expiration date is hardcoded as January 1, 2023.
        val expirationDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2024-03-23")
        val currentDate = Date()

        return currentDate.after(expirationDate)
    }

    override fun onStop() {
        super.onStop()

        // Stops TTS when it is speaking
        if(tts.isSpeaking()) {
            tts.stop();
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Shut down TTS
        if(tts != null){
            tts.shutdown();
        }
    }

    // Initiates text-to-speech conversion
    private fun speakNow(speechMessage: String, uniqueId: String, speechListener: UtteranceProgressListener) {
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, uniqueId)

        // Set the language for the TTS engine
        tts.language = Locale.US

        // Set the speech rate to normal (1.0f).
        tts.setSpeechRate(1.0f)
        tts.setOnUtteranceProgressListener(speechListener)

        // Instruct the TTS engine to speak the given message
        tts.speak(speechMessage, TextToSpeech.QUEUE_FLUSH, params, uniqueId)
    }
}