package com.mowieinc.mowiekotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import java.io.IOException

class OnboardActivity : AppCompatActivity() {

    private lateinit var launchButton: Button
    private lateinit var logOutButton: Button

    private val connectId: String = "YOUR_CONNECT_ID_HERE" // Replace with actual ID from Pro object

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("OnboardActivity", "1")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard_needed)
        Log.d("OnboardActivity", "2")

        // Initialize UI elements
        launchButton = findViewById(R.id.launchButton)
        logOutButton = findViewById(R.id.logOutButton)
        Log.d("OnboardActivity", "3")
        // Set button click listeners
        launchButton.setOnClickListener { sendGetRequest() }
        logOutButton.setOnClickListener { signOut() }
        Log.d("OnboardActivity", "4")
    }

    private fun sendGetRequest() {
        val url = "https://mowie-pro-server.onrender.com/onboarding-link?account=$connectId"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Onboard", "Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    Log.d("Onboard", "Response: $responseBody")

                    // Open the received URL in the browser
                    runOnUiThread {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    }
                }
            }
        })
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}