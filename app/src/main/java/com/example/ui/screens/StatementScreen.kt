package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.TransactionRecord
import com.example.ui.components.TransactionListItem
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.PrimaryRose
import com.example.util.BanglaFormatter

@Composable
fun StatementScreen(
    transactions: List<TransactionRecord>,
    language: AppLanguage,
    onTransactionClick: (TransactionRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = language == AppLanguage.BANGLA

    var selectedFilter by remember { mutableStateOf(0) } // 0: All, 1: Outflow, 2: Inflow
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = transactions.filter { trx ->
        val matchesFilter = when (selectedFilter) {
            1 -> trx.isExpense
            2 -> !trx.isExpense
            else -> true
        }
        val matchesSearch = if (searchQuery.isBlank()) true else {
            trx.title.contains(searchQuery, ignoreCase = true) ||
                    trx.titleBn.contains(searchQuery, ignoreCase = true) ||
                    trx.recipientName.contains(searchQuery, ignoreCase = true) ||
                    trx.recipientOrSource.contains(searchQuery, ignoreCase = true) ||
                    trx.trxId.contains(searchQuery, ignoreCase = true)
        }
        matchesFilter && matchesSearch
    }

    val totalExpense = transactions.filter { it.isExpense }.sumOf { it.amount + it.fee }
    val totalIncome = transactions.filter { !it.isExpense }.sumOf { it.amount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isBangla) "লেনদেন বিবরণী ও স্টেটমেন্ট" else "Transaction Statement",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = if (isBangla) "আপনার সকল ইনকাম ও খরচের পুঙ্খানুপুঙ্খ হিসাব" else "Detailed track record of all income & expenditures",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        // Summary Inflow / Outflow Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Outflow card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF1F2)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Expense",
                                tint = Color(0xFFE11D48),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "মোট খরচ" else "Total Out",
                                fontSize = 12.sp,
                                color = Color(0xFFE11D48),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = BanglaFormatter.formatCurrency(totalExpense, language),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                // Inflow card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFECFDF5)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Income",
                                tint = EmeraldTeal,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "মোট প্রাপ্তি" else "Total In",
                                fontSize = 12.sp,
                                color = EmeraldTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = BanglaFormatter.formatCurrency(totalIncome, language),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }
            }
        }

        // Search Box
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(if (isBangla) "নাম, নাম্বার বা TrxID দিয়ে খুঁজুন..." else "Search by name, phone or TrxID...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("statement_search_input"),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF94A3B8)
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRose,
                    focusedLabelColor = PrimaryRose,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
        }

        // Filter Chips
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val filters = listOf(
                    if (isBangla) "সকল লেনদেন (${BanglaFormatter.formatNumber(transactions.size, language)})" else "All (${transactions.size})",
                    if (isBangla) "খরচ (Expenses)" else "Expenses",
                    if (isBangla) "প্রাপ্তি (Income)" else "Income"
                )

                filters.forEachIndexed { index, title ->
                    FilterChip(
                        selected = selectedFilter == index,
                        onClick = { selectedFilter = index },
                        label = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryRose.copy(alpha = 0.15f),
                            selectedLabelColor = PrimaryRose
                        )
                    )
                }
            }
        }

        // Transactions list or Empty state
        if (filteredList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Empty",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isBangla) "কোনো লেনদেন পাওয়া যায়নি" else "No transactions found",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }
            }
        } else {
            items(filteredList) { trx ->
                TransactionListItem(
                    transaction = trx,
                    language = language,
                    onClick = { onTransactionClick(trx) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
