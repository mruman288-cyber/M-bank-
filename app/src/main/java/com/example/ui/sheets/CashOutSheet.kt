package com.example.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.UserAccount
import com.example.ui.components.HoldToConfirmButton
import com.example.ui.theme.PrimaryRose
import com.example.util.BanglaFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashOutSheet(
    sheetState: SheetState,
    language: AppLanguage,
    user: UserAccount?,
    onDismiss: () -> Unit,
    onConfirmCashOut: (agentNumber: String, agentName: String, amount: Double, pin: String, (Boolean, String) -> Unit) -> Unit
) {
    val isBangla = language == AppLanguage.BANGLA

    var agentNumber by remember { mutableStateOf("") }
    var agentName by remember { mutableStateOf("রফিক টেলিকম ও এজেন্ট") }
    var amountText by remember { mutableStateOf("") }
    var pinText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val enteredAmount = amountText.toDoubleOrNull() ?: 0.0
    val calculatedFee = (enteredAmount * 0.0149).coerceAtLeast(0.0)
    val roundedFee = Math.round(calculatedFee * 100.0) / 100.0
    val totalDebit = enteredAmount + roundedFee

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("cash_out_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalAtm,
                            contentDescription = "Cash Out",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBangla) "ক্যাশ আউট (Cash Out)" else "Cash Out",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A)
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Agent input
            OutlinedTextField(
                value = agentNumber,
                onValueChange = {
                    agentNumber = it
                    errorMessage = null
                },
                label = { Text(if (isBangla) "এজেন্ট নাম্বার লিখুন" else "Agent Number") },
                placeholder = { Text("01811-992211") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cash_out_agent_input"),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Storefront, contentDescription = "Agent", tint = PrimaryRose)
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRose,
                    focusedLabelColor = PrimaryRose
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Amount input
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    if (it.isEmpty() || it.all { c -> c.isDigit() || c == '.' }) {
                        amountText = it
                        errorMessage = null
                    }
                },
                label = { Text(if (isBangla) "টাকার পরিমাণ (৳)" else "Amount (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cash_out_amount_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRose,
                    focusedLabelColor = PrimaryRose
                )
            )

            // Quick Fee & Total Breakdown Card
            if (enteredAmount > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isBangla) "অ্যাপ চার্জ (১.৪৯%):" else "App Fee (1.49%):",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = BanglaFormatter.formatCurrency(roundedFee, language),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isBangla) "সর্বমোট কর্তন:" else "Total Deduction:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = BanglaFormatter.formatCurrency(totalDebit, language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryRose
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5-digit PIN
            OutlinedTextField(
                value = pinText,
                onValueChange = {
                    if (it.length <= 5 && it.all { c -> c.isDigit() }) {
                        pinText = it
                        errorMessage = null
                    }
                },
                label = { Text(if (isBangla) "পিন নম্বর লিখুন (Default: 12345)" else "Enter PIN (Default: 12345)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cash_out_pin_input"),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "PIN", tint = PrimaryRose)
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRose,
                    focusedLabelColor = PrimaryRose
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFDC2626),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isSubmitting) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = PrimaryRose, modifier = Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = if (isBangla) "ক্যাশ আউট সম্পন্ন হচ্ছে..." else "Processing Cash Out...", color = PrimaryRose)
                }
            } else {
                HoldToConfirmButton(
                    text = if (isBangla) "ক্যাশ আউট করতে ট্যাপ করে ধরে রাখুন" else "Tap and hold to Cash Out",
                    onConfirmed = {
                        val amt = amountText.toDoubleOrNull()
                        if (agentNumber.isBlank()) {
                            errorMessage = if (isBangla) "এজেন্ট নাম্বার লিখুন" else "Enter agent number"
                            return@HoldToConfirmButton
                        }
                        if (amt == null || amt < 50) {
                            errorMessage = if (isBangla) "সর্বনিম্ন ক্যাশ আউট ৫০ টাকা" else "Minimum cash out ৳50"
                            return@HoldToConfirmButton
                        }
                        if (pinText.length != 5) {
                            errorMessage = if (isBangla) "৫ ডিজিটের পিন লিখুন" else "Enter 5-digit PIN"
                            return@HoldToConfirmButton
                        }

                        isSubmitting = true
                        onConfirmCashOut(agentNumber, agentName, amt, pinText) { success, msg ->
                            isSubmitting = false
                            if (!success) {
                                errorMessage = msg
                            }
                        }
                    }
                )
            }
        }
    }
}
