package com.example.firbaseauthentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_user_detail.*

class UserDetail : AppCompatActivity() {
    private var mDatabaseReference : DatabaseReference?=null
    private var mFirebaseAuth : FirebaseAuth?=null
    private var mDatabase : FirebaseDatabase?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        initialise()
        setupUI()
    }

    private fun setupUI() {
        sign_out_button.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        startActivity(Login.getLaunchIntent(this))
        FirebaseAuth.getInstance().signOut();
    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mFirebaseAuth = FirebaseAuth.getInstance()

        logout_btn.setOnClickListener {
            mFirebaseAuth!!.signOut()
           // startActivity(Intent(applicationContext,Login::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        val mUser = mFirebaseAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)


        tv_email.text = mUser.email
        tv_email_verifiied.text = mUser.isEmailVerified.toString()

        mUserReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", "onCancelled: "+error.message)
                startActivity(Intent(applicationContext,Login::class.java))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                tv_name.text= snapshot.child("name").value as String
            }

        })
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, Login::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
}