package com.padc_9.firebase_auth.activities.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.padc_9.firebase_auth.R
import com.padc_9.firebase_auth.activities.vos.UserVO
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException

class ProfileActivity: AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    private var user_id: String? = null

    private val CHOOSE_IMAGE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_profile)

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        setUpListener()

        fetchUserInfo()

    }

    private fun fetchUserInfo(){
        if (firebaseAuth.currentUser != null) {
            val email = firebaseAuth.currentUser!!.email
            firestore.collection("users")
                .whereEqualTo("email", email)
                .addSnapshotListener(EventListener<QuerySnapshot> { queryDocumentSnapshots, e ->
                    if (e != null) {
                        Log.w("error", "Listen error", e)
                        return@EventListener
                    }

                    for (snapshot in queryDocumentSnapshots!!) {
                        val model = snapshot.toObject<UserVO>(
                            UserVO::class.java)
                        //user_id = model.id

                        userName_tv.text = model.userName
                        userAcc_tv.text = model.email
                        user_iv.load(R.drawable.ic_heart_black_24dp)
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.data != null) {
            var uriProfileImage = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uriProfileImage)
                user_iv.setImageBitmap(bitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun showImageChooser(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE)
    }

    private fun setUpListener(){
        logOut_btn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("Are you sure want to log out?")
                .setNegativeButton(getString(R.string.str_cancel), object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog!!.dismiss()
                    }
                })
                .setPositiveButton(getString(R.string.str_ok), object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (firebaseAuth.currentUser != null) {
                            firebaseAuth.signOut()
                            Log.d("success", "Log out successful!")

                            finish()
                            startActivity(Intent(this@ProfileActivity, LogInActivity::class.java))
                        }
                    }
                }).show()
        }

        user_iv.setOnClickListener {
            showImageChooser()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}