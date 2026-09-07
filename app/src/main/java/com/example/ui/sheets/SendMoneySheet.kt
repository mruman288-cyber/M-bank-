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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.data.model.SavedBeneficiary
import com.example.data.model.UserAccount
import com.example.ui.components.HoldToConfirmButton
import com.example.ui.theme.PrimaryRose
import com.example.ui.theme.PrimaryRoseDark
import com.example.util.BanglaFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneySheet(
    sheetState: SheetState,
    language: AppLanguage,
    user: UserAccount?,
    beneficiaries: List<SavedBeneficiary>,
    onDismiss: () -> Unit,
    onConfirmSend: (recipientNumber: String, recipientName: String, amount: Double, reference: String, pin: String, (Boolean, String) -> Unit) -> Unit
) {
    val isBangla = language == AppLanguage.BANGLA

    var recipientNumber by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var referenceText by remember { mutableStateOf("") }
    var pinText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val quickAmounts = listOf(100.0, 500.0, 1000.0, 2000.0, 5000.0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("send_money_bottom_sheet")
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
                            .background(PrimaryRose.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = PrimaryRose,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBangla) "সেন্ড মানি (Send Money)" else "Send Money",
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

            // Available balance hint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) "বর্তমান ব্যালেন্স:" else "Available Balance:",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = BanglaFormatter.formatCurrency(user?.balance ?: 0.0, language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryRose
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick select contact
            if (beneficiaries.isNotEmpty()) {
                Text(
                    text = if (isBangla) "সংরক্ষিত নাম্বার নির্বাচন করুন:" else "Select from Saved Contacts:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(beneficiaries) { contact ->
                        FilterChip(
                            selected = recipientNumber == contact.phone,
                            onClick = {
                                recipientNumber = contact.phone
                                recipientName = contact.name
                            },
                            label = { Text(contact.name, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryRose.copy(alpha = 0.15f),
                                selectedLabelColor = PrimaryRose
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Recipient Number Input
            OutlinedTextField(
                value = recipientNumber,
                onValueChange = {
                    recipientNumber = it
                    errorMessage = null
                },
                label = { Text(if (isBangla) "প্রাপকের মোবাইল নাম্বার" else "Recipient Mobile Number") },
                placeholder = { Text("01XXXXXXXXX") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("send_money_recipient_input"),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ContactPhone,
                        contentDescription = "Recipient",
                        tint = PrimaryRose
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRose,
                    focusedLabelColor = PrimaryRose
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    if (it.isEmpty() || it.all { char -> char.isDigit() || char == '.' }) {
                        amountText = it
                        errorMessage = null
                    }
                },
                label = { Text(if (isBangla) "টাকার পরিমাণ (৳)" else "Amount (৳)") },
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("send_money_amount_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRose,
                    focusedLabelColor = PrimaryRose
                )
            )

            // Quick Amount Buttons
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickAmounts.forEach { amt ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { amountText = amt.toInt().toString() },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = if (isBangla) "৳${BanglaFormatter.toBanglaDigits(amt.toInt().toString())}" else "৳${amt.toInt()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reference (Optional)
            OutlinedTextField(
                value = referenceText,
                onValueChange = { referenceText = it },
                label = { Text(if (isBangla) "রেফারেন্স (ঐচ্ছিক)" else "Reference (Optional)") },
                placeholder = { Text(if (isBangla) "যেমন: উপহার বা খরচ" else "e.g. Gift or Expense") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRose,
                    focusedLabelColor = PrimaryRose
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 5-digit PIN input
            OutlinedTextField(
                value = pinText,
                onValueChange = {
                    if (it.length <= 5 && it.all { char -> char.isDigit() }) {
                        pinText = it
                        errorMessage = null
                    }
                },
                label = { Text(if (isBangla) "আপনার ৫ ডিজিটের পিন (Default: 12345)" else "Your 5-Digit PIN (Default: 12345)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("send_money_pin_input"),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "PIN",
                        tint = PrimaryRose
                    )
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

            // Hold to send button
            if (isSubmitting) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = PrimaryRose, modifier = Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = if (isBangla) "লেনদেন সম্পন্ন হচ্ছে..." else "Processing...", color = PrimaryRose)
                }
            } else {
                HoldToConfirmButton(
                    text = if (isBangla) "সেন্ড মানি করতে ট্যাপ করে ধরে রাখুন" else "Tap and hold to Send Money",
                    onConfirmed = {
                        val amt = amountText.toDoubleOrNull()
                        if (recipientNumber.isBlank()) {
                            errorMessage = if (isBangla) "দয়া করে প্রাপকের নাম্বার লিখুন" else "Please enter recipient number"
                            return@HoldToConfirmButton
                        }
                        if (amt == null || amt < 10) {
                            errorMessage = if (isBangla) "সর্বনিম্ন ১০ টাকা পাঠাতে হবে" else "Minimum amount is ৳10"
                            return@HoldToConfirmButton
                        }
                        if (pinText.length != 5) {
                            errorMessage = if (isBangla) "৫ ডিজিটের পিন প্রদান করুন" else "Enter 5-digit PIN"
                            return@HoldToConfirmButton
                        }

                        isSubmitting = true
                        onConfirmSend(
                            recipientNumber,
                            recipientName,
                            amt,
                            referenceText,
                            pinText
                        ) { success, msg ->
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
