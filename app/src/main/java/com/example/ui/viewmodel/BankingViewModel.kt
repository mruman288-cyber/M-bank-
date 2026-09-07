package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BankingDatabase
import com.example.data.model.AppLanguage
import com.example.data.model.QuickOffer
import com.example.data.model.SavedBeneficiary
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionType
import com.example.data.model.UserAccount
import com.example.data.repository.BankingRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SheetType {
    SEND_MONEY,
    MOBILE_RECHARGE,
    CASH_OUT,
    MAKE_PAYMENT,
    ADD_MONEY,
    PAY_BILL,
    SAVINGS,
    REMITTANCE,
    MY_QR_CODE,
    SCAN_QR,
    CHANGE_PIN
}

class BankingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BankingRepository

    val userAccount: StateFlow<UserAccount?>
    val transactions: StateFlow<List<TransactionRecord>>
    val beneficiaries: StateFlow<List<SavedBeneficiary>>

    private val _language = MutableStateFlow(AppLanguage.BANGLA)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isBalanceRevealed = MutableStateFlow(false)
    val isBalanceRevealed: StateFlow<Boolean> = _isBalanceRevealed.asStateFlow()

    private val _isBalanceLoading = MutableStateFlow(false)
    val isBalanceLoading: StateFlow<Boolean> = _isBalanceLoading.asStateFlow()

    private val _activeSheet = MutableStateFlow<SheetType?>(null)
    val activeSheet: StateFlow<SheetType?> = _activeSheet.asStateFlow()

    private val _selectedTransactionForReceipt = MutableStateFlow<TransactionRecord?>(null)
    val selectedTransactionForReceipt: StateFlow<TransactionRecord?> = _selectedTransactionForReceipt.asStateFlow()

    private val _selectedBottomTab = MutableStateFlow(0)
    val selectedBottomTab: StateFlow<Int> = _selectedBottomTab.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _recentSuccessTrx = MutableStateFlow<TransactionRecord?>(null)
    val recentSuccessTrx: StateFlow<TransactionRecord?> = _recentSuccessTrx.asStateFlow()

    private var balanceAutoHideJob: Job? = null

    val offersList = listOf(
        QuickOffer(
            id = "off_1",
            titleEn = "Mobile Recharge Cashback",
            titleBn = "মোবাইল রিচার্জে ১০% ক্যাশব্যাক",
            descEn = "Get up to ৳50 cashback on recharge of ৳100 or more.",
            descBn = "১০০ টাকা বা তার বেশি রিচার্জে পাচ্ছেন সর্বোচ্চ ৫০ টাকা পর্যন্ত তাৎক্ষণিক ক্যাশব্যাক।",
            cashbackTag = "১০% ছাড়",
            expiry = "৩১ সেপ্টেম্বর"
        ),
        QuickOffer(
            id = "off_2",
            titleEn = "Free Send Money",
            titleBn = "প্রিয় নাম্বারে সেন্ড মানি ফ্রি",
            descEn = "Send money to up to 5 favorite numbers with zero service fee.",
            descBn = "৫টি পর্যন্ত প্রিয় নাম্বারে প্রতি মাসে ২৫,০০০ টাকা পর্যন্ত সম্পূর্ণ ফ্রি সেন্ড মানি করুন।",
            cashbackTag = "০% চার্জ",
            expiry = "সারাবছর"
        ),
        QuickOffer(
            id = "off_3",
            titleEn = "Electricity Bill Pay Offer",
            titleBn = "বিদ্যুৎ বিল পেমেন্টে কুপন",
            descEn = "Pay electricity bill and win ৳100 discount coupon.",
            descBn = "যেকোনো বিদ্যুৎ বিল পরিশোধে পাচ্ছেন দারাজ শপিং-এ ১০০ টাকা ডিসকাউন্ট কুপন।",
            cashbackTag = "৳১০০ কুপন",
            expiry = "১৫ সেপ্টেম্বর"
        )
    )

    init {
        val dao = BankingDatabase.getDatabase(application).bankingDao()
        repository = BankingRepository(dao)

        userAccount = repository.userAccount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        transactions = repository.transactions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        beneficiaries = repository.beneficiaries.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.BANGLA) AppLanguage.ENGLISH else AppLanguage.BANGLA
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun setBottomTab(tabIndex: Int) {
        _selectedBottomTab.value = tabIndex
    }

    fun toggleBalanceReveal() {
        if (_isBalanceRevealed.value) {
            _isBalanceRevealed.value = false
            balanceAutoHideJob?.cancel()
            return
        }

        viewModelScope.launch {
            _isBalanceLoading.value = true
            delay(400) // Realistic authentic banking tap feedback
            _isBalanceLoading.value = false
            _isBalanceRevealed.value = true

            balanceAutoHideJob?.cancel()
            balanceAutoHideJob = launch {
                delay(4500)
                _isBalanceRevealed.value = false
            }
        }
    }

    fun openSheet(sheet: SheetType) {
        _activeSheet.value = sheet
    }

    fun closeSheet() {
        _activeSheet.value = null
    }

    fun showReceipt(trx: TransactionRecord) {
        _selectedTransactionForReceipt.value = trx
    }

    fun dismissReceipt() {
        _selectedTransactionForReceipt.value = null
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun dismissSuccessTrx() {
        _recentSuccessTrx.value = null
    }

    fun performSendMoney(
        recipientNumber: String,
        recipientName: String,
        amount: Double,
        reference: String,
        enteredPin: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = userAccount.value ?: run {
            onComplete(false, "User not loaded")
            return
        }

        if (enteredPin != user.pin) {
            onComplete(false, if (_language.value == AppLanguage.BANGLA) "ভুল পিন নম্বর প্রদান করেছেন!" else "Incorrect PIN entered!")
            return
        }

        if (amount < 10) {
            onComplete(false, if (_language.value == AppLanguage.BANGLA) "সর্বনিম্ন পরিমাণ ১০ টাকা" else "Minimum amount is ৳10")
            return
        }

        val fee = if (amount > 1000) 5.0 else 0.0

        viewModelScope.launch {
            val result = repository.executeTransaction(
                type = TransactionType.SEND_MONEY,
                recipientOrSource = recipientNumber,
                recipientName = recipientName.ifBlank { recipientNumber },
                amount = amount,
                fee = fee,
                reference = reference,
                isCredit = false
            )

            result.fold(
                onSuccess = { trx ->
                    _recentSuccessTrx.value = trx
                    closeSheet()
                    onComplete(true, if (_language.value == AppLanguage.BANGLA) "সেন্ড মানি সফল হয়েছে!" else "Send Money Successful!")
                },
                onFailure = { err ->
                    onComplete(false, err.message ?: "Transaction failed")
                }
            )
        }
    }

    fun performMobileRecharge(
        phoneNumber: String,
        operator: String,
        amount: Double,
        isPrepaid: Boolean,
        enteredPin: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = userAccount.value ?: return

        if (enteredPin != user.pin) {
            onComplete(false, if (_language.value == AppLanguage.BANGLA) "ভুল পিন নম্বর!" else "Incorrect PIN!")
            return
        }

        if (amount < 10) {
            onComplete(false, if (_language.value == AppLanguage.BANGLA) "সর্বনিম্ন রিচার্জ ১০ টাকা" else "Minimum recharge is ৳10")
            return
        }

        viewModelScope.launch {
            val result = repository.executeTransaction(
                type = TransactionType.MOBILE_RECHARGE,
                recipientOrSource = phoneNumber,
                recipientName = "$operator (${if (isPrepaid) "Prepaid" else "Postpaid"})",
                amount = amount,
                fee = 0.0,
                reference = "$operator Recharge",
                operatorOrProvider = operator,
                isCredit = false
            )

            result.fold(
                onSuccess = { trx ->
                    _recentSuccessTrx.value = trx
                    closeSheet()
                    onComplete(true, if (_language.value == AppLanguage.BANGLA) "মোবাইল রিচার্জ সফল হয়েছে!" else "Recharge Successful!")
                },
                onFailure = { err ->
                    onComplete(false, err.message ?: "Failed")
                }
            )
        }
    }

    fun performCashOut(
        agentNumber: String,
        agentName: String,
        amount: Double,
        enteredPin: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = userAccount.value ?: return

        if (enteredPin != user.pin) {
            onComplete(false, if (_language.value == AppLanguage.BANGLA) "ভুল পিন নম্বর!" else "Incorrect PIN!")
            return
        }

        val fee = (amount * 0.0149).coerceAtLeast(5.0) // 1.49% app fee

        viewModelScope.launch {
            val result = repository.executeTransaction(
                type = TransactionType.CASH_OUT,
                recipientOrSource = agentNumber,
                recipientName = agentName.ifBlank { "Agent $agentNumber" },
                amount = amount,
                fee = Math.round(fee * 100.0) / 100.0,
                reference = "Agent Cash Out",
                isCredit = false
            )

            result.fold(
                onSuccess = { trx ->
                    _recentSuccessTrx.value = trx
                    closeSheet()
                    onComplete(true, if (_language.value == AppLanguage.BANGLA) "ক্যাশ আউট সফল হয়েছে!" else "Cash Out Successful!")
                },
                onFailure = { err ->
                    onComplete(false, err.message ?: "Failed")
                }
            )
        }
    }

    fun performPayment(
        merchantCode: String,
        merchantName: String,
        amount: Double,
        invoiceRef: String,
        enteredPin: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = userAccount.value ?: return

        if (enteredPin != user.pin) {
            onComplete(false, if (_language.value == AppLanguage.BANGLA) "ভুল পিন নম্বর!" else "Incorrect PIN!")
            return
        }

        viewModelScope.launch {
            val result = repository.executeTransaction(
                type = TransactionType.MAKE_PAYMENT,
                recipientOrSource = merchantCode,
                recipientName = merchantName.ifBlank { "Merchant $merchantCode" },
                amount = amount,
                fee = 0.0,
                reference = invoiceRef.ifBlank { "Payment" },
                isCredit = false
            )

            result.fold(
                onSuccess = { trx ->
                    _recentSuccessTrx.value = trx
                    closeSheet()
                    onComplete(true, if (_language.value == AppLanguage.BANGLA) "পেমেন্ট সফল হয়েছে!" else "Payment Successful!")
                },
                onFailure = { err ->
                    onComplete(false, err.message ?: "Failed")
                }
            )
        }
    }

    fun performAddMoney(
        sourceType: String, // "Mastercard / Visa Card" or "Bank Transfer"
        accountDetails: String,
        amount: Double,
        onComplete: (Boolean, String) -> Unit
    ) {
        if (amount < 50) {
            onComplete(false, if (_language.value == AppLanguage.BANGLA) "সর্বনিম্ন ৫০ টাকা অ্যাড মানি করা যাবে" else "Minimum add money is ৳50")
            return
        }

        viewModelScope.launch {
            val result = repository.executeTransaction(
                type = TransactionType.ADD_MONEY,
                recipientOrSource = sourceType,
                recipientName = accountDetails,
                amount = amount,
                fee = 0.0,
                reference = "Add Money to Wallet",
                isCredit = true
            )

            result.fold(
                onSuccess = { trx ->
                    _recentSuccessTrx.value = trx
                    closeSheet()
                    onComplete(true, if (_language.value == AppLanguage.BANGLA) "টাকা সফলভাবে ওয়ালেটে যুক্ত হয়েছে!" else "Money added successfully!")
                },
                onFailure = { err ->
                    onComplete(false, err.message ?: "Failed")
                }
            )
        }
    }

    fun performPayBill(
        billerName: String,
        billerNameBn: String,
        billNumber: String,
        amount: Double,
        month: String,
        enteredPin: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = userAccount.value ?: return

        if (enteredPin != user.pin) {
            onComplete(false, if (_language.value == AppLanguage.BANGLA) "ভুল পিন নম্বর!" else "Incorrect PIN!")
            return
        }

        viewModelScope.launch {
            val result = repository.executeTransaction(
                type = TransactionType.PAY_BILL,
                recipientOrSource = billerName,
                recipientName = "Bill No: $billNumber ($month)",
                amount = amount,
                fee = 0.0,
                reference = "$billerName - $month",
                operatorOrProvider = billerName,
                isCredit = false
            )

            result.fold(
                onSuccess = { trx ->
                    _recentSuccessTrx.value = trx
                    closeSheet()
                    onComplete(true, if (_language.value == AppLanguage.BANGLA) "বিল পরিশোধ সফল হয়েছে!" else "Bill Paid Successfully!")
                },
                onFailure = { err ->
                    onComplete(false, err.message ?: "Failed")
                }
            )
        }
    }

    fun updatePin(newPin: String, onComplete: (Boolean, String) -> Unit) {
        if (newPin.length != 5 || !newPin.all { it.isDigit() }) {
            onComplete(false, if (_language.value == AppLanguage.BANGLA) "পিন অবশ্যই ৫ ডিজিটের সংখ্যা হতে হবে" else "PIN must be 5 digits")
            return
        }

        viewModelScope.launch {
            val success = repository.updatePin(newPin)
            if (success) {
                closeSheet()
                onComplete(true, if (_language.value == AppLanguage.BANGLA) "পিন সফলভাবে পরিবর্তন করা হয়েছে" else "PIN updated successfully")
            } else {
                onComplete(false, "Failed to update PIN")
            }
        }
    }

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateBiometrics(enabled)
            _toastMessage.value = if (_language.value == AppLanguage.BANGLA) {
                if (enabled) "বায়োমেট্রিক লগইন চালু হয়েছে" else "বায়োমেট্রিক লগইন বন্ধ হয়েছে"
            } else {
                if (enabled) "Biometric login enabled" else "Biometric login disabled"
            }
        }
    }

    fun toggleFaceRecognition(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateFaceRecognition(enabled)
            _toastMessage.value = if (_language.value == AppLanguage.BANGLA) {
                if (enabled) "ফেস রিকগনিশন (ফেস আইডি) চালু হয়েছে" else "ফেস রিকগনিশন বন্ধ হয়েছে"
            } else {
                if (enabled) "Face ID authentication enabled" else "Face ID authentication disabled"
            }
        }
    }

    fun updateTwoFactorAuth(mode: String, secret: String? = null, requireForTx: Boolean? = null) {
        viewModelScope.launch {
            repository.updateTwoFactorAuth(mode, secret, requireForTx)
            _toastMessage.value = if (_language.value == AppLanguage.BANGLA) {
                "২-স্তরীয় নিরাপত্তা (2FA) সেটিংস আপডেট হয়েছে"
            } else {
                "Two-Factor Authentication settings updated"
            }
        }
    }

    fun addBeneficiary(name: String, phone: String) {
        viewModelScope.launch {
            repository.addBeneficiary(name, phone)
            _toastMessage.value = if (_language.value == AppLanguage.BANGLA) "নাম্বার সংরক্ষিত হয়েছে" else "Contact saved"
        }
    }
}
