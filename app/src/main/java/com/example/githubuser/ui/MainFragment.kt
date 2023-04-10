package com.example.githubuser.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.*
import com.example.githubuser.data.Result
import com.example.githubuser.data.local.entity.FavoriteUser
import com.example.githubuser.databinding.FragmentMainBinding
import com.example.githubuser.ui.adapter.MainAdapter
import com.example.githubuser.ui.factory.ModeViewModelFactory
import com.example.githubuser.ui.factory.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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

    private fun adapter() = MainAdapter()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val pref = SettingPreferences.getInstance(requireContext().dataStore)

        val modeViewModel = ViewModelProvider(this, ModeViewModelFactory(pref)).get(
            ModeViewModel::class.java
        )

        modeViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

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
            MainAdapter.OnItemClickCallback {
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