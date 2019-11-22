package com.padc_9.firebase_auth.activities.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.padc_9.firebase_auth.R
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity: AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_reset_password)

        firebaseAuth = FirebaseAuth.getInstance()

        setUpListener()
    }

    private fun setUpListener(){
        send_btn.setOnClickListener {
            val email = resetpassword_email_et.text.toString()

            if (email.isEmpty()) {
                resetpassword_email_et.error = "Email is required!"
                resetpassword_email_et.requestFocus()
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                resetpassword_email_et.error = "Please enter a valid email!"
                resetpassword_email_et.requestFocus()
            }

            progressbar.visibility = View.VISIBLE
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    progressbar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Check email to reset your password!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Fail to send reset password email!", Toast.LENGTH_LONG).show()
                    }
                }

            finish()
            startActivity(Intent(this, LogInActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LogInActivity::class.java))
    }
}