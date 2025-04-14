package com.mowieinc.mowiekotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RedirectActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Optionally show a splash or loading screen
        setContentView(R.layout.activity_redirect)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user == null) {
            // Go back to login if user is not authenticated
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(user.uid)
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val accountType = snapshot.child("accountType").getValue(String::class.java)
                    when (accountType) {
                        "User" -> startActivity(Intent(this@RedirectActivity, UserHomeActivity::class.java))
                        "Pro" -> startActivity(Intent(this@RedirectActivity, ProHomeActivity::class.java))
                        else -> {
                            // Default or error case
                            startActivity(Intent(this@RedirectActivity, LoginActivity::class.java))
                        }
                    }
                    finish()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    startActivity(Intent(this@RedirectActivity, LoginActivity::class.java))
                    finish()
                }
            })
        }
    }
}