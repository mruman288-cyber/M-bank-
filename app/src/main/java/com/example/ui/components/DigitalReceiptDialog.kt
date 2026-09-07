package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.data.model.TransactionRecord
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.PrimaryRose
import com.example.util.BanglaFormatter

@Composable
fun DigitalReceiptDialog(
    transaction: TransactionRecord,
    language: AppLanguage,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = language == AppLanguage.BANGLA
    val title = if (isBangla) transaction.titleBn else transaction.title

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .testTag("digital_receipt_dialog"),
            shape = RoundedCornerShape(28.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = EmeraldTeal,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isBangla) "$title সফল হয়েছে!" else "$title Successful!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = BanglaFormatter.formatCurrency(transaction.amount, language),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    color = PrimaryRose
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(16.dp))

                // Receipt Breakdown Rows
                ReceiptRow(
                    label = if (isBangla) "লেনদেন আইডি (TrxID)" else "Transaction ID",
                    value = transaction.trxId,
                    isHighlight = true
                )

                ReceiptRow(
                    label = if (isBangla) "প্রাপক / মাধ্যম" else "Recipient / Source",
                    value = transaction.recipientName.ifBlank { transaction.recipientOrSource }
                )

                if (transaction.recipientOrSource.isNotBlank() && transaction.recipientOrSource != transaction.recipientName) {
                    ReceiptRow(
                        label = if (isBangla) "অ্যাকাউন্ট / নাম্বার" else "Account / Number",
                        value = transaction.recipientOrSource
                    )
                }

                ReceiptRow(
                    label = if (isBangla) "তারিখ ও সময়" else "Date & Time",
                    value = BanglaFormatter.formatDate(transaction.timestamp, language)
                )

                ReceiptRow(
                    label = if (isBangla) "সার্ভিস চার্জ" else "Service Fee",
                    value = BanglaFormatter.formatCurrency(transaction.fee, language)
                )

                val total = transaction.amount + transaction.fee
                ReceiptRow(
                    label = if (isBangla) "মোট পরিমাণ" else "Total Amount",
                    value = BanglaFormatter.formatCurrency(total, language),
                    isBold = true
                )

                if (transaction.reference.isNotBlank()) {
                    ReceiptRow(
                        label = if (isBangla) "রেফারেন্স" else "Reference",
                        value = transaction.reference
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("receipt_done_button"),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRose)
                ) {
                    Text(
                        text = if (isBangla) "ঠিক আছে (হোম)" else "Done (Back to Home)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (isBold || isHighlight) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlight) PrimaryRose else Color(0xFF0F172A)
        )
    }
}
