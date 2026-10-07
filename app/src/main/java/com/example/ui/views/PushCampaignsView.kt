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
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

data class PushNotificationCampaign(
    val id: String,
    val title: String,
    val body: String,
    val topic: String,
    val deliveredCount: Int,
    val sentAt: String,
    val status: String = "DELIVERED"
)

@Composable
fun PushCampaignsView(
    activeProjectId: String,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var notifTitle by remember { mutableStateOf("") }
    var notifBody by remember { mutableStateOf("") }
    var targetTopic by remember { mutableStateOf("all_users") }

    val campaigns = remember {
        mutableStateListOf(
            PushNotificationCampaign("p1", "Tournament Match #4 Starting!", "Room key is now live in Esports section.", "esports_players", 1420, "2 hours ago"),
            PushNotificationCampaign("p2", "Zentrix Cloud Update v2.5", "Faster database query performance enabled.", "all_users", 8940, "Yesterday"),
            PushNotificationCampaign("p3", "Weekend Double XP Event", "Log in to earn double tournament points.", "gamers", 5310, "3 days ago")
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
        // Push Header
        item {
            ConsoleCard(borderColor = ZentrixAmber.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ZentrixAmber.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = ZentrixAmber, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Zentrix Push Notifications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "FCM & WebSocket push dispatcher for Android APK & Web clients",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    StatusBadge(text = "DISPATCHER ON", color = ZentrixGreen)
                }
            }
        }

        // Send Push Composer Card
        item {
            ConsoleCard {
                Text(
                    text = "Compose New Campaign",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notifTitle,
                    onValueChange = { notifTitle = it },
                    label = { Text("Notification Title") },
                    modifier = Modifier.fillMaxWidth().testTag("push_title_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notifBody,
                    onValueChange = { notifBody = it },
                    label = { Text("Message Body / Deep Link Text") },
                    modifier = Modifier.fillMaxWidth().testTag("push_body_input"),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Target Segment / Topic:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("all_users", "esports_players", "android_v1", "gamers").forEach { t ->
                        FilterChip(
                            selected = targetTopic == t,
                            onClick = { targetTopic = t },
                            label = { Text(t, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (notifTitle.isNotBlank()) {
                            campaigns.add(
                                0,
                                PushNotificationCampaign(
                                    id = "p_${System.currentTimeMillis()}",
                                    title = notifTitle,
                                    body = notifBody,
                                    topic = targetTopic,
                                    deliveredCount = 1,
                                    sentAt = "Just now"
                                )
                            )
                            notifTitle = ""
                            notifBody = ""
                            onShowToast("Push notification dispatched to topic: $targetTopic")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixAmber, contentColor = Color(0xFF090D16)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("send_push_btn")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Broadcast Notification Now", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Campaign History Header
        item {
            Text(
                text = "Recent Delivery History (${campaigns.size})",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        items(campaigns, key = { it.id }) { c ->
            ConsoleCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = c.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                        Text(text = c.body, color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Topic: ${c.topic} • Delivered: ${c.deliveredCount} devices • ${c.sentAt}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    StatusBadge(text = c.status, color = ZentrixGreen)
                }
            }
        }
    }
}
