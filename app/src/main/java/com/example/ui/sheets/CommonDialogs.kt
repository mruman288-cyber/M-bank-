package com.example.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.ui.theme.PrimaryRose

@Composable
fun ChangePinDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSavePin: (String) -> Unit
) {
    val isBangla = language == AppLanguage.BANGLA
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .testTag("change_pin_dialog")
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (isBangla) "নতুন পিন সেট করুন" else "Set New 5-Digit PIN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = newPin,
                    onValueChange = {
                        if (it.length <= 5 && it.all { c -> c.isDigit() }) newPin = it
                    },
                    label = { Text(if (isBangla) "নতুন ৫ ডিজিট পিন" else "New 5-Digit PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRose,
                        focusedLabelColor = PrimaryRose
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = {
                        if (it.length <= 5 && it.all { c -> c.isDigit() }) confirmPin = it
                    },
                    label = { Text(if (isBangla) "নতুন পিন নিশ্চিত করুন" else "Confirm New PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRose,
                        focusedLabelColor = PrimaryRose
                    )
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorMsg!!, color = Color(0xFFDC2626), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text(if (isBangla) "বাতিল" else "Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newPin.length != 5) {
                                errorMsg = if (isBangla) "৫ ডিজিটের সংখ্যা লিখুন" else "Must be 5 digits"
                                return@Button
                            }
                            if (newPin != confirmPin) {
                                errorMsg = if (isBangla) "উভয় পিন একই নয়" else "PINs do not match"
                                return@Button
                            }
                            onSavePin(newPin)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRose)
                    ) {
                        Text(if (isBangla) "সংরক্ষণ" else "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun AddContactDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onAdd: (name: String, phone: String) -> Unit
) {
    val isBangla = language == AppLanguage.BANGLA
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .testTag("add_contact_dialog")
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (isBangla) "নতুন প্রিয় নাম্বার যোগ করুন" else "Add New Favorite Contact",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBangla) "নাম" else "Name") },
                    placeholder = { Text(if (isBangla) "যেমন: আব্বা বা বন্ধু" else "e.g. Father, Friend") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRose,
                        focusedLabelColor = PrimaryRose
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isBangla) "মোবাইল নাম্বার" else "Mobile Number") },
                    placeholder = { Text("01XXXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRose,
                        focusedLabelColor = PrimaryRose
                    )
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorMsg!!, color = Color(0xFFDC2626), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text(if (isBangla) "বাতিল" else "Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank() || phone.isBlank()) {
                                errorMsg = if (isBangla) "নাম ও নাম্বার উভয়টি লিখুন" else "Enter both name and phone"
                                return@Button
                            }
                            onAdd(name, phone)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRose)
                    ) {
                        Text(if (isBangla) "যোগ করুন" else "Add")
                    }
                }
            }
        }
    }
}
