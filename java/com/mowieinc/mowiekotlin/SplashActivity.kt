package com.mowieinc.mowiekotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hide status and navigation bars for a full-screen experience
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        val videoView: VideoView = findViewById(R.id.videoView)
        val videoUri = Uri.parse("android.resource://${packageName}/raw/splash_video")
        videoView.setVideoURI(videoUri)

        videoView.setOnCompletionListener {
            navigateToNextScreen()
        }

        videoView.setOnErrorListener { _, _, _ ->
            navigateToNextScreen() // Fallback if video fails to play
            true
        }

        videoView.start()
    }

    private fun navigateToNextScreen() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

            databaseRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val accountType = snapshot.child("accountType").getValue(Int::class.java) ?: 3
                    Log.d("SplashActivity", "accountType: $accountType")

                    when (accountType) {
                        0 -> {
                            Log.d("SplashActivity", "UserHomeActivity")
                            startActivity(Intent(this, UserHomeActivity::class.java))
                            finish()
                        }
                        else -> {
                            val background = snapshot.child("backgroundcheck").getValue(String::class.java) ?: "Unknown"
                            val onboard = snapshot.child("needonboard").getValue(String::class.java) ?: "Unknown"

                            Log.d("SplashActivity", "background: $background")
                            Log.d("SplashActivity", "onboard: $onboard")

                            when {
                                background == "false" -> {
                                    Log.d("SplashActivity", "BackgroundCheckActivity")
                                    startActivity(Intent(this, BackgroundCheckActivity::class.java))
                                }
                                onboard == "true" -> {
                                    Log.d("SplashActivity", "OnboardActivity")
                                    startActivity(Intent(this, OnboardActivity::class.java))
                                }
                                else -> {
                                    Log.d("SplashActivity", "ProHomeActivity")
                                    startActivity(Intent(this, ProHomeActivity::class.java))
                                }
                            }
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}
