package com.mowieinc.mowiekotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    // Define your Firebase Database reference
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val databaseReference = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FirebaseApp.initializeApp(this)

        val emailField: EditText = findViewById(R.id.usernameField)
        val passwordField: EditText = findViewById(R.id.passwordField)
        val submitButton: Button = findViewById(R.id.submitButton)
        val needAccountButton: TextView = findViewById(R.id.needaccount)
        val forgotPasswordButton: TextView = findViewById(R.id.forgotPassword)

        // Handle have account button click
        needAccountButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        submitButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            // Validate inputs
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (password.isEmpty()) {
                Toast.makeText(this, "Enter a valid password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Perform Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                        navigateToHomeActivity()
                    } else {
                        val errorMessage =
                            task.exception?.message ?: "Login failed. Please try again."
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun dismissKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun navigateToHomeActivity() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = databaseReference.child(userId)
            Log.d("LoginActivity", "User Id: ${userId} DB Ref Id: ${userRef} ")
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val accountType = snapshot.child("accountType").getValue(Int::class.java)
                        if (accountType == 0) {
                            Log.d("LoginActivity", "Login Customer Successful!")
                            dismissKeyboard()
                            startActivity(Intent(this@LoginActivity, UserHomeActivity::class.java))
                            finish()
                        } else {
                            startActivity(Intent(this@LoginActivity, ProHomeActivity::class.java))
                            finish()
                        }
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "User data not found!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "Failed to retrieve user data!", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "Database error: ${error.message}")
                }
            })
        } else {
            Toast.makeText(this, "No user is logged in!", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("LoginActivity", "Activity destroyed.")
    }
}