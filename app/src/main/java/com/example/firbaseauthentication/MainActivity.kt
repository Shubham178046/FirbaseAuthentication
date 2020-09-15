package com.example.firbaseauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var TAG="CreateAccountActivity"
    private var mFirebaseAuth : FirebaseAuth?=null
    private var mDatabase : FirebaseDatabase?=null
    private var mDatabaseReference : DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        initialise()
}

    private fun initialise() {

        mDatabase= FirebaseDatabase.getInstance()
        mDatabaseReference=mDatabase!!.reference.child("Users")
        mFirebaseAuth = FirebaseAuth.getInstance()

        registar_btn.setOnClickListener {
            createNewAccount()
        }
    }

    private fun createNewAccount() {
        if(name.text.toString().trim().length == 0)
        {
            name.error="Field Cannot Be Blank"
        }
        else if(email.text.toString().trim().length == 0)
        {
            email.error="Field Cannot Be Blank"
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email.text).matches())
        {
            email.error="Invalid Email Id"
        }
        else if(password.text.toString().trim().length == 0)
        {
            password.error="Field Cannot Be Blank"
        }
        else
        {
            Log.d(TAG, "Data: "+ email.text.toString().trim() +password.text.toString().trim() )
            mFirebaseAuth!!.createUserWithEmailAndPassword(email.text.toString().trim(),password.text.toString().trim())
                .addOnCompleteListener(this) {task ->
                    if(task.isSuccessful)
                    {
                        Log.d(TAG, "createUserWithEmail:success")
                        val userId = mFirebaseAuth!!.currentUser!!.uid

                        verifyEmail()

                        val currentUserDb = mDatabaseReference!!.child(userId)
                        currentUserDb.child("name").setValue(name.text.toString())

                        updateUserInfoAndUI()
                    }
                    else
                    {
                        Log.d(TAG, "createNewAccount: ", task.exception)
                    }

                }
        }
    }

    private fun updateUserInfoAndUI() {
        val intent= Intent(applicationContext,Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun verifyEmail() {
        val mUser= mFirebaseAuth!!.currentUser
        mUser!!.sendEmailVerification()
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email send to " + mUser.email, Toast.LENGTH_LONG)
                }
                else
                {
                    Log.d(TAG, "verifyEmail: ",task.exception)
                    Toast.makeText(applicationContext,"Failed To send Verification Email.", Toast.LENGTH_LONG)

                }
            }
    }
}