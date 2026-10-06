package com.example.ui.views

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DataDocumentEntity
import com.example.ui.components.CodeBlockView
import com.example.ui.components.ConsoleCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.*

@Composable
fun DatabaseView(
    documents: List<DataDocumentEntity>,
    activeProjectId: String,
    selectedCollection: String?,
    onSelectCollection: (String) -> Unit,
    onAddDocumentClick: () -> Unit,
    onDeleteDocument: (DataDocumentEntity) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var docToDelete by remember { mutableStateOf<DataDocumentEntity?>(null) }

    // Derive collections for the active project
    val availableCollections = remember(documents) {
        documents.map { it.collectionName }.distinct().ifEmpty {
            listOf("tournaments", "users", "configs")
        }
    }

    val currentCollection = selectedCollection ?: availableCollections.firstOrNull() ?: "tournaments"

    val filteredDocs = remember(documents, currentCollection) {
        documents.filter { it.collectionName == currentCollection }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
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
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = ZentrixCyan,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Zentrix Cloud Database",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Firestore ও Supabase এর মতো নো-এসকিউএল কালেকশন ও টেবিল ম্যানেজার",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZentrixCyan,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = ConsoleCardBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "প্রজেক্ট: $activeProjectId",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Button(
                        onClick = onAddDocumentClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZentrixCyan,
                            contentColor = Color(0xFF041E28)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_document_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("নতুন ডকুমেন্ট লিখুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Collection Selector Chips
        item {
            Column {
                Text(
                    text = "কালেকশন নির্বাচন (Collections):",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableCollections.forEach { colName ->
                        val isSelected = colName == currentCollection
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectCollection(colName) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) Color(0xFF041E28) else ZentrixCyan
                                )
                            },
                            label = {
                                Text(
                                    text = colName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ZentrixCyan,
                                selectedLabelColor = Color(0xFF041E28),
                                containerColor = ConsoleSurfaceVariant,
                                labelColor = TextPrimary
                            )
                        )
                    }
                }
            }
        }

        if (filteredDocs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "কালেকশন '$currentCollection' এ কোনো রেকর্ড পাওয়া যায়নি। নতুন ডকুমেন্ট লিখুন।",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        items(filteredDocs, key = { it.id }) { doc ->
            ConsoleCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = doc.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Doc ID: ${doc.id}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = ZentrixCyan
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                copyToClipboard(context, doc.title, doc.dataJson) {
                                    onShowToast("ডকুমেন্ট JSON কপি হয়েছে!")
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy JSON",
                                tint = ZentrixCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = { docToDelete = doc },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Doc",
                                tint = ZentrixRed.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // JSON Body Box
                Surface(
                    color = CodeBackground,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ConsoleCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = doc.dataJson,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }

    if (docToDelete != null) {
        val d = docToDelete!!
        AlertDialog(
            onDismissRequest = { docToDelete = null },
            containerColor = ConsoleSurface,
            title = {
                Text("ডকুমেন্ট মুছে ফেলতে চান?", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "ডকুমেন্ট '${d.title}' (${d.id}) স্থায়ীভাবে ক্লাউড ডাটাবেস থেকে মুছে যাবে।",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteDocument(d)
                        docToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZentrixRed)
                ) {
                    Text("মুছে ফেলুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { docToDelete = null }) {
                    Text("বাতিল", color = TextSecondary)
                }
            }
        )
    }
}
