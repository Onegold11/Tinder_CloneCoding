package com.clonecoding.clonetinder.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.clonecoding.clonetinder.R
import com.clonecoding.clonetinder.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * 로그인 액티비티
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = DataBindingUtil.setContentView(
            this, R.layout.activity_login
        )
        binding.lifecycleOwner = this

        this.auth = FirebaseAuth.getInstance()

        this.initLoginButton()
        this.initSignUpButton()
        this.initEmailAndPasswordEditText()
    }

    private fun initEmailAndPasswordEditText() {

        this.binding.emailEditText.addTextChangedListener {
            val enable = this.binding.emailEditText.text.isNotEmpty() &&
                    this.binding.passwordEditText.text.isNotEmpty()
            this.binding.loginButton.isEnabled = enable
            this.binding.signUpButton.isEnabled = enable
        }
        this.binding.passwordEditText.addTextChangedListener {
            val enable = this.binding.emailEditText.text.isNotEmpty() &&
                    this.binding.passwordEditText.text.isNotEmpty()
            this.binding.loginButton.isEnabled = enable
            this.binding.signUpButton.isEnabled = enable
        }
    }

    private fun initLoginButton() {

        val loginButton = this.binding.loginButton
        loginButton.setOnClickListener {

            val emailEditText = this.binding.emailEditText
            val passwordEditText = this.binding.passwordEditText

            this.auth.signInWithEmailAndPassword(
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            ).addOnCompleteListener(this) {

                if (it.isSuccessful) {

                    finish()
                } else {

                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initSignUpButton() {

        val signUpButton = this.binding.signUpButton
        signUpButton.setOnClickListener {

            val emailEditText = this.binding.emailEditText
            val passwordEditText = this.binding.passwordEditText

            this.auth.createUserWithEmailAndPassword(
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            ).addOnCompleteListener(this) {

                if(it.isSuccessful) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                } else {

                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}