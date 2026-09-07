package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionType
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.PrimaryRose
import com.example.util.BanglaFormatter

@Composable
fun TransactionListItem(
    transaction: TransactionRecord,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = language == AppLanguage.BANGLA
    val title = if (isBangla) transaction.titleBn else transaction.title

    val iconData = when (transaction.type) {
        TransactionType.SEND_MONEY.name -> Pair(Icons.Default.Send, Color(0xFFFCE4EC) to Color(0xFFE2136E))
        TransactionType.MOBILE_RECHARGE.name -> Pair(Icons.Default.PhoneAndroid, Color(0xFFE0F2FE) to Color(0xFF0284C7))
        TransactionType.CASH_OUT.name -> Pair(Icons.Default.LocalAtm, Color(0xFFFEF3C7) to Color(0xFFD97706))
        TransactionType.MAKE_PAYMENT.name -> Pair(Icons.Default.ShoppingBag, Color(0xFFDCFCE7) to Color(0xFF16A34A))
        TransactionType.ADD_MONEY.name -> Pair(Icons.Default.CreditCard, Color(0xFFEDE9FE) to Color(0xFF7C3AED))
        TransactionType.PAY_BILL.name -> Pair(Icons.Default.ReceiptLong, Color(0xFFFEE2E2) to Color(0xFFDC2626))
        else -> Pair(Icons.Default.ArrowUpward, Color(0xFFF1F5F9) to Color(0xFF64748B))
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("trx_item_${transaction.trxId}"),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Category Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconData.second.first),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconData.first,
                        contentDescription = title,
                        tint = iconData.second.second,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transaction.recipientName.ifBlank { transaction.recipientOrSource },
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = BanglaFormatter.formatDate(transaction.timestamp, language),
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Amount
            Column(horizontalAlignment = Alignment.End) {
                val amountText = BanglaFormatter.formatCurrency(transaction.amount, language)
                Text(
                    text = if (transaction.isExpense) "- $amountText" else "+ $amountText",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (transaction.isExpense) Color(0xFF0F172A) else EmeraldTeal
                )
                if (transaction.fee > 0.0) {
                    Text(
                        text = if (isBangla) "চার্জ: ${BanglaFormatter.formatCurrency(transaction.fee, language)}"
                        else "Fee: ${BanglaFormatter.formatCurrency(transaction.fee, language)}",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
                Text(
                    text = "TrxID: ${transaction.trxId}",
                    fontSize = 10.sp,
                    color = PrimaryRose.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
