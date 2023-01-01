package com.example.chatapp.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.telegram_status_color)

        setSupportActionBar(binding.appBarMain.toolbar)
        drawerLayout = binding.drawerLayout

//        drawerLayout.setStatusBarBackgroundColor(R.color.telegram_drawer_color)
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        setupActionBarWithNavController(navController, drawerLayout)
        navView.setupWithNavController(navController)

        binding.apply {
            newGroup.setOnClickListener {
                drawerLayout.closeDrawer(GravityCompat.START)
                Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.newGroupFragment)
            }
            contacts.setOnClickListener {
                drawerLayout.closeDrawer(GravityCompat.START)
                Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.contactsFragment)
            }
            savedMessages.setOnClickListener {
                drawerLayout.closeDrawer(GravityCompat.START)
                Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.savedMessagesFragment)
            }
            settings.setOnClickListener {
                drawerLayout.closeDrawer(GravityCompat.START)
                Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.settingsFragment)
            }
            inviteFriends.setOnClickListener {
                drawerLayout.closeDrawer(GravityCompat.START)
                Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.inviteFriendsFragment)
            }
            functionTelegram.setOnClickListener {
                drawerLayout.closeDrawer(GravityCompat.START)
                Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.telegramFunctionFragment)
            }
        }
    }

    fun hide() {
        binding.appBarMain.fab.visibility = View.GONE
        supportActionBar?.hide()
    }

    fun show() {
        binding.appBarMain.fab.visibility = View.VISIBLE
        supportActionBar?.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(drawerLayout) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun status(status: String) {
        val reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().uid?:"")

        val hashMap = HashMap<String, Any>()
        hashMap.put("status", status)
        reference.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onStop() {
        super.onStop()
        status("offline")
    }

    override fun onStart() {
        super.onStart()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }

    override fun onDestroy() {
        super.onDestroy()
        status("offline")
    }

}