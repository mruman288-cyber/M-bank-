package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.UserAccount
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.PrimaryRose
import com.example.util.BanglaFormatter

@Composable
fun ProfileScreen(
    user: UserAccount?,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onToggleBiometric: (Boolean) -> Unit,
    onToggleFaceRecognition: (Boolean) -> Unit = {},
    onUpdateTwoFactorAuth: (mode: String, secret: String?, requireForTx: Boolean?) -> Unit = { _, _, _ -> },
    onChangePinClick: () -> Unit,
    onAddContactClick: () -> Unit,
    onTestFingerprint: () -> Unit = {},
    onTestFaceRecognition: () -> Unit = {},
    onTestTwoFactor: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isBangla = language == AppLanguage.BANGLA
    val name = if (isBangla) user?.name ?: "গ্রাহক" else user?.nameEn ?: "Customer"
    val phone = user?.phone ?: "017XX-XXXXXX"
    val clipboardManager = LocalClipboardManager.current

    val twoFactorMode = user?.twoFactorAuthMode ?: "SMS"
    val is2faTxRequired = user?.is2faRequiredForTransactions ?: true
    val authSecret = user?.authenticatorSecret ?: "JBSWY3DPEHPK3PXP"

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
                text = if (isBangla) "আমার প্রোফাইল ও নিরাপত্তা" else "Profile & Security",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A)
            )
        }

        // Profile Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_profile_card"),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(PrimaryRose.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.take(1),
                            color = PrimaryRose,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = EmeraldTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = if (isBangla) BanglaFormatter.toBanglaDigits(phone) else phone,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isBangla) "জাতীয় পরিচয়পত্র যাচাইকৃত" else "NID Verified Account",
                                color = EmeraldTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Biometrics & PIN Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "বায়োমেট্রিক ও পিন নিরাপত্তা" else "Biometrics & PIN Security",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldTeal.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = if (isBangla) "সক্রিয়" else "Active",
                                color = EmeraldTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Change PIN
                    SettingRow(
                        icon = Icons.Default.Lock,
                        title = if (isBangla) "৫-সংখ্যার পিন পরিবর্তন করুন" else "Change 5-Digit PIN",
                        subtitle = if (isBangla) "বর্তমান পিন: ${user?.pin ?: "12345"}" else "Current PIN: ${user?.pin ?: "12345"}",
                        onClick = onChangePinClick
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 10.dp))

                    // Fingerprint Biometric Switch & Test
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometrics",
                                tint = PrimaryRose,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = if (isBangla) "আঙ্গুলের ছাপ (ফিঙ্গারপ্রিন্ট)" else "Fingerprint Login",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (isBangla) "বায়োমেট্রিক স্পর্শে দ্রুত প্রমাণীকরণ" else "Fast touch authentication",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .clickable { onTestFingerprint() }
                                    .testTag("test_fingerprint_chip")
                            ) {
                                Text(
                                    text = if (isBangla) "পরীক্ষা" else "Test",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryRose,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = user?.isBiometricEnabled ?: true,
                                onCheckedChange = onToggleBiometric,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PrimaryRose
                                )
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 10.dp))

                    // Face Recognition Switch & Test
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = "Face ID",
                                tint = PrimaryRose,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = if (isBangla) "ফেস রিকগনিশন (ফেস আইডি)" else "Face ID Authentication",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (isBangla) "এআই থ্রিডি ফেসিয়াল ল্যান্ডমার্ক ম্যাচ" else "AI 3D facial landmark match",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .clickable { onTestFaceRecognition() }
                                    .testTag("test_face_id_chip")
                            ) {
                                Text(
                                    text = if (isBangla) "পরীক্ষা" else "Test",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryRose,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = user?.isFaceRecognitionEnabled ?: true,
                                onCheckedChange = onToggleFaceRecognition,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PrimaryRose
                                )
                            )
                        }
                    }
                }
            }
        }

        // Two-Factor Authentication (2FA) Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("two_factor_auth_card"),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "2FA",
                                tint = PrimaryRose,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBangla) "দ্বি-স্তরীয় নিরাপত্তা (2FA)" else "Two-Factor Auth (2FA)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (twoFactorMode != "NONE") EmeraldTeal.copy(alpha = 0.12f) else Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = when (twoFactorMode) {
                                    "SMS" -> if (isBangla) "এসএমএস ওটিপি" else "SMS OTP"
                                    "AUTHENTICATOR" -> if (isBangla) "অথেন্টিকেটর অ্যাপ" else "TOTP App"
                                    else -> if (isBangla) "বন্ধ" else "Disabled"
                                },
                                color = if (twoFactorMode != "NONE") EmeraldTeal else Color(0xFF64748B),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isBangla) "আপনার অ্যাকাউন্টকে হ্যাকিং ও অননুমোদিত লেনদেন থেকে সুরক্ষিত রাখতে ২-ধাপের যাচাইকরণ ব্যবহার করুন।"
                        else "Protect your wallet from unauthorized transactions with 2-step verification.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2FA Mode Selection Chips
                    Text(
                        text = if (isBangla) "যাচাইকরণের মাধ্যম নির্বাচন:" else "Verification Method:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // SMS Option
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onUpdateTwoFactorAuth("SMS", null, null) }
                                .border(
                                    width = if (twoFactorMode == "SMS") 2.dp else 1.dp,
                                    color = if (twoFactorMode == "SMS") PrimaryRose else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            color = if (twoFactorMode == "SMS") PrimaryRose.copy(alpha = 0.06f) else Color.White
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = "SMS",
                                    tint = if (twoFactorMode == "SMS") PrimaryRose else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isBangla) "এসএমএস ওটিপি" else "SMS OTP",
                                    fontSize = 12.sp,
                                    fontWeight = if (twoFactorMode == "SMS") FontWeight.Bold else FontWeight.Medium,
                                    color = if (twoFactorMode == "SMS") PrimaryRose else Color(0xFF0F172A)
                                )
                            }
                        }

                        // Authenticator App Option
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onUpdateTwoFactorAuth("AUTHENTICATOR", null, null) }
                                .border(
                                    width = if (twoFactorMode == "AUTHENTICATOR") 2.dp else 1.dp,
                                    color = if (twoFactorMode == "AUTHENTICATOR") PrimaryRose else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            color = if (twoFactorMode == "AUTHENTICATOR") PrimaryRose.copy(alpha = 0.06f) else Color.White
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = "TOTP",
                                    tint = if (twoFactorMode == "AUTHENTICATOR") PrimaryRose else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isBangla) "অথেন্টিকেটর" else "TOTP App",
                                    fontSize = 12.sp,
                                    fontWeight = if (twoFactorMode == "AUTHENTICATOR") FontWeight.Bold else FontWeight.Medium,
                                    color = if (twoFactorMode == "AUTHENTICATOR") PrimaryRose else Color(0xFF0F172A)
                                )
                            }
                        }

                        // Off Option
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onUpdateTwoFactorAuth("NONE", null, null) }
                                .border(
                                    width = if (twoFactorMode == "NONE") 2.dp else 1.dp,
                                    color = if (twoFactorMode == "NONE") PrimaryRose else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            color = if (twoFactorMode == "NONE") PrimaryRose.copy(alpha = 0.06f) else Color.White
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Off",
                                    tint = if (twoFactorMode == "NONE") PrimaryRose else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isBangla) "নিষ্ক্রিয়" else "Disabled",
                                    fontSize = 12.sp,
                                    fontWeight = if (twoFactorMode == "NONE") FontWeight.Bold else FontWeight.Medium,
                                    color = if (twoFactorMode == "NONE") PrimaryRose else Color(0xFF0F172A)
                                )
                            }
                        }
                    }

                    // Authenticator Secret Code Helper
                    if (twoFactorMode == "AUTHENTICATOR") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isBangla) "সিক্রেট কী (Secret Key):" else "Secret Key:",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = authSecret,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryRose.copy(alpha = 0.12f),
                                    modifier = Modifier.clickable {
                                        clipboardManager.setText(AnnotatedString(authSecret))
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = PrimaryRose,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isBangla) "কপি" else "Copy",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryRose
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Require 2FA for Transactions Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBangla) "লেনদেনের জন্য ২-ধাপ যাচাই আবশ্যক" else "Require 2FA for Transactions",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = if (isBangla) "টাকা পাঠানো বা ক্যাশ আউটের সময় কোড চাইবে" else "Prompts for code during payments/transfers",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Switch(
                            checked = is2faTxRequired,
                            onCheckedChange = { onUpdateTwoFactorAuth(twoFactorMode, null, it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryRose
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Test 2FA Trigger Button
                    Button(
                        onClick = onTestTwoFactor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("test_2fa_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F172A)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "২-ফ্যাক্টর প্রমাণীকরণ টেস্ট করুন" else "Test 2FA Authentication Flow",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // App General Settings
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isBangla) "সাধারণ সেটিংস" else "General Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Language Switch
                    SettingRow(
                        icon = Icons.Default.Translate,
                        title = if (isBangla) "অ্যাপের ভাষা পরিবর্তন" else "App Language",
                        subtitle = if (isBangla) "বর্তমান ভাষা: বাংলা" else "Current: English",
                        onClick = onToggleLanguage
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 10.dp))

                    // Add Contact
                    SettingRow(
                        icon = Icons.Default.PersonAdd,
                        title = if (isBangla) "নতুন প্রিয় নাম্বার যুক্ত করুন" else "Add Favorite Contact",
                        subtitle = if (isBangla) "সেন্ড মানি ও রিচার্জের জন্য" else "For fast send money & recharge",
                        onClick = onAddContactClick
                    )
                }
            }
        }

        // 24/7 Helpline & Support Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isBangla) "সহায়তা ও যোগাযোগ" else "Help & Support",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HeadsetMic,
                            contentDescription = "Helpline",
                            tint = EmeraldTeal,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = if (isBangla) "২৪/৭ কাস্টমার হেল্পলাইন" else "24/7 Customer Care",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = if (isBangla) "ডায়াল করুন: ১৬২৪৭ অথবা ০২-৯৬৬৯৯০০" else "Dial: 16247 or 02-9669900",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }

        // App Info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isBangla) "মোবাইল ব্যাংকিং অ্যাপ সংস্করণ ১.০ (সুরক্ষিত)" else "Mobile Banking App v1.0 (Secured)",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "256-bit SSL & AI Biometric Security",
                    fontSize = 10.sp,
                    color = Color(0xFFCBD5E1)
                )
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = PrimaryRose,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
