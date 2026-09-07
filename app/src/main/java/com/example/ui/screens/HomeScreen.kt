package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.SavedBeneficiary
import com.example.data.model.TransactionRecord
import com.example.data.model.UserAccount
import com.example.ui.components.BalanceCard
import com.example.ui.components.RecentContactsRow
import com.example.ui.components.ServicesGrid
import com.example.ui.components.TransactionListItem
import com.example.ui.theme.PrimaryRose
import com.example.ui.theme.PrimaryRoseDark
import com.example.ui.viewmodel.SheetType

@Composable
fun HomeScreen(
    user: UserAccount?,
    transactions: List<TransactionRecord>,
    beneficiaries: List<SavedBeneficiary>,
    language: AppLanguage,
    isBalanceRevealed: Boolean,
    isBalanceLoading: Boolean,
    onTapBalance: () -> Unit,
    onQrClick: () -> Unit,
    onServiceClick: (SheetType) -> Unit,
    onContactClick: (SavedBeneficiary) -> Unit,
    onAddContactClick: () -> Unit,
    onTransactionClick: (TransactionRecord) -> Unit,
    onToggleLanguage: () -> Unit,
    onSeeAllTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = language == AppLanguage.BANGLA

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App branding / title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(PrimaryRose, PrimaryRoseDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "৳",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isBangla) "মোবাইল ব্যাংকিং" else "Mobile Banking",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = if (isBangla) "ডিজিটাল ওয়ালেট ও সার্ভিস" else "Digital Wallet & Services",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Action buttons: Language Toggle & Notifications
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Switch Pill
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onToggleLanguage() }
                            .testTag("language_toggle_button"),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Language",
                                tint = PrimaryRose,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "ENG" else "বাংলা",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryRose
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = PrimaryRose) {
                                    Text("2", color = Color.White, fontSize = 9.sp)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color(0xFF334155),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Interactive Balance Card
        item {
            BalanceCard(
                user = user,
                language = language,
                isBalanceRevealed = isBalanceRevealed,
                isLoading = isBalanceLoading,
                onTapBalance = onTapBalance,
                onQrClick = onQrClick
            )
        }

        // Core 8 Services Grid
        item {
            ServicesGrid(
                language = language,
                onServiceClick = onServiceClick
            )
        }

        // Promo / Cashback Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.banking_promo_banner),
                        contentDescription = "Cashback Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay with promo text
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xEE0B132B),
                                        Color(0x880B132B),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                            Surface(
                                color = PrimaryRose,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (isBangla) "ধামাকা অফার" else "MEGA OFFER",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBangla) "রিচার্জ ও সেন্ড মানিতে সর্বোচ্চ ৫০ টাকা ক্যাশব্যাক!"
                                else "Up to ৳50 Cashback on Recharge & Send Money!",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Recent Contacts
        item {
            RecentContactsRow(
                beneficiaries = beneficiaries,
                language = language,
                onContactClick = onContactClick,
                onAddContactClick = onAddContactClick
            )
        }

        // Recent Transactions Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) "সাম্প্রতিক লেনদেন" else "Recent Transactions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = if (isBangla) "সব লেনদেন" else "See All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryRose,
                    modifier = Modifier.clickable { onSeeAllTransactions() }
                )
            }
        }

        // Recent Transactions List (first 4)
        items(transactions.take(4)) { trx ->
            TransactionListItem(
                transaction = trx,
                language = language,
                onClick = { onTransactionClick(trx) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
