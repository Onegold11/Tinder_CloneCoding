package com.clonecoding.clonetinder.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.clonecoding.clonetinder.R
import com.clonecoding.clonetinder.databinding.ActivityLoginBinding
import com.clonecoding.clonetinder.viewmodels.LoginViewModel
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
     * 뷰 모델
     */
    private lateinit var viewModel: LoginViewModel

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

        // 뷰 모델
        this.viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        // 데이터 바인딩
        this.binding = DataBindingUtil.setContentView(
            this, R.layout.activity_login
        )
        this.binding.lifecycleOwner = this
        this.binding.viewModel = this.viewModel

        // 파이어베이스
        this.auth = FirebaseAuth.getInstance()
        this.callbackManager = CallbackManager.Factory.create()

        // 버튼 초기화
        this.initLoginButton()
        this.initSignUpButton()
        this.initFacebookLoginButton()
        this.initEmailAndPasswordEditText()
    }

    /**
     * 이메일/비밀번호 뷰 초기화
     */
    private fun initEmailAndPasswordEditText() {

        this.viewModel.emailText.observe(this, {

            this.saveButtonVisibleState()
        })

        this.viewModel.passwordText.observe(this, {

            this.saveButtonVisibleState()
        })
    }

    /**
     * 버튼의 visible 상태를 저장합니다.
     */
    private fun saveButtonVisibleState() {

        this.viewModel.isActiveButton.value = this.binding.emailEditText.text.isNotEmpty() &&
                this.binding.passwordEditText.text.isNotEmpty()
    }

    /**
     * 로그인 버튼 초기화
     */
    private fun initLoginButton() {

        this.viewModel.onLoginListener = { email: String, password: String ->

            this.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {

                    if (it.isSuccessful) {

                        handleSuccessLogin()
                    } else {

                        this.showToastMessage("로그인 실패")
                    }
                }
        }
    }

    /**
     * 회원 가입 버튼 초기화
     */
    private fun initSignUpButton() {

        this.viewModel.onSignUpListener = { email: String, password: String ->

            this.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {

                    if (it.isSuccessful) {

                        this.showToastMessage("회원가입 성공")
                    } else {

                        this.showToastMessage("회원가입 실패")
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
        fbLoginButton.registerCallback(
            this.callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult) {
                    // 로그인 성공
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    this@LoginActivity.auth.signInWithCredential(credential)
                        .addOnCompleteListener(this@LoginActivity) {
                            if (it.isSuccessful) {

                                handleSuccessLogin()
                            } else {

                                this@LoginActivity.showToastMessage("페이스북 로그인 실패")
                            }
                        }
                }

                override fun onCancel() {}

                override fun onError(error: FacebookException?) {

                    this@LoginActivity.showToastMessage("페이스북 로그인 실패")
                }
            })
    }

    /**
     * 로그인이 성공했을 때 호출
     */
    private fun handleSuccessLogin() {

        if (this.auth.currentUser == null) {

            this.showToastMessage("로그인에 실패했습니다.")
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

    /**
     * 토스트 메시지를 출력합니다.
     */
    private fun showToastMessage(message: String) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        this.callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}