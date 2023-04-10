package com.example.githubuser.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.githubuser.databinding.FragmentModeBinding
import com.example.githubuser.ui.factory.ModeViewModelFactory
import androidx.datastore.preferences.core.Preferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ModeFragment : Fragment() {

    private var binding: FragmentModeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentModeBinding.inflate(inflater, container, false)

        val switchTheme = binding?.switchTheme

        val pref = SettingPreferences.getInstance(requireContext().dataStore)

        val modeViewModel =
            ViewModelProvider(this, ModeViewModelFactory(pref))[ModeViewModel::class.java]

        modeViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme?.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme?.isChecked = false
            }
        }

        switchTheme?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            modeViewModel.saveThemeSetting(isChecked)
        }
        return binding?.root
    }
}