package com.padc_9.firebase_auth.activities.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.padc_9.firebase_auth.R
import com.padc_9.firebase_auth.activities.vos.UserVO
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.root

class SignUpActivity: AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_sign_up)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStore = FirebaseFirestore.getInstance()

        imageView.load(R.drawable.ic_heart_black_24dp)

        setUpListener()
    }

    private fun userAccountCreation(){

        var userName = username_et.text.toString().trim()
        var email = email_et.text.toString().trim()
        var password = password_et.text.toString().trim()

        if (userName.isEmpty()){
            username_et.error = "Email is required!"
            username_et.requestFocus()
            return
        }

        if (email.isEmpty()) {
            email_et.error = "Email is required!"
            email_et.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_et.error = "Please enter a valid email!"
            email_et.requestFocus()
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

        doUserSignUp(userName, email, password)

    }

    private fun doUserSignUp(userName: String, email: String, password: String) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {

                    Log.d("if", "Sign Up Success")
                    Snackbar.make(root, "Sign Up Successful", Snackbar.LENGTH_SHORT).show()
                    storeUserDataToFirestore(userName, email, password)

                    finish()
                    startActivity(Intent(this, LogInActivity::class.java))
                } else {
                    Log.d("else", "Sign Up Failed")

                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Log.d("already", "Already registered")
                        Snackbar.make(root, "You have already registered!!", Snackbar.LENGTH_SHORT).show()
                    } else {
                        Log.d("error", "FirebaseAuthError => " + task.exception!!.message)
                        Snackbar.make(root, "Something went wrong!!", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener{
                it
            }
    }

    private fun storeUserDataToFirestore(userName: String, email: String, password: String) {

        val user = UserVO(userName, email, password)

        firebaseStore.collection("users")
            .add(user)
            .addOnSuccessListener(OnSuccessListener<DocumentReference> {
                Log.d("store", "Store user data successful!")
            })
            .addOnFailureListener(OnFailureListener { e ->
                Log.d("store fail", "StoreUserDataError => " + e.message)
            })
    }

    private fun setUpListener(){
        logIn_tv.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }

        signUp_btn.setOnClickListener {
            userAccountCreation()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(Intent(this, LogInActivity::class.java))
    }
}