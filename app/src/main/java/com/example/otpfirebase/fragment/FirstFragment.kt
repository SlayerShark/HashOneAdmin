package com.example.otpfirebase.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.otpfirebase.R
import com.example.otpfirebase.databinding.FragmentFirstBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class FirstFragment : Fragment() {
    private lateinit var binding: FragmentFirstBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(layoutInflater, container, false)
        val auth = Firebase.auth

        binding.btnSendOtp.setOnClickListener {
            val countryPicker = binding.tilMobile.prefixText.toString()
            val number = binding.edtMobile.text.toString()
            val mobileNumber = "$countryPicker$number"
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(mobileNumber)       // Phone number to verify
                .setTimeout(10L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(requireActivity())                 // Activity (for callback binding)
                .setCallbacks(callbacks())          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }




        return binding.root
    }

    private fun callbacks() = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)

        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("TAG", "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragContainer, SecondFragment())
                .addToBackStack("log")
                .commit()

            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("TAG", "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
//            storedVerificationId = verificationId
//            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }


}