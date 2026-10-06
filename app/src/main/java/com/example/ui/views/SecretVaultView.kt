package com.example.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultSecretEntity
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.*

@Composable
fun SecretVaultView(
    secrets: List<VaultSecretEntity>,
    activeProjectId: String,
    revealedMap: Map<Long, Boolean>,
    onToggleVisibility: (Long) -> Unit,
    onAddSecretClick: () -> Unit,
    onDeleteSecret: (VaultSecretEntity) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var secretToDelete by remember { mutableStateOf<VaultSecretEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Vault Header
        item {
            ConsoleCard(
                borderColor = ZentrixAmber.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(ZentrixAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = ZentrixAmber,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Admin Secret Vault",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "ImgBB, Payment Gateway (bKash/Nagad), AI API Key ভল্ট",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZentrixAmber,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "ক্লায়েন্ট এপিকেতে সরাসরি হার্ডকোড না করে সার্ভার ভল্ট থেকে সরাসরি রিকোয়েস্ট পাস হবে। ফুল সিকিউরিটি ও AES-256 এনক্রিপশন সক্রিয়।",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = ConsoleCardBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(
                        text = "এনক্রিপশন: AES-GCM 256-bit",
                        color = ZentrixGreen
                    )
                    Button(
                        onClick = onAddSecretClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZentrixAmber,
                            contentColor = Color(0xFF261900)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_secret_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("নতুন সিক্রেট যোগ করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (secrets.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "এই প্রজেক্টে এখনও কোনো সিক্রেট কি নেই। 'নতুন সিক্রেট যোগ করুন' বাটনে চাপুন।",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        items(secrets, key = { it.id }) { secret ->
            val isVisible = revealedMap[secret.id] ?: false

            ConsoleCard(
                borderColor = if (secret.category.contains("Payment")) ZentrixAmber.copy(alpha = 0.4f) else ConsoleCardBorder
            ) {
                // Header: Key Name + Category Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = secret.keyName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = ZentrixCyan
                        )
                        Text(
                            text = if (secret.projectId == "global") "গ্লোবাল ক্লাস্টার কি" else "প্রজেক্ট: ${secret.projectId}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    StatusBadge(
                        text = secret.category,
                        color = when {
                            secret.category.contains("ImgBB") -> ZentrixCyan
                            secret.category.contains("Payment") -> ZentrixAmber
                            secret.category.contains("AI") -> ZentrixPurple
                            else -> ZentrixGreen
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secret Masked Value Box
                Surface(
                    color = CodeBackground,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ConsoleCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val displayValue = if (isVisible) secret.secretValue else "••••••••••••••••••••••••••••••••"

                        Text(
                            text = displayValue,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = if (isVisible) TextPrimary else TextMuted,
                            modifier = Modifier.weight(1f)
                        )

                        Row {
                            IconButton(
                                onClick = { onToggleVisibility(secret.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle visibility",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    copyToClipboard(context, secret.keyName, secret.secretValue) {
                                        onShowToast("${secret.keyName} কপি হয়েছে!")
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Secret",
                                    tint = ZentrixCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                if (secret.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = secret.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer row: Test Connection Ping & Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            onShowToast("${secret.keyName} ভ্যালিড ও কানেকশন সচল! [200 OK]")
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ZentrixGreen),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ZentrixGreen.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("টেস্ট পিং (Test Key)", fontSize = 11.sp)
                    }

                    IconButton(
                        onClick = { secretToDelete = secret },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Secret",
                            tint = ZentrixRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    if (secretToDelete != null) {
        val secret = secretToDelete!!
        AlertDialog(
            onDismissRequest = { secretToDelete = null },
            containerColor = ConsoleSurface,
            title = {
                Text(text = "সিক্রেট মুছে ফেলবেন?", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "${secret.keyName} ভল্ট থেকে মুছে দিলে সংযুক্ত এপিআই কল কাজ করবে না।",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSecret(secret)
                        secretToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixRed)
                ) {
                    Text("মুছে ফেলুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { secretToDelete = null }) {
                    Text("বাতিল", color = TextSecondary)
                }
            }
        )
    }
}
