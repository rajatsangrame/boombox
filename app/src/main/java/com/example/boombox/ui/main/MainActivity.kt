package com.example.boombox.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.boombox.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import com.example.boombox.R
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private val mainViewModel by viewModels<MainViewModel>()
  private lateinit var binding: ActivityMainBinding
  private var currentFragmentId = 0

  private val fragmentList = arrayListOf(AudioFragment(), VideoFragment())
  private val permissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
    if (result) notificationPermissionGranted()
    else Log.d(TAG, "notification permission not granted")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    requestNotificationPermission()

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
        R.id.navigation_audio -> {
          binding.viewPager.setCurrentItem(0, true)
        }
        R.id.navigation_video -> {
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

  fun requestNotificationPermission() {
    when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
              ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED -> {
        permissionResult.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
      else -> notificationPermissionGranted()
    }
  }

  private fun notificationPermissionGranted() {
    NotificationUtil.createNotificationChannel(
      this,
      getString(R.string.notification_channel_id),
      R.string.notification_channel_music,
      R.string.notification_desc,
      NotificationUtil.IMPORTANCE_LOW
    )
  }

  companion object {
    private const val TAG = "MainActivity"
  }

}
