package com.clonecoding.clonetinder.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clonecoding.clonetinder.data.CardItem
import com.clonecoding.clonetinder.repository.UserRepository

/**
 * Like 액티비티 뷰 모델
 */
class LikeViewModel : ViewModel(){

    // 유저 저장소
    private var repository: UserRepository

    // uid
    val uid: MutableLiveData<String?> = MutableLiveData("")

    // User name
    val userName: MutableLiveData<String?> = MutableLiveData("")

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
     * 현재 사용자 데이터베이스 설정
     */
    fun initCurrentUserDB() = this.repository.setCurrentUserCallback(this.userName)

    /**
     * 사용자 데이터베이스 설정
     */
    fun initUserDB() = this.repository.setUserCallback(this._cardItems, this.cardItems)

    /**
     * 사용자의 이름을 저장
     *
     * @param name
     *      사용자 이름
     */
    fun saveUserName(name: String) = this.repository.saveUserName(name)

    /**
     * 해당 대상을 like 합니다
     *
     * @param item
     */
    fun likeFriend(position: Int): CardItem {

        val card = this._cardItems[position]

        this._cardItems.removeFirst()
        this.cardItems.value = this._cardItems

        this.repository.likeFriend(card)
        this.repository.saveMatchIfOtherUserLikeMe(card.userId)

        return card
    }

    /**
     * 해당 대상을 dislike 합니다
     *
     * @param item
     */
    fun dislikeFriend(position: Int): CardItem {

        val card = this._cardItems[position]

        this._cardItems.removeFirst()
        this.cardItems.value = this._cardItems
        this.repository.dislikeFriend(card)

        return card
    }
}