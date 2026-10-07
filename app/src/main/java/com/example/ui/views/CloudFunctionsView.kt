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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

data class ZentrixFunctionItem(
    val name: String,
    val trigger: String,
    val runtime: String,
    val executions: Int,
    val avgLatency: String,
    val status: String = "ACTIVE"
)

data class ZentrixCronJob(
    val name: String,
    val cron: String,
    val target: String,
    val nextRun: String,
    val status: String = "SCHEDULED"
)

data class ZentrixWebhookItem(
    val url: String,
    val eventType: String,
    val secretKey: String,
    val status: String = "ENABLED"
)

@Composable
fun CloudFunctionsView(
    activeProjectId: String,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableStateOf("Functions") }

    val functions = remember {
        mutableStateListOf(
            ZentrixFunctionItem("onUserRegistered", "Auth Trigger", "Python 3.11", 148, "42ms"),
            ZentrixFunctionItem("generateTournamentBracket", "HTTP POST", "Python 3.11", 620, "65ms"),
            ZentrixFunctionItem("onScoreSubmitted", "Database Write", "Python 3.11", 2840, "38ms"),
            ZentrixFunctionItem("cleanupExpiredTokens", "Cron Schedule", "Python 3.11", 96, "88ms")
        )
    }

    val cronJobs = remember {
        mutableStateListOf(
            ZentrixCronJob("Daily Score Reset", "0 0 * * *", "functions/reset_scores", "Tomorrow 00:00 UTC"),
            ZentrixCronJob("Token Expiry Sweep", "*/15 * * * *", "functions/cleanupExpiredTokens", "In 12 minutes"),
            ZentrixCronJob("Tournament Room Health", "*/5 * * * *", "functions/check_room_heartbeat", "In 3 minutes")
        )
    }

    val webhooks = remember {
        mutableStateListOf(
            ZentrixWebhookItem("https://api.mygame.io/webhooks/payment", "payment.completed", "whsec_live_9942a"),
            ZentrixWebhookItem("https://discord.com/api/webhooks/tournament-bot", "database.created", "whsec_live_bb71c"),
            ZentrixWebhookItem("https://admin.zentrix.io/hooks/auth-alert", "user.deleted", "whsec_live_dd10e")
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ZentrixBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Functions Header
        item {
            ConsoleCard(borderColor = ZentrixViolet.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ZentrixViolet.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = ZentrixViolet, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Cloud Functions & Automation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Serverless Python triggers, cron schedulers & outbound webhooks",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    StatusBadge(text = "PYTHON 3.11", color = ZentrixCyan)
                }
            }
        }

        // Subtabs: Functions | Scheduler | Webhooks
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Functions", "Cron Scheduler", "Webhooks").forEach { tab ->
                    val isSelected = selectedSubTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubTab = tab },
                        label = { Text(tab, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ZentrixViolet.copy(alpha = 0.25f),
                            selectedLabelColor = ZentrixViolet
                        )
                    )
                }
            }
        }

        if (selectedSubTab == "Functions") {
            items(functions) { fn ->
                ConsoleCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = fn.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            Text(
                                text = "Trigger: ${fn.trigger} • Runtime: ${fn.runtime} • Latency: ${fn.avgLatency}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Executions today: ${fn.executions}",
                                color = ZentrixCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Button(
                            onClick = { onShowToast("Executed test trigger for ${fn.name} (latency: ${fn.avgLatency})") },
                            colors = ButtonDefaults.buttonColors(containerColor = ZentrixSurfaceVariant, contentColor = ZentrixCyan),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ZentrixCyan.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Invoke", fontSize = 11.sp)
                        }
                    }
                }
            }
        } else if (selectedSubTab == "Cron Scheduler") {
            items(cronJobs) { job ->
                ConsoleCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = job.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            Text(
                                text = "Cron: ${job.cron} • Target: ${job.target}",
                                fontFamily = FontFamily.Monospace,
                                color = ZentrixAmber,
                                fontSize = 11.sp
                            )
                            Text(text = "Next execution: ${job.nextRun}", color = TextMuted, fontSize = 11.sp)
                        }

                        StatusBadge(text = job.status, color = ZentrixGreen)
                    }
                }
            }
        } else {
            items(webhooks) { wh ->
                ConsoleCard {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Event: ${wh.eventType}", fontWeight = FontWeight.Bold, color = ZentrixCyan, fontSize = 13.sp)
                            StatusBadge(text = wh.status, color = ZentrixGreen)
                        }
                        Text(
                            text = "URL: ${wh.url}",
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Signing Secret: ${wh.secretKey.take(10)}••••••••",
                            fontFamily = FontFamily.Monospace,
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
