package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.SavedBeneficiary
import com.example.ui.components.DigitalReceiptDialog
import com.example.ui.components.FaceRecognitionDialog
import com.example.ui.components.FingerprintScannerDialog
import com.example.ui.components.TwoFactorVerifyDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LimitsAndOffersScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StatementScreen
import com.example.ui.sheets.AddContactDialog
import com.example.ui.sheets.AddMoneySheet
import com.example.ui.sheets.CashOutSheet
import com.example.ui.sheets.ChangePinDialog
import com.example.ui.sheets.MobileRechargeSheet
import com.example.ui.sheets.MyQrDialog
import com.example.ui.sheets.PayBillSheet
import com.example.ui.sheets.PaymentSheet
import com.example.ui.sheets.RemittanceSheet
import com.example.ui.sheets.SavingsSheet
import com.example.ui.sheets.SendMoneySheet
import com.example.ui.theme.PrimaryRose
import com.example.ui.viewmodel.BankingViewModel
import com.example.ui.viewmodel.SheetType
import kotlinx.coroutines.launch

enum class MainNavigationTab {
    HOME,
    STATEMENT,
    LIMITS_OFFERS,
    PROFILE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankingApp(
    viewModel: BankingViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.userAccount.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val beneficiaries by viewModel.beneficiaries.collectAsState()
    val offers = viewModel.offersList
    val language by viewModel.language.collectAsState()
    val isBalanceRevealed by viewModel.isBalanceRevealed.collectAsState()
    val isBalanceLoading by viewModel.isBalanceLoading.collectAsState()
    val activeSheet by viewModel.activeSheet.collectAsState()
    val activeReceipt by viewModel.selectedTransactionForReceipt.collectAsState()
    val recentSuccessTrx by viewModel.recentSuccessTrx.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val isBangla = language == AppLanguage.BANGLA
    var currentTab by remember { mutableStateOf(MainNavigationTab.HOME) }

    var showMyQrDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }
    var showFingerprintDialog by remember { mutableStateOf(false) }
    var showFaceRecognitionDialog by remember { mutableStateOf(false) }
    var showTwoFactorDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Show toast when triggered
    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = Color(0xFFF8FAFC),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                // 1. Home
                NavigationBarItem(
                    selected = currentTab == MainNavigationTab.HOME,
                    onClick = { currentTab = MainNavigationTab.HOME },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavigationTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text(
                            text = if (isBangla) "হোম" else "Home",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == MainNavigationTab.HOME) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryRose,
                        selectedTextColor = PrimaryRose,
                        indicatorColor = PrimaryRose.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_tab_home")
                )

