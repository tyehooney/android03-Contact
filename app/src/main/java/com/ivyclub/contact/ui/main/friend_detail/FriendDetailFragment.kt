package com.ivyclub.contact.ui.main.friend_detail

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ivyclub.contact.R
import com.ivyclub.contact.databinding.FragmentFriendDetailBinding
import com.ivyclub.contact.util.BaseFragment
import com.ivyclub.data.model.FriendData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendDetailFragment :
    BaseFragment<FragmentFriendDetailBinding>(R.layout.fragment_friend_detail) {
    val me = FriendData(
        "01054985135",
        "Kim MinJi",
        "1900.01.01",
        "부캠동기",
        listOf(1,2),
        false,
        mapOf("나이" to "32", "성격" to "좋음", "MBTI" to "ESTJ", "학벌" to "좋음", "캐미" to "나쁨")
    )
    private val viewModel: FriendDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        loadFriendDetail(1)
        initButtons(1)
        arguments?.getLong("id")?.let {
            loadFriendDetail(it)
            initButtons(it)
        }
    }

    private fun loadFriendDetail(id: Long) {
        viewModel.loadFriendData(id)
    }

    private fun setObserver() {
        viewModel.friendData.observe(this, {
            initDetails(it)
        })
    }

    private fun initButtons(id: Long) {
        with(binding) {
            btnFavorite.setOnClickListener {
                val animation = AnimationUtils.loadAnimation(context, R.anim.star_animation)
                btnFavorite.startAnimation(animation)
                viewModel.setFavorite(id, btnFavorite.isChecked)
            }
            ivEdit.setOnClickListener {
                Toast.makeText(context,"Good",Toast.LENGTH_SHORT).show()
            }
            ivBackIcon.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initDetails(friend: FriendData) {
        with(binding) {
            tvName.text = friend.name
            tvGroup.text = friend.groupName
            tvPhoneNum.text = friend.phoneNumber
            btnFavorite.isChecked = friend.isFavorite
            for (key in friend.extraInfo.keys) {
                llExtraInfo.addView(getTitle(key))
                llExtraInfo.addView(getContent(friend.extraInfo[key] ?: ""))
            }
            btnCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${friend.phoneNumber}"))
                startActivity(intent)
            }
        }
        bindPlan()
    }

    private fun getTitle(text: String): TextView {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 100, 0, 0)
        val tv = TextView(context)
        tv.text = text
        tv.setTypeface(null, Typeface.BOLD)
        tv.setTextColor(Color.BLACK)
        tv.textSize = 20f
        tv.layoutParams = layoutParams
        return tv
    }

    private fun getContent(text: String): TextView {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 16, 0, 0)
        val tv = TextView(context)
        tv.text = text
        tv.setTextColor(Color.BLACK)
        tv.textSize = 20f
        tv.layoutParams = layoutParams
        return tv
    }

    private fun bindPlan() {
        binding.llPlan1.visibility = View.GONE
        binding.llPlan2.visibility = View.GONE
    }
}