package com.clonecoding.clonetinder.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.clonecoding.clonetinder.R
import com.clonecoding.clonetinder.adapters.MatchedUserAdapter
import com.clonecoding.clonetinder.databinding.ActivityMatchedUserBinding
import com.clonecoding.clonetinder.viewmodels.MatchedUserViewModel

class MatchedUserActivity : AppCompatActivity() {

    // 뷰 모델
    private lateinit var viewModel: MatchedUserViewModel

    // 뷰 바인딩
    private lateinit var binding: ActivityMatchedUserBinding

    private val adapter = MatchedUserAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.initDataBinding()

        this.initViewModel()

        this.initMatchedUserRecyclerView()

        this.viewModel.updateMatchedUser()
    }

    /**
     * 뷰 모델 설정
     */
    private fun initViewModel() {

        this.viewModel = ViewModelProvider(this)[MatchedUserViewModel::class.java]

        this.viewModel.uid.observe(this, {

            if (it == null) {
                Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        })

        this.viewModel.cardItems.observe(this, {

            this@MatchedUserActivity.adapter.submitList(it)
        })
    }

    /**
     * 데이터 바인딩 설정
     */
    private fun initDataBinding() {

        this.binding = DataBindingUtil.setContentView(
            this, R.layout.activity_matched_user
        )
        this.binding.lifecycleOwner = this
    }

    private fun initMatchedUserRecyclerView() {

        val recyclerView = this.binding.matchedUserRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}