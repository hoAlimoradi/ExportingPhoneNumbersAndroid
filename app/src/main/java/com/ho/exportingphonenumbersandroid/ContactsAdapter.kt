package com.ho.exportingphonenumbersandroid

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ho.exportingphonenumbersandroid.data.ContactItemModel

class ContactsAdapter(private val contacts: List<ContactItemModel>, private val onSelectionChanged: (String) -> Unit) :
    RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        val checkBox: CheckBox = itemView.findViewById(R.id.contactCheckBox)

        fun bind(contact: ContactItemModel) {
            nameTextView.text = contact.displayName
            phoneTextView.text = contact.phoneNumber
            checkBox.isChecked = contact.isSelected

            checkBox.setOnCheckedChangeListener { _, isChecked ->
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

    override fun getItemCount(): Int = contacts.size
}
//
//class ContactsAdapter۱(private val contacts: List<ContactItemModel>) :
//    RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {
//
//    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//       // val avatarImageView: ImageView = itemView.findViewById(R.id.avatarImageView)
//        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
//        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
//        val checkBox: CheckBox = itemView.findViewById(R.id.contactCheckBox)
//
//        fun bind(contact: ContactItemModel) {
//            nameTextView.text = contact.displayName
//            phoneTextView.text = contact.phoneNumber
//            checkBox.isChecked = contact.isSelected
//
//            checkBox.setOnCheckedChangeListener { _, isChecked ->
//                contact.isSelected = isChecked
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.contact_list_item, parent, false)
//        return ContactViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
//        holder.bind(contacts[position])
//    }
//
//    override fun getItemCount(): Int = contacts.size
//}
