package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // SEND_MONEY, MOBILE_RECHARGE, etc.
    val title: String,
    val titleBn: String,
    val recipientOrSource: String,
    val recipientName: String = "",
    val amount: Double,
    val fee: Double = 0.0,
    val trxId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isExpense: Boolean = true,
    val reference: String = "",
    val operatorOrProvider: String = ""
)
