package com.mowieinc.mowiekotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.mowieinc.mowiekotlin.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        user = intent.getParcelableExtra("user_data")

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Help"

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Setup Hamburger Menu
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        user?.let {
            // Use user data here
            println("User received: ${it.firstname} ${it.lastname}")
            // Handle navigation menu clicks
            navigationView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_home -> {
                        navigateToHome()
                        Log.d("UserHomeActivity", "Navigating to Home")
                    }

                    R.id.menu_profile -> {
                        navigateToProfile(user!!)
                        Log.d("UserHomeActivity", "Navigating to Profile")
                    }

                    R.id.menu_job -> {
                        navigateToNewJob(user!!)
                        Log.d("UserHomeActivity", "Navigating to New Job")
                    }

//                    R.id.menu_payment -> {
//                        navigateToPayment(user!!)
//                        Log.d("UserHomeActivity", "Navigating to Payment")
//                    }

                    R.id.menu_promo -> {
                        navigateToPromo(user!!)
                        Log.d("UserHomeActivity", "Navigating to Promo")
                    }

                    R.id.menu_help -> {
                        navigateToHelp(user!!)
                        Log.d("UserHomeActivity", "Navigating to Help")
                    }

                    R.id.menu_about -> {
                        navigateToAbout(user!!)
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
        }

        // Load help content
        binding.helpTextView.text = "Frequently Asked Questions:\n\n" +
                "1. How do I create a job request?\n" +
                "Tap on the 'Create Job' option in the menu and fill in the required details to submit your request.\n\n" +
                "2. How do I pay for services?\n" +
                "Payment is handled through the app and can be made with a credit or debit card.\n\n" +
                "3. How do I contact customer support?\n" +
                "You can reach us at support@mowieapp.com for assistance.\n\n" +
                "If you have further questions, feel free to contact our support team!"

        // Set up email and phone actions
        binding.contactEmailButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@mowieapp.com")
                putExtra(Intent.EXTRA_SUBJECT, "Help Request")
            }
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        }

        /*binding.contactCallButton.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:5551234567")
            }
            startActivity(callIntent)
        }*/
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