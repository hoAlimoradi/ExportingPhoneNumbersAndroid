package com.ho.exportingphonenumbersandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ho.exportingphonenumbersandroid.data.ContactItemModel

class ContactsAdapter(
    private val onSelectionChanged: (String) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    private val contacts: MutableList<ContactItemModel> = mutableListOf()

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        private val checkBox: CheckBox = itemView.findViewById(R.id.contactCheckBox)

        fun bind(contact: ContactItemModel) {
            nameTextView.text = contact.displayName

            checkBox.isChecked = contact.isSelected
            phoneTextView.text = contact.phoneNumber
            checkBox.setOnClickListener {
                onSelectionChanged(contact.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_list_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    fun updateList(items: List<ContactItemModel>) {
        contacts.clear()
        contacts.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = contacts.size
}