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
import com.mowieinc.mowiekotlin.databinding.ActivityUserHomeBinding

class UserHomeActivity : AppCompatActivity() {
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private lateinit var binding: ActivityUserHomeBinding
    private lateinit var databaseRef: DatabaseReference
    private val database = FirebaseDatabase.getInstance()
    private lateinit var jobAdapter: JobAdapter

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("UserHomeActivity", "User Id: ${userId}")
        val emptyTextView = findViewById<TextView>(R.id.emptyTextView) // TextView from XML

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // Set up RecyclerView
        jobAdapter = JobAdapter(
            onItemClick = { job ->
                // Handle job item click
                val intent = Intent(this, JobDetailsActivity::class.java).apply {
                    putExtra("JOB_DETAILS", job)
                }
                startActivity(intent)
            },
            onDeleteClick = { job ->
                // Handle delete icon click
                AlertDialog.Builder(this)
                    .setTitle("Delete Job")
                    .setMessage("Are you sure you want to delete ${job.streetname}?")
                    .setPositiveButton("Yes") { _, _ ->
                        // Call delete logic
                        val databaseReference =
                            FirebaseDatabase.getInstance().getReference("job").child(job.userid)
                                .child(job.jobid)

                        databaseReference.removeValue()
                            .addOnSuccessListener {
                                Log.d("Firebase", "Job successfully deleted.")
                                Toast.makeText(this, "Job deleted successfully", Toast.LENGTH_SHORT)
                                    .show()
                                navigateToHome()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firebase", "Failed to delete job: ${exception.message}")
                                Toast.makeText(this, "Failed to delete job", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                    .setNegativeButton("No", null)
                    .show()
            },
            emptyTextView = emptyTextView // Pass the empty state TextView
        )

        binding.recyclerViewJobs.apply {
            layoutManager = LinearLayoutManager(this@UserHomeActivity)
            adapter = jobAdapter
        }

        getUser(userId!!) { user ->
            if (user != null) {
                println("User found: ${user.firstname} ${user.lastname}")

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
                    Log.d("UserHomeActivity", "Navigating to Home")
                }
                R.id.menu_profile -> {
                    navigateToProfile(user)
                    Log.d("UserHomeActivity", "Navigating to Profile")
                }
                R.id.menu_job -> {
                    navigateToNewJob(user)
                    Log.d("UserHomeActivity", "Navigating to New Job")
                }
//                R.id.menu_payment -> {
//                    navigateToPayment(user)
//                    Log.d("UserHomeActivity", "Navigating to Payment")
//                }
                R.id.menu_promo -> {
                    navigateToPromo(user)
                    Log.d("UserHomeActivity", "Navigating to Promo")
                }
                R.id.menu_help -> {
                    navigateToHelp(user)
                    Log.d("UserHomeActivity", "Navigating to Help")
                }
                R.id.menu_about -> {
                    navigateToAbout(user)
                    Log.d("UserHomeActivity", "Navigating to About")
                }
                R.id.menu_logout -> {
                    navigateToLogout()
                    Log.d("UserHomeActivity", "Navigating to Logout")
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        fetchJobs()
            } else {
                println("User not found.")
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
                    jobAdapter.filterList(it) // Filter job list
                    searchView.clearFocus()  // Hide keyboard after search
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                jobAdapter.filterList(newText.orEmpty()) // Dynamic filtering
                return true
            }
        })

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun fetchJobs() {
        databaseRef = database.getReference("job").child(userId.toString())

        databaseRef.limitToFirst(50).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jobs = mutableListOf<Job>()
                snapshot.children.forEach { jobSnapshot ->
                    val job = jobSnapshot.getValue(Job::class.java)
                    val packageValue: String? = jobSnapshot.child("package")
                        .getValue(String::class.java) // Get raw value of package
                    Log.d("UserHomeActivity", "Job Package: $packageValue")

                    if (job != null) {
                        Log.d("UserHomeActivity", "Job: $job")
                        job.packageName = packageValue ?: "" // Update the packageName with the raw value
                        jobs.add(job)
                    } else {
                        Log.w("FetchJobs", "Job data missing or malformed: ${jobSnapshot.key}")
                    }
                }
                jobAdapter.submitFullList(jobs)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FetchJobs", "Failed to fetch jobs: ${error.message}")
            }
        })
    }

    fun getUser(userId: String, onResult: (User?) -> Unit) {
        // Get a reference to the database
        val database = Firebase.database
        val userRef = database.getReference("users").child(userId) // Adjust path as needed

        // Attach a listener to read the data
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java) // Map snapshot to User object
                    onResult(user) // Pass the user object back to the caller
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
        // Replace with your intent or fragment transaction
        val intent = Intent(this, UserHomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToProfile(user: User) {
        // Replace with your intent or fragment transaction
        val intent = Intent(this, ProfilePageActivity::class.java).apply {
            putExtra("user_data", user)
        }
        startActivity(intent)
    }

    private fun navigateToNewJob(user: User) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Property Size")
        builder.setMessage("Choose the size of the property for the new job.")

        // First option: Large property
        builder.setPositiveButton("6k - 10k sq ft") { _, _ ->
            val intent = Intent(this, CreateJobActivity::class.java).apply {
                putExtra("user_data", user)
                putExtra("yardsize", "large")
            }
            startActivity(intent)
        }

        // Second option: Medium property
        builder.setNegativeButton("4k - 6k sq ft") { _, _ ->
            val intent = Intent(this, CreateJobActivity::class.java).apply {
                putExtra("user_data", user)
                putExtra("yardsize", "medium")
            }
            startActivity(intent)
        }

        // Third option: Small property
        builder.setNeutralButton("0 - 4k sq ft") { _, _ ->
            val intent = Intent(this, CreateJobActivity::class.java).apply {
                putExtra("user_data", user)
                putExtra("yardsize", "standard")
            }
            startActivity(intent)
        }

        // Show the dialog
        builder.create().show()
    }

//    private fun navigateToPayment(user: User) {
//        // Replace with your intent or fragment transaction
//        val intent = Intent(this, PaymentActivity::class.java).apply {
//            putExtra("user_data", user)
//        }
//        startActivity(intent)
//    }

    private fun navigateToPromo(user: User) {
        val showPromotions = false // Replace this with your condition for checking promotions

        if (showPromotions) {
            val intent = Intent(this, UserHomeActivity::class.java).apply {
                putExtra("user_data", user)
            }
            startActivity(intent)
        } else {
            showNoPromotionsAlert()
        }
    }

    private fun navigateToHelp(user: User) {
        // Replace with your intent or fragment transaction
        val intent = Intent(this, HelpActivity::class.java).apply {
            putExtra("user_data", user)
        }
        startActivity(intent)
    }

    private fun navigateToAbout(user: User) {
        // Replace with your intent or fragment transaction
        val intent = Intent(this, AboutUsActivity::class.java).apply {
            putExtra("user_data", user)
        }
        startActivity(intent)
    }

    private fun navigateToLogout() {
        // Replace with your intent or fragment transaction
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