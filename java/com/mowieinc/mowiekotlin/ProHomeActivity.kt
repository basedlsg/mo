package com.mowieinc.mowiekotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mowieinc.mowiekotlin.databinding.ActivityProHomeBinding

class ProHomeActivity : AppCompatActivity() {
    private val proId = FirebaseAuth.getInstance().currentUser?.uid

    private lateinit var binding: ActivityProHomeBinding
    private lateinit var databaseRef: DatabaseReference
    private val database = FirebaseDatabase.getInstance()
    private lateinit var jobAdapter: JobAdapter

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("ProHomeActivity", "Pro Id: ${proId}")
        val emptyTextView = findViewById<TextView>(R.id.emptyTextView) // TextView from XML

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        jobAdapter = JobAdapter(
            onItemClick = { job ->
                val intent = Intent(this, JobDetailsActivity::class.java).apply {
                    putExtra("JOB_DETAILS", job)
                }
                startActivity(intent)
            },
            onDeleteClick = { job ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Job")
                    .setMessage("Are you sure you want to delete ${job.streetname}?")
                    .setPositiveButton("Yes") { _, _ ->
                        val databaseReference = FirebaseDatabase.getInstance()
                            .getReference("job")
                            .child(job.userid)
                            .child(job.jobid)

                        databaseReference.removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Job deleted successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navigateToHome()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    this,
                                    "Failed to delete job: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    .setNegativeButton("No", null)
                    .show()
            },
            emptyTextView = emptyTextView // Pass the empty state TextView
        )

        binding.recyclerViewJobs.apply {
            layoutManager = LinearLayoutManager(this@ProHomeActivity)
            adapter = jobAdapter
        }

    getPro(proId!!) { pro ->
        if (pro != null) {
            println("User found: ${pro.firstname} ${pro.lastname}")

            // Setup Hamburger Menu
            val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            // Handle navigation menu clicks
            navigationView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_home -> {
                        navigateToHome()
                        Log.d("ProHomeActivity", "Navigating to Home")
                    }
                    R.id.menu_earning -> {
                        navigateToEarning(pro)
                        Log.d("ProHomeActivity", "Navigating to Earnings")
                    }
                    R.id.menu_rating -> {
                        navigateToRating(pro)
                        Log.d("ProHomeActivity", "Navigating to Ratings")
                    }
                    R.id.menu_joblist -> {
                        navigateToJobList(pro)
                        Log.d("ProHomeActivity", "Navigating to Job List")
                    }
                    R.id.menu_account -> {
                        navigateToAccount(pro)
                        Log.d("ProHomeActivity", "Navigating to Account")
                    }
                    R.id.menu_promo -> {
                        navigateToPromo(pro)
                        Log.d("ProHomeActivity", "Navigating to Promo")
                    }
                    R.id.menu_logout -> {
                        navigateToLogout()
                        Log.d("ProHomeActivity", "Navigating to Logout")
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

            fetchJobs()
        } else {
            println("Pro not found.")
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

    private fun fetchJobs() {
        val databaseRef = FirebaseDatabase.getInstance().getReference("job")

        // Query jobs where proid matches the current userId
        databaseRef.orderByChild("proid").equalTo(proId.toString())
            .limitToFirst(50) // Limit results to 50 for efficiency
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val jobs = mutableListOf<Job>()

                    for (jobSnapshot in snapshot.children) {
                        val job = jobSnapshot.getValue(Job::class.java)
                        job?.let {
                            // Handle "package" safely
                            it.packageName = jobSnapshot.child("package").getValue(String::class.java) ?: ""
                            jobs.add(it)
                        } ?: Log.w("FetchJobs", "Job data missing or malformed: ${jobSnapshot.key}")
                    }

                    // Update adapter with filtered jobs
                    jobAdapter.submitFullList(jobs)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FetchJobs", "Failed to fetch jobs: ${error.message}")
                }
            })
    }

            fun getPro(proId: String, onResult: (Pro?) -> Unit) {
                // Get a reference to the database
                val database = Firebase.database
                val proRef = database.getReference("users").child(proId) // Adjust path as needed

                // Attach a listener to read the data
                proRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val pro = snapshot.getValue(Pro::class.java) // Map snapshot to User object
                            onResult(pro) // Pass the user object back to the caller
                        } else {
                            onResult(null) // User not found
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onResult(null) // Handle error case
                        println("Database error: ${error.message}")
                    }
                })
            }

    private fun navigateToHome() {
        val intent = Intent(this, ProHomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToEarning(pro: Pro) {
        val intent = Intent(this, EarningsActivity::class.java).apply {
            putExtra("pro_data", pro)
        }
        startActivity(intent)
    }

    private fun navigateToRating(pro: Pro) {
        val intent = Intent(this, ProHomeActivity::class.java).apply {
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
        val intent = Intent(this, JobListActivity::class.java).apply {
            putExtra("pro_data", pro)
        }
        startActivity(intent)
    }

    private fun navigateToPromo(pro: Pro) {
        val showPromotions = false // Replace this with your condition for checking promotions

        if (showPromotions) {
            val intent = Intent(this, ProHomeActivity::class.java).apply {
                putExtra("pro_data", pro)
            }
            startActivity(intent)
        } else {
            showNoPromotionsAlert()
        }
    }

    private fun navigateToLogout() {
        FirebaseAuth.getInstance().signOut()

        // Clear local storage
        getSharedPreferences("PREFS", MODE_PRIVATE).edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

private fun showNoPromotionsAlert() {
    AlertDialog.Builder(this)
        .setTitle("No Promotions Available")
        .setMessage("There are no promotions at this time. Please try again later.")
        .setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss() // Close the dialog
        }
        .show()
    }
}