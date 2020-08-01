package com.jainam.story2.auth

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.itextpdf.kernel.pdf.filters.IFilterHandler

import com.jainam.story2.R
import kotlinx.android.synthetic.main.login_fragment.*


private const val RC_SIGN_IN: Int = 1

class LoginFragment : Fragment() {



    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var tts: TextToSpeech

    //private val textToSpeech = TextToSpeech(context,listener,"com.google.tts")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

       // viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        sign_in_button.setOnClickListener{
            sign_in_button.setImageResource(R.drawable.google_login_pressed_button)
            signIn()
        }


    }
    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent

        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            progressBar.visibility = View.VISIBLE
            sign_in_button.visibility = View.INVISIBLE
            appLogoImage.visibility = View.INVISIBLE


            handleSignInResult(task)
        }

    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)

            firebaseAuthWithGoogle(account!!)
            // Signed in successfully, show authenticated UI.

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SIGN", "signInResult:failed code=" + e.statusCode)
            //updateUiWithUser(null)
        }
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SIGN", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }

            }
    }
    private fun onInitListener() : TextToSpeech.OnInitListener {

        return TextToSpeech.OnInitListener {status ->
            if (status == TextToSpeech.SUCCESS){
                if (isGoogleTTSPresent()){
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment2())
                }else{
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToGoogleTTSFragment())
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

    private fun updateUI(user: FirebaseUser?) {
        if (user!= null){
            progressBar.visibility = View.INVISIBLE
            tts = TextToSpeech(context, onInitListener())
        }else{
            sign_in_button.visibility = View.VISIBLE
        }
    }
}
