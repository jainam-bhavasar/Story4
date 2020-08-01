package com.jainam.story2.google_tts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.jainam.story2.databinding.FragmentGoogleTtsBinding
import com.pixplicity.sharp.Sharp
import kotlinx.android.synthetic.main.fragment_google_tts.*
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass.
 */
class GoogleTTSFragment : Fragment() {
    private lateinit var binding: FragmentGoogleTtsBinding
    val TAG = "GTTS"
    private lateinit var tts: TextToSpeech
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGoogleTtsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: gtts")

        binding.button.setOnClickListener {
            val uri =
                Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.tts")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        tts = TextToSpeech(context, onInitListener())
        Log.i(TAG, "onResume: text to speech initialised")
        Sharp.loadAsset(context?.assets,"ic_google_tts.svg").into(imageGoogleTTS)
    }
    private fun onInitListener() : TextToSpeech.OnInitListener {

        return TextToSpeech.OnInitListener {status ->
            if (status == TextToSpeech.SUCCESS){
                CoroutineScope(Dispatchers.Default).launch {
                    while (!isGoogleTTSPresent()){
                        delay(3000)
                    }
                    if (isGoogleTTSPresent()){
                      findNavController().navigate(GoogleTTSFragmentDirections.actionGoogleTTSFragmentToHomeFragment2())
                    }
                }

            }
        }

    }
    private fun isGoogleTTSPresent(): Boolean {
        var found = false
        for (engineInfo in tts.engines) {
            if (engineInfo.name == "com.google.android.tts") {
                found = true
            }
        }
        return found
    }
}