package com.example.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.*

@Composable
fun ConsoleSettingsView(
    totalProjectsCount: Int,
    totalSecretsCount: Int,
    totalUsersCount: Int,
    totalDocsCount: Int,
    onClearAllData: () -> Unit,
    onLoadStarterTemplates: () -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showConfirmWipeDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ConsoleCard(
                borderColor = ZentrixCyan.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(ZentrixCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = ZentrixCyan,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Console & Cluster Management",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Manage cluster data persistence, fresh starts, and backup exports",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Section: Cluster Configuration Details
        item {
            Text(
                text = "Cluster Specifications",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            ConsoleCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SettingDetailRow(label = "Backend Protocol", value = "Zentrix BaaS v2.5 Protocol")
                    SettingDetailRow(label = "Default Cluster Region", value = "ap-south-1 (Mumbai / Dhaka CDN)")
                    SettingDetailRow(label = "Primary Endpoint", value = "https://api.zentrixcloud.io/v1")
                    SettingDetailRow(label = "Local Storage Engine", value = "Android Room SQLite (Encrypted)")
                    SettingDetailRow(label = "Vault Cipher", value = "AES-256-GCM Zero-Trust")
                }
            }
        }

        // Section: Clean Slate & Demo Data Management
        item {
            Text(
                text = "Database State & Fresh Start",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            ConsoleCard(borderColor = ZentrixRed.copy(alpha = 0.4f)) {
                Text(
                    text = "Zero Demo Data / Reset Cluster",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Permanently wipes all demo records, test keys, and sample data. Allows you to begin with a 100% clean, pristine backend.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { showConfirmWipeDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Wipe All Data (Fresh Clean Start)", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Optional Templates
        item {
            ConsoleCard {
                Text(
                    text = "Load Starter Templates",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Optionally load starter configurations for Esports and Calculator if you want a reference blueprint.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onLoadStarterTemplates,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ZentrixCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ZentrixCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Load Starter App Templates", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showConfirmWipeDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmWipeDialog = false },
            containerColor = ConsoleSurface,
            title = {
                Text("Confirm Wipe All Data?", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "This will erase all projects, secrets, user accounts, and documents from the local database. No demo data will remain. Are you sure?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showConfirmWipeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixRed)
                ) {
                    Text("Yes, Wipe All Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmWipeDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SettingDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextMuted, fontSize = 12.sp)
        Text(
            text = value,
            color = TextPrimary,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
        )
    }
}
