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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GasMeter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
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
import com.example.data.model.BillCategory
import com.example.ui.components.HoldToConfirmButton
import com.example.ui.theme.PrimaryRose

data class BillerOption(
    val id: String,
    val name: String,
    val nameBn: String,
    val category: BillCategory
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayBillSheet(
    sheetState: SheetState,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onConfirmPayBill: (billerName: String, billerNameBn: String, accountNum: String, amount: Double, month: String, pin: String, (Boolean, String) -> Unit) -> Unit
) {
    val isBangla = language == AppLanguage.BANGLA

    val billers = listOf(
        BillerOption("desco", "DESCO Prepaid / Postpaid", "ডেসকো বিদ্যুৎ", BillCategory.ELECTRICITY),
        BillerOption("dpdc", "DPDC Prepaid", "ডিপিডিসি বিদ্যুৎ", BillCategory.ELECTRICITY),
        BillerOption("nesco", "NESCO Electricity", "নেসকো বিদ্যুৎ", BillCategory.ELECTRICITY),
        BillerOption("titas", "Titas Gas", "তিতাস গ্যাস", BillCategory.GAS),
        BillerOption("wasa", "Dhaka WASA", "ঢাকা ওয়াসা পানি", BillCategory.WATER),
        BillerOption("link3", "Link3 Broadband", "লিংকথ্রি ইন্টারনেট", BillCategory.INTERNET),
        BillerOption("carnival", "Carnival Internet", "কার্নিভাল নেট", BillCategory.INTERNET)
    )

    var selectedCategory by remember { mutableStateOf(BillCategory.ELECTRICITY) }
    var selectedBiller by remember { mutableStateOf(billers.first()) }
    var accountNum by remember { mutableStateOf("1048291") }
    var billMonth by remember { mutableStateOf("আগস্ট ২০২৪") }
    var amountText by remember { mutableStateOf("1250") }
    var pinText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val filteredBillers = billers.filter { it.category == selectedCategory }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("pay_bill_bottom_sheet")
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
                            .background(Color(0xFFFEE2E2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Bill Pay",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBangla) "বিল পে (Pay Bill)" else "Pay Utility Bill",
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

            // Category Chips
            Text(
                text = if (isBangla) "বিলের ধরণ নির্বাচন করুন:" else "Select Bill Category:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BillCategory.values().forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = {
                            selectedCategory = cat
                            val firstForCat = billers.firstOrNull { it.category == cat }
                            if (firstForCat != null) selectedBiller = firstForCat
                        },
                        label = { Text(if (isBangla) cat.titleBn else cat.titleEn, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryRose.copy(alpha = 0.15f),
                            selectedLabelColor = PrimaryRose
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Provider selection
            Text(
                text = if (isBangla) "প্রতিষ্ঠান (Biller):" else "Provider (Biller):",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredBillers) { biller ->
                    FilterChip(
                        selected = selectedBiller == biller,
                        onClick = { selectedBiller = biller },
                        label = { Text(if (isBangla) biller.nameBn else biller.name, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryRose.copy(alpha = 0.15f),
                            selectedLabelColor = PrimaryRose
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Account / Meter Number
            OutlinedTextField(
                value = accountNum,
                onValueChange = {
                    accountNum = it
                    errorMessage = null
                },
                label = { Text(if (isBangla) "বিল / গ্রাহক / মিটার নম্বর" else "Bill / Account / Meter Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bill_account_input"),
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
                label = { Text(if (isBangla) "বিলের পরিমাণ (৳)" else "Bill Amount (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bill_amount_input"),
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
                    .testTag("bill_pin_input"),
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
                    Text(text = if (isBangla) "বিল পরিশোধ হচ্ছে..." else "Paying bill...", color = PrimaryRose)
                }
            } else {
                HoldToConfirmButton(
                    text = if (isBangla) "বিল পরিশোধ করতে ট্যাপ করে ধরে রাখুন" else "Tap and hold to Pay Bill",
                    onConfirmed = {
                        val amt = amountText.toDoubleOrNull()
                        if (accountNum.isBlank()) {
                            errorMessage = if (isBangla) "গ্রাহক নম্বর লিখুন" else "Enter account number"
                            return@HoldToConfirmButton
                        }
                        if (amt == null || amt < 10) {
                            errorMessage = if (isBangla) "বিলের পরিমাণ সঠিক নয়" else "Invalid amount"
                            return@HoldToConfirmButton
                        }
                        if (pinText.length != 5) {
                            errorMessage = if (isBangla) "৫ ডিজিটের পিন লিখুন" else "Enter 5-digit PIN"
                            return@HoldToConfirmButton
                        }

                        isSubmitting = true
                        onConfirmPayBill(
                            selectedBiller.name,
                            selectedBiller.nameBn,
                            accountNum,
                            amt,
                            billMonth,
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
