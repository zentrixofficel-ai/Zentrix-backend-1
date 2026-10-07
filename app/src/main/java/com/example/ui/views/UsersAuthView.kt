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
    var selectedAuthTab by remember { mutableStateOf("Users") } // "Users" or "Sign-in method" (from video 0:44-1:00)
    var searchQuery by remember { mutableStateOf("") }
    var userToDelete by remember { mutableStateOf<AuthUserEntity?>(null) }
    val selectedUids = remember { mutableStateListOf<String>() }

    val filteredUsers = remember(users, searchQuery) {
        val query = searchQuery.trim()
        if (query.isBlank()) users else {
            users.filter {
                it.email.contains(query, ignoreCase = true) ||
                it.phoneNumber.contains(query, ignoreCase = true) ||
                it.displayName.contains(query, ignoreCase = true) ||
                it.uid.contains(query, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FirebaseBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Firebase Title & Subtabs (Exact copy of video 0:45)
        item {
            Column {
                Text(
                    text = "Authentication",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Users", "Sign-in method", "Templates", "Usage", "Settings").forEach { tabName ->
                        val isSelected = selectedAuthTab == tabName
                        Surface(
                            color = if (isSelected) Color(0xFFE8F0FE) else Color.Transparent,
                            shape = RoundedCornerShape(20.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, ButtonBlue) else null,
                            modifier = Modifier.clickable { selectedAuthTab = tabName }
                        ) {
                            Text(
                                text = tabName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ButtonBlue else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // TAB 1: USERS
        if (selectedAuthTab == "Users" || selectedAuthTab != "Sign-in method") {
            // Search Bar & Blue "Add user" button (from video 0:46)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by email, phone, or UID", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FirebaseSurface,
                            unfocusedContainerColor = FirebaseSurface,
                            focusedBorderColor = ButtonBlue,
                            unfocusedBorderColor = FirebaseCardBorder
                        )
                    )

                    // Blue Button: Add user (as in video)
                    Button(
                        onClick = onAddUserClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        modifier = Modifier.testTag("add_user_button")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add user", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bulk Action Bar (Red Bulk Block, Green Unblock, Black Delete)
            if (users.isNotEmpty()) {
                item {
                    Surface(
                        color = FirebaseSurface,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FirebaseCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
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
                                    text = if (selectedUids.isEmpty()) "Select all users" else "${selectedUids.size} selected",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            if (selectedUids.isNotEmpty()) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // RED Button: Bulk Block
                                    Button(
                                        onClick = {
                                            onBulkBlock(selectedUids.toList())
                                            selectedUids.clear()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ButtonRed),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Block", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // GREEN Button: Unblock
                                    Button(
                                        onClick = {
                                            onBulkUnblock(selectedUids.toList())
                                            selectedUids.clear()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Unblock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // BLACK Button: Delete
                                    Button(
                                        onClick = {
                                            onBulkDelete(selectedUids.toList())
                                            selectedUids.clear()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBlack),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // User Table / Cards (as in video 0:46)
            if (filteredUsers.isEmpty()) {
                item {
                    ConsoleCard {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.PeopleOutline, contentDescription = null, tint = TextMuted, modifier = Modifier.size(44.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No users in this project", fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Click 'Add user' above to create user accounts.", color = TextMuted, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            items(filteredUsers, key = { it.uid }) { user ->
                var showRoleMenu by remember { mutableStateOf(false) }
                val isChecked = selectedUids.contains(user.uid)
                val isBlocked = user.status == "BLOCKED" || user.status == "BANNED"

                ConsoleCard(
                    borderColor = if (isBlocked) ButtonRed.copy(alpha = 0.5f) else FirebaseCardBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) selectedUids.add(user.uid) else selectedUids.remove(user.uid)
                            }
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = user.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )

                                if (isBlocked) {
                                    StatusBadge(text = "BLOCKED", color = ButtonRed)
                                } else {
                                    StatusBadge(text = "ACTIVE", color = ButtonGreen)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Email ID
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    copyToClipboard(context, "Email", user.email) {
                                        onShowToast("Email copied: ${user.email}")
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = ButtonBlue, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(user.email, fontSize = 12.sp, color = TextPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextMuted, modifier = Modifier.size(11.dp))
                            }

                            // Mobile Phone Number
                            if (user.phoneNumber.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        copyToClipboard(context, "Phone", user.phoneNumber) {
                                            onShowToast("Mobile copied: ${user.phoneNumber}")
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = ButtonGreen, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(user.phoneNumber, fontSize = 12.sp, color = ButtonGreen, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextMuted, modifier = Modifier.size(11.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "UID: ${user.uid}  •  Provider: ${user.provider.uppercase()}",
                                fontSize = 10.sp,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Role Selector Menu
                        Box {
                            Surface(
                                color = FirebaseSurfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FirebaseCardBorder),
                                modifier = Modifier.clickable { showRoleMenu = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(user.role, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }

                            DropdownMenu(
                                expanded = showRoleMenu,
                                onDismissRequest = { showRoleMenu = false }
                            ) {
                                listOf("ADMIN", "STAFF", "USER").forEach { role ->
                                    DropdownMenuItem(
                                        text = { Text(role) },
                                        onClick = {
                                            onUpdateRole(user.uid, role)
                                            showRoleMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = FirebaseCardBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Buttons: Block (RED) / Unblock (GREEN) and Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isBlocked) {
                            Button(
                                onClick = { onToggleBan(user) },
                                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen, contentColor = Color.White),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Unblock User", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { onToggleBan(user) },
                                colors = ButtonDefaults.buttonColors(containerColor = ButtonRed, contentColor = Color.White),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Block User", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        IconButton(
                            onClick = { userToDelete = user },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = ButtonRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // TAB 2: SIGN-IN METHOD (Exact copy of video 0:49 - 0:56)
        if (selectedAuthTab == "Sign-in method") {
            item {
                ConsoleCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sign-in providers",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        // Blue Button: Add new provider (like video 0:49)
                        Button(
                            onClick = { onShowToast("Configuring providers") },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add new provider", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = FirebaseCardBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Native providers list from video
                    val providers = listOf(
                        Triple("Email/Password", Icons.Default.Email, true),
                        Triple("Phone (SMS OTP)", Icons.Default.Phone, true),
                        Triple("Google", Icons.Default.AccountCircle, true),
                        Triple("Anonymous", Icons.Default.PersonOutline, true)
                    )

                    providers.forEach { (name, icon, isEnabled) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, contentDescription = null, tint = ButtonBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(text = if (isEnabled) "ENABLED" else "DISABLED", color = if (isEnabled) ButtonGreen else TextMuted)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.Edit, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                        HorizontalDivider(color = FirebaseCardBorder.copy(alpha = 0.5f))
                    }
                }
            }

            // SMS Multi-factor Authentication card from video (0:50)
            item {
                ConsoleCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F0FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = ButtonBlue, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text("SMS Multi-factor authentication", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                            Text("Allow users to add an extra layer of security to their account.", color = TextSecondary, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onShowToast("SMS MFA activated") },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Upgrade", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (userToDelete != null) {
        val u = userToDelete!!
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            containerColor = FirebaseSurface,
            title = { Text("Delete User Account?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Permanently remove '${u.displayName}' (${u.email})?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser(u)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) { Text("Cancel") }
            }
        )
    }
}
