package com.mowieinc.mowiekotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BackgroundCheckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_background_needed) // Ensure XML file is named correctly

        // UI Elements
        val logoImage: ImageView = findViewById(R.id.backgroundlogoImage)
        val titleText: TextView = findViewById(R.id.backgroundtitleText)
        val descriptionText: TextView = findViewById(R.id.backgrounddescriptionText)
        val btnsupportRequest: Button = findViewById(R.id.supportRequest)
        val btnLogout: Button = findViewById(R.id.backgroundLogout)
        val btndeleteAccount: Button = findViewById(R.id.deleteAccount)

        // Button to resend background check request
        btnsupportRequest.setOnClickListener {
            // Simulate API call or action
            Toast.makeText(this, "Resending background check request...", Toast.LENGTH_SHORT).show()

            // TODO: Implement API call to resend background check request
        }

        // Button to log out
        btnLogout.setOnClickListener {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()

            // TODO: Implement logout functionality (clear session, go to login screen)
            val intent = Intent(this, LoginActivity::class.java) // Replace with your actual login activity
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Button to resend background check request
        btndeleteAccount.setOnClickListener {
            // Simulate API call or action
            Toast.makeText(this, "Resending background check request...", Toast.LENGTH_SHORT).show()

            // TODO: Implement API call to resend background check request
        }
    }
}