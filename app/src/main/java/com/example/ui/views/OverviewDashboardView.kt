package com.example.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun OverviewDashboardView(
    activeProject: ProjectEntity?,
    totalProjectsCount: Int,
    totalUsersCount: Int,
    totalDocsCount: Int,
    totalSecretsCount: Int,
    onNavigateTab: (ConsoleTab) -> Unit,
    onCreateProjectClick: () -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ZentrixBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Platform Hero Card
        item {
            ConsoleCard(borderColor = ZentrixCyan.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ZENTRIX CLUSTER DASHBOARD",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Central BaaS platform for Android apps, websites & games",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    StatusBadge(text = "CLUSTER OPERATIONAL", color = ZentrixGreen)
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = ZentrixCardBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Hardware telemetry row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TelemetryChip(label = "CPU Load", value = "14.2%", color = ZentrixCyan)
                    TelemetryChip(label = "RAM Heap", value = "38.6%", color = ZentrixViolet)
                    TelemetryChip(label = "Disk Pool", value = "24.1%", color = ZentrixGreen)
                    TelemetryChip(label = "Uptime", value = "99.98%", color = ZentrixAmber)
                }
            }
        }

        // Section: Cluster Vital Statistics (Requirement 1)
        item {
            Text(
                text = "Key Infrastructure Metrics",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricMetricCard(
                        title = "Projects",
                        value = "$totalProjectsCount",
                        subtext = "Isolated Tenants",
                        icon = Icons.Default.Layers,
                        color = ZentrixCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMetricCard(
                        title = "Active Users",
                        value = "$totalUsersCount",
                        subtext = "Accounts & Sessions",
                        icon = Icons.Default.PeopleAlt,
                        color = ZentrixViolet,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricMetricCard(
                        title = "Database Docs",
                        value = "$totalDocsCount",
                        subtext = "Collections & Tables",
                        icon = Icons.Default.Storage,
                        color = ZentrixGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMetricCard(
                        title = "Secret Vault",
                        value = "$totalSecretsCount",
                        subtext = "Encrypted Keys",
                        icon = Icons.Default.VpnKey,
                        color = ZentrixAmber,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricMetricCard(
                        title = "Realtime WSS",
                        value = "18",
                        subtext = "Active Listeners",
                        icon = Icons.Default.WifiTethering,
                        color = ZentrixCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMetricCard(
                        title = "API Traffic",
                        value = "1.4k",
                        subtext = "Requests Today",
                        icon = Icons.Default.Speed,
                        color = ZentrixGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Actions Row (Requirement 1)
        item {
            Text(
                text = "Quick Control Actions",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCreateProjectClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixCyan, contentColor = Color(0xFF090D16)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                    modifier = Modifier.weight(1f).testTag("quick_new_project")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New App", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onNavigateTab(ConsoleTab.AUTH) },
                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixViolet, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                    modifier = Modifier.weight(1f).testTag("quick_add_user")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add User", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onNavigateTab(ConsoleTab.DATABASE) },
                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixGreen, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                    modifier = Modifier.weight(1f).testTag("quick_open_db")
                ) {
                    Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Database", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active Project Focus Card
        item {
            ConsoleCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Active Target Application",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = activeProject?.appName ?: "No App Selected",
                            color = ZentrixCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { onNavigateTab(ConsoleTab.SDK_DOCS) },
                        colors = ButtonDefaults.buttonColors(containerColor = ZentrixSurfaceVariant, contentColor = ZentrixCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ZentrixCyan.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Connect SDK", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = ZentrixCardBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Package: ${activeProject?.packageName ?: "N/A"}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Env: ${activeProject?.environment ?: "Production"}",
                        fontSize = 11.sp,
                        color = ZentrixGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun TelemetryChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 10.sp)
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun MetricMetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    ConsoleCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(text = title, color = TextSecondary, fontSize = 11.sp)
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    fontSize = 18.sp
                )
                Text(text = subtext, color = TextMuted, fontSize = 10.sp)
            }
        }
    }
}
