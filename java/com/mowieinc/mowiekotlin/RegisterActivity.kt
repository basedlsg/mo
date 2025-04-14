package com.mowieinc.mowiekotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import okhttp3.MediaType.Companion.parse
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class RegisterActivity : AppCompatActivity() {

    // Define your Firebase Database reference
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Get references to UI elements
        val firstNameInput = findViewById<EditText>(R.id.etFirstName)
        val lastNameInput = findViewById<EditText>(R.id.etLastName)
        val emailInput = findViewById<EditText>(R.id.etEmail)
        val phoneInput = findViewById<EditText>(R.id.etPhone)
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val confirmPasswordInput = findViewById<EditText>(R.id.etConfirmPassword)
        val accountTypeSwitch = findViewById<Switch>(R.id.switchAccountType)
        val registerButton = findViewById<Button>(R.id.btnRegister)
        val haveAccountButton = findViewById<TextView>(R.id.haveaccount)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Set default state for the switch
        accountTypeSwitch.text = "Customer"
        accountTypeSwitch.setOnCheckedChangeListener { _, isChecked ->
            accountTypeSwitch.text = if (isChecked) "Professional" else "Customer"
        }

        // Handle have account button click
        haveAccountButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Handle register button click
        registerButton.setOnClickListener {
            val firstName = firstNameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val email = emailInput.text.toString()
            val phone = phoneInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()
            val accountType = if (accountTypeSwitch.isChecked) "Professional" else "Customer"

            if (firstName.isBlank()) {
                Toast.makeText(this, "Please enter valid First Name", Toast.LENGTH_SHORT).show()
            } else if (lastName.isBlank()) {
                Toast.makeText(this, "Please enter valid Last Name", Toast.LENGTH_SHORT).show()
            } else if (email.isBlank() || !email.contains('@')) {
                Toast.makeText(this, "Please enter valid Email", Toast.LENGTH_SHORT).show()
            } else if (phone.isBlank() || phone.length != 10) {
                Toast.makeText(this, "Please enter valid Phone Number", Toast.LENGTH_SHORT).show()
            } else if (password.isBlank() || phone.length < 8) {
                Toast.makeText(
                    this,
                    "Please enter valid Password, 8 characters or more!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (confirmPassword != password) {
                Toast.makeText(this, "Passwords do NOT match!", Toast.LENGTH_SHORT).show()
            } else {
                // Show progress
                toggleProgress(progressBar, true)

                val client = OkHttpClient()
                val gson = Gson()

                // Redirect user based on account type
                if (accountType == "Customer") {

                    // Create a Customer object
                    val customer = Customer(
                        Email = email,
                        Phone = phone,
                        Name = "$firstName $lastName"
                    )

                    val url = "https://mowie-service-server.onrender.com/v1/customers"
                    val json = gson.toJson(customer)

                    // Correct way to use MediaType with extension function
                    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()

                    // Request body
                    val requestBody = RequestBody.create(mediaType, json)

                    // Build POST request
                    val request = Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            println("Request failed: ${e.message}")
                            toggleProgress(progressBar, true)
                        }

                        override fun onResponse(call: Call, response: Response) {
                            response.use {
                                if (!response.isSuccessful) {
                                    println("Unexpected code $response")
                                    toggleProgress(progressBar, true)
                                    return
                                } else {
                                    toggleProgress(progressBar, true)

                                    // Parse the response correctly
                                    val responseBody = response.body?.string()
                                    if (responseBody != null && responseBody.startsWith("cus_")) {
                                        // If the response is a plain string starting with "cus_", treat it as the customer ID
                                        val customerid =
                                            responseBody.trim() // Remove any unwanted whitespace

                                        println("Received customerid: $customer")
                                        Log.d("RegisterActivity", "Customer Id: ${customerid}")
                                        registerUser(
                                            customerid,
                                            firstName,
                                            lastName,
                                            email,
                                            phone,
                                            password
                                        )
                                    }
                                }
                            }
                        }
                    })
                } else {
                    val intent = Intent(this, ProSignUpActivity::class.java).apply {
                        putExtra("firstname", firstName)
                        putExtra("lastname", lastName)
                        putExtra("email", email)
                        putExtra("phone", phone)
                        putExtra("password", password)
                        putExtra("accountType", accountType)
                    }
                    startActivity(intent)
                    toggleProgress(progressBar, true)
                }
            }
        }
    }

    private fun toggleProgress(progressBar: ProgressBar, show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

fun registerUser(
    customerid: String,
    firstName: String,
    lastName: String,
    email: String,
    phone: String,
    password: String
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Registration successful, save user data in database
                val user = auth.currentUser
                val userId = user?.uid

                println("User ID: $userId")

                userId?.let {
                    val userMap = hashMapOf(
                        "accountType" to 0,
                        "email" to email,
                        "phonenumber" to phone,
                        "firstname" to firstName,
                        "lastname" to lastName,
                        "customerid" to customerid,
                        "id" to userId,
                        "profilephotourl" to "Not Set"
                    )

                    val userReference = database.child("users").child(userId).setValue(userMap)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Registration successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Navigate to login activity
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to save user data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            } else {
                // Registration failed
                Toast.makeText(
                    this,
                    "Registration failed: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}