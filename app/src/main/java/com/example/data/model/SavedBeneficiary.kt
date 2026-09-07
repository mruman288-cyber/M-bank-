package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_beneficiaries")
data class SavedBeneficiary(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val avatarColor: Long = 0xFFE2136E,
    val isFavorite: Boolean = true
)
