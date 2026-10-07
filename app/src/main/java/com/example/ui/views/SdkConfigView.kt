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
import com.example.data.model.ProjectEntity
import com.example.data.repository.KeyType
import com.example.ui.components.CodeBlockView
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.*

@Composable
fun SdkConfigView(
    project: ProjectEntity?,
    onRotateKey: (String, KeyType) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf("Kotlin (Android)") }
    var keyToRotateConfirm by remember { mutableStateOf<KeyType?>(null) }

    if (project == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Apps, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("No Active App Selected", color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("Select or register an application in the Projects tab to view SDK credentials.", color = TextMuted, fontSize = 12.sp)
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Highlight Header
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
                            .background(ZentrixPurple.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = ZentrixCyan,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Zentrix SDK & Connection Hub",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "App: ${project.appName} • pkg: ${project.packageName}",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall,
                            color = ZentrixCyan,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Similar to Firebase SDK initialization, Zentrix generates distinct keys for Admin, Staff/Moderators, and Client applications with custom package bindings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        // Section: 3 Tier Security Keys
        item {
            Text(
                text = "1. Security Credentials & Access Keys",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        // 1. Admin Master Key
        item {
            KeyCard(
                title = "Admin Master Secret Key",
                subTitle = "Full root access for vault management, DB drop, and keys",
                keyString = project.adminKey,
                badgeText = "SUPER_ADMIN",
                badgeColor = ZentrixRed,
                scopeDescription = "Unrestricted access. Keep strictly on server backend. Never package into public client APKs.",
                onCopy = {
                    copyToClipboard(context, "Admin Key", project.adminKey) {
                        onShowToast("Admin Key copied to clipboard")
                    }
                },
                onRotate = { keyToRotateConfirm = KeyType.ADMIN }
            )
        }

        // 2. Staff Key
        item {
            KeyCard(
                title = "Staff & Moderator Key",
                subTitle = "For operators, moderators, tournament ref, and support staff",
                keyString = project.staffKey,
                badgeText = "STAFF_AUTHORIZED",
                badgeColor = ZentrixAmber,
                scopeDescription = "Permitted for tournament slot allocation, room creation, and user support. Vault secrets are blocked.",
                onCopy = {
                    copyToClipboard(context, "Staff Key", project.staffKey) {
                        onShowToast("Staff Key copied to clipboard")
                    }
                },
                onRotate = { keyToRotateConfirm = KeyType.STAFF }
            )
        }

        // 3. Client Public Key
        item {
            KeyCard(
                title = "Client App Public Key",
                subTitle = "Safe for client Android APK and frontend web integration",
                keyString = project.clientPublicKey,
                badgeText = "PUBLIC_CLIENT",
                badgeColor = ZentrixGreen,
                scopeDescription = "Bound to package '${project.packageName}'. Restricted by Zero-Trust security rules.",
                onCopy = {
                    copyToClipboard(context, "Client Key", project.clientPublicKey) {
                        onShowToast("Client Public Key copied to clipboard")
                    }
                },
                onRotate = { keyToRotateConfirm = KeyType.CLIENT }
            )
        }

        // Section: Code Integration Snippets
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "2. Client SDK Initialization Snippet",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        // Language Selectors
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Kotlin (Android)", "JavaScript", "Flutter (Dart)", "REST (cURL)").forEach { lang ->
                    val isSelected = lang == selectedLanguage
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedLanguage = lang },
                        label = { Text(lang, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ZentrixCyan,
                            selectedLabelColor = ConsoleBackground,
                            containerColor = ConsoleSurfaceVariant,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        // Snippet Box
        item {
            val codeSnippet = when (selectedLanguage) {
                "Kotlin (Android)" -> """
// Android Kotlin Client Setup (in Application or Activity)
// Package: ${project.packageName}
package ${project.packageName}

import io.zentrix.sdk.ZentrixClient
import io.zentrix.sdk.ZentrixOptions

class MainApplication : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Zentrix Cloud Client
        val zentrix = ZentrixClient.Builder()
            .setPackageName("${project.packageName}")
            .setProjectId("${project.id}")
            .setPublicKey("${project.clientPublicKey}")
            .setStaffToken("${project.staffKey}") // Provide only in Admin/Staff portal
            .setEndpoint("https://api.zentrixcloud.io/v1")
            .enableOfflinePersistence(true)
            .build()
    }
}
""".trimIndent()

                "JavaScript" -> """
// Zentrix Cloud SDK Initialization (Node.js or Web)
import { initializeZentrix } from '@zentrix/cloud-sdk';

const zentrix = initializeZentrix({
  packageName: "${project.packageName}",
  projectId: "${project.id}",
  apiKey: "${project.clientPublicKey}",
  staffKey: "${project.staffKey}",
  endpoint: "https://api.zentrixcloud.io/v1",
  environment: "${project.environment.lowercase()}",
  auth: {
    providers: ["gmail", "email_password", "phone_otp"]
  }
});

// Fetch documents from database:
const documents = await zentrix.db.collection('tournaments').get();
console.log("Connected to Zentrix cluster:", zentrix.status);
""".trimIndent()

                "Flutter (Dart)" -> """
// Flutter / Dart Integration
import 'package:zentrix_flutter/zentrix_flutter.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  final zentrix = await Zentrix.initialize(
    packageName: '${project.packageName}',
    projectId: '${project.id}',
    apiKey: '${project.clientPublicKey}',
    staffToken: '${project.staffKey}',
    options: ZentrixOptions(
      region: 'ap-south-1',
      enableSecurityRules: true,
    ),
  );
  
  runApp(const MyApp());
}
""".trimIndent()

                else -> """
# cURL REST API Example
curl -X GET "https://api.zentrixcloud.io/v1/projects/${project.id}/data" \
  -H "X-Zentrix-Package: ${project.packageName}" \
  -H "Authorization: Bearer ${project.staffKey}" \
  -H "X-Zentrix-Client-Key: ${project.clientPublicKey}" \
  -H "Content-Type: application/json"
""".trimIndent()
            }

            CodeBlockView(
                code = codeSnippet,
                title = "$selectedLanguage • ${project.appName}",
                onCopy = {
                    copyToClipboard(context, "SDK Code", codeSnippet) {
                        onShowToast("$selectedLanguage code copied to clipboard")
                    }
                }
            )
        }
    }

    // Key Rotation Confirmation Dialog
    if (keyToRotateConfirm != null) {
        val keyType = keyToRotateConfirm!!
        AlertDialog(
            onDismissRequest = { keyToRotateConfirm = null },
            containerColor = ConsoleSurface,
            title = {
                Text(
                    text = "Rotate ${keyType.name} Key?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Rotating this credential will immediately invalidate the previous key. Any apps using the old key must be updated. Confirm rotation?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRotateKey(project.id, keyType)
                        keyToRotateConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixRed)
                ) {
                    Text("Rotate Key")
                }
            },
            dismissButton = {
                TextButton(onClick = { keyToRotateConfirm = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun KeyCard(
    title: String,
    subTitle: String,
    keyString: String,
    badgeText: String,
    badgeColor: Color,
    scopeDescription: String,
    onCopy: () -> Unit,
    onRotate: () -> Unit
) {
    ConsoleCard(
        borderColor = badgeColor.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            StatusBadge(text = badgeText, color = badgeColor)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Monospace key bar
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
                Text(
                    text = keyString,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = ZentrixCyan,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Key",
                        tint = ZentrixCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = scopeDescription,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            fontSize = 11.sp,
            lineHeight = 15.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onRotate,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = ZentrixAmber, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Rotate Key", color = ZentrixAmber, fontSize = 11.sp)
            }
        }
    }
}
