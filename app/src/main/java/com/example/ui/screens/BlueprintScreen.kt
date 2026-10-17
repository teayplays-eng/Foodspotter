package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BlueprintSpecs
import com.example.model.ApiEndpoint

enum class BlueprintTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    SYSTEM_ARCH("Architecture", Icons.Default.Hub),
    POSTGRES_SCHEMA("PostgreSQL DDL", Icons.Default.Storage),
    FIRESTORE_SCHEMA("Firestore NoSQL", Icons.Default.CloudQueue),
    API_CONTRACTS("REST & WS APIs", Icons.Default.Api),
    FEATURE_SPECS("Core Specs", Icons.Default.MenuBook)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlueprintScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(BlueprintTab.SYSTEM_ARCH) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Architecture,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FoodSpotter Blueprint",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = "Full-Stack System Architecture & Specifications",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Horizontal Tab Selector
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(BlueprintTab.values()) { tab ->
                    val isSelected = selectedTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.label, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        modifier = Modifier.testTag("blueprint_tab_${tab.name}")
                    )
                }
            }

            HorizontalDivider()

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                when (selectedTab) {
                    BlueprintTab.SYSTEM_ARCH -> {
                        item {
                            BlueprintCard(
                                title = "End-to-End System Topology",
                                subtitle = "Cloud Infrastructure & Real-time Queue Engine"
                            ) {
                                CodeSnippet(code = BlueprintSpecs.systemArchitectureOverview)
                            }
                        }
                        item {
                            BlueprintCard(
                                title = "Microservices & Components",
                                subtitle = "Distributed modules for scale and sub-second queue sync"
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ModuleSpec(
                                        title = "1. Real-Time Virtual Queue Engine",
                                        stack = "Redis ZSET + Node.js / Elixir OTP + WebSockets",
                                        detail = "Sub-10ms queue position lookups and dynamic countdown estimation based on historical table dwell times."
                                    )
                                    ModuleSpec(
                                        title = "2. Geospatial Discovery Engine",
                                        stack = "PostGIS + Redis Geohash (Precision 7)",
                                        detail = "Bbox and radius queries with GiST spatial indexing for 60fps mobile map rendering."
                                    )
                                    ModuleSpec(
                                        title = "3. Food Crawl Route Optimizer",
                                        stack = "Traveling Foodie Graph Algorithm (Dijkstra / TSP)",
                                        detail = "Orders food stops by walking proximity, eliminating backtracking and computing total calorie/step metrics."
                                    )
                                }
                            }
                        }
                    }

                    BlueprintTab.POSTGRES_SCHEMA -> {
                        item {
                            BlueprintCard(
                                title = "Relational DDL & PostGIS Schema",
                                subtitle = "Production SQL schema with spatial indices & constraints"
                            ) {
                                CodeSnippet(code = BlueprintSpecs.postgresDdlSchema)
                            }
                        }
                    }

                    BlueprintTab.FIRESTORE_SCHEMA -> {
                        item {
                            BlueprintCard(
                                title = "NoSQL Document Blueprint (Firestore)",
                                subtitle = "High-velocity document collection schema & real-time sync"
                            ) {
                                CodeSnippet(code = BlueprintSpecs.firestoreSchemaJson)
                            }
                        }
                    }

                    BlueprintTab.API_CONTRACTS -> {
                        item {
                            Text(
                                text = "Core REST & WebSocket Endpoints",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        items(BlueprintSpecs.apiEndpoints) { endpoint ->
                            ApiEndpointCard(endpoint = endpoint)
                        }
                    }

                    BlueprintTab.FEATURE_SPECS -> {
                        item {
                            BlueprintCard(
                                title = "Core Feature Specifications",
                                subtitle = "Queue state machine, geofenced visits, and trust score"
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    BlueprintSpecs.coreFeatureSpecs.forEach { spec ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = spec,
                                                fontSize = 13.sp,
                                                modifier = Modifier.padding(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BlueprintCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun CodeSnippet(code: String) {
    Surface(
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Text(
            text = code,
            color = Color(0xFF81C784),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
fun ModuleSpec(title: String, stack: String, detail: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(text = "Tech: $stack", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = detail, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ApiEndpointCard(endpoint: ApiEndpoint) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = when (endpoint.method) {
                        "GET" -> Color(0xFF2E7D32)
                        "POST" -> Color(0xFF1565C0)
                        "WS" -> Color(0xFF6A1B9A)
                        else -> MaterialTheme.colorScheme.primary
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = endpoint.method,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = endpoint.path,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = endpoint.summary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (endpoint.requestSample != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Request Body:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                CodeSnippet(code = endpoint.requestSample)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Response:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            CodeSnippet(code = endpoint.responseSample)
        }
    }
}
