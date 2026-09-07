package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.PrimaryRose
import com.example.ui.theme.PrimaryRoseDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FingerprintScannerDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val isBangla = language == AppLanguage.BANGLA
    val coroutineScope = rememberCoroutineScope()

    var scanState by remember { mutableStateOf<ScanState>(ScanState.WAITING) }

    // Infinite pulse animation for sensor ring
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    fun startScanning() {
        if (scanState == ScanState.SCANNING || scanState == ScanState.SUCCESS) return
        coroutineScope.launch {
            scanState = ScanState.SCANNING
            delay(1100)
            scanState = ScanState.SUCCESS
            delay(700)
            onSuccess()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .testTag("fingerprint_scanner_dialog"),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryRose.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = PrimaryRose,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "বায়োমেট্রিক নিরাপত্তা" else "Biometric Security",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
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

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (isBangla) "আঙ্গুলের ছাপ স্ক্যান করুন" else "Fingerprint Authentication",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when (scanState) {
                        ScanState.WAITING -> if (isBangla) "নিচের সেন্সরে স্পর্শ করুন অথবা ট্যাপ করুন" else "Touch the sensor below to authenticate"
                        ScanState.SCANNING -> if (isBangla) "বায়োমেট্রিক তথ্য যাচাই করা হচ্ছে..." else "Verifying biometric fingerprint..."
                        ScanState.SUCCESS -> if (isBangla) "বায়োমেট্রিক সফলভাবে সনাক্ত হয়েছে!" else "Biometric Identity Verified!"
                    },
                    fontSize = 13.sp,
                    color = when (scanState) {
                        ScanState.SUCCESS -> EmeraldTeal
                        ScanState.SCANNING -> PrimaryRose
                        else -> Color(0xFF64748B)
                    },
                    fontWeight = if (scanState == ScanState.SUCCESS) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Interactive Sensor Button
                Box(
                    modifier = Modifier
                        .size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer pulsating halo
                    if (scanState == ScanState.WAITING || scanState == ScanState.SCANNING) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .scale(if (scanState == ScanState.SCANNING) 1.2f else pulseScale)
                                .clip(CircleShape)
                                .background(
                                    if (scanState == ScanState.SCANNING) PrimaryRose.copy(alpha = 0.2f)
                                    else PrimaryRose.copy(alpha = 0.08f)
                                )
                        )
                    }

                    // Middle Ring
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(
                                when (scanState) {
                                    ScanState.SUCCESS -> EmeraldTeal.copy(alpha = 0.15f)
                                    ScanState.SCANNING -> PrimaryRose.copy(alpha = 0.15f)
                                    else -> Color(0xFFF1F5F9)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (scanState == ScanState.SCANNING) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(96.dp),
                                color = PrimaryRose,
                                strokeWidth = 3.dp
                            )
                        }

                        // Core Button
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .clip(CircleShape)
                                .background(
                                    when (scanState) {
                                        ScanState.SUCCESS -> Brush.linearGradient(listOf(EmeraldTeal, Color(0xFF059669)))
                                        ScanState.SCANNING -> Brush.linearGradient(listOf(PrimaryRose, PrimaryRoseDark))
                                        else -> Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)))
                                    }
                                )
                                .clickable { startScanning() }
                                .testTag("touch_fingerprint_sensor_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (scanState == ScanState.SUCCESS) Icons.Default.CheckCircle else Icons.Default.Fingerprint,
                                contentDescription = "Fingerprint Sensor",
                                tint = if (scanState == ScanState.WAITING) PrimaryRose else Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Bottom Hint & Actions
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isBangla) "💡 সেন্সরে চাপ দিয়ে ফিঙ্গারপ্রিন্ট পরীক্ষা করুন" else "💡 Tap the sensor above to simulate scan",
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }
        }
    }
}

private enum class ScanState {
    WAITING,
    SCANNING,
    SUCCESS
}
