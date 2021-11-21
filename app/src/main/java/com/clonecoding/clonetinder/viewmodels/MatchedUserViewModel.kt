package com.clonecoding.clonetinder.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clonecoding.clonetinder.data.CardItem
import com.clonecoding.clonetinder.repository.UserRepository

class MatchedUserViewModel : ViewModel() {

    // 유저 저장소
    private var repository: UserRepository

    // uid
    val uid: MutableLiveData<String?> = MutableLiveData("")

    // 리싸이클러뷰 아이템
    private val _cardItems: MutableList<CardItem> = mutableListOf()

    // 리싸이클러뷰 아이템
    val cardItems: MutableLiveData<MutableList<CardItem>> = MutableLiveData()

    /**
     * 초기화
     */
    init {

        this.repository = UserRepository(this.uid)
    }

    /**
     * 매치된 사용자 목록 업데이트
     */
    fun updateMatchedUser() = this.repository.setMatchUserCallback(this._cardItems, this.cardItems)
}