package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SavedBeneficiary
import com.example.data.model.TransactionRecord
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface BankingDao {

    @Query("SELECT * FROM user_account WHERE id = 1 LIMIT 1")
    fun getUserAccount(): Flow<UserAccount?>

    @Query("SELECT * FROM user_account WHERE id = 1 LIMIT 1")
    suspend fun getUserAccountDirect(): UserAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(user: UserAccount)

    @Update
    suspend fun updateUserAccount(user: UserAccount)

    @Query("UPDATE user_account SET balance = :newBalance WHERE id = 1")
    suspend fun updateBalance(newBalance: Double)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionRecord): Long

    @Query("SELECT * FROM saved_beneficiaries ORDER BY name ASC")
    fun getAllBeneficiaries(): Flow<List<SavedBeneficiary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeneficiaries(beneficiaries: List<SavedBeneficiary>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeneficiary(beneficiary: SavedBeneficiary)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()
}
