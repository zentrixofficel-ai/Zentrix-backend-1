package com.example.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.ui.components.ConsoleCard
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
    var selectedDatabaseType by remember { mutableStateOf("Cloud Firestore") } // Firestore or Realtime DB (like video 0:19)

    val availableCollections = remember(documents) {
        documents.map { it.collectionName }.distinct()
    }

    val currentCollection = selectedCollection ?: availableCollections.firstOrNull() ?: "main"

    val filteredDocs = remember(documents, currentCollection) {
        documents.filter { it.collectionName == currentCollection }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FirebaseBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Firebase Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDatabaseType,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Blue Button: Add Document
                    Button(
                        onClick = onAddDocumentClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_document_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add document", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Database Switcher (Firestore / Realtime Database)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Cloud Firestore", "Realtime Database", "Rules", "Indexes", "Usage").forEach { dbTab ->
                        val isSelected = selectedDatabaseType == dbTab
                        Surface(
                            color = if (isSelected) Color(0xFFE8F0FE) else Color.Transparent,
                            shape = RoundedCornerShape(20.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, ButtonBlue) else null,
                            onClick = { selectedDatabaseType = dbTab }
                        ) {
                            Text(
                                text = dbTab,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ButtonBlue else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Collection Selector Chips
        if (availableCollections.isNotEmpty()) {
            item {
                ConsoleCard {
                    Text("Root Collections:", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableCollections.forEach { colName ->
                            val isSelected = colName == currentCollection
                            Surface(
                                color = if (isSelected) Color(0xFFE8F0FE) else FirebaseSurfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, ButtonBlue) else null,
                                modifier = Modifier.clickable { onSelectCollection(colName) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Folder, contentDescription = null, tint = ButtonYellow, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = colName,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = if (isSelected) ButtonBlue else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (filteredDocs.isEmpty()) {
            item {
                ConsoleCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Storage, contentDescription = null, tint = TextMuted, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Collection is empty", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Click 'Add document' above to create records.", color = TextMuted, fontSize = 12.sp)
                        }
                    }
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
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Path: /${doc.collectionName}/${doc.id}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = ButtonBlue
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Black Button: Copy JSON
                        Button(
                            onClick = {
                                copyToClipboard(context, doc.title, doc.dataJson) {
                                    onShowToast("JSON copied")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonBlack),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 10.sp)
                        }

                        // Red Button: Delete
                        IconButton(
                            onClick = { docToDelete = doc },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = ButtonRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // JSON Fields Body Box
                Surface(
                    color = CodeBackground,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = doc.dataJson,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = CodeText,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }

    if (docToDelete != null) {
        val d = docToDelete!!
        AlertDialog(
            onDismissRequest = { docToDelete = null },
            containerColor = FirebaseSurface,
            title = { Text("Delete Document?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Delete document '${d.title}' permanently from Firestore?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteDocument(d)
                        docToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { docToDelete = null }) { Text("Cancel") }
            }
        )
    }
}
