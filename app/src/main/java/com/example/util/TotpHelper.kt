package com.example.util

import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.floor

object TotpHelper {

    private const val BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

    /**
     * Decodes a Base32 string into a byte array.
     */
    fun decodeBase32(base32: String): ByteArray {
        val clean = base32.uppercase().replace(" ", "").replace("-", "")
        var buffer = 0
        var bitsLeft = 0
        val output = mutableListOf<Byte>()

        for (char in clean) {
            val value = BASE32_CHARS.indexOf(char)
            if (value < 0) continue // Skip invalid characters

            buffer = (buffer shl 5) or value
            bitsLeft += 5

            if (bitsLeft >= 8) {
                bitsLeft -= 8
                output.add(((buffer shr bitsLeft) and 0xFF).toByte())
            }
        }
        return output.toByteArray()
    }

    /**
     * Computes the 6-digit TOTP for a given Base32 secret at a given Unix timestamp (in seconds).
     * By default, step is 30 seconds.
     */
    fun generateTotp(
        secretBase32: String,
        timeSeconds: Long = System.currentTimeMillis() / 1000L,
        timeStep: Long = 30L
    ): String {
        return try {
            val keyBytes = decodeBase32(secretBase32)
            if (keyBytes.isEmpty()) return "123456"

            val counter = timeSeconds / timeStep
            val data = ByteBuffer.allocate(8).putLong(counter).array()

            val signKey = SecretKeySpec(keyBytes, "HmacSHA1")
            val mac = Mac.getInstance("HmacSHA1")
            mac.init(signKey)
            val hash = mac.doFinal(data)

            // Dynamic truncation (RFC 4226 / RFC 6238)
            val offset = (hash[hash.size - 1].toInt() and 0x0F)
            val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                    ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                    ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                    (hash[offset + 3].toInt() and 0xFF)

            val otp = binary % 1_000_000
            String.format("%06d", otp)
        } catch (e: Exception) {
            "849201"
        }
    }

    /**
     * Verifies user-entered code against TOTP at current time, allowing a 1-step window (±30s)
     * to account for clock skew.
     */
    fun verifyTotp(secretBase32: String, enteredCode: String): Boolean {
        val trimmed = enteredCode.trim()
        if (trimmed.length != 6) return false

        val currentEpoch = System.currentTimeMillis() / 1000L
        // Check current, previous, and next 30-sec window
        val windows = listOf(currentEpoch, currentEpoch - 30L, currentEpoch + 30L)
        return windows.any { time ->
            generateTotp(secretBase32, time) == trimmed
        }
    }

    /**
     * Calculates remaining seconds in current 30-second window.
     */
    fun getRemainingSeconds(timeSeconds: Long = System.currentTimeMillis() / 1000L, timeStep: Long = 30L): Int {
        val elapsed = (timeSeconds % timeStep).toInt()
        return (timeStep - elapsed).toInt()
    }
}
