package com.example.githubuser.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.githubuser.R
import com.example.githubuser.data.local.entity.FavoriteUser
import com.example.githubuser.data.remote.response.DetailResponse
import com.example.githubuser.databinding.ActivityDetailUserBinding
import com.example.githubuser.ui.adapter.SectionsPagerAdapter
import com.example.githubuser.ui.factory.ViewModelFactory
import com.example.githubuser.ui.viewModel.DetailUserViewModel
import com.example.githubuser.ui.viewModel.FollowViewModel
import com.example.githubuser.ui.viewModel.MainViewModel
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

        val factory: ViewModelFactory = ViewModelFactory.getInstance(application)
        val followViewModel: FollowViewModel by viewModels {
            factory
        }
        val mainViewModel: MainViewModel by viewModels {
            factory
        }

        val newName = intent.getStringExtra(name)
        val url = intent.getStringExtra(avatarUrl)

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
            var isFavorite = true
            mainViewModel.searchFavoriteByUsername(newName).observe(this) {
                if (it) {
                    isFavorite = true
                    ivBookmark?.setImageDrawable(
                        ContextCompat.getDrawable(
                            ivBookmark.context,
                            R.drawable.ic_favorite
                        )
                    )
                } else {
                    isFavorite = false
                    ivBookmark?.setImageDrawable(
                        ContextCompat.getDrawable(
                            ivBookmark.context,
                            R.drawable.ic_favorite_border
                        )
                    )
                }
            }
            val user = FavoriteUser(newName, url)
            binding?.fabFavorites?.setOnClickListener {
                if (isFavorite) {
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
    }
}