package com.example.firbaseauthentication

import android.content.ContentProviderClient
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    private var TAG = "SignInActivity"
    private var mFirebaseAuth: FirebaseAuth? = null
    val RC_SIGN_IN : Int=1
    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var mGoogleSignInOptions : GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        initialise()
        setupUI()
        configureGoogleSignIn()
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }
    private fun setupUI() {
        signInbtn.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        mFirebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if(task.isSuccessful)
                {
                    startActivity(UserDetail.getLaunchIntent(this))
                }
                else
                {
                    Toast.makeText(this, "Auth Google sign in failed:(", Toast.LENGTH_LONG).show()
                }
            }

    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(UserDetail.getLaunchIntent(this))
            finish()
        }
    }

    private fun initialise() {
        mFirebaseAuth = FirebaseAuth.getInstance()
        forgot_password_txt.setOnClickListener {
            startActivity(Intent(applicationContext, forgot_password::class.java))
        }
        registar.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
        login.setOnClickListener {
            loginUser()
        }
    }
    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, Login::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
    private fun loginUser() {
        if (login_email.text.toString().trim().length == 0) {
            login_email.error = "Field Cannot Be Blank"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(login_email.text.toString()).matches()) {
            login_email.error = "Invalid Email id"
        } else if (login_password.text.toString().trim().length == 0) {
            login_password.error = "Field Cannot Be Blank"
        } else {
            mFirebaseAuth!!.signInWithEmailAndPassword(
                login_email.text.toString(),
                login_password.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        updateUI()
                    } else {
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            this, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun updateUI() {
        Toast.makeText(
            this, "Login Successfull",
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(this, UserDetail::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}