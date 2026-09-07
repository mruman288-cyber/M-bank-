package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.QuickOffer
import com.example.data.model.UserAccount
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.PrimaryRose
import com.example.util.BanglaFormatter

@Composable
fun LimitsAndOffersScreen(
    user: UserAccount?,
    offers: List<QuickOffer>,
    language: AppLanguage,
    onOfferClick: (QuickOffer) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = language == AppLanguage.BANGLA

    val dailyUsed = user?.dailyUsed ?: 0.0
    val dailyLimit = user?.dailyLimit ?: 25000.0
    val dailyProgress = (dailyUsed / dailyLimit).toFloat().coerceIn(0f, 1f)

    val monthlyUsed = user?.monthlyUsed ?: 0.0
    val monthlyLimit = user?.monthlyLimit ?: 200000.0
    val monthlyProgress = (monthlyUsed / monthlyLimit).toFloat().coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isBangla) "লেনদেন সীমা ও বিশেষ অফার" else "Limits & Special Offers",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = if (isBangla) "আপনার অ্যাকাউন্টের দৈনিক ও মাসিক ট্রানজেকশন লিমিট" else "Check daily and monthly transaction limits and reward deals",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        // Limits Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("limits_overview_card"),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Limits",
                            tint = PrimaryRose,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "ট্রানজেকশন লিমিট ট্র্যাকার" else "Transaction Limits Tracker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Daily Limit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "দৈনিক ব্যবহার" else "Daily Used",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "${BanglaFormatter.formatCurrency(dailyUsed, language)} / ${BanglaFormatter.formatCurrency(dailyLimit, language)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { dailyProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = PrimaryRose,
                        trackColor = Color(0xFFF1F5F9)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Monthly Limit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "মাসিক ব্যবহার" else "Monthly Used",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "${BanglaFormatter.formatCurrency(monthlyUsed, language)} / ${BanglaFormatter.formatCurrency(monthlyLimit, language)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { monthlyProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmeraldTeal,
                        trackColor = Color(0xFFF1F5F9)
                    )
                }
            }
        }

        // Service limits info table
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isBangla) "সেবা ভিত্তিক শর্তাবলী ও চার্জ" else "Service Limits & Fees",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LimitDetailRow(
                        service = if (isBangla) "সেন্ড মানি (প্রিয় নাম্বার)" else "Send Money (Favorites)",
                        limit = if (isBangla) "২৫,০০০ টাকা / মাস" else "৳25,000 / month",
                        charge = if (isBangla) "ফ্রি (০ টাকা)" else "Free (৳0)"
                    )
                    LimitDetailRow(
                        service = if (isBangla) "ক্যাশ আউট (অ্যাপ থেকে)" else "Cash Out (App)",
                        limit = if (isBangla) "৫০,০০০ টাকা / দিন" else "৳50,000 / day",
                        charge = if (isBangla) "১.৪৯% চার্জ" else "1.49% fee"
                    )
                    LimitDetailRow(
                        service = if (isBangla) "মোবাইল রিচার্জ" else "Mobile Recharge",
                        limit = if (isBangla) "১০,০০০ টাকা / দিন" else "৳10,000 / day",
                        charge = if (isBangla) "ফ্রি (০ টাকা)" else "Free (৳0)"
                    )
                    LimitDetailRow(
                        service = if (isBangla) "ইউটিলিটি বিল পে" else "Utility Bill Pay",
                        limit = if (isBangla) "অসীমিত" else "Unlimited",
                        charge = if (isBangla) "ফ্রি (০ টাকা)" else "Free (৳0)"
                    )
                }
            }
        }

        // Offers Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = "Offers",
                    tint = PrimaryRose,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "চলমান ক্যাশব্যাক ও কুপন অফার" else "Active Cashback & Offers",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A)
                )
            }
        }

        // Offers Cards
        items(offers) { offer ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onOfferClick(offer) }
                    .testTag("offer_card_${offer.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFDE8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Gift",
                            tint = PrimaryRose,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) offer.titleBn else offer.titleEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A)
                            )

                            Surface(
                                color = PrimaryRose,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = offer.cashbackTag,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isBangla) offer.descBn else offer.descEn,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isBangla) "মেয়াদ: ${offer.expiry}" else "Expiry: ${offer.expiry}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LimitDetailRow(
    service: String,
    limit: String,
    charge: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = service, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
            Text(text = limit, fontSize = 11.sp, color = Color(0xFF64748B))
        }
        Text(
            text = charge,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (charge.contains("ফ্রি") || charge.contains("Free")) EmeraldTeal else PrimaryRose
        )
    }
}
