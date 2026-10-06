package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, bnName: String, desc: String, category: String, publishStatus: String, env: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var bnName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Esports / Gaming") }
    var publishStatus by remember { mutableStateOf("Play Store Live") }
    var environment by remember { mutableStateOf("Production") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ConsoleSurface,
        title = {
            Text("নতুন প্রজেক্ট তৈরি করুন (Create Project)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                    label = { Text("প্রজেক্টের ইংরেজি নাম (যেমন: Zentrix Esport)") },
                    modifier = Modifier.fillMaxWidth().testTag("proj_name_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = bnName,
                    onValueChange = { bnName = it },
                    label = { Text("বাংলা টাইটেল (যেমন: ফ্রি ফায়ার টুর্নামেন্ট)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("সংক্ষিপ্ত বিবরণ (Description)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 2
                )

                Text("ক্যাটাগরি নির্বাচন:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

                Text("প্লে স্টোর পাবলিশ স্ট্যাটাস:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                listOf("Play Store Live", "No Play Store (Direct APK)", "Launch 2027").forEach { status ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = publishStatus == status,
                            onClick = { publishStatus = status }
                        )
                        Text(status, color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, if (bnName.isNotBlank()) bnName else name, description, category, publishStatus, environment)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZentrixCyan, contentColor = ConsoleBackground),
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("submit_create_proj_btn")
            ) {
                Text("তৈরি করুন (Create)", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = TextSecondary)
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
            Text("ভল্টে নতুন সিক্রেট কি যোগ করুন", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                    label = { Text("কি নাম (যেমন: BKASH_MERCHANT_KEY, IMGBB_API_KEY)") },
                    modifier = Modifier.fillMaxWidth().testTag("secret_name_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = secretValue,
                    onValueChange = { secretValue = it },
                    label = { Text("সিক্রেট ভ্যালু / টোকেন (Secret Token)") },
                    modifier = Modifier.fillMaxWidth().testTag("secret_val_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("ক্যাটাগরি:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                    label = { Text("নোট / ব্যবহার নির্দেশিকা (ঐচ্ছিক)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isGlobal,
                        onCheckedChange = { isGlobal = it }
                    )
                    Text("সব প্রজেক্টের জন্য গ্লোবাল কি হিসেবে ব্যবহার হবে", color = TextSecondary, fontSize = 11.sp)
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
                Text("ভল্টে সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AddUserDialog(
    activeProjectId: String,
    onDismiss: () -> Unit,
    onAdd: (email: String, name: String, role: String, provider: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("USER") }
    var provider by remember { mutableStateOf("gmail") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ConsoleSurface,
        title = {
            Text("নতুন ইউজার / অ্যাকাউন্ট যুক্ত করুন", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                    label = { Text("ব্যবহারকারীর পুরো নাম (Full Name)") },
                    modifier = Modifier.fillMaxWidth().testTag("user_name_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("ইমেইল অ্যাড্রেস / Gmail Account") },
                    modifier = Modifier.fillMaxWidth().testTag("user_email_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("অ্যাকাউন্ট রোল (Role):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("USER" to "প্লেয়ার/ইউজার", "STAFF" to "স্টাফ", "ADMIN" to "এডমিন").forEach { (r, label) ->
                        FilterChip(
                            selected = role == r,
                            onClick = { role = r },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                Text("লগইন প্রোভাইডার (Auth Provider):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                listOf("gmail" to "Google Gmail Account", "email_password" to "Email & Password", "phone_otp" to "Phone Number (OTP)").forEach { (p, label) ->
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
                    if (email.isNotBlank()) {
                        onAdd(email, if (name.isNotBlank()) name else email.substringBefore("@"), role, provider)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZentrixPurple),
                enabled = email.isNotBlank(),
                modifier = Modifier.testTag("submit_add_user_btn")
            ) {
                Text("ইউজার তৈরি করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = TextSecondary)
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
    var collection by remember { mutableStateOf(initialCollection ?: "tournaments") }
    var title by remember { mutableStateOf("") }
    var jsonBody by remember {
        mutableStateOf("""{
  "name": "Custom Item",
  "status": "ACTIVE",
  "createdAt": "2026-10-06"
}""")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ConsoleSurface,
        title = {
            Text("ডাটাবেসে নতুন ডকুমেন্ট তৈরি করুন", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                    label = { Text("কালেকশন বা টেবিল নাম (যেমন: tournaments)") },
                    modifier = Modifier.fillMaxWidth().testTag("doc_collection_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("ডকুমেন্ট টাইটেল (যেমন: Match #12 Finals)") },
                    modifier = Modifier.fillMaxWidth().testTag("doc_title_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("ডকুমেন্ট ডাটা (JSON ফরম্যাট):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

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
                Text("সেভ করুন (Save)", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = TextSecondary)
            }
        }
    )
}
