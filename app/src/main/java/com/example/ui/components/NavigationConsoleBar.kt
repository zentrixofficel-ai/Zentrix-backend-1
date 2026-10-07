package com.example.ui.components

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
        color = FirebaseSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, FirebaseCardBorder),
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        val scrollState = rememberScrollState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConsoleTab.values().forEach { tab ->
                val isSelected = tab == currentTab
                val icon: ImageVector = when (tab) {
                    ConsoleTab.PROJECTS -> Icons.Default.Settings
                    ConsoleTab.SDK_CONNECT -> Icons.Default.Code
                    ConsoleTab.VAULT -> Icons.Default.VpnKey
                    ConsoleTab.AUTH -> Icons.Default.PeopleAlt
                    ConsoleTab.DATABASE -> Icons.Default.Storage
                    ConsoleTab.SECURITY -> Icons.Default.Shield
                    ConsoleTab.DATA_EXPLORER -> Icons.Default.ManageSearch
                    ConsoleTab.ANALYTICS -> Icons.Default.Analytics
                    ConsoleTab.SETTINGS -> Icons.Default.Tune
                }

                Surface(
                    color = if (isSelected) Color(0xFFE8F0FE) else Color.Transparent,
                    shape = RoundedCornerShape(20.dp),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, ButtonBlue) else null,
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .testTag("nav_tab_${tab.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) ButtonBlue else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tab.title,
                            color = if (isSelected) ButtonBlue else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
