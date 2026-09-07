package com.example.util

import com.example.data.model.AppLanguage
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BanglaFormatter {

    private val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun toBanglaDigits(input: String): String {
        val sb = StringBuilder()
        for (char in input) {
            if (char in '0'..'9') {
                sb.append(banglaDigits[char - '0'])
            } else {
                sb.append(char)
            }
        }
        return sb.toString()
    }

    fun formatCurrency(amount: Double, language: AppLanguage): String {
        val df = DecimalFormat("#,##,##0.00")
        val formatted = df.format(amount)
        return if (language == AppLanguage.BANGLA) {
            "৳ ${toBanglaDigits(formatted)}"
        } else {
            "৳ $formatted"
        }
    }

    fun formatNumber(number: Number, language: AppLanguage): String {
        val str = number.toString()
        return if (language == AppLanguage.BANGLA) {
            toBanglaDigits(str)
        } else {
            str
        }
    }

    fun formatDate(timestamp: Long, language: AppLanguage): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
        val formatted = sdf.format(Date(timestamp))
        return if (language == AppLanguage.BANGLA) {
            val banglaMonths = mapOf(
                "Jan" to "জানু", "Feb" to "ফেব্রু", "Mar" to "মার্চ",
                "Apr" to "এপ্রিল", "May" to "মে", "Jun" to "জুন",
                "Jul" to "জুলাই", "Aug" to "আগস্ট", "Sep" to "সেপ্টে",
                "Oct" to "অক্টো", "Nov" to "নভে", "Dec" to "ডিসে",
                "AM" to "সকাল", "PM" to "সন্ধ্যা"
            )
            var result = formatted
            for ((en, bn) in banglaMonths) {
                result = result.replace(en, bn)
            }
            toBanglaDigits(result)
        } else {
            formatted
        }
    }
}
