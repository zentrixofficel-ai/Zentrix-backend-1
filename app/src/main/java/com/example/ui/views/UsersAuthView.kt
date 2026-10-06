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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthUserEntity
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun UsersAuthView(
    users: List<AuthUserEntity>,
    activeProjectId: String,
    onAddUserClick: () -> Unit,
    onUpdateRole: (String, String) -> Unit,
    onToggleBan: (AuthUserEntity) -> Unit,
    onDeleteUser: (AuthUserEntity) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRoleFilter by remember { mutableStateOf("ALL") }
    var userToDelete by remember { mutableStateOf<AuthUserEntity?>(null) }

    val filteredUsers = remember(users, selectedRoleFilter) {
        if (selectedRoleFilter == "ALL") users else users.filter { it.role == selectedRoleFilter }
    }

    val adminCount = users.count { it.role == "ADMIN" }
    val staffCount = users.count { it.role == "STAFF" }
    val regularCount = users.count { it.role == "USER" }

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
                            text = "Authentication & User Management",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Gmail, Email/Password লগইন এবং এডমিন-স্টাফ পারমিশন",
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
                            Text("মোট ইউজার", color = TextMuted, fontSize = 10.sp)
                            Text("${users.size}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("এডমিন", color = ZentrixRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("$adminCount", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("স্টাফ রেফারী", color = ZentrixAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("$staffCount", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("প্লেয়ার/ইউজার", color = ZentrixCyan, fontSize = 10.sp)
                            Text("$regularCount", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                        text = "প্রজেক্ট: $activeProjectId",
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
                        Text("নতুন ইউজার যোগ করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Role Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL" to "সকল", "ADMIN" to "এডমিন", "STAFF" to "স্টাফ", "USER" to "প্লেয়ার/ইউজার").forEach { (roleKey, roleLabel) ->
                    val isSelected = selectedRoleFilter == roleKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedRoleFilter = roleKey },
                        label = { Text(roleLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ZentrixPurple,
                            selectedLabelColor = Color.White,
                            containerColor = ConsoleSurfaceVariant,
                            labelColor = TextSecondary
                        )
                    )
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
                    Text("এই ক্যাটাগরিতে কোনো ইউজার নেই", color = TextMuted, fontSize = 12.sp)
                }
            }
        }

        items(filteredUsers, key = { it.uid }) { user ->
            var showRoleMenu by remember { mutableStateOf(false) }

            ConsoleCard(
                borderColor = when (user.role) {
                    "ADMIN" -> ZentrixRed.copy(alpha = 0.4f)
                    "STAFF" -> ZentrixAmber.copy(alpha = 0.4f)
                    else -> ConsoleCardBorder
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar Box with role initial
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                when (user.role) {
                                    "ADMIN" -> ZentrixRed.copy(alpha = 0.2f)
                                    "STAFF" -> ZentrixAmber.copy(alpha = 0.2f)
                                    else -> ZentrixCyan.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.displayName.take(1).uppercase(),
                            color = when (user.role) {
                                "ADMIN" -> ZentrixRed
                                "STAFF" -> ZentrixAmber
                                else -> ZentrixCyan
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (user.status == "BANNED") {
                                StatusBadge(text = "BANNED", color = ZentrixRed)
                            }
                        }
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "UID: ${user.uid} • ${if (user.provider == "gmail") "Google Gmail" else "Email/Password"}",
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

                // Action Bar: Ban/Unban & Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onToggleBan(user) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (user.status == "BANNED") Icons.Default.LockOpen else Icons.Default.Block,
                            contentDescription = null,
                            tint = if (user.status == "BANNED") ZentrixGreen else ZentrixAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (user.status == "BANNED") "আনব্যান করুন (Unban)" else "ব্যান করুন (Ban)",
                            color = if (user.status == "BANNED") ZentrixGreen else ZentrixAmber,
                            fontSize = 11.sp
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
                Text("ইউজার ডিলিট করবেন?", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "${u.displayName} (${u.email}) এর অ্যাকাউন্ট মুছে ফেললে তারা ক্লাউডে লগইন করতে পারবে না।",
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
                    Text("মুছে ফেলুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("বাতিল", color = TextSecondary)
                }
            }
        )
    }
}
