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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.PrimaryRose
import com.example.util.BanglaFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsSheet(
    sheetState: SheetState,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onStartSavings: (String, Double, Int) -> Unit
) {
    val isBangla = language == AppLanguage.BANGLA

    val institutions = listOf(
        "IDLC Finance Ltd.",
        "BRAC Bank DPS",
        "Dhaka Bank Islamic DPS",
        "IPDC Finance"
    )

    var selectedInst by remember { mutableStateOf(institutions.first()) }
    var selectedTenure by remember { mutableStateOf(12) } // months
    var selectedMonthlyDeposit by remember { mutableStateOf(1000.0) }
    var isSubmitted by remember { mutableStateOf(false) }

    val tenures = listOf(6, 12, 24, 36)
    val monthlyAmounts = listOf(500.0, 1000.0, 2000.0, 5000.0)

    val interestRate = 0.085 // 8.5%
    val totalPrincipal = selectedMonthlyDeposit * selectedTenure
    val estimatedMaturity = totalPrincipal * (1 + (interestRate * (selectedTenure / 12.0) / 2))

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("savings_bottom_sheet")
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
                            .background(Color(0xFFCCFBF1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = "Savings",
                            tint = Color(0xFF0D9488),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBangla) "সেভিংস ডিপিএস (Savings DPS)" else "Savings Scheme",
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

            Text(
                text = if (isBangla) "ব্যাংক বা আর্থিক প্রতিষ্ঠান নির্বাচন করুন:" else "Select Institution:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(8.dp))
            institutions.forEach { inst ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedInst = inst },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedInst == inst) PrimaryRose.copy(alpha = 0.1f) else Color(0xFFF8FAFC)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = inst,
                            tint = if (selectedInst == inst) PrimaryRose else Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = inst,
                            fontSize = 14.sp,
                            fontWeight = if (selectedInst == inst) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedInst == inst) PrimaryRose else Color(0xFF1E293B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Monthly amount selector
            Text(
                text = if (isBangla) "মাসিক জমার পরিমাণ:" else "Monthly Deposit Amount:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                monthlyAmounts.forEach { amt ->
                    FilterChip(
                        selected = selectedMonthlyDeposit == amt,
                        onClick = { selectedMonthlyDeposit = amt },
                        label = { Text("৳${BanglaFormatter.formatNumber(amt.toInt(), language)}", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryRose.copy(alpha = 0.15f),
                            selectedLabelColor = PrimaryRose
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tenure selector
            Text(
                text = if (isBangla) "মেয়াদ (মাস):" else "Tenure (Months):",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tenures.forEach { t ->
                    FilterChip(
                        selected = selectedTenure == t,
                        onClick = { selectedTenure = t },
                        label = { Text(if (isBangla) "${BanglaFormatter.formatNumber(t, language)} মাস" else "$t Mos", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryRose.copy(alpha = 0.15f),
                            selectedLabelColor = PrimaryRose
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calculation Preview Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF0FDF4)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBangla) "প্রত্যাশিত মুনাফা ও মেয়াদের শেষে মোট প্রাপ্তি:" else "Estimated Return at Maturity:",
                        fontSize = 12.sp,
                        color = Color(0xFF166534),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = BanglaFormatter.formatCurrency(estimatedMaturity, language),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldTeal
                    )
                    Text(
                        text = if (isBangla) "(মুনাফার হার প্রায় ৮.৫% বার্ষিক)" else "(Estimated 8.5% annual yield)",
                        fontSize = 11.sp,
                        color = Color(0xFF166534)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onStartSavings(selectedInst, selectedMonthlyDeposit, selectedTenure)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("start_savings_button"),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRose)
            ) {
                Text(
                    text = if (isBangla) "সেভিংস ডিপিএস চালু করুন" else "Open Savings Scheme",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemittanceSheet(
    sheetState: SheetState,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isBangla = language == AppLanguage.BANGLA

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("remittance_bottom_sheet")
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
                            .background(Color(0xFFF3E8FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Remittance",
                            tint = Color(0xFF9333EA),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBangla) "প্রবাসী রেমিট্যান্স (Remittance)" else "Foreign Remittance",
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

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFAF5FF)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBangla) "সরকারি ২.৫% প্রণোদনা তাৎক্ষণিক জমা!" else "Get Instant 2.5% Govt. Incentive!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF6B21A8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBangla) "বিদেশ থেকে সরাসরি মোবাইল ব্যাংকিং একাউন্টে টাকা আসলে সরকার থেকে অতিরিক্ত ২.৫% ক্যাশ বোনাস প্রদান করা হয়।"
                        else "Direct incoming remittance to your mobile wallet receives instant 2.5% government cash bonus.",
                        fontSize = 12.sp,
                        color = Color(0xFF581C87),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isBangla) "অনুমোদিত রেমিট্যান্স পার্টনারসমূহ:" else "Authorized Remittance Partners:",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val partners = listOf(
                "Western Union", "MoneyGram", "Ria Money Transfer",
                "WorldRemit", "TapTap Send", "Remitly"
            )

            partners.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pair.forEach { p ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = p,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.padding(10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRose)
            ) {
                Text(
                    text = if (isBangla) "ঠিক আছে" else "Got It",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
