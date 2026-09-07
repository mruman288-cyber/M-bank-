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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.viewmodel.SheetType

data class ServiceItem(
    val sheetType: SheetType,
    val titleEn: String,
    val titleBn: String,
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconTint: Color,
    val badgeBn: String? = null,
    val badgeEn: String? = null
)

@Composable
fun ServicesGrid(
    language: AppLanguage,
    onServiceClick: (SheetType) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = language == AppLanguage.BANGLA

    val services = listOf(
        ServiceItem(
            sheetType = SheetType.SEND_MONEY,
            titleEn = "Send Money",
            titleBn = "সেন্ড মানি",
            icon = Icons.Default.Send,
            iconBgColor = Color(0xFFFCE4EC),
            iconTint = Color(0xFFE2136E),
            badgeBn = "ফ্রি",
            badgeEn = "Free"
        ),
        ServiceItem(
            sheetType = SheetType.MOBILE_RECHARGE,
            titleEn = "Recharge",
            titleBn = "মোবাইল রিচার্জ",
            icon = Icons.Default.PhoneAndroid,
            iconBgColor = Color(0xFFE0F2FE),
            iconTint = Color(0xFF0284C7),
            badgeBn = "অফার",
            badgeEn = "Offer"
        ),
        ServiceItem(
            sheetType = SheetType.CASH_OUT,
            titleEn = "Cash Out",
            titleBn = "ক্যাশ আউট",
            icon = Icons.Default.LocalAtm,
            iconBgColor = Color(0xFFFEF3C7),
            iconTint = Color(0xFFD97706)
        ),
        ServiceItem(
            sheetType = SheetType.MAKE_PAYMENT,
            titleEn = "Payment",
            titleBn = "পেমেন্ট",
            icon = Icons.Default.ShoppingBag,
            iconBgColor = Color(0xFFDCFCE7),
            iconTint = Color(0xFF16A34A)
        ),
        ServiceItem(
            sheetType = SheetType.ADD_MONEY,
            titleEn = "Add Money",
            titleBn = "অ্যাড মানি",
            icon = Icons.Default.CreditCard,
            iconBgColor = Color(0xFFEDE9FE),
            iconTint = Color(0xFF7C3AED)
        ),
        ServiceItem(
            sheetType = SheetType.PAY_BILL,
            titleEn = "Pay Bill",
            titleBn = "বিল পে",
            icon = Icons.Default.ReceiptLong,
            iconBgColor = Color(0xFFFEE2E2),
            iconTint = Color(0xFFDC2626)
        ),
        ServiceItem(
            sheetType = SheetType.SAVINGS,
            titleEn = "Savings",
            titleBn = "সেভিংস",
            icon = Icons.Default.Savings,
            iconBgColor = Color(0xFFCCFBF1),
            iconTint = Color(0xFF0D9488)
        ),
        ServiceItem(
            sheetType = SheetType.REMITTANCE,
            titleEn = "Remittance",
            titleBn = "রেমিট্যান্স",
            icon = Icons.Default.Public,
            iconBgColor = Color(0xFFF3E8FF),
            iconTint = Color(0xFF9333EA),
            badgeBn = "২.৫% বোনাস",
            badgeEn = "2.5% Bonus"
        )
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp)
        ) {
            // First row of 4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                services.take(4).forEach { item ->
                    ServiceButton(
                        item = item,
                        isBangla = isBangla,
                        onClick = { onServiceClick(item.sheetType) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Second row of 4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                services.drop(4).take(4).forEach { item ->
                    ServiceButton(
                        item = item,
                        isBangla = isBangla,
                        onClick = { onServiceClick(item.sheetType) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceButton(
    item: ServiceItem,
    isBangla: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tag = "service_${item.sheetType.name.lowercase()}"
    val title = if (isBangla) item.titleBn else item.titleEn
    val badge = if (isBangla) item.badgeBn else item.badgeEn

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(item.iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = title,
                    tint = item.iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }

            if (badge != null) {
                Box(
                    modifier = Modifier
                        .padding(top = 0.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2136E))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}
