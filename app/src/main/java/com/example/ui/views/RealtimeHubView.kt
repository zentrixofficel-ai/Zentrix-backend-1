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
import com.example.ui.components.CodeBlockView
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun RealtimeHubView(
    project: ProjectEntity?,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var testLiveCounter by remember { mutableStateOf(108) }
    var simulatedTypingUser by remember { mutableStateOf("Player_Zentrix99") }
    var isSimulatingTyping by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ZentrixBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Realtime Header
        item {
            ConsoleCard(borderColor = ZentrixCyan.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ZentrixCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.WifiTethering, contentDescription = null, tint = ZentrixCyan, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Zentrix Realtime Engine",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "WebSocket duplex synchronization, live presence & event streaming",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    StatusBadge(text = "LIVE WS", color = ZentrixGreen)
                }
            }
        }

        // Live Telemetry Row (Requirement 6)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricItem(label = "Active Sockets", value = "18", color = ZentrixCyan, modifier = Modifier.weight(1f))
                MetricItem(label = "Events / sec", value = "42.8", color = ZentrixViolet, modifier = Modifier.weight(1f))
                MetricItem(label = "Messages / sec", value = "16.4", color = ZentrixGreen, modifier = Modifier.weight(1f))
            }
        }

        // Live Presence & State Simulator
        item {
            ConsoleCard {
                Text(
                    text = "Live Presence & Counter Simulator",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
                Text(
                    text = "Test live listeners, typing indicator broadcast, and atomic counter updates",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Live Shared Counter:", color = TextSecondary, fontSize = 11.sp)
                        Text(
                            text = "$testLiveCounter",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = ZentrixCyan
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                testLiveCounter++
                                onShowToast("Counter incremented: $testLiveCounter")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ZentrixCyan, contentColor = Color(0xFF090D16)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+ Increment", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                isSimulatingTyping = !isSimulatingTyping
                                onShowToast(if (isSimulatingTyping) "Broadcasted: $simulatedTypingUser is typing..." else "Typing stopped")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSimulatingTyping) ZentrixAmber else ZentrixSurfaceVariant,
                                contentColor = if (isSimulatingTyping) Color.Black else ZentrixCyan
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isSimulatingTyping) "Typing Active..." else "Simulate Typing", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // WebSocket Code Snippet
        item {
            val wsSnippet = """
// Zentrix Realtime WebSocket Listener (Kotlin)
val ws = ZentrixRealtime.connect(
    endpoint = "wss://ws.zentrixcloud.io/realtime",
    projectId = "${project?.id ?: "zentrix-esport"}",
    apiKey = "${project?.clientPublicKey ?: "zx_pub_live_key"}"
)

ws.subscribe("presence/${project?.id ?: "zentrix-esport"}") { event ->
    println("Live event received: " + event.payload)
}
""".trimIndent()

            CodeBlockView(
                code = wsSnippet,
                title = "realtime_client.kt",
                onCopy = { onShowToast("WebSocket client code copied!") }
            )
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    ConsoleCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = label, color = TextSecondary, fontSize = 10.sp)
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = color,
                fontSize = 18.sp
            )
        }
    }
}
