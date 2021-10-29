package com.clonecoding.clonetinder.ui.like

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.clonecoding.clonetinder.R
import com.clonecoding.clonetinder.databinding.ActivityLikeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LikeActivity : AppCompatActivity() {

    /**
     * 뷰 바인딩
     */
    private lateinit var binding: ActivityLikeBinding

    /**
     * 파이어베이스 객체
     */
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Firebase Realtime Database 객체
     */
    private lateinit var userDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = DataBindingUtil.setContentView(
            this, R.layout.activity_like
        )
        this.binding.lifecycleOwner = this

        userDB = Firebase.database.reference
            .child("Users")
        val currentUserDB = userDB.child(
            this.getCurrentUserID()
        )
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.child("name").value == null) {

                    showNameInputPopup()
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun showNameInputPopup() {

        val editText = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("이름을 입력해주세요")
            .setView(editText)
            .setPositiveButton("저장"){_, _ ->

                if (editText.text.isEmpty()) {

                    showNameInputPopup()
                } else {

                    saveUserName(editText.text.toString())
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun saveUserName(name: String) {

        val userId = this.getCurrentUserID()
        val currentUserDB = this.userDB
            .child(userId)
        val user = mutableMapOf<String, Any>()

        user["userId"] = userId
        user["name"] = name

        currentUserDB.updateChildren(user)
    }

    private fun getCurrentUserID(): String {

        if (this.auth.currentUser == null) {

            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        return this.auth.currentUser?.uid.orEmpty()
    }
}