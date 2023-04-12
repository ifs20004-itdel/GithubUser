package com.example.githubuser.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.databinding.FragmentMainBinding
import com.example.githubuser.ui.adapter.MainAdapter
import com.example.githubuser.ui.factory.ViewModelFactory
import com.example.githubuser.ui.viewModel.MainViewModel

class FavoriteUserFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: MainViewModel by viewModels {
            factory
        }
        val fUserAdapter = MainAdapter()
        viewModel.getFavoriteUser().observe(viewLifecycleOwner) { bookmarkedFUser ->
            fUserAdapter.submitList(bookmarkedFUser)
        }

        fUserAdapter.setOnItemClickCallback(object :
            MainAdapter.OnItemClickCallback {
            override fun onItemClicked(login: String, url: String?) {
                showSelectedUser(login, url)
            }
        })

        binding?.rvUsers?.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = fUserAdapter
        }
    }

    private fun showSelectedUser(user: String, url: String?) {
        val intent = Intent(activity, DetailUserActivity::class.java)
        intent.putExtra(DetailUserActivity.name, user)
        intent.putExtra(DetailUserActivity.avatarUrl, url)
        activity?.startActivity(intent)
    }
}