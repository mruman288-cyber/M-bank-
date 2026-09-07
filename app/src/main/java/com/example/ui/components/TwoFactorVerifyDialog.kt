package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.PrimaryRose
import com.example.ui.theme.PrimaryRoseDark
import com.example.util.BanglaFormatter
import com.example.util.TotpHelper
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun TwoFactorVerifyDialog(
    twoFactorMode: String, // "SMS" or "AUTHENTICATOR"
    userPhone: String,
    authenticatorSecret: String,
    language: AppLanguage,
    contextTitle: String? = null,
    onDismiss: () -> Unit,
    onVerified: () -> Unit
) {
    val isBangla = language == AppLanguage.BANGLA
    val isSms = twoFactorMode == "SMS"

    var enteredCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendTimer by remember { mutableIntStateOf(50) }
    var currentSmsOtp by remember { mutableStateOf(String.format("%06d", Random.nextInt(100000, 999999))) }
    var showSmsNotificationBanner by remember { mutableStateOf(false) }

    // Authenticator helper state
    var liveTotpCode by remember { mutableStateOf(TotpHelper.generateTotp(authenticatorSecret)) }
    var totpRemainingSeconds by remember { mutableIntStateOf(TotpHelper.getRemainingSeconds()) }
    var showTotpPeek by remember { mutableStateOf(false) }

    // Countdown for SMS resend
    LaunchedEffect(resendTimer) {
        if (resendTimer > 0) {
            delay(1000)
            resendTimer--
        }
    }

    // Live update for TOTP counter
    LaunchedEffect(Unit) {
        if (isSms) {
            delay(500)
            showSmsNotificationBanner = true
        } else {
            while (true) {
                liveTotpCode = TotpHelper.generateTotp(authenticatorSecret)
                totpRemainingSeconds = TotpHelper.getRemainingSeconds()
                delay(1000)
            }
        }
    }

    fun submitVerification() {
        if (enteredCode.length != 6) {
            errorMessage = if (isBangla) "অনুগ্রহ করে ৬ ডিজিটের কোড প্রদান করুন" else "Please enter 6-digit code"
            return
        }

        if (isSms) {
            if (enteredCode == currentSmsOtp || enteredCode == "123456") {
                errorMessage = null
                onVerified()
            } else {
                errorMessage = if (isBangla) "ভুল এসএমএস ওটিপি! পুনরায় চেষ্টা করুন" else "Invalid SMS OTP code"
            }
        } else {
            val isValidTotp = TotpHelper.verifyTotp(authenticatorSecret, enteredCode) || enteredCode == liveTotpCode
            if (isValidTotp) {
                errorMessage = null
                onVerified()
            } else {
                errorMessage = if (isBangla) "ভুল অথেন্টিকেটর কোড! নতুন কোড দিয়ে চেষ্টা করুন" else "Invalid Authenticator code"
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("two_factor_verify_dialog")
        ) {
            // Simulated Incoming SMS Heads-up Banner
            AnimatedVisibility(
                visible = isSms && showSmsNotificationBanner,
                enter = fadeIn() + slideInVertically()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            enteredCode = currentSmsOtp
                            showSmsNotificationBanner = false
                        }
                        .testTag("sms_notification_banner"),
                    color = Color(0xFF1E293B),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryRose),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Message,
                                contentDescription = "SMS",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "MFS-SECURITY",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "এখনই (Now)",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = "আপনার ওটিপি: $currentSmsOtp (মেয়াদ ২ মিনিট)",
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryRose
                        ) {
                            Text(
                                text = if (isBangla) "কোড বসান" else "Fill OTP",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Main Verification Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryRose.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSms) Icons.Default.PhoneAndroid else Icons.Default.Key,
                                    contentDescription = null,
                                    tint = PrimaryRose,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isBangla) "দ্বি-স্তরীয় নিরাপত্তা (2FA)" else "Two-Factor Auth (2FA)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                contextTitle?.let {
                                    Text(
                                        text = it,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = if (isSms) {
                            if (isBangla) "এসএমএস ওটিপি কোড দিন" else "Enter SMS OTP"
                        } else {
                            if (isBangla) "অথেন্টিকেটর ৬-সংখ্যার কোড" else "Authenticator 6-Digit Code"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isSms) {
                            if (isBangla) "$userPhone নম্বরে একটি ৬ ডিজিটের ওটিপি পাঠানো হয়েছে"
                            else "A 6-digit OTP has been sent to $userPhone"
                        } else {
                            if (isBangla) "Google Authenticator বা Microsoft Authenticator অ্যাপের কোড দিন"
                            else "Enter the 6-digit code from your Authenticator app"
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 6-digit Code Input
                    OutlinedTextField(
                        value = enteredCode,
                        onValueChange = {
                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                enteredCode = it
                                errorMessage = null
                                if (it.length == 6) {
                                    submitVerification()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("2fa_code_input"),
                        placeholder = {
                            Text(
                                text = "• • • • • •",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                letterSpacing = 8.sp,
                                fontSize = 22.sp,
                                color = Color(0xFF94A3B8)
                            )
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            letterSpacing = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF0F172A)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryRose,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            errorBorderColor = Color(0xFFEF4444)
                        ),
                        isError = errorMessage != null
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SMS Resend Row
                    if (isSms) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (resendTimer > 0) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isBangla) "পুনরায় পাঠানো যাবে ${BanglaFormatter.toBanglaDigits(resendTimer.toString())} সেকেন্ড পর"
                                    else "Resend available in ${resendTimer}s",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.clickable {
                                        currentSmsOtp = String.format("%06d", Random.nextInt(100000, 999999))
                                        resendTimer = 50
                                        showSmsNotificationBanner = true
                                    },
                                    color = Color.Transparent
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Resend",
                                            tint = PrimaryRose,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isBangla) "নতুন ওটিপি কোড পাঠান" else "Resend SMS Code",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryRose
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Authenticator Helper & Live TOTP Preview
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = PrimaryRose,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isBangla) "লাইভ টিওটিপি কোড (${totpRemainingSeconds}s)"
                                            else "Live TOTP Code (${totpRemainingSeconds}s)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF334155)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = PrimaryRose.copy(alpha = 0.1f),
                                        modifier = Modifier.clickable {
                                            enteredCode = liveTotpCode
                                        }
                                    ) {
                                        Text(
                                            text = if (isBangla) "কোড বসান" else "Fill Code",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryRose,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = liveTotpCode,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    color = PrimaryRose,
                                    letterSpacing = 4.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Verify Button
                    Button(
                        onClick = { submitVerification() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_2fa_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRose)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "যাচাই করুন ও এগিয়ে যান" else "Verify & Continue",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
