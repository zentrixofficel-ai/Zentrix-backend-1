package com.example.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.RuleSimulationResult
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun SecurityRulesView(
    project: ProjectEntity?,
    onSaveRules: (String) -> Unit,
    onSimulate: (role: String, operation: String, path: String) -> RuleSimulationResult,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (project == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Please select or create an application to manage security rules.", color = TextSecondary)
        }
        return
    }

    var rulesText by remember(project.id, project.securityRules) {
        mutableStateOf(project.securityRules)
    }

    // Simulator State
    var simRole by remember { mutableStateOf("STAFF") }
    var simOp by remember { mutableStateOf("WRITE") }
    var simPath by remember { mutableStateOf("/tournaments/match_01") }
    var simResult by remember { mutableStateOf<RuleSimulationResult?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner
        item {
            ConsoleCard(
                borderColor = ZentrixGreen.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(ZentrixGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = ZentrixGreen,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Cloud Security Rules v2.4",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Zero-Trust granular access control for ${project.appName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZentrixGreen,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Specify read and write policies per collection based on request authentication and user roles (Admin, Staff, or Client).",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        // Editable Code Editor Area
        item {
            ConsoleCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "security.rules • ${project.packageName}",
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = FontFamily.Monospace,
                        color = ZentrixCyan,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = {
                            onSaveRules(rulesText)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZentrixGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("deploy_rules_button")
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Deploy Rules", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = rulesText,
                    onValueChange = { rulesText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .testTag("security_rules_input"),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CodeBackground,
                        unfocusedContainerColor = CodeBackground,
                        focusedBorderColor = ZentrixCyan,
                        unfocusedBorderColor = ConsoleCardBorder
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Simulator
        item {
            ConsoleCard(
                borderColor = ZentrixPurple.copy(alpha = 0.4f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleFilled,
                        contentDescription = null,
                        tint = ZentrixViolet,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Security Rules Simulator (Pre-flight Sandbox)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Test read/write permissions for specific roles before deploying to live users.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Simulator Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("User Role", color = TextMuted, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            listOf("ADMIN", "STAFF", "USER").forEach { r ->
                                FilterChip(
                                    selected = simRole == r,
                                    onClick = { simRole = r },
                                    label = { Text(r, fontSize = 10.sp) },
                                    modifier = Modifier.padding(end = 4.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ZentrixCyan,
                                        selectedLabelColor = ConsoleBackground
                                    )
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(0.7f)) {
                        Text("Operation", color = TextMuted, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            listOf("READ", "WRITE").forEach { op ->
                                FilterChip(
                                    selected = simOp == op,
                                    onClick = { simOp = op },
                                    label = { Text(op, fontSize = 10.sp) },
                                    modifier = Modifier.padding(end = 4.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ZentrixAmber,
                                        selectedLabelColor = ConsoleBackground
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = simPath,
                    onValueChange = { simPath = it },
                    label = { Text("Target Document Path (e.g. /tournaments/1)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ConsoleSurfaceVariant,
                        unfocusedContainerColor = ConsoleSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        simResult = onSimulate(simRole, simOp, simPath)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixPurple),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Run Simulation Test", fontWeight = FontWeight.Bold)
                }

                if (simResult != null) {
                    val res = simResult!!
                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = if (res.allowed) ZentrixGreen.copy(alpha = 0.15f) else ZentrixRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (res.allowed) ZentrixGreen else ZentrixRed
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(
                                    text = if (res.allowed) "ACCESS GRANTED" else "ACCESS DENIED",
                                    color = if (res.allowed) ZentrixGreen else ZentrixRed
                                )
                                Text(
                                    text = "Role: $simRole • $simOp",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = res.matchedRule,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = if (res.allowed) ZentrixGreen else ZentrixRed,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = res.details,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
