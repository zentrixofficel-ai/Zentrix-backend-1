package com.example.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
    onDeleteProject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner Header
        item {
            ConsoleCard(
                borderColor = ZentrixCyan.copy(alpha = 0.4f)
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
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = ZentrixCyan,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Zentrix Multi-Project Cluster",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "ফায়ারবেস ও সুপাবেসের মতো আপনার সকল নিজস্ব অ্যাপ্লিকেশনের সেন্ট্রাল ব্যাকএন্ড কন্ট্রোল।",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = ConsoleCardBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "মোট এক্টিভ প্রজেক্ট: ${projects.size} টি",
                        color = ZentrixCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedButton(
                        onClick = onCreateProjectClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ZentrixCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ZentrixCyan),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("নতুন প্রজেক্ট বানান", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(projects, key = { it.id }) { project ->
            val isSelected = project.id == activeProjectId

            ConsoleCard(
                borderColor = if (isSelected) ZentrixCyan else ConsoleCardBorder
            ) {
                // Top Row: Category + Publish Status badge + Environment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val statusColor = when {
                        project.publishStatus.contains("Live") -> ZentrixGreen
                        project.publishStatus.contains("Direct APK") -> ZentrixAmber
                        project.publishStatus.contains("2027") -> ZentrixPurple
                        else -> ZentrixBlue
                    }

                    StatusBadge(
                        text = project.category,
                        color = ZentrixCyan
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatusBadge(
                            text = project.publishStatus,
                            color = statusColor
                        )
                        StatusBadge(
                            text = project.environment,
                            color = if (project.environment == "Production") ZentrixGreen else ZentrixAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Project Title & Bengali Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = project.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) ZentrixCyan else TextPrimary
                        )
                        Text(
                            text = project.bnName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (isSelected) {
                        Surface(
                            color = ZentrixCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = ZentrixCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stats strip: Active Users & Daily API Hits
                Surface(
                    color = ConsoleSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "সক্রিয় ইউজার", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = "${project.activeUsersCount} জন",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        VerticalDivider(
                            modifier = Modifier.height(24.dp),
                            color = ConsoleCardBorder
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "আজকের রিকোয়েস্ট", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = "${project.apiRequestsToday} Hits",
                                color = ZentrixCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        VerticalDivider(
                            modifier = Modifier.height(24.dp),
                            color = ConsoleCardBorder
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "প্রজেক্ট ID", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = project.id,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onSelectProject(project.id)
                            onNavigateTab(ConsoleTab.SDK_CONNECT)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZentrixCyan,
                            contentColor = Color(0xFF041E28)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("connect_sdk_btn_${project.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Connect SDK", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            onSelectProject(project.id)
                            onNavigateTab(ConsoleTab.DATABASE)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ConsoleCardBorder),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "ডাটাবেস", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            onSelectProject(project.id)
                            onNavigateTab(ConsoleTab.VAULT)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ZentrixAmber),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ZentrixAmber.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "ভল্ট কি", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
