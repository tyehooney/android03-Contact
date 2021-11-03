package com.ivyclub.contact.ui.onboard.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ivyclub.contact.databinding.ItemContactBinding
import com.ivyclub.contact.databinding.ItemPlanListBinding
import com.ivyclub.data.model.AppointmentData
import com.ivyclub.data.model.PhoneContactData

class ContactAdapter: ListAdapter<PhoneContactData,ContactAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemContactBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemContactBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PhoneContactData) {
            binding.tvName.text = data.name
            binding.tvPhoneNum.text = data.phoneNumber
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<PhoneContactData>() {
            override fun areItemsTheSame(oldItem: PhoneContactData, newItem: PhoneContactData) =
                oldItem.phoneNumber == newItem.phoneNumber

            override fun areContentsTheSame(oldItem: PhoneContactData, newItem: PhoneContactData) =
                oldItem == newItem
        }
    }
}