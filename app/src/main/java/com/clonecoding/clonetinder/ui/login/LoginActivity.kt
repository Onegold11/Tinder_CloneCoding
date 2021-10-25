package com.clonecoding.clonetinder.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.clonecoding.clonetinder.R
import com.clonecoding.clonetinder.databinding.ActivityLoginBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * 로그인 액티비티
 */
class LoginActivity : AppCompatActivity() {

    /**
     * 뷰 바인딩
     */
    private lateinit var binding: ActivityLoginBinding

    /**
     * 파이어베이스 권한 객체
     */
    private lateinit var auth: FirebaseAuth

    /**
     * 페이스북 콜백 매니저
     */
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = DataBindingUtil.setContentView(
            this, R.layout.activity_login
        )
        binding.lifecycleOwner = this

        this.auth = FirebaseAuth.getInstance()
        this.callbackManager = CallbackManager.Factory.create()

        this.initLoginButton()
        this.initSignUpButton()
        this.initFacebookLoginButton()
        this.initEmailAndPasswordEditText()
    }

    /**
     * 이메일/비밀번호 뷰 초기화
     */
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

    /**
     * 로그인 버튼 초기화
     */
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

                    handleSuccessLogin()
                } else {

                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 회원 가입 버튼 초기화
     */
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

    /**
     * 페이스북 로그인 버튼 초기화
     */
    private fun initFacebookLoginButton() {

        val fbLoginButton = this.binding.facebookLoginButton

        fbLoginButton.setPermissions("email", "public_profile")
        fbLoginButton.registerCallback(this.callbackManager, object : FacebookCallback<LoginResult> {
            
            override fun onSuccess(result: LoginResult) {
                // 로그인 성공
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                this@LoginActivity.auth.signInWithCredential(credential)
                    .addOnCompleteListener(this@LoginActivity) {
                        if(it.isSuccessful) {

                            handleSuccessLogin()
                        } else {

                            Toast.makeText(this@LoginActivity, "페이스북 로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onCancel() {}

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@LoginActivity, "페이스북 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * 로그인이 성공했을 때 호출
     */
    private fun handleSuccessLogin() {

        if (this.auth.currentUser == null) {

            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = this.auth.currentUser?.uid.orEmpty()
        val currentUserDB = Firebase.database.reference
            .child("Users")
            .child(userId)
        val user = mutableMapOf<String, Any>()

        user["userId"] = userId

        currentUserDB.updateChildren(user)

        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        this.callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}