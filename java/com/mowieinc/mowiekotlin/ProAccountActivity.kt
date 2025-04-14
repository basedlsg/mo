package com.mowieinc.mowiekotlin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class ProAccountActivity : AppCompatActivity() {

    private var pro: Pro? = null

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var profileImage: ImageView
    private lateinit var carImage: ImageView
    private lateinit var phoneNumberInput: EditText
    private lateinit var updatePhoneButton: Button
    private lateinit var resetPasswordButton: Button

    private val PICK_USER_IMAGE = 1
    private val PICK_CAR_IMAGE = 2
    private var selectedImageView: ImageView? = null // To track which image is being updated

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_page) // Make sure this matches your XML file name

        // Initialize Views
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        profileImage = findViewById(R.id.profileImage)
        carImage = findViewById(R.id.carImage)
        phoneNumberInput = findViewById(R.id.phoneNumberInput)
        updatePhoneButton = findViewById(R.id.updatePhoneButton)
        resetPasswordButton = findViewById(R.id.resetPasswordButton)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Click listener for selecting User Image
        profileImage.setOnClickListener {
            selectedImageView = profileImage
            openImagePicker(PICK_USER_IMAGE)
        }

        // Click listener for selecting Car Image
        carImage.setOnClickListener {
            selectedImageView = carImage
            openImagePicker(PICK_CAR_IMAGE)
        }

        // Update Phone Number Button Click
        updatePhoneButton.setOnClickListener {
            val phoneNumber = phoneNumberInput.text.toString()
            if (phoneNumber.isNotEmpty()) {
                Toast.makeText(this, "Phone Number Updated: $phoneNumber", Toast.LENGTH_SHORT)
                    .show()
                // TODO: Update phone number in the backend
            } else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }

        // Reset Password Button Click
        resetPasswordButton.setOnClickListener {
            Toast.makeText(this, "Password reset link sent to your email", Toast.LENGTH_SHORT)
                .show()
            // TODO: Implement password reset functionality
        }

        // Setup Hamburger Menu
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        pro?.let {
            // Use user data here
            println("User received: ${it.firstname} ${it.lastname}")
            // Handle navigation menu clicks
            navigationView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_home -> navigateToHome()
                    R.id.menu_earning -> navigateToEarning(pro!!)
                    R.id.menu_rating -> navigateToRating(pro!!)
                    R.id.menu_joblist -> navigateToJobList(pro!!)
                    R.id.menu_account -> navigateToAccount(pro!!)
                    R.id.menu_promo -> navigateToPromo(pro!!)
                    R.id.menu_logout -> navigateToLogout()
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.menu_action_bar, menu)
            val searchItem = menu.findItem(R.id.menu_search)
            val searchView = searchItem?.actionView as? SearchView

            return true
        }

        // Open Image Picker
        private fun openImagePicker(requestCode: Int) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, requestCode)
        }

        // Handle Image Selection
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK && data != null) {
                val imageUri: Uri? = data.data
                selectedImageView?.setImageURI(imageUri)
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
    }