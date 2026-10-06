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
import androidx.compose.ui.platform.testTag
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
    var selectedLanguage by remember { mutableStateOf("JavaScript") }
    var keyToRotateConfirm by remember { mutableStateOf<KeyType?>(null) }

    if (project == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("অনুগ্রহ করে একটি প্রজেক্ট নির্বাচন করুন", color = TextSecondary)
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
                            text = "Zentrix SDK & Staff Keys",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Firebase এর মতো আপনার অ্যাপসে `const zentrix = ...` দিয়ে কানেক্ট করুন।",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZentrixCyan,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "প্রজেক্ট বানানো সম্পন্ন হলে Admin, Staff ও Client অ্যাপের জন্য আলাদা সিক্রেট কি দিয়ে ক্লাউড ডাটাবেস ও অথ সার্ভিস এক্সেস করা যায়।",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        // Section: The 3 Tier Security Keys
        item {
            Text(
                text = "১. সিকিউরিটি কি ম্যানেজার (Security Credentials)",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        // 1. Admin Master Key
        item {
            KeyCard(
                title = "Admin Master Secret Key",
                bnTitle = "এডমিন মাস্টার কি (সম্পূর্ণ ফুল এক্সেস)",
                keyString = project.adminKey,
                badgeText = "SUPER_ADMIN",
                badgeColor = ZentrixRed,
                scopeDescription = "সম্পূর্ণ রুট এক্সেস: সিক্রেট ভল্ট, পেমেন্ট গেটওয়ে, ইউজার ডিলিট এবং ডাটাবেস কন্ট্রোল।",
                onCopy = {
                    copyToClipboard(context, "Zentrix Admin Key", project.adminKey) {
                        onShowToast("Admin Key কপি হয়েছে!")
                    }
                },
                onRotate = { keyToRotateConfirm = KeyType.ADMIN }
            )
        }

        // 2. Staff Key
        item {
            KeyCard(
                title = "Staff & Moderator Key",
                bnTitle = "স্টাফ ও মডারেটর কি (টুর্নামেন্ট/সাপোর্ট রেফারী)",
                keyString = project.staffKey,
                badgeText = "STAFF_AUTHORIZED",
                badgeColor = ZentrixAmber,
                scopeDescription = "স্টাফ পারমিশন: টুর্নামেন্ট স্লট তৈরি, ম্যাচ রুম ও পাসওয়ার্ড দেয়া, রেজাল্ট আপলোড। ভল্ট এক্সেস ব্লকড।",
                onCopy = {
                    copyToClipboard(context, "Zentrix Staff Key", project.staffKey) {
                        onShowToast("Staff Key কপি হয়েছে!")
                    }
                },
                onRotate = { keyToRotateConfirm = KeyType.STAFF }
            )
        }

        // 3. Client Public Key
        item {
            KeyCard(
                title = "Client App Public Key",
                bnTitle = "মোবাইল ও ওয়েব অ্যাপের ক্লায়েন্ট কি",
                keyString = project.clientPublicKey,
                badgeText = "PUBLIC_CLIENT",
                badgeColor = ZentrixGreen,
                scopeDescription = "এপিকে (APK) ও ব্রাউজারে সুরক্ষিত। ইউজার লগইন, পাবলিক টুর্নামেন্ট ভিউ ও স্কিমা চেক।",
                onCopy = {
                    copyToClipboard(context, "Zentrix Client Key", project.clientPublicKey) {
                        onShowToast("Client Public Key কপি হয়েছে!")
                    }
                },
                onRotate = { keyToRotateConfirm = KeyType.CLIENT }
            )
        }

        // Section: Code Integration Snippets
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "২. কোড কানেকশন স্নাইপেট (`const zentrix = ...`)",
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
                listOf("JavaScript", "Kotlin (Android)", "Flutter (Dart)", "REST (cURL)").forEach { lang ->
                    val isSelected = lang == selectedLanguage
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedLanguage = lang },
                        label = { Text(lang, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ZentrixCyan,
                            selectedLabelColor = Color(0xFF041E28),
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
                "JavaScript" -> """
// Firebase const firebaseConfig এর মতো Zentrix ক্লাউড ইনিশিয়ালাইজেশন
import { initializeZentrix } from '@zentrix/cloud-sdk';

const zentrix = initializeZentrix({
  projectId: "${project.id}",
  apiKey: "${project.clientPublicKey}",
  staffKey: "${project.staffKey}", // Staff / Referee প্যানেল কানেকশনে ব্যবহৃত
  endpoint: "https://api.zentrixcloud.io/v1",
  environment: "${project.environment.lowercase()}",
  auth: {
    providers: ["gmail", "email_password", "phone_otp"]
  }
});

// টুর্নামেন্ট ডাটা রিড করা:
const snapshot = await zentrix.db.collection('tournaments').get();
console.log("Connected to Zentrix Backend Cluster:", zentrix.status);
""".trimIndent()

                "Kotlin (Android)" -> """
// Android Kotlin Client Initialization (app/src/main/...)
import io.zentrix.sdk.ZentrixClient

val zentrix = ZentrixClient.Builder()
    .setProjectId("${project.id}")
    .setPublicKey("${project.clientPublicKey}")
    .setStaffToken("${project.staffKey}") // স্টাফ ও এডমিন অপারেশনের জন্য
    .setEndpoint("https://api.zentrixcloud.io/v1")
    .enableOfflinePersistence(true)
    .build()

// লাইভ ডাটা বা টুর্নামেন্ট লিসেন করা:
zentrix.database.collection("tournaments")
    .addSnapshotListener { docs, error ->
        // Real-time update
    }
""".trimIndent()

                "Flutter (Dart)" -> """
// Flutter / Dart Integration
import 'package:zentrix_flutter/zentrix_flutter.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  final zentrix = await Zentrix.initialize(
    projectId: '${project.id}',
    apiKey: '${project.clientPublicKey}',
    staffToken: '${project.staffKey}',
    options: ZentrixOptions(
      region: 'ap-south-1',
      enableSecurityRules: true,
    ),
  );
  
  runApp(MyApp());
}
""".trimIndent()

                else -> """
# cURL REST API Example
curl -X GET "https://api.zentrixcloud.io/v1/projects/${project.id}/data/tournaments" \
  -H "Authorization: Bearer ${project.staffKey}" \
  -H "X-Zentrix-Client-Key: ${project.clientPublicKey}" \
  -H "Content-Type: application/json"
""".trimIndent()
            }

            CodeBlockView(
                code = codeSnippet,
                title = "$selectedLanguage • ${project.name}",
                onCopy = {
                    copyToClipboard(context, "Zentrix SDK Snippet", codeSnippet) {
                        onShowToast("$selectedLanguage কোড ক্লিপবোর্ডে কপি হয়েছে!")
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
                    text = "${keyType.name} Key পরিবর্তন করতে চান?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "নতুন কি তৈরি করলে পুরোনো কি দিয়ে চলমান অ্যাপের রিকোয়েস্ট বন্ধ হয়ে যাবে। আপনি কি নিশ্চিত?",
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
                    Text("হ্যাঁ, নতুন কি জেনারেট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { keyToRotateConfirm = null }) {
                    Text("বাতিল", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun KeyCard(
    title: String,
    bnTitle: String,
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
                    text = bnTitle,
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
                Text("রিনিউ কি (Rotate)", color = ZentrixAmber, fontSize = 11.sp)
            }
        }
    }
}
