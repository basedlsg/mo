package com.mowieinc.mowiekotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mowieinc.mowiekotlin.databinding.JobPostCardBinding

class JobPostAdapter(
    private val onItemClick: (Job) -> Unit, // For general item click
    private val onDeleteClick: (Job) -> Unit, // For delete icon click
    private val emptyTextView: TextView // Pass the empty view
) : ListAdapter<Job, JobPostAdapter.JobViewHolder>(JobDiffCallback()) {

    private var fullJobList: List<Job> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = JobPostCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position)
        holder.bind(job, onItemClick, onDeleteClick)
    }

    class JobViewHolder(private val binding: JobPostCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job, onItemClick: (Job) -> Unit, onDeleteClick: (Job) -> Unit) {
            binding.apply {
                textStreetName.text = "Street: ${job.streetname}"
                textCity.text = "City: ${job.cityaddress}"
                textRegularity.text = "Regularity: ${job.frequency}"
                textPackage.text = "Package: ${job.packageName}"
                textDay.text = "Day: ${job.day}"

                root.setOnClickListener { onItemClick(job) }
                deleteIcon.setOnClickListener { onDeleteClick(job) }
            }
        }
    }

    fun submitFullList(jobList: List<Job>) {
        fullJobList = jobList
        submitList(ArrayList(jobList))
    }

    fun filterList(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullJobList
        } else {
            fullJobList.filter { job ->
                listOf(
                    job.streetname, job.cityaddress, job.frequency,
                    job.packageName, job.day
                ).any { it.contains(query, ignoreCase = true) }
            }
        }
        submitList(ArrayList(filteredList))
    }

    fun getJobCount(): Int {
        return fullJobList.size
    }

    private fun checkEmptyState() {
        emptyTextView.visibility = if (fullJobList.isEmpty()) View.VISIBLE else View.GONE
    }
}

// DiffUtil for efficient updates
class JobPostDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.jobid == newItem.jobid // Replace with unique ID if available
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}
