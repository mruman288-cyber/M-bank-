package com.example.data.model

enum class AppLanguage(val code: String, val displayName: String) {
    BANGLA("bn", "বাংলা"),
    ENGLISH("en", "English")
}

enum class TransactionType(
    val titleEn: String,
    val titleBn: String
) {
    SEND_MONEY("Send Money", "সেন্ড মানি"),
    MOBILE_RECHARGE("Mobile Recharge", "মোবাইল রিচার্জ"),
    CASH_OUT("Cash Out", "ক্যাশ আউট"),
    MAKE_PAYMENT("Payment", "পেমেন্ট"),
    ADD_MONEY("Add Money", "অ্যাড মানি"),
    PAY_BILL("Pay Bill", "বিল পে"),
    SAVINGS("Savings DPS", "সেভিংস ডিপিএস"),
    REMITTANCE("Remittance", "রেমিট্যান্স")
}

enum class MobileOperator(
    val displayName: String,
    val prefix: String,
    val colorHex: Long
) {
    GRAMEENPHONE("Grameenphone", "017 / 013", 0xFF00A2E8),
    BANGLALINK("Banglalink", "019 / 014", 0xFFFF7700),
    ROBI("Robi", "018", 0xFFE60000),
    AIRTEL("Airtel", "016", 0xFFD60000),
    TELETALK("Teletalk", "015", 0xFF008000)
}

enum class BillCategory(
    val titleEn: String,
    val titleBn: String
) {
    ELECTRICITY("Electricity", "বিদ্যুৎ"),
    GAS("Gas", "গ্যাস"),
    WATER("Water", "পানি"),
    INTERNET("Internet", "ইন্টারনেট")
}

data class QuickOffer(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val descEn: String,
    val descBn: String,
    val cashbackTag: String,
    val expiry: String
)
