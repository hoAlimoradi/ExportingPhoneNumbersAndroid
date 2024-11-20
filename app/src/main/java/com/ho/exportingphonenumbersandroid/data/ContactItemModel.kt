package com.ho.exportingphonenumbersandroid.data

data class ContactItemModel(
    val id: String,
    val displayName: String,
    val phoneNumber: String,
    var isSelected: Boolean = false
)
