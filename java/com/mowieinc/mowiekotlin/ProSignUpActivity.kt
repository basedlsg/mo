package com.mowieinc.mowiekotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import org.json.JSONObject

class ProSignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_signup)

        val firstName = intent.getStringExtra("firstname")
        val lastName = intent.getStringExtra("lastname")
        val email = intent.getStringExtra("email")
        val phone = intent.getStringExtra("phone")
        val password = intent.getStringExtra("password")
        val accountType = intent.getStringExtra("accountType")

        val logo: ImageView = findViewById(R.id.logo)
        val businessName: EditText = findViewById(R.id.business_name)
        val ein: EditText = findViewById(R.id.ein)
        val submitButton: Button = findViewById(R.id.submit_button)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Handle register button click
        submitButton.setOnClickListener {
            if (businessName.text.isBlank()) {
                Toast.makeText(this, "Please enter valid Business Name", Toast.LENGTH_SHORT).show()
            } else if (ein.text.length == 9) {
                Toast.makeText(this, "Please enter valid EIN Number", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(
                    accountType!!,
                    firstName!!,
                    lastName!!,
                    email!!,
                    phone!!,
                    password!!,
                    businessName.toString(),
                    ein.toString()
                )
            }
        }
    }

    fun registerUser(
        accountType: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String,
        businessName: String,
        ein: String
    ) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration successful, save user data in database
                    val user = auth.currentUser
                    val userId = user?.uid

                    println("User ID: $userId")

                    createConnect(mapOf("email" to email, "id" to userId!!)) { result ->
                        result.onSuccess { customerId ->
                            println("Customer ID: $customerId")

                            userId?.let {
                                val userMap = hashMapOf(
                                    "accountType" to 1,
                                    "backgroundcheck" to "false",
                                    "businessname" to businessName,
                                    "carphotourl" to "Not Set",
                                    "connectid" to customerId,
                                    "einnumber" to ein,
                                    "email" to email,
                                    "firstname" to firstName,
                                    "id" to userId,
                                    "lastname" to lastName,
                                    "needonboard" to "true",
                                    "phonenumber" to phone,
                                    "profilephotourl" to "Not Set",
                                    "rating" to 0
                                )

                                val userReference =
                                    database.child("users").child(userId).setValue(userMap)
                                        .addOnCompleteListener { dbTask ->
                                            if (dbTask.isSuccessful) {
                                                Toast.makeText(
                                                    this,
                                                    "Registration successful",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                // Navigate to login activity
                                                startActivity(
                                                    Intent(
                                                        this,
                                                        LoginActivity::class.java
                                                    )
                                                )
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
                        }.onFailure { error ->
                            println("Error: ${error.message}")
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

    fun createConnect(pro: Map<String, Any>, completion: (Result<String>) -> Unit) {
        val url = "https://mowie-pro-server.onrender.com/v1/accounts"

        val email = pro["email"] as? String ?: ""
        val proid = pro["id"] as? String ?: ""

        println("Email: $email, Pro ID: $proid")

        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("proid", proid)

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                completion(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        completion(Result.failure(IOException("Server error: ${response.code}")))
                        return
                    }

                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val customerId = jsonResponse.optString("customerId", "")
                            if (customerId.isNotEmpty()) {
                                completion(Result.success(customerId))
                            } else {
                                completion(Result.failure(IOException("Invalid response format")))
                            }
                        } catch (e: Exception) {
                            completion(Result.failure(e))
                        }
                    } else {
                        completion(Result.failure(IOException("No data received")))
                    }
                }
            }
        })
    }
}