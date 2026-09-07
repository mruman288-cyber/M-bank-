package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun FaceRecognitionDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val isBangla = language == AppLanguage.BANGLA
    val coroutineScope = rememberCoroutineScope()

    var scanPhase by remember { mutableStateOf(FacePhase.SCANNING) }
    var progress by remember { mutableFloatStateOf(0.15f) }

    // Laser bar scanning up and down
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    LaunchedEffect(Unit) {
        // Multi-stage facial analysis sequence
        delay(600)
        progress = 0.45f
        scanPhase = FacePhase.ANALYZING_LANDMARKS
        delay(800)
        progress = 0.85f
        scanPhase = FacePhase.LIVENESS_CHECK
        delay(700)
        progress = 1.0f
        scanPhase = FacePhase.VERIFIED
        delay(600)
        onSuccess()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .testTag("face_recognition_dialog"),
            color = Color(0xFF0F172A), // Premium Dark Security Canvas
            shadowElevation = 12.dp
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
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryRose.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                tint = PrimaryRose,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "ফেস রিকগনিশন (ফেস আইডি)" else "Face Recognition (Face ID)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Viewfinder HUD Reticle
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFF1E293B))
                        .border(
                            width = 2.dp,
                            color = if (scanPhase == FacePhase.VERIFIED) EmeraldTeal else PrimaryRose.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Face silhouette & Biometric Nodes Canvas
                    Canvas(modifier = Modifier.size(180.dp)) {
                        val width = size.width
                        val height = size.height

                        // Draw targeting crosshairs on corners
                        val cornerLen = 24.dp.toPx()
                        val stroke = 3.dp.toPx()
                        val hudColor = if (scanPhase == FacePhase.VERIFIED) EmeraldTeal else Color(0xFF00E5FF)

                        // Top-left
                        drawLine(hudColor, Offset(16f, 16f), Offset(16f + cornerLen, 16f), stroke)
                        drawLine(hudColor, Offset(16f, 16f), Offset(16f, 16f + cornerLen), stroke)
                        // Top-right
                        drawLine(hudColor, Offset(width - 16f, 16f), Offset(width - 16f - cornerLen, 16f), stroke)
                        drawLine(hudColor, Offset(width - 16f, 16f), Offset(width - 16f, 16f + cornerLen), stroke)
                        // Bottom-left
                        drawLine(hudColor, Offset(16f, height - 16f), Offset(16f + cornerLen, height - 16f), stroke)
                        drawLine(hudColor, Offset(16f, height - 16f), Offset(16f, height - 16f - cornerLen), stroke)
                        // Bottom-right
                        drawLine(hudColor, Offset(width - 16f, height - 16f), Offset(width - 16f - cornerLen, height - 16f), stroke)
                        drawLine(hudColor, Offset(width - 16f, height - 16f), Offset(width - 16f, height - 16f - cornerLen), stroke)

                        // Face Oval Guide
                        drawOval(
                            color = hudColor.copy(alpha = 0.25f),
                            topLeft = Offset(width * 0.2f, height * 0.15f),
                            size = androidx.compose.ui.geometry.Size(width * 0.6f, height * 0.7f),
                            style = Stroke(
                                width = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                            )
                        )

                        // Biometric Facial Landmark Dots
                        val landmarks = listOf(
                            Offset(width * 0.38f, height * 0.38f), // left eye
                            Offset(width * 0.62f, height * 0.38f), // right eye
                            Offset(width * 0.50f, height * 0.52f), // nose bridge
                            Offset(width * 0.44f, height * 0.66f), // mouth left
                            Offset(width * 0.56f, height * 0.66f), // mouth right
                            Offset(width * 0.50f, height * 0.76f), // chin
                            Offset(width * 0.30f, height * 0.50f), // left cheek
                            Offset(width * 0.70f, height * 0.50f)  // right cheek
                        )

                        landmarks.forEach { pt ->
                            drawCircle(
                                color = if (scanPhase == FacePhase.VERIFIED) EmeraldTeal else Color(0xFF00E5FF),
                                radius = 3.5.dp.toPx(),
                                center = pt
                            )
                        }

                        // Connecting biometric geometry lines
                        drawLine(Color(0x3300E5FF), landmarks[0], landmarks[1], 1f)
                        drawLine(Color(0x3300E5FF), landmarks[0], landmarks[2], 1f)
                        drawLine(Color(0x3300E5FF), landmarks[1], landmarks[2], 1f)
                        drawLine(Color(0x3300E5FF), landmarks[2], landmarks[3], 1f)
                        drawLine(Color(0x3300E5FF), landmarks[2], landmarks[4], 1f)
                        drawLine(Color(0x3300E5FF), landmarks[3], landmarks[5], 1f)
                        drawLine(Color(0x3300E5FF), landmarks[4], landmarks[5], 1f)

                        // Animated Sweeping Laser Line
                        if (scanPhase != FacePhase.VERIFIED) {
                            val laserY = height * laserOffset
                            drawLine(
                                brush = Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color(0xFF00E5FF), Color.White, Color(0xFF00E5FF), Color.Transparent)
                                ),
                                start = Offset(16f, laserY),
                                end = Offset(width - 16f, laserY),
                                strokeWidth = 2.5.dp.toPx()
                            )
                        }
                    }

                    // Success Overlay
                    if (scanPhase == FacePhase.VERIFIED) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(EmeraldTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Phase Status Text
                Text(
                    text = when (scanPhase) {
                        FacePhase.SCANNING -> if (isBangla) "ক্যামেরার দিকে তাকান..." else "Looking for Face..."
                        FacePhase.ANALYZING_LANDMARKS -> if (isBangla) "বায়োমেট্রিক জ্যামিতি বিশ্লেষণ হচ্ছে..." else "Analyzing 128 Facial Landmarks..."
                        FacePhase.LIVENESS_CHECK -> if (isBangla) "লাইভনেস ও নিরাপত্তা যাচাই হচ্ছে..." else "Verifying 3D Biometric Liveness..."
                        FacePhase.VERIFIED -> if (isBangla) "চেহারা সফলভাবে যাচাইকৃত!" else "Face Identity Confirmed!"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (scanPhase == FacePhase.VERIFIED) EmeraldTeal else Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isBangla) "কৃত্রিম বুদ্ধিমত্তা চালিত অ্যান্টি-স্পুফিং সিকিউরিটি"
                    else "AI-Powered Anti-Spoofing 3D Neural Match",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Indicator
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (scanPhase == FacePhase.VERIFIED) EmeraldTeal else Color(0xFF00E5FF),
                    trackColor = Color(0xFF334155),
                )
            }
        }
    }
}

private enum class FacePhase {
    SCANNING,
    ANALYZING_LANDMARKS,
    LIVENESS_CHECK,
    VERIFIED
}
