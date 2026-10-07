package com.example.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
    var selectedSetupMode by remember { mutableStateOf("npm") } // npm, CDN, Config (like video 0:15)
    var keyToRotateConfirm by remember { mutableStateOf<KeyType?>(null) }

    if (project == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(FirebaseBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Select an app to view Firebase SDK setup & configuration.", color = TextSecondary)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FirebaseBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Firebase Header
        item {
            ConsoleCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ButtonYellow.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = ButtonYellow,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SDK setup and configuration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "App: ${project.appName} • Package: ${project.packageName}",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall,
                            color = ButtonBlue,
                            fontSize = 11.sp
                        )
                    }

                    StatusBadge(text = "ONLINE", color = ButtonGreen)
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = FirebaseCardBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Radio selector for npm / CDN / Config / Android Kotlin / GitHub CI/CD (Exact copy of video 0:15)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("npm", "CDN", "Config", "Android Kotlin", "GitHub CI/CD").forEach { mode ->
                        val isSelected = selectedSetupMode == mode
                        Surface(
                            color = if (isSelected) Color(0xFFE8F0FE) else FirebaseSurfaceVariant,
                            shape = RoundedCornerShape(20.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, ButtonBlue) else null,
                            onClick = { selectedSetupMode = mode }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedSetupMode = mode },
                                    colors = RadioButtonDefaults.colors(selectedColor = ButtonBlue),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = mode,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ButtonBlue else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Firebase Credentials Cards with Yellow, Black, Red, Blue, Green buttons
        item {
            Text(
                text = "Credentials & Authorization Keys",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        // Admin Key Card
        item {
            ConsoleCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Admin Master Secret Key", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Full root access for vault, dropping database, and cloud tasks", color = TextSecondary, fontSize = 11.sp)
                    }
                    StatusBadge(text = "ADMIN", color = ButtonRed)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = FirebaseSurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FirebaseCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = project.adminKey,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = ButtonBlack,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                copyToClipboard(context, "Admin Key", project.adminKey) {
                                    onShowToast("Admin Key copied")
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = ButtonBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Yellow button: Rotate
                    Button(
                        onClick = { keyToRotateConfirm = KeyType.ADMIN },
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonYellow, contentColor = Color(0xFF202124)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rotate Key", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Staff Key Card
        item {
            ConsoleCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Staff / Operator Token", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Scoped for tournament refereeing, room creation, and user management", color = TextSecondary, fontSize = 11.sp)
                    }
                    StatusBadge(text = "STAFF", color = ButtonYellow)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = FirebaseSurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FirebaseCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = project.staffKey,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = ButtonBlack,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                copyToClipboard(context, "Staff Key", project.staffKey) {
                                    onShowToast("Staff Key copied")
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = ButtonBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Client Public Key
        item {
            ConsoleCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Web / Mobile Client API Key", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Safe for inclusion in public client applications", color = TextSecondary, fontSize = 11.sp)
                    }
                    StatusBadge(text = "CLIENT", color = ButtonGreen)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = FirebaseSurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FirebaseCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = project.clientPublicKey,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = ButtonBlack,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                copyToClipboard(context, "Client Key", project.clientPublicKey) {
                                    onShowToast("Client Key copied")
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = ButtonBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Code Snippet Box (Exactly like video 0:15)
        item {
            val codeSnippet = when (selectedSetupMode) {
                "CDN" -> """
<!-- Firebase SDK from CDN -->
<script type="module">
  import { initializeApp } from "https://www.gstatic.com/firebasejs/10.9.0/firebase-app.js";
  import { getAnalytics } from "https://www.gstatic.com/firebasejs/10.9.0/firebase-analytics.js";

  const firebaseConfig = {
    apiKey: "${project.clientPublicKey}",
    authDomain: "${project.id}.firebaseapp.com",
    projectId: "${project.id}",
    storageBucket: "${project.id}.appspot.com",
    messagingSenderId: "86138678855",
    appId: "1:86138678855:web:${project.id.take(8)}"
  };

  const app = initializeApp(firebaseConfig);
  const analytics = getAnalytics(app);
</script>
""".trimIndent()

                "Config" -> """
// Firebase Configuration Object
const firebaseConfig = {
  apiKey: "${project.clientPublicKey}",
  authDomain: "${project.id}.firebaseapp.com",
  projectId: "${project.id}",
  storageBucket: "${project.id}.appspot.com",
  messagingSenderId: "86138678855",
  appId: "1:86138678855:web:${project.id.take(8)}"
};
export default firebaseConfig;
""".trimIndent()

                "Android Kotlin" -> """
// Android Kotlin Client Setup (${project.packageName})
import io.zentrix.sdk.ZentrixClient

val firebase = ZentrixClient.Builder()
    .setPackageName("${project.packageName}")
    .setProjectId("${project.id}")
    .setApiKey("${project.clientPublicKey}")
    .setEndpoint("https://${project.id}.firebaseio.com")
    .build()
""".trimIndent()

                "GitHub CI/CD" -> """
# .github/workflows/android-build-deploy.yml
name: Zentrix CI/CD Android & Backend

on:
  push:
    branches: [ main, release ]
  pull_request:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build-android:
    name: Build & Test Android APK
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Source Code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Make Gradle Wrapper Executable
        run: chmod +x gradlew || true

      - name: Inject Project Secrets & Environment
        env:
          PROJECT_ID: "${project.id}"
          CLIENT_KEY: "${project.clientPublicKey}"
          ADMIN_KEY: "${project.adminKey}"
          PACKAGE_NAME: "${project.packageName}"
        run: |
          mkdir -p app/src/main/assets
          cat <<EOF > app/src/main/assets/zentrix-services.json
          {
            "project_info": {
              "project_id": "${project.id}",
              "project_number": "86138678855",
              "package_name": "${project.packageName}"
            },
            "client": [
              {
                "client_info": {
                  "mobilesdk_app_id": "1:86138678855:android:${project.id.take(8)}"
                },
                "api_key": [
                  { "current_key": "${project.clientPublicKey}" }
                ]
              }
            ]
          }
          EOF

      - name: Assemble Debug APK
        run: ./gradlew assembleDebug --stacktrace || gradle assembleDebug

      - name: Run Unit Tests
        run: ./gradlew testDebugUnitTest || gradle testDebugUnitTest

      - name: Archive APK Artifacts
        uses: actions/upload-artifact@v4
        with:
          name: ${project.appName.replace(" ", "_")}-release-apk
          path: app/build/outputs/apk/debug/*.apk

  deploy-rules:
    name: Sync Backend Security Rules
    needs: build-android
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Deploy Rules to Zentrix Cloud Cluster
        env:
          ZENTRIX_ADMIN_KEY: "${project.adminKey}"
        run: |
          echo "Deploying zero-trust rules for ${project.id}..."
          curl -X POST "https://api.zentrixcloud.io/v1/projects/${project.id}/rules" \
            -H "Authorization: Bearer ${project.adminKey}" \
            -H "Content-Type: application/json" \
            -d '{"status": "DEPLOYED", "env": "${project.environment}"}' || true
""".trimIndent()

                else -> """
// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "${project.clientPublicKey}",
  authDomain: "${project.id}.firebaseapp.com",
  projectId: "${project.id}",
  storageBucket: "${project.id}.appspot.com",
  messagingSenderId: "86138678855",
  appId: "1:86138678855:web:${project.id.take(8)}"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
""".trimIndent()
            }

            CodeBlockView(
                code = codeSnippet,
                title = "$selectedSetupMode • ${project.appName}",
                onCopy = {
                    copyToClipboard(context, "Firebase Code", codeSnippet) {
                        onShowToast("Firebase SDK code snippet copied!")
                    }
                }
            )
        }

        // Blue Button: Download google-services.json & Black Button: View Docs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onShowToast("google-services.json generated for ${project.packageName}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download config", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        onShowToast("Opening Firebase docs")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBlack, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Documentation", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }

    if (keyToRotateConfirm != null) {
        val keyType = keyToRotateConfirm!!
        AlertDialog(
            onDismissRequest = { keyToRotateConfirm = null },
            containerColor = FirebaseSurface,
            title = { Text("Rotate ${keyType.name} Key?", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Are you sure you want to generate a new key? The old key will immediately stop working.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        onRotateKey(project.id, keyType)
                        keyToRotateConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonRed)
                ) {
                    Text("Rotate Key")
                }
            },
            dismissButton = {
                TextButton(onClick = { keyToRotateConfirm = null }) { Text("Cancel") }
            }
        )
    }
}
