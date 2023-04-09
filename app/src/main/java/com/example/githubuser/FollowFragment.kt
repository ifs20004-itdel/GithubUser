package com.example.githubuser

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.databinding.FragmentFollowBinding
import com.example.githubuser.ui.DetailUserActivity
import com.example.githubuser.ui.adapter.UserAdapter

class FollowFragment : Fragment() {
    private lateinit var binding: FragmentFollowBinding

    private fun followViewModel(): FollowViewModel {
        return ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[FollowViewModel::class.java]
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
        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME)
        }
        if (position == 1) {
            username?.let {
                followViewModel().getFollowerList(it)
            }
            followViewModel().followList.observe(
                viewLifecycleOwner
            ) {
                setUserFollow(it)
            }
        } else {
            username?.let {
                followViewModel().getFollowingList(it)
            }
            followViewModel().followList.observe(
                viewLifecycleOwner
            ) {
                setUserFollow(it)
            }
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
            override fun onItemClicked(data: String) {
                showSelectedUser(data)
            }
        })
    }

    private fun showSelectedUser(user: String) {
        val intent = Intent(view?.context, DetailUserActivity::class.java)
        intent.putExtra(DetailUserActivity.name, user)
        startActivity(intent)
    }

    companion object {
        var ARG_POSITION = "0"
        var ARG_USERNAME = "EXTRA_NAME"
    }
}