package com.ho.exportingphonenumbersandroid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ho.exportingphonenumbersandroid.data.ContactItemModel

class ContactsViewModel : ViewModel() {
    private val _contacts = MutableLiveData<List<ContactItemModel>>()
    val contacts: LiveData<List<ContactItemModel>> get() = _contacts

    fun loadContacts(contactList: List<ContactItemModel>) {
        _contacts.value = contactList
    }

    fun toggleSelection(contactId: String) {
        _contacts.value = _contacts.value?.map { contact ->
            if (contact.id == contactId) contact.copy(isSelected = !contact.isSelected) else contact
        }
    }
}

