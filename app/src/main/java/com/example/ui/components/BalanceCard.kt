package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.UserAccount
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.PrimaryRose
import com.example.ui.theme.PrimaryRoseDark
import com.example.util.BanglaFormatter

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BalanceCard(
    user: UserAccount?,
    language: AppLanguage,
    isBalanceRevealed: Boolean,
    isLoading: Boolean,
    onTapBalance: () -> Unit,
    onQrClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = language == AppLanguage.BANGLA
    val displayName = if (isBangla) user?.name ?: "গ্রাহক" else user?.nameEn ?: "Customer"
    val displayPhone = if (isBangla) BanglaFormatter.toBanglaDigits(user?.phone ?: "017XX-XXXXXX") else (user?.phone ?: "017XX-XXXXXX")
    val displayPoints = if (isBangla) BanglaFormatter.toBanglaDigits((user?.rewardPoints ?: 0).toString()) else (user?.rewardPoints ?: 0).toString()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = PrimaryRose.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE2136E),
                            Color(0xFFC2185B),
                            Color(0xFF9C0F48)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                // User info & QR header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (displayName.isNotBlank()) displayName.take(1) else "R",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = displayName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = displayPhone,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                        }
                    }

                    // QR Code icon button
                    IconButton(
                        onClick = onQrClick,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .testTag("qr_scanner_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Interactive Tap For Balance Pill
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(30.dp))
                        .clickable { onTapBalance() }
                        .testTag("tap_for_balance_pill"),
                    shape = RoundedCornerShape(30.dp),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryRose.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = "Wallet",
                                    tint = PrimaryRose,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            if (isLoading) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = PrimaryRose,
                                        strokeWidth = 2.5.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isBangla) "যাচাই হচ্ছে..." else "Checking...",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                AnimatedContent(
                                    targetState = isBalanceRevealed,
                                    transitionSpec = {
                                        if (targetState) {
                                            slideInVertically { height -> height } + fadeIn() with
                                                    slideOutVertically { height -> -height } + fadeOut()
                                        } else {
                                            slideInVertically { height -> -height } + fadeIn() with
                                                    slideOutVertically { height -> height } + fadeOut()
                                        }
                                    },
                                    label = "balance_anim"
                                ) { revealed ->
                                    if (revealed) {
                                        Text(
                                            text = BanglaFormatter.formatCurrency(user?.balance ?: 0.0, language),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PrimaryRoseDark
                                        )
                                    } else {
                                        Text(
                                            text = if (isBangla) "ট্যাপ করে ব্যালেন্স দেখুন" else "Tap for Balance",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = PrimaryRose
                                        )
                                    }
                                }
                            }
                        }

                        // Right side toggle eye
                        Icon(
                            imageVector = if (isBalanceRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Balance",
                            tint = PrimaryRose.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom strip with Reward Points badge & Limits hint
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "Points",
                            tint = GoldLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "$displayPoints রিওয়ার্ড পয়েন্ট" else "$displayPoints Reward Points",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = if (isBangla) "দৈনিক ব্যবহার: ${BanglaFormatter.formatCurrency(user?.dailyUsed ?: 0.0, language)}" else "Daily: ${BanglaFormatter.formatCurrency(user?.dailyUsed ?: 0.0, language)}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
