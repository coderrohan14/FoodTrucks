package com.rohan.foodorderingapp.activity.activity.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.navigation.NavigationView
import android.os.Bundle
import android.provider.Settings
import android.text.method.TextKeyListener.clear
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat

import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.database.MenuDatabase
import com.rohan.foodorderingapp.activity.activity.database.MenuEntity
import com.rohan.foodorderingapp.activity.activity.database.RestaurantDatabase
import com.rohan.foodorderingapp.activity.activity.database.RestaurantEntity
import com.rohan.foodorderingapp.activity.activity.fragment.*
import kotlinx.android.synthetic.main.drawer_header.*

class LoggedInActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var navigationView: NavigationView
    lateinit var frame: FrameLayout
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)

        drawerLayout = findViewById(R.id.drawerLayout)
        toolbar = findViewById(R.id.toolbar)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        navigationView = findViewById(R.id.navigationView)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        frame = findViewById(R.id.frame)
        setUpToolbar()
        progressLayout.visibility = View.VISIBLE
        openHome()
        sharedPreferences =
            getSharedPreferences(getString(R.string.data_file_name), Context.MODE_PRIVATE)
        val header:View = navigationView.getHeaderView(0)
        val headLinLay:LinearLayout = header.findViewById(R.id.headerLinLay)
        val txtWelcome:TextView = headLinLay.findViewById(R.id.txtWelcome)
        txtWelcome.text ="Hello, "+sharedPreferences.getString("Name","Hello User").toString()
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@LoggedInActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FavouritesFragment()).commit()
                    supportActionBar?.title = "Favourites"
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, ProfileFragment())
                        .commit()
                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.OrderHistory -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, OrderHistoryFragment())
                        .commit()
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.faq -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, FaqFragment())
                        .commit()
                    supportActionBar?.title = "FAQs"
                    drawerLayout.closeDrawers()
                }
                R.id.logOut -> {
                    val alert = AlertDialog.Builder(this@LoggedInActivity, R.style.MyDialogTheme)
                    alert.setTitle("Log Out?")
                    alert.setMessage("Are you sure you want to Log Out?")
                    alert.setPositiveButton("Yes") { text, listener ->
                        sharedPreferences.edit().clear().apply()
                        DBAsyncTask(this@LoggedInActivity).execute()
                        val new4 = Intent(this@LoggedInActivity, LoginActivity::class.java)
                        startActivity(new4)
                    }
                    alert.setNegativeButton("No") { text, listener ->
                        openHome()
                        drawerLayout.closeDrawers()
                    }
                    alert.setCancelable(false)
                    alert.create()
                    alert.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
        navigationView.itemIconTintList = null

    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Welcome"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openHome() {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "Home"
        navigationView.setCheckedItem(R.id.home)
        progressLayout.visibility=View.GONE
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when (frag) {
            !is HomeFragment -> openHome()
            else -> finishAffinity()
        }
    }

    class DBAsyncTask(val context: Context) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
           db.restaurantDao().deleteAll()
            return true
        }
    }
}

