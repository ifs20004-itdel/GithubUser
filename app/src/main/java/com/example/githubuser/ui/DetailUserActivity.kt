package com.example.githubuser.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.githubuser.FollowViewModel
import com.example.githubuser.R
import com.example.githubuser.DetailUserViewModel
import com.example.githubuser.data.local.entity.FavoriteUser
import com.example.githubuser.data.remote.response.DetailResponse
import com.example.githubuser.databinding.ActivityDetailUserBinding
import com.example.githubuser.ui.adapter.FavoriteUserAdapter
import com.example.githubuser.ui.adapter.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailUserActivity : AppCompatActivity() {

    private var binding: ActivityDetailUserBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val userDetailViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[DetailUserViewModel::class.java]

        val followViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[FollowViewModel::class.java]

        val factory: ViewModelFactory = ViewModelFactory.getInstance(application)
        val mainViewModel: MainViewModel by viewModels {
            factory
        }
        val newName = intent.getStringExtra(name)
        val url = intent.getStringExtra(avatarUrl)
        val bookmark = intent.getBooleanExtra(bookmarked, true)
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        var data: DetailResponse?
        if (newName != null) {
            userDetailViewModel.getUserDetail(newName) { userDetail ->
                if (userDetail != null) {
                    data = userDetail
                    setUserDetail(data!!)
                }
            }
            followViewModel.username = newName
            val ivBookmark = binding?.fabFavorites
            mainViewModel.searchBookmarkedByUsername(newName).observe(this) {
                if (it) {
                    ivBookmark?.setImageDrawable(
                        ContextCompat.getDrawable(
                            ivBookmark.context,
                            R.drawable.ic_favorite
                        )
                    )
                } else {
                    ivBookmark?.setImageDrawable(
                        ContextCompat.getDrawable(
                            ivBookmark.context,
                            R.drawable.ic_favorite_border
                        )
                    )
                }
            }

            binding?.fabFavorites?.setOnClickListener {
                val user = FavoriteUser(newName, url, bookmark)
                if (user.isBookmarked) {
                    mainViewModel.deleteFUser(user)
                } else {
                    mainViewModel.saveFUser(user)
                }
            }
            sectionsPagerAdapter.username = newName
        }

        userDetailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setUserDetail(userDetail: DetailResponse) {
        binding?.tvName?.text = userDetail.name
        binding?.tvUsername?.text = userDetail.login
        binding?.let {
            Glide.with(applicationContext)
                .load(userDetail.avatarUrl)
                .into(it.profileImage)
        }
        binding?.tvFollower?.text = "${userDetail.followers} Followers"
        binding?.tvFollowing?.text = "${userDetail.following} Following"
    }

    companion object {
        private val TAB_TITLES = intArrayOf(
            R.string.follower,
            R.string.following
        )
        const val name = "Name"
        const val avatarUrl = "link"
        const val bookmarked = "false"
    }
}