package com.example.otpfirebase.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.otpfirebase.R
import com.example.otpfirebase.databinding.FragmentSecondBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class SecondFragment : Fragment() {
    private lateinit var binding: FragmentSecondBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSecondBinding.inflate(layoutInflater, container, false)

        val verificationId = arguments?.getString("verificationId")
        val resendToken    = arguments?.getString("resendToken")
        val mobile         = arguments?.getString("mobile")

//        Toast.makeText(context, "otp: $verificationId", Toast.LENGTH_SHORT).show()
//        Toast.makeText(context, "token: $resendToken", Toast.LENGTH_SHORT).show()
//        Toast.makeText(context, "mobile: $mobile", Toast.LENGTH_SHORT).show()

        binding.btnVerify.setOnClickListener{
            val otpCode = (binding.otpBox1.text.toString() +  binding.otpBox2.text.toString()
                    + binding.otpBox3.text.toString() + binding.otpBox4.text.toString()
                    + binding.otpBox5.text.toString() + binding.otpBox6.text.toString())

            val credential = PhoneAuthProvider.getCredential(verificationId!!, otpCode)

            signInWithPhoneAuthCredential(credential)
        }


        binding.txtResendOtp.setOnClickListener {
            requireActivity()
                .supportFragmentManager
                .popBackStack("log", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        //this function for otp typing
        addOnTextChangeListener()



        return binding.root
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(context, "login success", Toast.LENGTH_SHORT).show()

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("tfailed", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun addOnTextChangeListener(){
        binding.otpBox1.addTextChangedListener(OtpTextWatcher(binding.otpBox1))
        binding.otpBox2.addTextChangedListener(OtpTextWatcher(binding.otpBox2))
        binding.otpBox3.addTextChangedListener(OtpTextWatcher(binding.otpBox3))
        binding.otpBox4.addTextChangedListener(OtpTextWatcher(binding.otpBox4))
        binding.otpBox5.addTextChangedListener(OtpTextWatcher(binding.otpBox5))
        binding.otpBox6.addTextChangedListener(OtpTextWatcher(binding.otpBox6))
    }
    inner class OtpTextWatcher(private val view: View): TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val otp = s.toString()
            when(view.id){
                R.id.otp_box_1 -> if (otp.length == 1) binding.otpBox2.requestFocus()
                R.id.otp_box_2 -> if (otp.length == 1) binding.otpBox3.requestFocus() else if (otp.isEmpty()) binding.otpBox1.requestFocus()
                R.id.otp_box_3 -> if (otp.length == 1) binding.otpBox4.requestFocus() else if (otp.isEmpty()) binding.otpBox2.requestFocus()
                R.id.otp_box_4 -> if (otp.length == 1) binding.otpBox5.requestFocus() else if (otp.isEmpty()) binding.otpBox3.requestFocus()
                R.id.otp_box_5 -> if (otp.length == 1) binding.otpBox6.requestFocus() else if (otp.isEmpty()) binding.otpBox4.requestFocus()
                R.id.otp_box_6 -> if (otp.isEmpty()) binding.otpBox5.requestFocus()
            }
        }
    }



}