package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.ui.theme.*

@Composable
fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, appName: String, packageName: String, versionName: String, desc: String, category: String, publishStatus: String, env: String) -> Unit
) {
    var appName by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("com.company.") }
    var versionName by remember { mutableStateOf("1.0.0") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Mobile Application") }
    var publishStatus by remember { mutableStateOf("Development") }
    var environment by remember { mutableStateOf("Production") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ConsoleSurface,
        title = {
            Text("Register New Application", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = appName,
                    onValueChange = {
                        appName = it
                        if (packageName == "com.company." || packageName.startsWith("com.company.")) {
                            val clean = it.lowercase().filter { c -> c.isLetterOrDigit() }
                            packageName = "com.company.$clean"
                        }
                    },
                    label = { Text("App Name (e.g. Zentrix Esport)") },
                    modifier = Modifier.fillMaxWidth().testTag("proj_name_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name / Application ID (e.g. com.zentrix.esport)") },
                    modifier = Modifier.fillMaxWidth().testTag("proj_pkg_input"),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = versionName,
                    onValueChange = { versionName = it },
                    label = { Text("Initial Version (e.g. 1.0.0)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Functionality Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 2
                )

                Text("Application Category:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                val categories = listOf("Esports / Gaming", "Utility", "Games", "Social / Chat", "Media Streaming", "Artificial Intelligence")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.split(" ").first(), fontSize = 10.sp) }
                        )
                    }
                }

                Text("Environment Tier:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Production", "Staging", "Development").forEach { env ->
                        FilterChip(
                            selected = environment == env,
                            onClick = { environment = env },
                            label = { Text(env, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (appName.isNotBlank() && packageName.isNotBlank()) {
                        onCreate(appName, appName, packageName, versionName, description, category, publishStatus, environment)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZentrixCyan, contentColor = ConsoleBackground),
                enabled = appName.isNotBlank() && packageName.isNotBlank(),
                modifier = Modifier.testTag("submit_create_proj_btn")
            ) {
                Text("Register App", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun EditProjectDialog(
    project: ProjectEntity,
    onDismiss: () -> Unit,
    onSave: (ProjectEntity) -> Unit
) {
    var appName by remember { mutableStateOf(project.appName) }
    var packageName by remember { mutableStateOf(project.packageName) }
    var versionName by remember { mutableStateOf(project.versionName) }
    var description by remember { mutableStateOf(project.description) }
    var category by remember { mutableStateOf(project.category) }
    var publishStatus by remember { mutableStateOf(project.publishStatus) }
    var environment by remember { mutableStateOf(project.environment) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FirebaseSurface,
        title = {
            Text("Edit / Update Application", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = appName,
                    onValueChange = { appName = it },
                    label = { Text("App Name") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_app_name_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name / Application ID") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_pkg_name_input"),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = versionName,
                    onValueChange = { versionName = it },
                    label = { Text("Version Name (e.g. 1.2.0)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 2
                )

                Text("Environment Tier:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Production", "Staging", "Development").forEach { env ->
                        FilterChip(
                            selected = environment == env,
                            onClick = { environment = env },
                            label = { Text(env, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Publish Status:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Production Ready", "No Play Store", "Pending 2027", "Internal Testing").forEach { stat ->
                        FilterChip(
                            selected = publishStatus == stat,
                            onClick = { publishStatus = stat },
                            label = { Text(stat, fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (appName.isNotBlank() && packageName.isNotBlank()) {
                        val updated = project.copy(
                            name = appName,
                            appName = appName,
                            packageName = packageName,
                            versionName = versionName,
                            description = description,
                            category = category,
                            publishStatus = publishStatus,
                            environment = environment
                        )
                        onSave(updated)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue, contentColor = Color.White),
                enabled = appName.isNotBlank() && packageName.isNotBlank(),
                modifier = Modifier.testTag("submit_update_proj_btn")
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AddSecretDialog(
    activeProjectId: String,
    onDismiss: () -> Unit,
    onAdd: (keyName: String, secretValue: String, category: String, desc: String, isGlobal: Boolean) -> Unit
) {
    var keyName by remember { mutableStateOf("") }
    var secretValue by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Payment Gateway") }
    var description by remember { mutableStateOf("") }
    var isGlobal by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ConsoleSurface,
        title = {
            Text("Store New Vault Secret", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = keyName,
                    onValueChange = { keyName = it },
                    label = { Text("Key Identifier (e.g. IMGBB_API_KEY, BKASH_APP_KEY)") },
                    modifier = Modifier.fillMaxWidth().testTag("secret_name_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = secretValue,
                    onValueChange = { secretValue = it },
                    label = { Text("Secret Token / API Key / Credential") },
                    modifier = Modifier.fillMaxWidth().testTag("secret_val_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Category:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                val cats = listOf("Payment Gateway", "Image Upload (ImgBB)", "AI Service", "Webhook", "Custom")
                cats.forEach { c ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = category == c,
                            onClick = { category = c }
                        )
                        Text(c, color = TextPrimary, fontSize = 12.sp)
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Usage Description / Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isGlobal,
                        onCheckedChange = { isGlobal = it }
                    )
                    Text("Apply as Cluster Global Secret across all apps", color = TextSecondary, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (keyName.isNotBlank() && secretValue.isNotBlank()) {
                        onAdd(keyName, secretValue, category, description, isGlobal)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZentrixAmber, contentColor = ConsoleBackground),
                enabled = keyName.isNotBlank() && secretValue.isNotBlank(),
                modifier = Modifier.testTag("submit_add_secret_btn")
            ) {
                Text("Save to Vault", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AddUserDialog(
    activeProjectId: String,
    onDismiss: () -> Unit,
    onAdd: (email: String, phone: String, name: String, role: String, provider: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("USER") }
    var provider by remember { mutableStateOf("gmail") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ConsoleSurface,
        title = {
            Text("Create User Account", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name / Display Name") },
                    modifier = Modifier.fillMaxWidth().testTag("user_name_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address / Gmail Account") },
                    modifier = Modifier.fillMaxWidth().testTag("user_email_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Mobile Phone Number (e.g. +8801712345678)") },
                    modifier = Modifier.fillMaxWidth().testTag("user_phone_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Account Role:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("USER" to "Regular User", "STAFF" to "Staff / Mod", "ADMIN" to "Master Admin").forEach { (r, label) ->
                        FilterChip(
                            selected = role == r,
                            onClick = { role = r },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Auth Identity Provider:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                listOf("gmail" to "Google Account (Gmail)", "email_password" to "Email & Password", "phone_otp" to "Phone Number (SMS OTP)").forEach { (p, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = provider == p,
                            onClick = { provider = p }
                        )
                        Text(label, color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isNotBlank() || phoneNumber.isNotBlank()) {
                        val effectiveEmail = if (email.isNotBlank()) email else "$phoneNumber@zentrix.user"
                        val effectiveName = if (name.isNotBlank()) name else effectiveEmail.substringBefore("@")
                        onAdd(effectiveEmail, phoneNumber, effectiveName, role, provider)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZentrixPurple),
                enabled = email.isNotBlank() || phoneNumber.isNotBlank(),
                modifier = Modifier.testTag("submit_add_user_btn")
            ) {
                Text("Register User", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AddDocumentDialog(
    activeProjectId: String,
    initialCollection: String?,
    onDismiss: () -> Unit,
    onAdd: (collection: String, title: String, json: String) -> Unit
) {
    var collection by remember { mutableStateOf(initialCollection ?: "records") }
    var title by remember { mutableStateOf("") }
    var jsonBody by remember {
        mutableStateOf("""{
  "title": "Sample Document",
  "status": "ACTIVE",
  "timestamp": 1728219000
}""")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ConsoleSurface,
        title = {
            Text("Store Document in Database", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = collection,
                    onValueChange = { collection = it },
                    label = { Text("Collection or Table Name (e.g. tournaments, items)") },
                    modifier = Modifier.fillMaxWidth().testTag("doc_collection_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Document Title (e.g. Tournament Match #1)") },
                    modifier = Modifier.fillMaxWidth().testTag("doc_title_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Document Payload (JSON):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = jsonBody,
                    onValueChange = { jsonBody = it },
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (collection.isNotBlank() && title.isNotBlank()) {
                        onAdd(collection, title, jsonBody)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZentrixCyan, contentColor = ConsoleBackground),
                enabled = collection.isNotBlank() && title.isNotBlank(),
                modifier = Modifier.testTag("submit_add_doc_btn")
            ) {
                Text("Save Document", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