                // 2. Statement
                NavigationBarItem(
                    selected = currentTab == MainNavigationTab.STATEMENT,
                    onClick = { currentTab = MainNavigationTab.STATEMENT },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavigationTab.STATEMENT) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                            contentDescription = "Statement"
                        )
                    },
                    label = {
                        Text(
                            text = if (isBangla) "স্টেটমেন্ট" else "Statement",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == MainNavigationTab.STATEMENT) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryRose,
                        selectedTextColor = PrimaryRose,
                        indicatorColor = PrimaryRose.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_tab_statement")
                )

                // 3. Limits & Offers
                NavigationBarItem(
                    selected = currentTab == MainNavigationTab.LIMITS_OFFERS,
                    onClick = { currentTab = MainNavigationTab.LIMITS_OFFERS },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavigationTab.LIMITS_OFFERS) Icons.Filled.LocalOffer else Icons.Outlined.LocalOffer,
                            contentDescription = "Offers"
                        )
                    },
                    label = {
                        Text(
                            text = if (isBangla) "লিমিট ও অফার" else "Limits",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == MainNavigationTab.LIMITS_OFFERS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryRose,
                        selectedTextColor = PrimaryRose,
                        indicatorColor = PrimaryRose.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_tab_offers")
                )

                // 4. Profile
                NavigationBarItem(
                    selected = currentTab == MainNavigationTab.PROFILE,
                    onClick = { currentTab = MainNavigationTab.PROFILE },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavigationTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = {
                        Text(
                            text = if (isBangla) "প্রোফাইল" else "Profile",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == MainNavigationTab.PROFILE) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryRose,
                        selectedTextColor = PrimaryRose,
                        indicatorColor = PrimaryRose.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainNavigationTab.HOME -> {
                    HomeScreen(
                        user = user,
                        transactions = transactions,
                        beneficiaries = beneficiaries,
                        language = language,
                        isBalanceRevealed = isBalanceRevealed,
                        isBalanceLoading = isBalanceLoading,
                        onTapBalance = { viewModel.toggleBalanceReveal() },
                        onQrClick = { showMyQrDialog = true },
                        onServiceClick = { sheetType ->
                            when (sheetType) {
                                SheetType.MY_QR_CODE -> showMyQrDialog = true
                                SheetType.SCAN_QR -> showMyQrDialog = true
                                SheetType.CHANGE_PIN -> showChangePinDialog = true
                                else -> viewModel.openSheet(sheetType)
                            }
                        },
                        onContactClick = { contact ->
                            viewModel.openSheet(SheetType.SEND_MONEY)
                        },
                        onAddContactClick = { showAddContactDialog = true },
                        onTransactionClick = { trx -> viewModel.showReceipt(trx) },
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onSeeAllTransactions = { currentTab = MainNavigationTab.STATEMENT }
                    )
                }

                MainNavigationTab.STATEMENT -> {
                    StatementScreen(
                        transactions = transactions,
                        language = language,
                        onTransactionClick = { trx -> viewModel.showReceipt(trx) }
                    )
                }

                MainNavigationTab.LIMITS_OFFERS -> {
                    LimitsAndOffersScreen(
                        user = user,
                        offers = offers,
                        language = language,
                        onOfferClick = { offer ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = if (isBangla) "অফার কোড সক্রিয় করা হয়েছে: ${offer.cashbackTag}"
                                    else "Offer code activated: ${offer.cashbackTag}",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                }

                MainNavigationTab.PROFILE -> {
                    ProfileScreen(
                        user = user,
                        language = language,
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onToggleBiometric = { viewModel.toggleBiometric(it) },
                        onToggleFaceRecognition = { viewModel.toggleFaceRecognition(it) },
                        onUpdateTwoFactorAuth = { mode, secret, reqForTx ->
                            viewModel.updateTwoFactorAuth(mode, secret, reqForTx)
                        },
                        onChangePinClick = { showChangePinDialog = true },
                        onAddContactClick = { showAddContactDialog = true },
                        onTestFingerprint = { showFingerprintDialog = true },
                        onTestFaceRecognition = { showFaceRecognitionDialog = true },
                        onTestTwoFactor = { showTwoFactorDialog = true }
                    )
                }
            }
        }
    }

    // Bottom Sheets
    when (activeSheet) {
        SheetType.SEND_MONEY -> {
            SendMoneySheet(
                sheetState = sheetState,
                language = language,
                user = user,
                beneficiaries = beneficiaries,
                onDismiss = { viewModel.closeSheet() },
                onConfirmSend = { recipientNumber, recipientName, amount, reference, pin, callback ->
                    viewModel.performSendMoney(recipientNumber, recipientName, amount, reference, pin, callback)
                }
            )
        }

        SheetType.MOBILE_RECHARGE -> {
            MobileRechargeSheet(
                sheetState = sheetState,
                language = language,
                user = user,
                onDismiss = { viewModel.closeSheet() },
                onConfirmRecharge = { phone, operator, amount, isPrepaid, pin, callback ->
                    viewModel.performMobileRecharge(phone, operator, amount, isPrepaid, pin, callback)
                }
            )
        }

        SheetType.CASH_OUT -> {
            CashOutSheet(
                sheetState = sheetState,
                language = language,
                user = user,
                onDismiss = { viewModel.closeSheet() },
                onConfirmCashOut = { agentNumber, agentName, amount, pin, callback ->
                    viewModel.performCashOut(agentNumber, agentName, amount, pin, callback)
                }
            )
        }

        SheetType.MAKE_PAYMENT -> {
            PaymentSheet(
                sheetState = sheetState,
                language = language,
                user = user,
                onDismiss = { viewModel.closeSheet() },
                onConfirmPayment = { merchantCode, merchantName, amount, invoiceRef, pin, callback ->
                    viewModel.performPayment(merchantCode, merchantName, amount, invoiceRef, pin, callback)
                }
            )
        }

        SheetType.ADD_MONEY -> {
            AddMoneySheet(
                sheetState = sheetState,
                language = language,
                onDismiss = { viewModel.closeSheet() },
                onConfirmAddMoney = { source, accountInfo, amount, callback ->
                    viewModel.performAddMoney(source, accountInfo, amount, callback)
                }
            )
        }

        SheetType.PAY_BILL -> {
            PayBillSheet(
                sheetState = sheetState,
                language = language,
                onDismiss = { viewModel.closeSheet() },
                onConfirmPayBill = { billerName, billerNameBn, accountNum, amount, month, pin, callback ->
                    viewModel.performPayBill(billerName, billerNameBn, accountNum, amount, month, pin, callback)
                }
            )
        }

        SheetType.SAVINGS -> {
            SavingsSheet(
                sheetState = sheetState,
                language = language,
                onDismiss = { viewModel.closeSheet() },
                onStartSavings = { inst, monthlyAmt, tenure ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = if (isBangla) "$inst এ মাসিক ৳${monthlyAmt.toInt()} টাকার ডিপিএস সফলভাবে চালু হয়েছে"
                            else "DPS at $inst started successfully with ৳${monthlyAmt.toInt()}/month",
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            )
        }

        SheetType.REMITTANCE -> {
            RemittanceSheet(
                sheetState = sheetState,
                language = language,
                onDismiss = { viewModel.closeSheet() }
            )
        }

        SheetType.MY_QR_CODE -> {
            showMyQrDialog = true
            viewModel.closeSheet()
        }

        SheetType.SCAN_QR -> {
            showMyQrDialog = true
            viewModel.closeSheet()
        }

        SheetType.CHANGE_PIN -> {
            showChangePinDialog = true
            viewModel.closeSheet()
        }

        null -> {}
    }

    // Auto show receipt on recent success transaction
    recentSuccessTrx?.let { trx ->
        DigitalReceiptDialog(
            transaction = trx,
            language = language,
            onDismiss = { viewModel.dismissSuccessTrx() }
        )
    }

    // Digital Receipt Slip Dialog (from transaction list click)
    activeReceipt?.let { trx ->
        DigitalReceiptDialog(
            transaction = trx,
            language = language,
            onDismiss = { viewModel.dismissReceipt() }
        )
    }

    // My QR Code Dialog
    if (showMyQrDialog) {
        MyQrDialog(
            user = user,
            language = language,
            onDismiss = { showMyQrDialog = false }
        )
    }

    // Change PIN Dialog
    if (showChangePinDialog) {
        ChangePinDialog(
            language = language,
            onDismiss = { showChangePinDialog = false },
            onSavePin = { newPin ->
                viewModel.updatePin(newPin) { success, msg ->
                    if (success) {
                        showChangePinDialog = false
                    }
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(msg)
                    }
                }
            }
        )
    }

    // Add Contact Dialog
    if (showAddContactDialog) {
        AddContactDialog(
            language = language,
            onDismiss = { showAddContactDialog = false },
            onAdd = { name, phone ->
                viewModel.addBeneficiary(name, phone)
                showAddContactDialog = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = if (isBangla) "প্রিয় নাম্বার যুক্ত করা হয়েছে" else "Contact added successfully"
                    )
                }
            }
        )
    }

    // Fingerprint Scanner Dialog
    if (showFingerprintDialog) {
        FingerprintScannerDialog(
            language = language,
            onDismiss = { showFingerprintDialog = false },
            onSuccess = {
                showFingerprintDialog = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        if (isBangla) "আঙ্গুলের ছাপ সফলভাবে যাচাই সম্পন্ন হয়েছে!" else "Fingerprint authenticated successfully!"
                    )
                }
            }
        )
    }

    // Face Recognition (Face ID) Dialog
    if (showFaceRecognitionDialog) {
        FaceRecognitionDialog(
            language = language,
            onDismiss = { showFaceRecognitionDialog = false },
            onSuccess = {
                showFaceRecognitionDialog = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        if (isBangla) "ফেস আইডি সফলভাবে যাচাই সম্পন্ন হয়েছে!" else "Face ID recognized and verified!"
                    )
                }
            }
        )
    }

    // Two-Factor Authentication (2FA) Dialog
    if (showTwoFactorDialog) {
        TwoFactorVerifyDialog(
            twoFactorMode = user?.twoFactorAuthMode ?: "SMS",
            userPhone = user?.phone ?: "017XXXXXXXX",
            authenticatorSecret = user?.authenticatorSecret ?: "JBSWY3DPEHPK3PXP",
            language = language,
            contextTitle = if (isBangla) "নিরাপত্তা ও লেনদেন যাচাই" else "Security & Transaction Auth",
            onDismiss = { showTwoFactorDialog = false },
            onVerified = {
                showTwoFactorDialog = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        if (isBangla) "দ্বি-স্তরীয় নিরাপত্তা (2FA) সফলভাবে যাচাই করা হয়েছে!" else "Two-factor verification successful!"
                    )
                }
            }
        )
    }
}
