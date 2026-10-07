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
import com.example.data.model.*
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.*

@Composable
fun DataExplorerView(
    projects: List<ProjectEntity>,
    secrets: List<VaultSecretEntity>,
    users: List<AuthUserEntity>,
    documents: List<DataDocumentEntity>,
    logs: List<AuditLogEntity>,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val totalSavedRecords = projects.size + secrets.size + users.size + documents.size + logs.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner
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
                            imageVector = Icons.Default.ManageSearch,
                            contentDescription = null,
                            tint = ZentrixCyan,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Central Data Explorer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Inspector for all saved projects, secrets, users, and documents",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZentrixCyan,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats row
                Surface(
                    color = ConsoleSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Records", color = TextMuted, fontSize = 10.sp)
                            Text("$totalSavedRecords", color = ZentrixCyan, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Apps", color = TextMuted, fontSize = 10.sp)
                            Text("${projects.size}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Vault Keys", color = TextMuted, fontSize = 10.sp)
                            Text("${secrets.size}", color = ZentrixAmber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Users", color = TextMuted, fontSize = 10.sp)
                            Text("${users.size}", color = ZentrixPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DB Docs", color = TextMuted, fontSize = 10.sp)
                            Text("${documents.size}", color = ZentrixGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Search Bar & Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search any saved key, package name, user, or JSON value...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ConsoleSurface,
                        unfocusedContainerColor = ConsoleSurface
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ALL" to "All Saved", "PROJECT" to "Apps", "SECRET" to "Vault", "USER" to "Users", "DOC" to "Documents").forEach { (key, label) ->
                        val isSelected = selectedFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = key },
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
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
        }

        if (totalSavedRecords == 0) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inbox, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No saved records found in cluster.", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("Zero demo data active. Create an application or store data to inspect it here.", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section: Saved Projects
        if (selectedFilter == "ALL" || selectedFilter == "PROJECT") {
            val filteredProjects = projects.filter {
                searchQuery.isBlank() || it.appName.contains(searchQuery, true) || it.packageName.contains(searchQuery, true)
            }
            if (filteredProjects.isNotEmpty()) {
                item {
                    Text("Saved Applications (${filteredProjects.size})", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                items(filteredProjects, key = { "proj_${it.id}" }) { p ->
                    ConsoleCard(borderColor = ZentrixCyan.copy(alpha = 0.3f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.appName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(p.packageName, fontFamily = FontFamily.Monospace, color = ZentrixCyan, fontSize = 12.sp)
                                Text("ID: ${p.id} • Version: ${p.versionName} • Env: ${p.environment}", color = TextMuted, fontSize = 10.sp)
                            }
                            StatusBadge(text = "APP", color = ZentrixCyan)
                        }
                    }
                }
            }
        }

        // Section: Saved Secrets
        if (selectedFilter == "ALL" || selectedFilter == "SECRET") {
            val filteredSecrets = secrets.filter {
                searchQuery.isBlank() || it.keyName.contains(searchQuery, true) || it.category.contains(searchQuery, true)
            }
            if (filteredSecrets.isNotEmpty()) {
                item {
                    Text("Saved Vault Secrets (${filteredSecrets.size})", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                items(filteredSecrets, key = { "sec_${it.id}" }) { s ->
                    ConsoleCard(borderColor = ZentrixAmber.copy(alpha = 0.3f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(s.keyName, fontFamily = FontFamily.Monospace, color = ZentrixAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Project: ${s.projectId} • Category: ${s.category}", color = TextMuted, fontSize = 11.sp)
                            }
                            StatusBadge(text = "SECRET", color = ZentrixAmber)
                        }
                    }
                }
            }
        }

        // Section: Saved Users
        if (selectedFilter == "ALL" || selectedFilter == "USER") {
            val filteredUsers = users.filter {
                searchQuery.isBlank() ||
                    it.email.contains(searchQuery, true) ||
                    it.phoneNumber.contains(searchQuery, true) ||
                    it.displayName.contains(searchQuery, true)
            }
            if (filteredUsers.isNotEmpty()) {
                item {
                    Text("Registered User Accounts (${filteredUsers.size})", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                items(filteredUsers, key = { "usr_${it.uid}" }) { u ->
                    val isBlocked = u.status == "BLOCKED" || u.status == "BANNED"
                    ConsoleCard(borderColor = if (isBlocked) ZentrixRed.copy(alpha = 0.5f) else ZentrixPurple.copy(alpha = 0.3f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(u.displayName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Email: ${u.email}", color = TextSecondary, fontSize = 12.sp)
                                if (u.phoneNumber.isNotBlank()) {
                                    Text("Mobile: ${u.phoneNumber}", fontFamily = FontFamily.Monospace, color = ZentrixGreen, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                                Text("UID: ${u.uid} • Role: ${u.role} • Status: ${u.status}", color = TextMuted, fontSize = 10.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (isBlocked) {
                                    StatusBadge(text = "BLOCKED", color = ZentrixRed)
                                }
                                StatusBadge(text = u.role, color = ZentrixPurple)
                            }
                        }
                    }
                }
            }
        }

        // Section: Saved Database Documents
        if (selectedFilter == "ALL" || selectedFilter == "DOC") {
            val filteredDocs = documents.filter {
                searchQuery.isBlank() || it.title.contains(searchQuery, true) || it.collectionName.contains(searchQuery, true) || it.dataJson.contains(searchQuery, true)
            }
            if (filteredDocs.isNotEmpty()) {
                item {
                    Text("Database Documents & Records (${filteredDocs.size})", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                items(filteredDocs, key = { "doc_${it.id}" }) { d ->
                    ConsoleCard(borderColor = ZentrixGreen.copy(alpha = 0.3f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(d.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Collection: ${d.collectionName} • ID: ${d.id}", fontFamily = FontFamily.Monospace, color = ZentrixGreen, fontSize = 11.sp)
                            }
                            StatusBadge(text = "DOC", color = ZentrixGreen)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = CodeBackground,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = d.dataJson,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = TextPrimary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
