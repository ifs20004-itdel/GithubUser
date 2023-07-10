package com.example.githubuser.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.data.remote.response.FollowResponseItem
import com.example.githubuser.databinding.FragmentFollowBinding
import com.example.githubuser.ui.adapter.UserAdapter
import com.example.githubuser.ui.factory.ViewModelFactory
import com.example.githubuser.ui.viewModel.FollowViewModel

class FollowFragment : Fragment() {
    private var _binding: FragmentFollowBinding? = null
    private fun followViewModel(): FollowViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        val viewModel: FollowViewModel by viewModels {
            factory
        }
        return viewModel
    }

    private var position = 1
    private var username: String? = ""

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)

        followViewModel().isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME)
        }
        if (position == 1) {
            username?.let {
                followViewModel().getFollow(it, true)
            }
            followViewModel().followList.observe(
                viewLifecycleOwner
            ) {
                setUserFollow(it)
            }
        } else {
            username?.let {
                followViewModel().getFollow(it, false)
            }
            followViewModel().followList.observe(
                viewLifecycleOwner
            ) {
                setUserFollow(it)
            }
        }

        binding.rvFollow.apply {
            layoutManager = LinearLayoutManager(context)
            val itemDecoration =
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            binding.rvFollow.addItemDecoration(itemDecoration)
            setHasFixedSize(true)
        }
    }

    private fun setUserFollow(listUser: List<FollowResponseItem>) {
        val user = ArrayList<String>()
        val imageAvatar = ArrayList<String>()
        for (i in listUser) {
            user.add(
                """
                    ${i.login}
                """.trimIndent()
            )
            imageAvatar.add(
                """
                    ${i.avatarUrl}
                """.trimIndent()
            )
        }
        val adapter = UserAdapter(user, imageAvatar)

        binding.rvFollow.adapter = adapter

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(username: String, url: String) {
                showSelectedUser(username, url)
            }
        })
    }

    private fun showSelectedUser(user: String, url: String?) {
        val intent = Intent(activity, DetailUserActivity::class.java)
        intent.putExtra(DetailUserActivity.name, user)
        intent.putExtra(DetailUserActivity.avatarUrl, url)
        startActivity(intent)
    }

    private fun showLoading(it: Boolean) {
        if (it) {
            binding.progressBarFragment.visibility = View.VISIBLE
        } else {
            binding.progressBarFragment.visibility = View.GONE
        }
    }

    companion object {
        var ARG_POSITION = "0"
        var ARG_USERNAME = "EXTRA_NAME"
    }
}