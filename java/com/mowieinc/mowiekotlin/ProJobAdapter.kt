package com.mowieinc.mowiekotlin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mowieinc.mowiekotlin.databinding.ProJobCardBinding

class ProJobAdapter(
    private val onItemClick: (Job) -> Unit,
    private val onDeleteClick: (Job) -> Unit
) : ListAdapter<Job, ProJobAdapter.JobViewHolder>(JobDiffCallback()) {

    private var fullJobList: List<Job> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ProJobCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position)
        holder.bind(job, onItemClick, onDeleteClick)
    }

    class JobViewHolder(private val binding: ProJobCardBinding) : RecyclerView.ViewHolder(binding.root) {
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
                    job.streetnumber, job.streetname, job.cityaddress,
                    job.stateaddress, job.zipcode, job.status,
                    job.packageName, job.day
                ).any { it.contains(query, ignoreCase = true) }
            }
        }
        submitList(ArrayList(filteredList))
    }
}