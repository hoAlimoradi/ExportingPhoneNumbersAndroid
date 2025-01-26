package com.ho.exportingphonenumbersandroid.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ContactItemModel(
    @Transient
    val id: String = "",
    @SerialName("name")
    val displayName: String,
    val phoneNumber: String,
    @Transient
    val isSelected: Boolean = false
)
