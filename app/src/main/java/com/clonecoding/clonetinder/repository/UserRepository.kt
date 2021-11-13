package com.clonecoding.clonetinder.repository

import androidx.lifecycle.MutableLiveData
import com.clonecoding.clonetinder.data.CardItem
import com.clonecoding.clonetinder.util.DBConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * User 정보를 가진 Firebase Realtime Database
 */
class UserRepository(
    private val uid: MutableLiveData<String?>
){

    // 데이터베이스 루트
    private val dbRoot = Firebase.database.reference

    // 유저 데이터베이스
    private val userDB = dbRoot.child(DBConstants.USER_DB_NAME)

    // 파이어베이스 권한
    private val auth = FirebaseAuth.getInstance()

    /**
     * 데이터베이스에서 현재 사용자에 대한 콜백 이벤트 설정
     */
    fun setCurrentUserCallback(
        userName: MutableLiveData<String?>
    ) {

        val currentUser = this.auth.currentUser

        if (currentUser == null) {

            this.uid.value = null
            return
        }

        uid.value = currentUser.uid

        this.userDB.child(this.uid.value!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.child("name").value == null) {

                    userName.value = null
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /**
     * 데이터베이스에 대한 콜백 이벤트 설정
     */
    fun setUserCallback(
        _cardItems: MutableList<CardItem>,
        cardItems: MutableLiveData<MutableList<CardItem>>
    ) {

        this.userDB.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val currentUser = auth.currentUser

                if (currentUser == null) {

                    this@UserRepository.uid.value = null
                    return
                }

                val currentUserId = currentUser.uid

                if (snapshot.child("userId").value != currentUserId
                    && snapshot.child("likedBy").child("like").hasChild(currentUserId).not()
                    && snapshot.child("likedBy").child("disLike").hasChild(currentUserId).not()) {

                    val userId = snapshot.child("userId").value.toString()
                    var name = "undecided"
                    if (snapshot.child("name").value != null) {

                        name = snapshot.child("name").value.toString()
                    }

                    _cardItems.add(CardItem(userId, name))
                    cardItems.value = _cardItems
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                cardItems.value?.find { it.userId == snapshot.key }?.let {

                    it.name = snapshot.child("name").value.toString()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    
    /**
     * 사용자의 이름을 저장
     *
     * @param name
     *      사용자 이름
     */
    fun saveUserName(name: String) {

        val userId = this.getCurrentUserID()

        userId?.let {
            val currentUserDB = this.userDB
                .child(it)

            val user = mutableMapOf<String, Any>()
            user["userId"] = it
            user["name"] = name

            currentUserDB.updateChildren(user)
        }
    }

    /**
     * 대상을 like 합니다
     *
     * @param item
     */
    fun likeFriend(item: CardItem) {

        val uid = this.getCurrentUserID()

        uid?.let {
            this.userDB.child(item.userId)
                .child("likedBy")
                .child("like")
                .child(it)
                .setValue(true)
        }
    }

    /**
     * 대상을 dislike 합니다
     *
     * @param item
     */
    fun dislikeFriend(item: CardItem) {

        val uid = this.getCurrentUserID()

        uid?.let {
            this.userDB.child(item.userId)
                .child("likedBy")
                .child("disLike")
                .child(uid)
                .setValue(true)
        }
    }

    /**
     * 사용자 Uid 반환
     *
     * @return 사용자 uid
     */
    private fun getCurrentUserID(): String? {

        if (this.auth.currentUser == null) {

            this.uid.value = null
            return this.uid.value
        }

        this.uid.value = this.auth.currentUser?.uid.orEmpty()
        return this.uid.value
    }
}