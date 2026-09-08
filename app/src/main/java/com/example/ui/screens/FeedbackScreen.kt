package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun FeedbackScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("Bug/Error") }
    var message by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    
    val reasons = listOf("Bug/Error", "Suggestion", "Withdraw Issue", "Other")
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumBackground)
    ) {
        // Ambient Background Blurs
        Box(
            modifier = Modifier
                .offset(x = (-50).dp, y = (-50).dp)
                .size(250.dp)
                .blur(100.dp)
                .background(IconTintBlue.copy(alpha = 0.2f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x9907090E))
                    .padding(horizontal = 24.dp, vertical = 22.dp)
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PremiumSurface)
                        .border(1.dp, PremiumOutline, RoundedCornerShape(12.dp))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Text("Send Feedback", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("We'd love to hear from you. Please fill out the form below.", color = PremiumOnSurfaceVariant, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Your Name", color = PremiumOnSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IconTintBlue,
                            unfocusedBorderColor = PremiumOutline,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = PremiumSurface,
                            unfocusedContainerColor = PremiumSurface
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number", color = PremiumOnSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IconTintBlue,
                            unfocusedBorderColor = PremiumOutline,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = PremiumSurface,
                            unfocusedContainerColor = PremiumSurface
                        )
                    )
                }

                item {
                    Box {
                        OutlinedTextField(
                            value = reason,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Reason", color = PremiumOnSurfaceVariant) },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PremiumOnSurfaceVariant) },
                            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = IconTintBlue,
                                unfocusedBorderColor = PremiumOutline,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = PremiumSurface,
                                unfocusedContainerColor = PremiumSurface
                            )
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(PremiumSurfaceVariant)
                        ) {
                            reasons.forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r, color = Color.White) },
                                    onClick = {
                                        reason = r
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Message / Bug / Problem", color = PremiumOnSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        shape = RoundedCornerShape(16.dp),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IconTintBlue,
                            unfocusedBorderColor = PremiumOutline,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = PremiumSurface,
                            unfocusedContainerColor = PremiumSurface
                        )
                    )
                }

                item {
                    Button(
                        onClick = {
                            if (name.isBlank() || message.isBlank()) {
                                Toast.makeText(context, "Name and Message are required", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isSubmitting = true
                            viewModel.submitFeedback(name, phone, reason, message)
                            Toast.makeText(context, "Feedback Submitted!", Toast.LENGTH_SHORT).show()
                            onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IconTintBlue),
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Submit Feedback", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
