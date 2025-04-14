package com.mowieinc.mowiekotlin

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.mowieinc.mowiekotlin.databinding.JobCardBinding

class JobAdapter(
    private val onItemClick: (Job) -> Unit, // For general item click
    private val onDeleteClick: (Job) -> Unit, // For delete icon click
    private val emptyTextView: TextView // Pass the empty view
) : ListAdapter<Job, JobAdapter.JobViewHolder>(JobDiffCallback()) {

    private var fullJobList: List<Job> = listOf() // Stores all jobs for filtering

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = JobCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position)
        Log.d("JobAdapter", "Binding package: ${job.packageName}") // Debug log
        holder.bind(job, onItemClick, onDeleteClick)
    }

    class JobViewHolder(private val binding: JobCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job, onItemClick: (Job) -> Unit, onDeleteClick: (Job) -> Unit) {
            binding.apply {
                textAddress.text = "${job.streetnumber} ${job.streetname}"
                textRegularity.text = "Regularity: ${job.frequency}"
                textStatus.text = "Job Status: ${job.status}"
                textPackage.text = "Package: ${job.packageName}"
                textDay.text = "Day: ${job.day}"

                root.setOnClickListener { onItemClick(job) }
                deleteIcon.setOnClickListener { onDeleteClick(job) }
            }
        }
    }

    // Save the original list and update the displayed list
    fun submitFullList(jobList: List<Job>) {
        fullJobList = jobList
        submitList(jobList)
        checkEmptyState()
    }

    // Filter method for search
    fun filterList(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullJobList // Reset to full list when query is empty
        } else {
            fullJobList.filter { job ->
                job.streetnumber.contains(query, ignoreCase = true) ||
                        job.streetname.contains(query, ignoreCase = true) ||
                        job.cityaddress.contains(query, ignoreCase = true) ||
                        job.stateaddress.contains(query, ignoreCase = true) ||
                        job.zipcode.contains(query, ignoreCase = true) ||
                        job.status.contains(query, ignoreCase = true) ||
                        job.packageName.contains(query, ignoreCase = true) ||
                        job.day.contains(query, ignoreCase = true)
            }
        }
        submitList(filteredList) // Update the displayed list
    }

    fun getJobCount(): Int {
        return fullJobList.size
    }

    private fun checkEmptyState() {
        emptyTextView.visibility = if (fullJobList.isEmpty()) View.VISIBLE else View.GONE
    }
}

// DiffUtil for efficient updates
class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.jobid == newItem.jobid // Replace with unique ID if available
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}
