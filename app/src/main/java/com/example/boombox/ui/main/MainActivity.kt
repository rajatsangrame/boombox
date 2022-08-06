package com.example.boombox.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.boombox.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import com.example.boombox.R
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private val mainViewModel by viewModels<MainViewModel>()
  private lateinit var binding: ActivityMainBinding
  private var currentFragmentId = 0

  private val fragmentList = arrayListOf(HomeFragment(), SavedFragment())

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.viewPager.isUserInputEnabled = false
    binding.viewPager.adapter = ScreenSlidePagerAdapter(this)
    binding.navigation.setOnItemSelectedListener(navigationListener)
  }

  private val navigationListener = object : OnItemSelectedListener {
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
      if (currentFragmentId == menuItem.itemId) {
        return false
      }
      currentFragmentId = menuItem.itemId
      when (menuItem.itemId) {
        R.id.navigation_home -> {
          binding.viewPager.setCurrentItem(0, true)
        }
        R.id.navigation_saved -> {
          binding.viewPager.setCurrentItem(1, true)
        }
      }
      return true
    }
  }

  private inner class ScreenSlidePagerAdapter(m: MainActivity) : FragmentStateAdapter(m) {

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = getFragment(position)

    private fun getFragment(position: Int) = fragmentList[position]

  }

}
