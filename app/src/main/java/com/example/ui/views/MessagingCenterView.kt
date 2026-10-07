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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

data class ZentrixChatMessage(
    val id: String,
    val sender: String,
    val text: String,
    val channel: String,
    val time: String,
    val isSystem: Boolean = false
)

@Composable
fun MessagingCenterView(
    activeProjectId: String,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedChannel by remember { mutableStateOf("general-lobby") }
    var inputMessage by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ZentrixChatMessage("m1", "Admin_Zentrix", "Welcome to tournament channel!", "tournament-room-1", "10:00 AM", true),
            ZentrixChatMessage("m2", "Player_Apex", "Ready for match #4!", "tournament-room-1", "10:02 AM"),
            ZentrixChatMessage("m3", "System_Bot", "Match room key generated.", "tournament-room-1", "10:03 AM", true),
            ZentrixChatMessage("m4", "Zentrix_Support", "Global broadcast announcement.", "general-lobby", "09:30 AM", true)
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
        // Messaging Header
        item {
            ConsoleCard(borderColor = ZentrixGreen.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ZentrixGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Forum, contentDescription = null, tint = ZentrixGreen, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Zentrix Messaging Center",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "1-to-1, group channels, broadcast alerts & in-game chat system",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    StatusBadge(text = "SOCKETS READY", color = ZentrixGreen)
                }
            }
        }

        // Channel Selector
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("general-lobby", "tournament-room-1", "support-channel").forEach { ch ->
                    val isSelected = selectedChannel == ch
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedChannel = ch },
                        label = { Text("# $ch", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ZentrixCyan.copy(alpha = 0.2f),
                            selectedLabelColor = ZentrixCyan
                        )
                    )
                }
            }
        }

        // Message Feed
        items(messages.filter { it.channel == selectedChannel }) { msg ->
            ConsoleCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = msg.sender,
                                fontWeight = FontWeight.Bold,
                                color = if (msg.isSystem) ZentrixAmber else ZentrixCyan,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = msg.time, color = TextMuted, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = msg.text, color = TextPrimary, fontSize = 13.sp)
                    }
                }
            }
        }

        // Message Dispatcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = { Text("Send message to #$selectedChannel", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )

                Button(
                    onClick = {
                        if (inputMessage.isNotBlank()) {
                            messages.add(
                                ZentrixChatMessage(
                                    id = "m_${System.currentTimeMillis()}",
                                    sender = "Admin",
                                    text = inputMessage,
                                    channel = selectedChannel,
                                    time = "Just now"
                                )
                            )
                            inputMessage = ""
                            onShowToast("Message dispatched to channel!")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixCyan, contentColor = Color(0xFF090D16)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
