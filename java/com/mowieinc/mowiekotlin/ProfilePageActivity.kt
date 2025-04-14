package com.mowieinc.mowiekotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class ProfilePageActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var profileImageView: ImageView
    private lateinit var phoneNumberField: EditText
    private lateinit var updatePhoneButton: Button
    private lateinit var resetPasswordButton: Button

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        auth = FirebaseAuth.getInstance()
        user = intent.getParcelableExtra("user_data")

        profileImageView = findViewById(R.id.profileImage)
        phoneNumberField = findViewById(R.id.phoneNumberInput)
        updatePhoneButton = findViewById(R.id.updatePhoneButton)
        resetPasswordButton = findViewById(R.id.resetPasswordButton)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    profileImageView.setImageURI(it)
                }
            }

        profileImageView.setOnClickListener {
            imagePickerLauncher.launch("image/*")
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

            updatePhoneButton.setOnClickListener {
                val phoneNumber = phoneNumberField.text.toString()
                startPhoneNumberVerification(phoneNumber, user!!)
            }

            resetPasswordButton.setOnClickListener {
                resetPassword()
            }

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
    }

    private fun startPhoneNumberVerification(phoneNumber: String, user: User) {
        // Implement phone number verification logic here
        if (phoneNumber.isBlank() || phoneNumber.length != 10) {
            Toast.makeText(this, "Please enter valid Phone Number", Toast.LENGTH_SHORT).show()
        } else {

            val updatedPhone = User(
                accountType = user.accountType,
                customerid = user.customerid,
                email = user.email,
                firstname = user.firstname,
                id = user.id,
                lastname = user.lastname,
                phonenumber = phoneNumber,
                profilephotourl = user.profilephotourl
            )

            val database = FirebaseDatabase.getInstance().getReference("users").child(user.id!!)

            database.setValue(updatedPhone)
                .addOnSuccessListener {
                    Toast.makeText(this, "Phone number updated in database", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Database update failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun resetPassword() {
        // Implement password reset logic here
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHome() {
        val intent = Intent(this, UserHomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToProfile(user: User) {
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
        val intent = Intent(this, HelpActivity::class.java).apply {
            putExtra("user_data", user)
        }
        startActivity(intent)
    }

    private fun navigateToAbout(user: User) {
        val intent = Intent(this, AboutUsActivity::class.java).apply {
            putExtra("user_data", user)
        }
        startActivity(intent)
    }

    private fun navigateToLogout() {
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