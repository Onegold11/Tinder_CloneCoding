package com.clonecoding.clonetinder.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.clonecoding.clonetinder.R
import com.google.firebase.auth.FirebaseAuth

/**
 * 메인 액티비티
 */
class MainActivity : AppCompatActivity() {

    /**
     * Firebase Auth 객체
     */
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        if (this.auth.currentUser == null) {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
        } else {
            startActivity(
                Intent(this, LikeActivity::class.java)
            )
            finish()
        }
    }
}