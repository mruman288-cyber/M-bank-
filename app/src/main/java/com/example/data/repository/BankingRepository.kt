package com.example.data.repository

import com.example.data.local.BankingDao
import com.example.data.model.SavedBeneficiary
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionType
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class BankingRepository(private val dao: BankingDao) {

    val userAccount: Flow<UserAccount?> = dao.getUserAccount()
    val transactions: Flow<List<TransactionRecord>> = dao.getAllTransactions()
    val beneficiaries: Flow<List<SavedBeneficiary>> = dao.getAllBeneficiaries()

    suspend fun ensureInitialized() {
        val existingUser = dao.getUserAccountDirect()
        if (existingUser == null) {
            val initialUser = UserAccount(
                id = 1,
                name = "মোঃ রুমান আহমেদ",
                nameEn = "Md. Ruman Ahmed",
                phone = "01712-345678",
                balance = 18450.75,
                rewardPoints = 480,
                pin = "12345",
                isBiometricEnabled = true,
                isFaceRecognitionEnabled = true,
                twoFactorAuthMode = "SMS",
                authenticatorSecret = "JBSWY3DPEHPK3PXP",
                is2faRequiredForTransactions = true,
                dailyLimit = 25000.0,
                dailyUsed = 3450.0,
                monthlyLimit = 200000.0,
                monthlyUsed = 48200.0
            )
            dao.insertUserAccount(initialUser)

            val initialBeneficiaries = listOf(
                SavedBeneficiary(name = "আম্মু (Mother)", phone = "01711-223344", avatarColor = 0xFFE2136E),
                SavedBeneficiary(name = "তানভীর (Brother)", phone = "01819-876543", avatarColor = 0xFF00897B),
                SavedBeneficiary(name = "সাকিব (Friend)", phone = "01912-998877", avatarColor = 0xFF3F51B5),
                SavedBeneficiary(name = "ফারহানা (Sister)", phone = "01623-456789", avatarColor = 0xFFFF6F00),
                SavedBeneficiary(name = "মুদি দোকান (Grocery)", phone = "01755-112233", avatarColor = 0xFF43A047)
            )
            dao.insertBeneficiaries(initialBeneficiaries)

            val now = System.currentTimeMillis()
            val hourMs = 3600000L
            val dayMs = 86400000L

            val initialTransactions = listOf(
                TransactionRecord(
                    type = TransactionType.SEND_MONEY.name,
                    title = "Send Money",
                    titleBn = "সেন্ড মানি",
                    recipientOrSource = "01711-223344",
                    recipientName = "আম্মু (Mother)",
                    amount = 2000.0,
                    fee = 5.0,
                    trxId = "BL7K92XP1",
                    timestamp = now - (2 * hourMs),
                    isExpense = true,
                    reference = "মাসিক খরচ"
                ),
                TransactionRecord(
                    type = TransactionType.MOBILE_RECHARGE.name,
                    title = "Mobile Recharge",
                    titleBn = "মোবাইল রিচার্জ",
                    recipientOrSource = "01712-345678",
                    recipientName = "আমার নাম্বার (Self)",
                    amount = 198.0,
                    fee = 0.0,
                    trxId = "MR5X110A",
                    timestamp = now - (5 * hourMs),
                    isExpense = true,
                    reference = "GP মিনিট ও ডাটা প্যাক",
                    operatorOrProvider = "Grameenphone"
                ),
                TransactionRecord(
                    type = TransactionType.ADD_MONEY.name,
                    title = "Add Money",
                    titleBn = "অ্যাড মানি",
                    recipientOrSource = "City Bank Ltd.",
                    recipientName = "Mastercard ****4291",
                    amount = 10000.0,
                    fee = 0.0,
                    trxId = "AM9012BF",
                    timestamp = now - (1 * dayMs),
                    isExpense = false,
                    reference = "বেতন ডিপোজিট"
                ),
                TransactionRecord(
                    type = TransactionType.PAY_BILL.name,
                    title = "Pay Bill",
                    titleBn = "বিল পে",
                    recipientOrSource = "DESCO Prepaid",
                    recipientName = "Customer ID: 1048291",
                    amount = 1250.0,
                    fee = 0.0,
                    trxId = "BP33948K",
                    timestamp = now - (2 * dayMs),
                    isExpense = true,
                    reference = "আগস্ট মাসের বিদ্যুৎ বিল",
                    operatorOrProvider = "DESCO"
                ),
                TransactionRecord(
                    type = TransactionType.CASH_OUT.name,
                    title = "Cash Out",
                    titleBn = "ক্যাশ আউট",
                    recipientOrSource = "01811-992211",
                    recipientName = "রফিক টেলিকম ও এজেন্ট",
                    amount = 3000.0,
                    fee = 44.70, // 1.49% app fee
                    trxId = "CO88219Q",
                    timestamp = now - (3 * dayMs),
                    isExpense = true,
                    reference = "মার্কেট কেনাকাটা"
                ),
                TransactionRecord(
                    type = TransactionType.MAKE_PAYMENT.name,
                    title = "Payment",
                    titleBn = "পেমেন্ট",
                    recipientOrSource = "Daraz Bangladesh",
                    recipientName = "Merchant 0130009988",
                    amount = 890.0,
                    fee = 0.0,
                    trxId = "PM44990L",
                    timestamp = now - (4 * dayMs),
                    isExpense = true,
                    reference = "Order #DZ-991823"
                )
            )

            for (trx in initialTransactions) {
                dao.insertTransaction(trx)
            }
        }
    }

    suspend fun executeTransaction(
        type: TransactionType,
        recipientOrSource: String,
        recipientName: String,
        amount: Double,
        fee: Double,
        reference: String,
        operatorOrProvider: String = "",
        isCredit: Boolean = false
    ): Result<TransactionRecord> {
        val user = dao.getUserAccountDirect() ?: return Result.failure(Exception("User account not found"))

        val totalDeduction = amount + fee

        if (!isCredit && user.balance < totalDeduction) {
            return Result.failure(Exception("পর্যাপ্ত ব্যালেন্স নেই (Insufficient balance)"))
        }

        val newBalance = if (isCredit) user.balance + amount else user.balance - totalDeduction
        val newDailyUsed = if (isCredit) user.dailyUsed else user.dailyUsed + amount
        val newMonthlyUsed = if (isCredit) user.monthlyUsed else user.monthlyUsed + amount
        val rewardBonus = if (isCredit) 10 else ((amount / 100).toInt().coerceAtLeast(2))

        val updatedUser = user.copy(
            balance = newBalance,
            dailyUsed = newDailyUsed,
            monthlyUsed = newMonthlyUsed,
            rewardPoints = user.rewardPoints + rewardBonus
        )
        dao.updateUserAccount(updatedUser)

        val trxId = generateTrxId()
        val record = TransactionRecord(
            type = type.name,
            title = type.titleEn,
            titleBn = type.titleBn,
            recipientOrSource = recipientOrSource,
            recipientName = recipientName.ifBlank { recipientOrSource },
            amount = amount,
            fee = fee,
            trxId = trxId,
            timestamp = System.currentTimeMillis(),
            isExpense = !isCredit,
            reference = reference,
            operatorOrProvider = operatorOrProvider
        )

        dao.insertTransaction(record)
        return Result.success(record)
    }

    suspend fun updatePin(newPin: String): Boolean {
        val user = dao.getUserAccountDirect() ?: return false
        dao.updateUserAccount(user.copy(pin = newPin))
        return true
    }

    suspend fun updateBiometrics(enabled: Boolean): Boolean {
        val user = dao.getUserAccountDirect() ?: return false
        dao.updateUserAccount(user.copy(isBiometricEnabled = enabled))
        return true
    }

    suspend fun updateFaceRecognition(enabled: Boolean): Boolean {
        val user = dao.getUserAccountDirect() ?: return false
        dao.updateUserAccount(user.copy(isFaceRecognitionEnabled = enabled))
        return true
    }

    suspend fun updateTwoFactorAuth(
        mode: String,
        secret: String? = null,
        requireForTx: Boolean? = null
    ): Boolean {
        val user = dao.getUserAccountDirect() ?: return false
        dao.updateUserAccount(
            user.copy(
                twoFactorAuthMode = mode,
                authenticatorSecret = secret ?: user.authenticatorSecret,
                is2faRequiredForTransactions = requireForTx ?: user.is2faRequiredForTransactions
            )
        )
        return true
    }

    suspend fun addBeneficiary(name: String, phone: String) {
        val colors = listOf(0xFFE2136E, 0xFF00897B, 0xFF3F51B5, 0xFFFF6F00, 0xFF43A047, 0xFF7B1FA2)
        val randomColor = colors.random()
        dao.insertBeneficiary(SavedBeneficiary(name = name, phone = phone, avatarColor = randomColor))
    }

    private fun generateTrxId(): String {
        val chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
        val prefix = "BL"
        val randomStr = (1..7).map { chars.random() }.joinToString("")
        return "$prefix$randomStr"
    }
}
