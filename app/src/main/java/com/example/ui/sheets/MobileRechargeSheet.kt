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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.data.model.MobileOperator
import com.example.data.model.UserAccount
import com.example.ui.components.HoldToConfirmButton
import com.example.ui.theme.PrimaryRose
import com.example.util.BanglaFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileRechargeSheet(
    sheetState: SheetState,
    language: AppLanguage,
    user: UserAccount?,
    onDismiss: () -> Unit,
    onConfirmRecharge: (phone: String, operator: String, amount: Double, isPrepaid: Boolean, pin: String, (Boolean, String) -> Unit) -> Unit
) {
    val isBangla = language == AppLanguage.BANGLA

    var phoneNumber by remember { mutableStateOf(user?.phone?.replace("-", "") ?: "") }
    var selectedOperator by remember { mutableStateOf(MobileOperator.GRAMEENPHONE) }
    var isPrepaid by remember { mutableStateOf(true) }
    var amountText by remember { mutableStateOf("50") }
    var pinText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val popularRecharges = listOf(
        Pair(20.0, if (isBangla) "জরুরী" else "Emergency"),
        Pair(50.0, if (isBangla) "টকটাইম" else "Talktime"),
        Pair(100.0, if (isBangla) "ক্যাশব্যাক" else "Cashback"),
        Pair(298.0, if (isBangla) "১০GB + ২০০মি." else "10GB + 200m"),
        Pair(498.0, if (isBangla) "মাসিক আনলিমিটেড" else "Monthly Unltd")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("mobile_recharge_bottom_sheet")
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
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = "Recharge",
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBangla) "মোবাইল রিচার্জ (Recharge)" else "Mobile Recharge",
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

            // Prepaid / Postpaid Tab
            TabRow(
                selectedTabIndex = if (isPrepaid) 0 else 1,
                containerColor = Color(0xFFF1F5F9),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .padding(2.dp)
            ) {
                Tab(
                    selected = isPrepaid,
                    onClick = { isPrepaid = true },
                    text = { Text(if (isBangla) "প্রিপেইড (Prepaid)" else "Prepaid", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = !isPrepaid,
                    onClick = { isPrepaid = false },
                    text = { Text(if (isBangla) "পোস্টপেইড (Postpaid)" else "Postpaid", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Phone Number Input
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                    errorMessage = null
                },
                label = { Text(if (isBangla) "মোবাইল নাম্বার" else "Mobile Number") },
                placeholder = { Text("01XXXXXXXXX") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recharge_phone_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRose,
                    focusedLabelColor = PrimaryRose
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Operator Selector
            Text(
                text = if (isBangla) "অপারেটর নির্বাচন করুন:" else "Select Operator:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(MobileOperator.values()) { op ->
                    FilterChip(
                        selected = selectedOperator == op,
                        onClick = { selectedOperator = op },
                        label = { Text(op.displayName, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(op.colorHex).copy(alpha = 0.2f),
                            selectedLabelColor = Color(op.colorHex)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    if (it.isEmpty() || it.all { c -> c.isDigit() || c == '.' }) {
                        amountText = it
                        errorMessage = null
                    }
                },
                label = { Text(if (isBangla) "রিচার্জের পরিমাণ (৳)" else "Recharge Amount (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recharge_amount_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRose,
                    focusedLabelColor = PrimaryRose
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Popular packs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(popularRecharges) { pack ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { amountText = pack.first.toInt().toString() },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "৳${BanglaFormatter.formatNumber(pack.first.toInt(), language)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryRose
                            )
                            Text(
                                text = pack.second,
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

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
                    .testTag("recharge_pin_input"),
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

            // Confirm
            if (isSubmitting) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = PrimaryRose, modifier = Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = if (isBangla) "রিচার্জ সম্পন্ন হচ্ছে..." else "Recharging...", color = PrimaryRose)
                }
            } else {
                HoldToConfirmButton(
                    text = if (isBangla) "রিচার্জ করতে ট্যাপ করে ধরে রাখুন" else "Tap and hold to Recharge",
                    onConfirmed = {
                        val amt = amountText.toDoubleOrNull()
                        if (phoneNumber.isBlank()) {
                            errorMessage = if (isBangla) "মোবাইল নাম্বার লিখুন" else "Enter mobile number"
                            return@HoldToConfirmButton
                        }
                        if (amt == null || amt < 10) {
                            errorMessage = if (isBangla) "সর্বনিম্ন রিচার্জ ১০ টাকা" else "Minimum recharge is ৳10"
                            return@HoldToConfirmButton
                        }
                        if (pinText.length != 5) {
                            errorMessage = if (isBangla) "৫ ডিজিটের পিন লিখুন" else "Enter 5-digit PIN"
                            return@HoldToConfirmButton
                        }

                        isSubmitting = true
                        onConfirmRecharge(
                            phoneNumber,
                            selectedOperator.displayName,
                            amt,
                            isPrepaid,
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
