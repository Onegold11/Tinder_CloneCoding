package com.clonecoding.clonetinder.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * 로그인 액티비티 뷰 모델
 */
class LoginViewModel : ViewModel() {

    /**
     * 이메일
     */
    val emailText = MutableLiveData<String>()

    /**
     * 비밀번호
     */
    val passwordText = MutableLiveData<String>()

    /**
     * 로그인/회원 가입 버튼 활성화 여부
     */
    val isActiveButton = MutableLiveData<Boolean>()

    /**
     * 로그인 이벤트
     */
    var onLoginListener: ((email: String, password: String) -> (Unit))? = null

    /**
     * 회원가입 이벤트
     */
    var onSignUpListener: ((email: String, password: String) -> (Unit))? = null

    /**
     * 로그인 버튼 클릭 이벤트
     */
    fun loginButtonClick() {

        this.onLoginListener?.invoke(
            this.emailText.value ?: "",
            this.passwordText.value ?: ""
        )
    }

    /**
     * 회원가입 버튼 클릭 이벤트
     */
    fun signUpButtonClick() {

        this.onSignUpListener?.invoke(
            this.emailText.value ?: "",
            this.passwordText.value ?: ""
        )
    }
}