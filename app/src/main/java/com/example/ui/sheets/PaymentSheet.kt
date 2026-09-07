package com.example.ui.sheets

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSheet(
    sheetState: SheetState,
    language: AppLanguage,
    user: UserAccount?,
    onDismiss: () -> Unit,
    onConfirmPayment: (merchantCode: String, merchantName: String, amount: Double, invoiceRef: String, pin: String, (Boolean, String) -> Unit) -> Unit
) {
    val isBangla = language == AppLanguage.BANGLA

    var merchantCode by remember { mutableStateOf("0130009988") }
    var merchantName by remember { mutableStateOf("Daraz Bangladesh") }
    var amountText by remember { mutableStateOf("") }
    var invoiceRef by remember { mutableStateOf("") }
    var pinText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("payment_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
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
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Payment",
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBangla) "পেমেন্ট করুন (Payment)" else "Make Payment",
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

            // Merchant Number
            OutlinedTextField(
                value = merchantCode,
                onValueChange = {
                    merchantCode = it
                    errorMessage = null
                },
                label = { Text(if (isBangla) "মার্চেন্ট নাম্বার বা কোড" else "Merchant Number or Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("merchant_code_input"),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Store, contentDescription = "Store", tint = PrimaryRose)
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRose,
                    focusedLabelColor = PrimaryRose
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Amount
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    if (it.isEmpty() || it.all { c -> c.isDigit() || c == '.' }) {
                        amountText = it
                        errorMessage = null
                    }
                },
                label = { Text(if (isBangla) "পেমেন্টের পরিমাণ (৳)" else "Payment Amount (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("payment_amount_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRose,
                    focusedLabelColor = PrimaryRose
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Invoice / Counter Ref
            OutlinedTextField(
                value = invoiceRef,
                onValueChange = { invoiceRef = it },
                label = { Text(if (isBangla) "কাউন্টার নম্বর / রেফারেন্স" else "Counter / Invoice Reference") },
                placeholder = { Text("e.g. Counter 1") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRose,
                    focusedLabelColor = PrimaryRose
                )
            )

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
                    .testTag("payment_pin_input"),
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
                    Text(text = if (isBangla) "পেমেন্ট সম্পন্ন হচ্ছে..." else "Processing Payment...", color = PrimaryRose)
                }
            } else {
                HoldToConfirmButton(
                    text = if (isBangla) "পেমেন্ট করতে ট্যাপ করে ধরে রাখুন" else "Tap and hold to Pay",
                    onConfirmed = {
                        val amt = amountText.toDoubleOrNull()
                        if (merchantCode.isBlank()) {
                            errorMessage = if (isBangla) "মার্চেন্ট নাম্বার লিখুন" else "Enter merchant code"
                            return@HoldToConfirmButton
                        }
                        if (amt == null || amt < 1) {
                            errorMessage = if (isBangla) "সঠিক পরিমাণ লিখুন" else "Enter valid amount"
                            return@HoldToConfirmButton
                        }
                        if (pinText.length != 5) {
                            errorMessage = if (isBangla) "৫ ডিজিটের পিন লিখুন" else "Enter 5-digit PIN"
                            return@HoldToConfirmButton
                        }

                        isSubmitting = true
                        onConfirmPayment(merchantCode, merchantName, amt, invoiceRef, pinText) { success, msg ->
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
