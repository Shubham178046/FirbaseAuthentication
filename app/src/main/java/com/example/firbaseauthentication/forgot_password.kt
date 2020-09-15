package com.example.firbaseauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_login.*

class forgot_password : AppCompatActivity() {
    private val TAG = "ForgotPasswordActivity"
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initialise()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun initialise() {
        mAuth = FirebaseAuth.getInstance()
        forgotbtn.setOnClickListener {
            sendPasswordResetEmail()
        }
    }

    private fun sendPasswordResetEmail() {
        if(forgot_email.text.toString().trim().length == 0)
        {
            forgot_email.error="Field Cannot Be Blank"
        }
        else if(Patterns.EMAIL_ADDRESS.matcher(forgot_email.text.toString()).matches())
        {
            forgot_email.error="Invalid Email id"
        }
        else
        {
            mAuth!!.sendPasswordResetEmail(forgot_email.text.toString())
                .addOnCompleteListener {task ->  
                    if(task.isSuccessful)
                    {
                        val message = "Email sent."
                        Log.d(TAG, message)
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        updateUI()
                    }
                    else
                    {
                        task.exception!!.message?.let { Log.w(TAG, it) }
                        Toast.makeText(this, "No user found with this email.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun updateUI() {
        val intent = Intent(this,Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}