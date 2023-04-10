package com.example.githubuser

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.githubuser.data.Result
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.data.local.entity.FavoriteUser
import com.example.githubuser.databinding.FragmentFollowBinding
import com.example.githubuser.ui.DetailUserActivity
import com.example.githubuser.ui.factory.ViewModelFactory
import com.example.githubuser.ui.adapter.MainAdapter

class FollowFragment : Fragment() {
    private lateinit var binding: FragmentFollowBinding

    private fun followViewModel(): FollowViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        val viewModel: FollowViewModel by viewModels {
            factory
        }
        return viewModel
    }

    private var position = 1
    private var username: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowBinding.inflate(inflater, container, false)

        followViewModel().isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFollow.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvFollow.addItemDecoration(itemDecoration)
        return binding.root
    }

    private fun showLoading(it: Boolean) {
        if (it) {
            binding.progressBarFragment.visibility = View.VISIBLE
        } else {
            binding.progressBarFragment.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainAdapter = MainAdapter()
        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME)
        }
        if (position == 1) {
            username?.let {
                followViewModel().getFollowerList(it).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        setUserFollow(result, mainAdapter)
                    }
                }
            }
        } else {
            username?.let {
                followViewModel().getFollowingList(it).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        setUserFollow(result, mainAdapter)
                    }
                }
            }
        }

        mainAdapter.setOnItemClickCallback(object : MainAdapter.OnItemClickCallback {
            override fun onItemClicked(login: String, url: String?, bookmark: Boolean) {
                showSelectedUser(login, url, bookmark)
            }
        })

        binding.rvFollow.apply {
            layoutManager = LinearLayoutManager(context)
            val itemDecoration =
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            binding.rvFollow.addItemDecoration(itemDecoration)
            setHasFixedSize(true)
            adapter = mainAdapter
        }
    }

    private fun setUserFollow(
        result: Result<List<FavoriteUser>>,
        mainAdapter: MainAdapter
    ) {
        when (result) {
            is Result.Loading -> {
                showLoading(true)
            }
            is Result.Success -> {
                showLoading(false)
                val fUserData = result.data
                mainAdapter.submitList(fUserData)
            }
            is Result.Error -> {
                showLoading(false)
                Toast.makeText(
                    context,
                    "Terjadi kesalahan: " + result.error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showSelectedUser(user: String, url: String?, bookmarked: Boolean) {
        val intent = Intent(activity, DetailUserActivity::class.java)
        intent.putExtra(DetailUserActivity.name, user)
        intent.putExtra(DetailUserActivity.avatarUrl, url)
        intent.putExtra(DetailUserActivity.bookmarked, bookmarked)

        startActivity(intent)
    }

    companion object {
        var ARG_POSITION = "0"
        var ARG_USERNAME = "EXTRA_NAME"
    }
}