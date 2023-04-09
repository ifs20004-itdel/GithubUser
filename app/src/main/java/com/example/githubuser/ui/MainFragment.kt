package com.example.githubuser.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.*
import com.example.githubuser.data.Result
import com.example.githubuser.data.local.entity.FavoriteUser
import com.example.githubuser.databinding.FragmentMainBinding
import com.example.githubuser.ui.adapter.FavoriteUserAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private var bottomNavigationView: BottomNavigationView? = null

    private fun favoriteViewModel(): MainViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        val viewModel: MainViewModel by viewModels {
            factory
        }
        return viewModel
    }

    private fun adapter() = FavoriteUserAdapter { favoriteUser ->
        if (favoriteUser.isBookmarked) {
            favoriteViewModel().deleteFUser(favoriteUser)
        } else {
            favoriteViewModel().saveFUser(favoriteUser)
        }
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fUserAdapter = adapter()

        favoriteViewModel().getUser().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                setUser(result, fUserAdapter)
            }
        }

        fUserAdapter.setOnItemClickCallback(object :
            FavoriteUserAdapter.OnItemClickCallback {
            override fun onItemClicked(login: String, url: String?, bookmark: Boolean) {
                showSelectedUser(login, url, bookmark)
            }
        })

        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(context)
            val itemDecoration =
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            binding.rvUsers.addItemDecoration(itemDecoration)
            setHasFixedSize(true)
            adapter = fUserAdapter
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.option_menu, menu)

        bottomNavigationView = view?.findViewById(R.id.nav_view)
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)

        searchView.setOnQueryTextFocusChangeListener { _, b ->
            if (b) {
                bottomNavigationView?.visibility = View.GONE
            } else {
                bottomNavigationView?.visibility = View.VISIBLE
            }
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                favoriteViewModel().getUserByUsername(query).observe(viewLifecycleOwner) {
                    setUser(it, adapter())
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                favoriteViewModel().getUserByUsername(newText).observe(viewLifecycleOwner) {
                    setUser(it, adapter())
                }
                return false
            }
        })
    }

    private fun setUser(
        result: Result<List<FavoriteUser>>,
        favoriteUserAdapter: FavoriteUserAdapter
    ) {
        when (result) {
            is Result.Loading -> {
                showLoading(true)
                binding.progressBar.visibility = View.VISIBLE
            }
            is Result.Success -> {
                showLoading(false)
                binding.progressBar.visibility = View.GONE
                val fUserData = result.data
                favoriteUserAdapter.submitList(fUserData)
            }
            is Result.Error -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    context,
                    "Terjadi kesalahan: " + result.error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showSelectedUser(user: String, avatarUrl: String?, bookmark: Boolean) {
        val intent = Intent(activity, DetailUserActivity::class.java)
        intent.putExtra(DetailUserActivity.name, user)
        intent.putExtra(DetailUserActivity.avatarUrl, avatarUrl)
        intent.putExtra(DetailUserActivity.bookmarked, bookmark)
        activity?.startActivity(intent)
    }
}