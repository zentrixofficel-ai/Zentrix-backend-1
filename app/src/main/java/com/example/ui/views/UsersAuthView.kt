package com.example.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthUserEntity
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.*

@Composable
fun UsersAuthView(
    users: List<AuthUserEntity>,
    activeProjectId: String,
    onAddUserClick: () -> Unit,
    onUpdateRole: (String, String) -> Unit,
    onToggleBan: (AuthUserEntity) -> Unit,
    onBulkBlock: (List<String>) -> Unit,
    onBulkUnblock: (List<String>) -> Unit,
    onBulkDelete: (List<String>) -> Unit,
    onDeleteUser: (AuthUserEntity) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var userToDelete by remember { mutableStateOf<AuthUserEntity?>(null) }

    // Multi-selection set for Bulk Actions (Bolk)
    val selectedUids = remember { mutableStateListOf<String>() }

    // Filter and search
    val filteredUsers = remember(users, selectedFilter, searchQuery) {
        users.filter { user ->
            val matchesFilter = when (selectedFilter) {
                "ALL" -> true
                "ACTIVE" -> user.status == "ACTIVE"
                "BLOCKED" -> user.status == "BLOCKED" || user.status == "BANNED"
                "ADMIN" -> user.role == "ADMIN"
                "STAFF" -> user.role == "STAFF"
                "USER" -> user.role == "USER"
                else -> true
            }
            val query = searchQuery.trim()
            val matchesQuery = query.isBlank() ||
                user.email.contains(query, ignoreCase = true) ||
                user.phoneNumber.contains(query, ignoreCase = true) ||
                user.displayName.contains(query, ignoreCase = true) ||
                user.uid.contains(query, ignoreCase = true)

            matchesFilter && matchesQuery
        }
    }

    val adminCount = users.count { it.role == "ADMIN" }
    val staffCount = users.count { it.role == "STAFF" }
    val regularCount = users.count { it.role == "USER" }
    val blockedCount = users.count { it.status == "BLOCKED" || it.status == "BANNED" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Auth Header Card
        item {
            ConsoleCard(
                borderColor = ZentrixPurple.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(ZentrixPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupervisorAccount,
                            contentDescription = null,
                            tint = ZentrixPurple,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Authentication & User Directory",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Email ID, Mobile Phone, RBAC Permissions & Bulk Block",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZentrixViolet,
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
                            Text("Total", color = TextMuted, fontSize = 10.sp)
                            Text("${users.size}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Admins", color = ZentrixRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("$adminCount", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Staff", color = ZentrixAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("$staffCount", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Blocked", color = ZentrixRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("$blockedCount", color = ZentrixRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = ConsoleCardBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (activeProjectId.isNotBlank()) "App ID: $activeProjectId" else "Global Cluster Directory",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Button(
                        onClick = onAddUserClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ZentrixPurple),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_user_button")
                    ) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add User", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Search Bar for Mobile Number and Email ID
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by Mobile Number, Email ID, Name, UID...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ConsoleSurface,
                    unfocusedContainerColor = ConsoleSurface
                )
            )
        }

        // Role & Status Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "All Accounts (${users.size})",
                    "ACTIVE" to "Active (${users.size - blockedCount})",
                    "BLOCKED" to "Blocked ($blockedCount)",
                    "ADMIN" to "Admins ($adminCount)",
                    "STAFF" to "Staff ($staffCount)",
                    "USER" to "Regular Users ($regularCount)"
                ).forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (key == "BLOCKED") ZentrixRed else ZentrixPurple,
                            selectedLabelColor = Color.White,
                            containerColor = ConsoleSurfaceVariant,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        // Bulk Actions Bar (Bolk Block / Unblock / Delete)
        if (users.isNotEmpty()) {
            item {
                Surface(
                    color = ConsoleSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ConsoleCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedUids.size == filteredUsers.size && filteredUsers.isNotEmpty(),
                                onCheckedChange = { checkAll ->
                                    selectedUids.clear()
                                    if (checkAll) {
                                        selectedUids.addAll(filteredUsers.map { it.uid })
                                    }
                                }
                            )
                            Text(
                                text = if (selectedUids.isEmpty()) "Select for Bulk Action" else "${selectedUids.size} Selected",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        if (selectedUids.isNotEmpty()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        onBulkBlock(selectedUids.toList())
                                        selectedUids.clear()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixRed),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Bulk Block", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        onBulkUnblock(selectedUids.toList())
                                        selectedUids.clear()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixGreen),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Unblock", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = {
                                        onBulkDelete(selectedUids.toList())
                                        selectedUids.clear()
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Bulk Delete", tint = ZentrixRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (filteredUsers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No matching user accounts found.", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Tap 'Add User' to register your real accounts with email and mobile number.", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }

        items(filteredUsers, key = { it.uid }) { user ->
            var showRoleMenu by remember { mutableStateOf(false) }
            val isChecked = selectedUids.contains(user.uid)
            val isBlocked = user.status == "BLOCKED" || user.status == "BANNED"

            ConsoleCard(
                borderColor = when {
                    isBlocked -> ZentrixRed.copy(alpha = 0.6f)
                    user.role == "ADMIN" -> ZentrixRed.copy(alpha = 0.3f)
                    user.role == "STAFF" -> ZentrixAmber.copy(alpha = 0.3f)
                    else -> ConsoleCardBorder
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Checkbox for bulk actions
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checked ->
                            if (checked) selectedUids.add(user.uid) else selectedUids.remove(user.uid)
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    // Avatar Box with role initial
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isBlocked -> ZentrixRed.copy(alpha = 0.2f)
                                    user.role == "ADMIN" -> ZentrixRed.copy(alpha = 0.2f)
                                    user.role == "STAFF" -> ZentrixAmber.copy(alpha = 0.2f)
                                    else -> ZentrixCyan.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.displayName.take(1).uppercase(),
                            color = when {
                                isBlocked -> ZentrixRed
                                user.role == "ADMIN" -> ZentrixRed
                                user.role == "STAFF" -> ZentrixAmber
                                else -> ZentrixCyan
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = user.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (isBlocked) {
                                    StatusBadge(text = "BLOCKED", color = ZentrixRed)
                                } else {
                                    StatusBadge(text = "ACTIVE", color = ZentrixGreen)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Email ID Row with copy button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                copyToClipboard(context, "User Email", user.email) {
                                    onShowToast("Email copied: ${user.email}")
                                }
                            }
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = ZentrixCyan, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Email", tint = TextMuted, modifier = Modifier.size(11.dp))
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Mobile Number Row with copy button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                if (user.phoneNumber.isNotBlank()) {
                                    copyToClipboard(context, "User Phone", user.phoneNumber) {
                                        onShowToast("Mobile copied: ${user.phoneNumber}")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = ZentrixGreen, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (user.phoneNumber.isNotBlank()) user.phoneNumber else "No mobile added",
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (user.phoneNumber.isNotBlank()) ZentrixGreen else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            if (user.phoneNumber.isNotBlank()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Phone", tint = TextMuted, modifier = Modifier.size(11.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "UID: ${user.uid} • Provider: ${user.provider.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }

                    // Role Chip with Dropdown
                    Box {
                        Surface(
                            color = when (user.role) {
                                "ADMIN" -> ZentrixRed.copy(alpha = 0.15f)
                                "STAFF" -> ZentrixAmber.copy(alpha = 0.15f)
                                else -> ZentrixCyan.copy(alpha = 0.15f)
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when (user.role) {
                                    "ADMIN" -> ZentrixRed
                                    "STAFF" -> ZentrixAmber
                                    else -> ZentrixCyan
                                }
                            ),
                            onClick = { showRoleMenu = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = user.role,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (user.role) {
                                        "ADMIN" -> ZentrixRed
                                        "STAFF" -> ZentrixAmber
                                        else -> ZentrixCyan
                                    }
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Change role",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            listOf("ADMIN", "STAFF", "USER").forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r, fontWeight = if (user.role == r) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        onUpdateRole(user.uid, r)
                                        showRoleMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = ConsoleCardBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(6.dp))

                // Action Bar: Instant Block / Unblock & Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { onToggleBan(user) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isBlocked) ZentrixGreen.copy(alpha = 0.2f) else ZentrixRed.copy(alpha = 0.2f),
                            contentColor = if (isBlocked) ZentrixGreen else ZentrixRed
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isBlocked) Icons.Default.LockOpen else Icons.Default.Block,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isBlocked) "Unblock User" else "Block User",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { userToDelete = user },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete user",
                            tint = ZentrixRed.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    if (userToDelete != null) {
        val u = userToDelete!!
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            containerColor = ConsoleSurface,
            title = {
                Text("Delete User Account?", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Permanently remove account '${u.displayName}' (${u.email})? They will lose access to the application immediately.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser(u)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
