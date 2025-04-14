package com.mowieinc.mowiekotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.toLowerCase
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mowieinc.mowiekotlin.ProductManager.getProductByLabel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject
import com.mowieinc.mowiekotlin.BuildConfig

class CreateJobActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    val stripeApiKey = BuildConfig.STRIPE_KEY

    // Retrieve the User object
    private var user: User? = null
    private var yardSize: String? = null

    var invoiceId: String = ""

    private lateinit var paymentSheet: PaymentSheet

    private val eligibleCities = setOf(
        "detroit", "eastpointe", "harper woods", "roseville", "warren", "hazel park",
        "oak park", "ferndale", "southfield", "ecorse", "river rouge", "redford",
        "inkster", "dearborn", "hamtramck", "highland park", "sterling heights",
        "madison heights", "clinton township", "troy"
    )

    private val eligibleStates = setOf(
        "mi"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = intent.getParcelableExtra("user_data")
        yardSize = intent.getStringExtra("yardsize")

        Log.d("CreateJobActivity", "YardSize: $yardSize")

        when (yardSize) {
            "standard" -> setContentView(R.layout.activity_new_job_standard)
            "medium" -> setContentView(R.layout.activity_new_job_medium)
            "large" -> setContentView(R.layout.activity_new_job_large)
            else -> setContentView(R.layout.activity_new_job_standard)
        }

        // Initialize PaymentConfiguration with your publishable key
        PaymentConfiguration.init(
            applicationContext,
            stripeApiKey
        )
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        toolbar = findViewById(R.id.toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.hamburger_menu_24)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        user?.let {
            // Use user data here
            println("User received: ${it.firstname} ${it.lastname}")

            navigationView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_home -> navigateToHome()
                    R.id.menu_profile -> navigateToProfile(user!!)
                    R.id.menu_job -> navigateToNewJob(user!!)
                    //R.id.menu_payment -> navigateToPayment(user!!)
                    R.id.menu_promo -> navigateToPromo(user!!)
                    R.id.menu_help -> navigateToHelp(user!!)
                    R.id.menu_about -> navigateToAbout(user!!)
                    R.id.menu_logout -> navigateToLogout()
                }
                true
            }

            Log.d("CreateJobActivity", "Create Job Post toolbar")

            val streetNumberEditText = findViewById<EditText>(R.id.editTextStreetNumber)
            val streetNameEditText = findViewById<EditText>(R.id.editTextStreetName)
            val cityEditText = findViewById<EditText>(R.id.editTextCity)
            val stateEditText = findViewById<EditText>(R.id.editTextState)
            val zipCodeEditText = findViewById<EditText>(R.id.editTextZipCode)
            val frequencySpinner = findViewById<Spinner>(R.id.spinnerFrequency)
            val daySpinner = findViewById<Spinner>(R.id.spinnerDay)
            val packageSpinner = findViewById<Spinner>(R.id.spinnerPackage)
            val submitButton = findViewById<Button>(R.id.buttonSubmitJob)

            submitButton.setOnClickListener {
                val streetNumber = streetNumberEditText.text.toString().trim()
                val streetName = streetNameEditText.text.toString().trim()
                val city = cityEditText.text.toString().trim()
                val state = stateEditText.text.toString().trim()
                val zipCode = zipCodeEditText.text.toString().trim()
                val frequency = frequencySpinner.selectedItem.toString()
                val day = daySpinner.selectedItem.toString()
                val packageType = packageSpinner.selectedItem.toString()

                if (validateInputs(streetNumber, streetName, city, state, zipCode)) {
                    createJob(
                        streetNumber,
                        streetName,
                        city,
                        state,
                        zipCode,
                        frequency,
                        day,
                        packageType,
                        object : JobCreationCallback {
                            override fun onJobCreated(jobId: String) {
                                val product = getProductByLabel(packageType, frequency)
                                if (product != null) {
                                    fetchEphemeralKey(
                                        user!!.customerid!!
                                    ) { ephemeralKeySecret ->
                                        if (ephemeralKeySecret.isNotEmpty()) {
                                            fetchPaymentIntentClientSecret(product, jobId, frequency, user!!.customerid!!) { clientSecret ->
                                                if (clientSecret.isNotEmpty()) {
                                                    val configuration = PaymentSheet.Configuration(
                                                        "Mowie",
                                                        PaymentSheet.CustomerConfiguration(
                                                            user!!.customerid!!,
                                                            ephemeralKeySecret
                                                        )
                                                    )
                                                    paymentSheet.presentWithPaymentIntent(
                                                        clientSecret,
                                                        configuration
                                                    )
                                                } else {
                                                    showToast("Failed to retrieve payment configuration")
                                                }
                                            }
                                        } else {
                                            showToast("Failed to retrieve ephemeral key")
                                        }
                                    }
                                } else {
                                    showToast("Product not found")
                                }
                            }

                            override fun onJobCreationFailed(errorMessage: String) {
                                showToast("Job creation failed: $errorMessage")
                            }
                        })
                }
            }
        }
    }

    interface JobCreationCallback {
        fun onJobCreated(jobId: String)
        fun onJobCreationFailed(errorMessage: String)
    }

    private fun fetchEphemeralKey(customerId: String, callback: (String) -> Unit) {
        val url = "https://mowie-service-server.onrender.com/create-ephemeral-key"
        val jsonBody = JSONObject().apply {
            put("customerId", customerId)
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonBody, { response ->
            val ephemeralKeySecret = response.optString("ephemeralKeySecret")
            if (ephemeralKeySecret.isNotEmpty()) callback(ephemeralKeySecret)
            else Toast.makeText(this, "Failed to retrieve ephemeral key", Toast.LENGTH_SHORT).show()
        }, { error ->
            Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
        })
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun fetchPaymentIntentClientSecret(product: Product, jobId: String, frequency: String, customerId: String, callback: (String) -> Unit) {

            val requestBody = JSONObject().apply {
                put("jobid", jobId)  // Example amount in cents
                put("name", product.name)
                put("description", product.description)
                put("amount", product.price)
                put("productid", product.productId)
                put("frequency", frequency)
                put("customerid", customerId)
            }
            Log.d("CreateJobActivity", "Fetching payment intent")

            val request = JsonObjectRequest(
                Request.Method.POST,
                "https://mowie-service-server.onrender.com/payment-sheet",
                requestBody,
                { response ->
                    Log.d("PaymentIntentResponse", response.toString())  // Debugging
                    val clientSecret = response.getString("client_secret")  // Correct key
                    callback(clientSecret)
                },
                { error ->
                    Log.e("PaymentIntentError", error.message.orEmpty())
                    Toast.makeText(this, "Failed to fetch payment intent: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
            Volley.newRequestQueue(this).add(request)
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show()
                navigateToNewJob(user!!)
            }
            is PaymentSheetResult.Failed -> {
                Log.d("CreateJobActivity", "Payment Failed: ${paymentSheetResult.error.message}")
                Toast.makeText(this, "Payment Failed: ${paymentSheetResult.error.message}", Toast.LENGTH_LONG).show()
                navigateToNewJob(user!!)
            }
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(this, "Payment Canceled", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateInputs(streetNumber: String, streetName: String, city: String, state: String, zipCode: String): Boolean {
        if (streetNumber.isEmpty()) {
            showToast("Street Number is required")
            return false
        }
        if (streetName.isEmpty()) {
            showToast("Street Name is required")
            return false
        }
        if (city.isEmpty() || !eligibleCities.contains(city.toLowerCase())) {
            showToast("Invaild City")
            return false
        }
        if (state.length != 2 || !eligibleStates.contains(state.toLowerCase())) {
            showToast("Invalid State")
            return false
        }
        if (zipCode.length != 5) {
            showToast("Zip Code must be 5 digits")
            return false
        }
        return true
    }

    private fun createJob(
        streetNumber: String,
        streetName: String,
        city: String,
        state: String,
        zipCode: String,
        frequency: String,
        day: String,
        packageType: String,
        callback: JobCreationCallback
    ) {
        val job = Job(
            streetnumber = streetNumber,
            streetname = streetName,
            cityaddress = city,
            stateaddress = state,
            zipcode = zipCode,
            frequency = frequency,
            packageName = packageType,
            day = day,
            yardsize = yardSize!!,
            note = "No Note Recorded",
            status = "Waiting on Payment",
            subid = "Waiting on Payment",
            userid = user?.id!!,
            proid = "Not Assigned",
            carphotourl = "Not Assigned",
            jobstate = "Waiting on Payment",
            jobid = "",
            profilephotourl = "Not Assigned",
            rating = "N/A"
        )

        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("job").child(user?.id!!)

        val jobKey = database.push().key
        if (jobKey != null) {
            val updatedJob = job.copy(jobid = jobKey)
            database.child(jobKey).setValue(updatedJob)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback.onJobCreated(updatedJob.jobid)
                    } else {
                        callback.onJobCreationFailed(task.exception?.message ?: "Failed to create job")
                    }
                }
        } else {
            callback.onJobCreationFailed("Failed to generate job ID")
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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