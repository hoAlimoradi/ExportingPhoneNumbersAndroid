package com.ho.exportingphonenumbersandroid

import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ho.exportingphonenumbersandroid.data.ContactItemModel
import kotlinx.coroutines.launch

class ContactsViewModel : ViewModel() {

    private val _contacts = MutableLiveData<List<ContactItemModel>>()
    val contacts: LiveData<List<ContactItemModel>> get() = _contacts

    var isFirst = true

    fun loadContacts(context: Context) {
        viewModelScope.launch {
            //shouldReload
            if (isFirst && _contacts.value.isNullOrEmpty()) {
                val contactList = getContactsFromContentProvider(context)
                _contacts.value = contactList
                isFirst = false
            } else {
                val existingContacts = contacts.value ?: emptyList()
                _contacts.value = existingContacts
            }
        }
    }

    private fun getContactsFromContentProvider(context: Context): List<ContactItemModel> {
        val contactsList = mutableListOf<ContactItemModel>()
        val contactsCursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        contactsCursor?.let {
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoUriIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)?.replace(" ", "")
                val photoUri = it.getString(photoUriIndex)

                if (!number.isNullOrEmpty()) {
                    contactsList.add(ContactItemModel(id, name, number))
                }
            }
            it.close()
        }
        return contactsList.distinctBy { it.id }
    }

    fun toggleSelection(contactId: String) {
        _contacts.value = contacts.value?.map { contact ->
            if (contact.id == contactId) {
                contact.copy(isSelected = !contact.isSelected)
            } else {
                contact
            }
        }
    }

    fun getSelectedItems(): List<ContactItemModel>? {
        return _contacts.value?.filter { it.isSelected }
    }
}
