package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_account")
data class UserAccount(
    @PrimaryKey val id: Int = 1,
    val name: String = "মোঃ রুমান আহমেদ",
    val nameEn: String = "Md. Ruman Ahmed",
    val phone: String = "01712-345678",
    val balance: Double = 18520.50,
    val rewardPoints: Int = 420,
    val pin: String = "12345",
    val isBiometricEnabled: Boolean = true,
    val isFaceRecognitionEnabled: Boolean = true,
    val twoFactorAuthMode: String = "SMS", // "NONE", "SMS", "AUTHENTICATOR"
    val authenticatorSecret: String = "JBSWY3DPEHPK3PXP",
    val is2faRequiredForTransactions: Boolean = true,
    val dailyLimit: Double = 25000.00,
    val dailyUsed: Double = 3200.00,
    val monthlyLimit: Double = 200000.00,
    val monthlyUsed: Double = 42500.00
)
