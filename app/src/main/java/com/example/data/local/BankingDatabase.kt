package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.SavedBeneficiary
import com.example.data.model.TransactionRecord
import com.example.data.model.UserAccount

@Database(
    entities = [
        UserAccount::class,
        TransactionRecord::class,
        SavedBeneficiary::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BankingDatabase : RoomDatabase() {
    abstract fun bankingDao(): BankingDao

    companion object {
        @Volatile
        private var INSTANCE: BankingDatabase? = null

        fun getDatabase(context: Context): BankingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BankingDatabase::class.java,
                    "mobile_banking.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
