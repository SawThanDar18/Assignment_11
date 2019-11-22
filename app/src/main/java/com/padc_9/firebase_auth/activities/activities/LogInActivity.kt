package com.padc_9.firebase_auth.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import coil.api.load
import com.facebook.*
import com.google.firebase.auth.FirebaseAuth
import com.padc_9.firebase_auth.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.password_et
import kotlinx.android.synthetic.main.activity_login.username_et
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.login_root
import org.json.JSONObject

class LogInActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var callbackManager : CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)

        FacebookSdk.sdkInitialize(applicationContext)

        mAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        imageView.load(R.drawable.ic_heart_black_24dp)

        setUpListener()
    }

    private fun userLogin(){

        var email = username_et.text.toString().trim()
        var password = password_et.text.toString().trim()

        if (email.isEmpty()) {
            username_et.error = "Email is required!"
            username_et.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            username_et.error = "Please enter a valid email!"
            username_et.requestFocus()
            return
        }

        if (password.isEmpty()) {
            password_et.error = "Password is required!"
            password_et.requestFocus()
            return
        }

        if (password.length < 6) {
            password_et.error = "Minimum length of password should be 6!"
            password_et.requestFocus()
            return
        }

        doLogIn(email, password)
    }

    private fun doLogIn(email: String, password: String) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("success", "Login Successful")
                Snackbar.make(login_root, "Login Successful", Snackbar.LENGTH_SHORT).show()
                finish()
                val intent = Intent(this, ProfileActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } else {
                Log.d("fail", "Login Fail")
            }
        }.addOnFailureListener {
            Log.d("fail", "Login Failed!")
            Snackbar.make(login_root, "You don't have an account!! Please Create One!!", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            Log.d("user", "User : " + currentUser.email!!)
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    private fun signInWithFacebook(){
        LoginManager.getInstance().logInWithReadPermissions(this,
            listOf( "name","email","password","photo")
        )
        LoginManager.getInstance().registerCallback(callbackManager,object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if(result!=null){
                    GraphRequest.newMeRequest(result.accessToken,object : GraphRequest.GraphJSONObjectCallback{
                        override fun onCompleted(`object`: JSONObject?, response: GraphResponse?) {
                            finish()
                            val intent = Intent(applicationContext, ProfileActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }

                    })
                }
                else Log.d("fail","fail")

            }

            override fun onCancel() {
                Log.d("cancel","cancel")
            }

            override fun onError(error: FacebookException?) {
                Log.d("error","error")
            }

        })
    }

    private fun signInWithGoogle(){

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        startActivity(Intent(this, GoogleProfileActivity::class.java))
    }

    private fun setUpListener(){
        signUp_tv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        login_btn.setOnClickListener {
            userLogin()
        }

        facebook_layout.setOnClickListener {
            signInWithFacebook()
        }

        google_layout.setOnClickListener {
            signInWithGoogle()
        }

        reset_pw_tv.setOnClickListener {
            finish()
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
    }

}
