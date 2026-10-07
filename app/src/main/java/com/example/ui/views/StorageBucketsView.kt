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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

data class ZentrixStorageItem(
    val id: String,
    val name: String,
    val size: String,
    val mimeType: String,
    val isPublic: Boolean,
    val updated: String
)

@Composable
fun StorageBucketsView(
    activeProjectId: String,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sampleFiles = remember {
        mutableStateListOf(
            ZentrixStorageItem("f1", "zentrix_esports_v1.0.apk", "48.2 MB", "application/vnd.android.package-archive", true, "Today 10:14 AM"),
            ZentrixStorageItem("f2", "banner_tournament_2026.png", "2.4 MB", "image/png", true, "Yesterday"),
            ZentrixStorageItem("f3", "user_avatar_default.webp", "140 KB", "image/webp", true, "3 days ago"),
            ZentrixStorageItem("f4", "database_backup_march.zip", "18.6 MB", "application/zip", false, "5 days ago")
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ZentrixBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Storage Header
        item {
            ConsoleCard(borderColor = ZentrixViolet.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ZentrixViolet.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = ZentrixViolet, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Zentrix Object Storage",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Secure bucket for APKs, images, videos, audio & zip bundles",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = {
                            sampleFiles.add(
                                ZentrixStorageItem(
                                    id = "f_${System.currentTimeMillis()}",
                                    name = "upload_${sampleFiles.size + 1}.png",
                                    size = "1.8 MB",
                                    mimeType = "image/png",
                                    isPublic = true,
                                    updated = "Just now"
                                )
                            )
                            onShowToast("File registered in storage bucket!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZentrixViolet, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upload", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Bucket Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ConsoleCard(modifier = Modifier.weight(1f)) {
                    Column {
                        Text("Total Stored", color = TextSecondary, fontSize = 11.sp)
                        Text("69.34 MB", fontWeight = FontWeight.Black, color = ZentrixCyan, fontSize = 18.sp)
                    }
                }
                ConsoleCard(modifier = Modifier.weight(1f)) {
                    Column {
                        Text("Bandwidth (Egress)", color = TextSecondary, fontSize = 11.sp)
                        Text("1.82 GB", fontWeight = FontWeight.Black, color = ZentrixGreen, fontSize = 18.sp)
                    }
                }
                ConsoleCard(modifier = Modifier.weight(1f)) {
                    Column {
                        Text("Signed URLs", color = TextSecondary, fontSize = 11.sp)
                        Text("12 Active", fontWeight = FontWeight.Black, color = ZentrixAmber, fontSize = 18.sp)
                    }
                }
            }
        }

        // File List Header
        item {
            Text(
                text = "Stored Assets (${sampleFiles.size})",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        items(sampleFiles, key = { it.id }) { file ->
            ConsoleCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ZentrixSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            val icon = if (file.name.endsWith(".apk")) Icons.Default.Android
                            else if (file.mimeType.startsWith("image")) Icons.Default.Image
                            else Icons.Default.InsertDriveFile
                            Icon(icon, contentDescription = null, tint = ZentrixCyan, modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = file.name,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${file.size} • ${file.mimeType} • ${file.updated}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { onShowToast("Signed download URL generated for ${file.name}") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = ZentrixCyan, modifier = Modifier.size(16.dp))
                        }

                        IconButton(
                            onClick = {
                                sampleFiles.remove(file)
                                onShowToast("Removed ${file.name}")
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = ZentrixRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
