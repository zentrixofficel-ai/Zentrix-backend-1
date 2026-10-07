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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.ui.ConsoleTab
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ProjectsView(
    projects: List<ProjectEntity>,
    activeProjectId: String,
    onSelectProject: (String) -> Unit,
    onNavigateTab: (ConsoleTab) -> Unit,
    onCreateProjectClick: () -> Unit,
    onEditProjectClick: (ProjectEntity) -> Unit,
    onDeleteProject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSettingsSubTab by remember { mutableStateOf("General") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FirebaseBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Firebase Project Settings Navigation Bar (Exactly like video 0:00 - 0:18)
        item {
            Column {
                Text(
                    text = "Project settings",
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
                    listOf("General", "Cloud Messaging", "Integration", "Service accounts", "Data privacy", "Users and permissions").forEach { tabName ->
                        val isSelected = selectedSettingsSubTab == tabName
                        Surface(
                            color = if (isSelected) Color(0xFFE8F0FE) else Color.Transparent,
                            shape = RoundedCornerShape(20.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, ButtonBlue) else null,
                            modifier = Modifier.clickable { selectedSettingsSubTab = tabName }
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

        // "Your project" Firebase Card (from video 0:00 - 0:18)
        item {
            ConsoleCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your project",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Black Action Button: View in Google Cloud
                    Button(
                        onClick = { /* External Cloud Link */ },
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBlack, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Cloud, contentDescription = null, modifier = Modifier.size(14.dp), tint = ButtonYellow)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View in Cloud", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = FirebaseCardBorder)
                Spacer(modifier = Modifier.height(12.dp))

                val activeProj = projects.find { it.id == activeProjectId } ?: projects.firstOrNull()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Project name", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = activeProj?.appName ?: "Zentrix ES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }

                    Column {
                        Text("Project ID", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = activeProj?.id ?: "zentrix-es",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = ButtonBlue
                        )
                    }

                    Column {
                        Text("Environment", color = TextMuted, fontSize = 11.sp)
                        StatusBadge(
                            text = activeProj?.environment ?: "Production",
                            color = ButtonGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Support email", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = "zentrixesport@gmail.com",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    // Yellow Action Button: Rotate Keys / Settings
                    Button(
                        onClick = { onNavigateTab(ConsoleTab.SDK_CONNECT) },
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonYellow, contentColor = Color(0xFF202124)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SDK Credentials", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // "Your apps" Section Header with BLUE Button: "Add app" (as in video)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your apps (${projects.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // Blue Button: Add App
                Button(
                    onClick = onCreateProjectClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_app_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add app", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Empty state
        if (projects.isEmpty()) {
            item {
                ConsoleCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = ButtonYellow, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Get started by adding Firebase to your app", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Create your first Android or Web application package.", color = TextMuted, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onCreateProjectClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Create Android App")
                        }
                    }
                }
            }
        }

        items(projects, key = { it.id }) { project ->
            val isSelected = project.id == activeProjectId

            ConsoleCard(
                borderColor = if (isSelected) ButtonBlue else FirebaseCardBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F0FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                tint = ButtonBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = project.appName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = project.packageName,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = ButtonBlue
                            )
                        }
                    }

                    // Green Active badge
                    StatusBadge(text = "LIVE", color = ButtonGreen)
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = FirebaseCardBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "App ID: 1:86138678855:android:${project.id.take(8)}",
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "Version: ${project.versionName}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // The 5 Colored Action Buttons: Yellow, Black, Red, Blue, Green
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // BLUE Button: Connect SDK
                    Button(
                        onClick = {
                            onSelectProject(project.id)
                            onNavigateTab(ConsoleTab.SDK_CONNECT)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SDK Config", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // GREEN Button: Database
                    Button(
                        onClick = {
                            onSelectProject(project.id)
                            onNavigateTab(ConsoleTab.DATABASE)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Database", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // YELLOW Button: Vault
                    Button(
                        onClick = {
                            onSelectProject(project.id)
                            onNavigateTab(ConsoleTab.VAULT)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonYellow, contentColor = Color(0xFF202124)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(0.9f)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Vault", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // BLACK Button: Edit / Update App
                    Button(
                        onClick = { onEditProjectClick(project) },
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBlack, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(0.85f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // RED Button: Delete
                    Button(
                        onClick = { onDeleteProject(project.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonRed, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(0.8f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}
