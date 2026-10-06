package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConsoleTab
import com.example.ui.theme.*

@Composable
fun NavigationConsoleBar(
    currentTab: ConsoleTab,
    onTabSelected: (ConsoleTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ConsoleSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ConsoleCardBorder),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        val scrollState = rememberScrollState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConsoleTab.values().forEach { tab ->
                val isSelected = tab == currentTab
                val icon: ImageVector = when (tab) {
                    ConsoleTab.PROJECTS -> Icons.Default.Apps
                    ConsoleTab.SDK_CONNECT -> Icons.Default.Code
                    ConsoleTab.VAULT -> Icons.Default.VpnKey
                    ConsoleTab.AUTH -> Icons.Default.Group
                    ConsoleTab.DATABASE -> Icons.Default.Storage
                    ConsoleTab.SECURITY -> Icons.Default.Shield
                    ConsoleTab.ANALYTICS -> Icons.Default.Insights
                }

                Surface(
                    color = if (isSelected) ZentrixCyan.copy(alpha = 0.18f) else ConsoleSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, ZentrixCyan) else null,
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .testTag("nav_tab_${tab.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) ZentrixCyan else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = tab.title,
                                color = if (isSelected) ZentrixCyan else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                            Text(
                                text = tab.bnTitle,
                                color = if (isSelected) ZentrixCyan.copy(alpha = 0.8f) else TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
