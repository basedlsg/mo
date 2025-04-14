package com.mowieinc.mowiekotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class EarningsActivity : AppCompatActivity() {

    private lateinit var pro: Pro // Assuming Pro is a data class with a connectId property

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var jobAdapter: JobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_earnings)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        val launchButton: Button = findViewById(R.id.launchButton)

        launchButton.setOnClickListener {
            Log.d("EarningsActivity", "Launch Button Selected!")
            sendPostRequest { result ->
                runOnUiThread {
                    when (result) {
                        is Result.Success -> {
                            val url = result.data
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                        }
                        is Result.Failure -> {
                            Log.e("EarningsActivity", "Request failed: ${result.exception}")
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_action_bar, menu)
        val searchItem = menu.findItem(R.id.menu_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    jobAdapter.filterList(it)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                jobAdapter.filterList(newText.orEmpty())
                return true
            }
        })
        return true
    }

    private fun setupDrawer(toolbar: Toolbar, user: User) {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> navigateToHome()
                R.id.menu_earning -> navigateToEarning(pro)
                R.id.menu_rating -> navigateToRating(pro)
                R.id.menu_joblist -> navigateToJobList(pro)
                R.id.menu_account -> navigateToAccount(pro)
                R.id.menu_promo -> navigateToPromo(pro)
                R.id.menu_logout -> navigateToLogout()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, ProHomeActivity::class.java))
    }

    private fun navigateToEarning(pro: Pro) {
        val intent = Intent(this, EarningsActivity::class.java).apply {
            putExtra("pro_data", pro)
        }
        startActivity(intent)
    }

    private fun navigateToRating(pro: Pro) {
        val intent = Intent(this, ProAccountActivity::class.java).apply {
            putExtra("pro_data", pro)
        }
        startActivity(intent)
    }

    private fun navigateToAccount(pro: Pro) {
        val intent = Intent(this, ProAccountActivity::class.java).apply {
            putExtra("pro_data", pro)
        }
        startActivity(intent)
    }

    private fun navigateToJobList(pro: Pro) {
        val intent = Intent(this, ProHomeActivity::class.java).apply {
            putExtra("pro_data", pro)
        }
        startActivity(intent)
    }

    private fun navigateToPromo(pro: Pro) {
        val showPromotions = false
        if (showPromotions) {
            startActivity(Intent(this, ProHomeActivity::class.java).apply {
                putExtra("pro_data", pro)
            })
        } else {
            AlertDialog.Builder(this)
                .setTitle("No Promotions Available")
                .setMessage("There are no promotions at this time. Please try again later.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun navigateToLogout() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun sendPostRequest(callback: (Result<String>) -> Unit) {
        val connectID = pro.connectid
        val url = "https://mowie-pro-server.onrender.com/v1/accounts/$connectID/login_links"

        val jsonPayload = JSONObject().apply {
            put("account", connectID)
        }

        val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("EarningsActivity", "Error: ${e.message}")
                callback(Result.Failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val jsonResponse = JSONObject(responseBody)
                    val link = jsonResponse.optString("url", "")
                    if (link.isNotEmpty()) {
                        callback(Result.Success(link))
                    } else {
                        callback(Result.Failure(Exception("Invalid URL")))
                    }
                }
            }
        })
    }

    // Custom Result wrapper class for handling success/failure
    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Failure(val exception: Exception) : Result<Nothing>()
    }
}
