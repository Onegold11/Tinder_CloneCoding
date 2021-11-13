package com.clonecoding.clonetinder.ui.like

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.clonecoding.clonetinder.R
import com.clonecoding.clonetinder.adapters.CardItemAdapter
import com.clonecoding.clonetinder.databinding.ActivityLikeBinding
import com.clonecoding.clonetinder.viewmodels.LikeViewModel
import com.google.firebase.database.*
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction

/**
 * Like 액티비티
 */
class LikeActivity : AppCompatActivity(), CardStackListener {

    // 뷰 모델
    private lateinit var viewModel: LikeViewModel

    // 뷰 바인딩
    private lateinit var binding: ActivityLikeBinding

    // 카드 Stack 어댑터
    private val adapter by lazy {

        CardItemAdapter()
    }

    // 카드 Stack 레이아웃 매니저
    private val manager by lazy {

        CardStackLayoutManager(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩
        this.initDataBinding()

        // 뷰 모델
        this.initViewModel()

        // 카드 스택 뷰 초기화
        this.initCardStackView()
    }

    /**
     * 데이터 바인딩 설정
     */
    private fun initDataBinding() {

        this.binding = DataBindingUtil.setContentView(
            this, R.layout.activity_like
        )
        this.binding.lifecycleOwner = this
    }

    /**
     * 뷰 모델 초기화
     */
    private fun initViewModel() {

        this.viewModel = ViewModelProvider(this)[LikeViewModel::class.java]
        this.viewModel.initCurrentUserDB()
        this.viewModel.initUserDB()

        this.viewModel.uid.observe(this, {

            if (it == null) {
                Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        })

        this.viewModel.userName.observe(this, {

            if (it == null) {
                this@LikeActivity.showNameInputPopup()
            }
        })

        this.viewModel.cardItems.observe(this, {

            this@LikeActivity.adapter.submitList(it)
            this@LikeActivity.adapter.notifyDataSetChanged()
        })
    }

    /**
     * Card stack view 초기화
     */
    private fun initCardStackView() {

        val stackView = this.binding.cardStackView
        stackView.layoutManager = this.manager
        stackView.adapter = this.adapter
    }

    /**
     * 처음 접속 시 이름을 설정하는 창 출력
     */
    private fun showNameInputPopup() {

        val editText = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("이름을 입력해주세요")
            .setView(editText)
            .setPositiveButton("저장") { _, _ ->

                if (editText.text.isEmpty()) {

                    showNameInputPopup()
                } else {

                    this.viewModel.saveUserName(editText.text.toString())
                }
            }
            .setCancelable(false)
            .show()
    }

    /**
     * 좋아요 기능
     */
    private fun like() {

        val card = this.viewModel.likeFriend(this.manager.topPosition - 1)

        card?.let {
            Toast.makeText(this, "${it.name}님을 Like 하셨습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 싫어요 기능
     */
    private fun disLike() {

        val card = this.viewModel.dislikeFriend(this.manager.topPosition - 1)

        card?.let {
            Toast.makeText(this, "${it.name}님을 Dislike 하셨습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}

    override fun onCardSwiped(direction: Direction?) {

        when (direction) {
            Direction.Right -> like()
            Direction.Left -> disLike()
            else -> {
                return
            }
        }
    }

    override fun onCardRewound() {
        return
    }

    override fun onCardCanceled() {
        return
    }

    override fun onCardAppeared(view: View?, position: Int) {
        return
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        return
    }
}