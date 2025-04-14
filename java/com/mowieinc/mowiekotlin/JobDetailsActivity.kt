package com.mowieinc.mowiekotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.mowieinc.mowiekotlin.databinding.ActivityJobDetailsBinding

class JobDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailsBinding

    val job: Job? = intent.getParcelableExtra("JOB_DETAILS")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Call Pro Button
        binding.callProButton.setOnClickListener {
            val phoneNumber = "tel:1234567890" // Replace with the actual Pro's phone number
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse(phoneNumber)
            }
            startActivity(callIntent)
        }

        // Set up Text Pro Button
        binding.textProButton.setOnClickListener {
            val phoneNumber = "sms:1234567890" // Replace with the actual Pro's phone number
            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(phoneNumber)
            }
            startActivity(smsIntent)
        }

        // Set up Save Note Button
        binding.jobNoteButton.setOnClickListener {
            val note = binding.noteText.editText?.text.toString()
            if (note.isNotBlank()) {
                // Handle saving the note (e.g., save to database)
                val job = Job(
                    streetnumber = job!!.streetnumber,
                    streetname = job.streetname,
                    cityaddress = job.cityaddress,
                    stateaddress = job.stateaddress,
                    zipcode = job.zipcode,
                    frequency = job.frequency,
                    packageName = job.packageName,
                    day = job.day,
                    yardsize = job.yardsize,
                    note = note,
                    status = job.status,
                    subid = job.subid,
                    userid = job.userid,
                    proid = job.proid,
                    carphotourl = job.carphotourl,
                    jobstate = job.jobstate,
                    jobid = job.jobid,
                    profilephotourl = job.profilephotourl,
                    rating = job.rating
                )

                val database = FirebaseDatabase.getInstance().getReference("job").child(job.userid).child(job.jobid)

                database.setValue(job)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("JobDetailsActivity", "Note updated successfully!")
                        } else {
                            Log.d("JobDetailsActivity", "Note was not Successful!")
                        }
                    }

                Toast.makeText(this, "Note saved: $note", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a note before saving.", Toast.LENGTH_SHORT).show()
            }
        }

        // Example: Show Payment Button dynamically
        val shouldShowPayment = true // Replace with your condition
        if (shouldShowPayment) {
            binding.paymentButton.visibility = View.VISIBLE
        }

        // Set up Payment Button
        binding.paymentButton.setOnClickListener {
            // Handle payment flow
            Toast.makeText(this, "Proceeding to payment...", Toast.LENGTH_SHORT).show()
        }
    }
}