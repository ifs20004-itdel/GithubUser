package com.example.githubuser.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
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
import com.example.githubuser.databinding.FragmentMainBinding
import com.example.githubuser.ui.adapter.MainAdapter
import com.example.githubuser.ui.adapter.UserAdapter
import com.example.githubuser.ui.factory.ModeViewModelFactory
import com.example.githubuser.ui.factory.ViewModelFactory
import com.example.githubuser.ui.preferences.SettingPreferences
import com.example.githubuser.ui.viewModel.MainViewModel
import com.example.githubuser.ui.viewModel.ModeViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null

    private fun mainViewModel(): MainViewModel {
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

        val modeViewModel =
            ViewModelProvider(this, ModeViewModelFactory(pref))[ModeViewModel::class.java]

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

        mainViewModel().isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        mainViewModel().user.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                setDataUser(result)
            }
        }
        fUserAdapter.setOnItemClickCallback(object :
            MainAdapter.OnItemClickCallback {
            override fun onItemClicked(login: String, url: String?) {
                showSelectedUser(login, url)
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

        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView?

        searchView?.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView?.queryHint = resources.getString(R.string.search_hint)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mainViewModel().getUser(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mainViewModel().getUser(newText)
                return false
            }
        })
    }

    private fun setDataUser(listUser: List<ItemsItem>) {
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

        binding.rvUsers.adapter = adapter

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(username: String, url: String) {
                showSelectedUser(username, url)
            }
        })
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showSelectedUser(user: String, avatarUrl: String?) {
        val intent = Intent(activity, DetailUserActivity::class.java)
        intent.putExtra(DetailUserActivity.name, user)
        intent.putExtra(DetailUserActivity.avatarUrl, avatarUrl)
        activity?.startActivity(intent)
    }
}