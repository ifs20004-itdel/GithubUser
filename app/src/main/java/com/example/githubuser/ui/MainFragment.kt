package com.example.githubuser.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.*
import com.example.githubuser.databinding.ActivityBottomNavigationBinding
import com.example.githubuser.databinding.FragmentMainBinding
import com.example.githubuser.ui.adapter.UserAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private var bottomNavigationView : BottomNavigationView? = null

    private fun mainViewModel(): MainViewModel {
        return ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MainViewModel::class.java]
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentMainBinding.inflate(inflater,container,false)
        val root: View = binding.root
        mainViewModel().user.observe(viewLifecycleOwner){
            user -> setDataUser(user)
        }

        mainViewModel().isLoading.observe(viewLifecycleOwner){
            showLoading(it)
        }

        val layoutManager = LinearLayoutManager(context)
        binding.rvUsers.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(context, layoutManager.orientation)
        binding.rvUsers.addItemDecoration(itemDecoration)
        binding.rvUsers.setHasFixedSize(true)

        return root
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.option_menu,menu)

        bottomNavigationView = view?.findViewById(R.id.nav_view)
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)

        searchView.setOnQueryTextFocusChangeListener { view, b ->
            if(b){
                bottomNavigationView?.visibility = View.GONE
            }else{
                bottomNavigationView?.visibility = View.VISIBLE
            }
        }
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                mainViewModel().searchUser(query)
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                mainViewModel().searchUser(newText)
                return false
            }
        })
    }


    private fun setDataUser(listUser: List<ItemsItem>){
        val user = ArrayList<String>()
        val imageAvatar = ArrayList<String>()
        for(i in listUser){
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

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback{
            override fun onItemClicked(data: String) {
                showSelectedUser(data)
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

    private fun showSelectedUser(user: String){
        val intent = Intent(activity, DetailUserActivity::class.java)
        intent.putExtra(DetailUserActivity.name,user)
        activity?.startActivity(intent)
    }
}