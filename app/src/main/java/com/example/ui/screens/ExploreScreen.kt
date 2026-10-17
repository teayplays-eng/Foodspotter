package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FoodCategory
import com.example.model.FoodSpot
import com.example.model.QueueLevel
import com.example.model.QueueTicket
import com.example.model.TicketStatus
import com.example.ui.components.SpotCard
import com.example.ui.theme.FoodieAmber
import com.example.ui.theme.FoodieFlamePrimary

import com.example.model.UserAccount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    spots: List<FoodSpot>,
    activeTickets: List<QueueTicket>,
    currentUser: UserAccount?,
    onSpotClick: (FoodSpot) -> Unit,
    onBookmarkToggle: (String) -> Unit,
    onOpenTickets: () -> Unit,
    onOpenCrawls: () -> Unit,
    onOpenMap: () -> Unit,
    onOpenArena: () -> Unit,
    onOpenAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FoodCategory.ALL) }
    var filterWalkInOnly by remember { mutableStateOf(false) }
    var filterNearOnly by remember { mutableStateOf(false) }
    var filterTopRatedOnly by remember { mutableStateOf(false) }

    val filteredSpots = remember(spots, searchQuery, selectedCategory, filterWalkInOnly, filterNearOnly, filterTopRatedOnly) {
        spots.filter { spot ->
            val matchesCategory = (selectedCategory == FoodCategory.ALL || spot.category == selectedCategory)
            val matchesSearch = searchQuery.isBlank() ||
                    spot.name.contains(searchQuery, ignoreCase = true) ||
                    spot.district.contains(searchQuery, ignoreCase = true) ||
                    spot.signatureDishes.any { it.name.contains(searchQuery, ignoreCase = true) } ||
                    spot.specialTags.any { it.contains(searchQuery, ignoreCase = true) }
            val matchesWalkIn = !filterWalkInOnly || (spot.queueLevel == QueueLevel.NO_WAIT || spot.queueLevel == QueueLevel.FAST)
            val matchesNear = !filterNearOnly || spot.distanceKm <= 1.2
            val matchesRating = !filterTopRatedOnly || spot.rating >= 4.85f

            matchesCategory && matchesSearch && matchesWalkIn && matchesNear && matchesRating
        }
    }

    val currentWaitingTicket = activeTickets.firstOrNull { it.status == TicketStatus.WAITING || it.status == TicketStatus.CALLING }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Header brand & location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = FoodieFlamePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "FoodSpotter",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Downtown Culinary District • 2.5 km",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Profile / Auth Avatar Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable(onClick = onOpenAuth)
                                .testTag("explore_auth_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (currentUser != null) {
                                    Text(text = currentUser.avatarEmoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currentUser.displayName.take(8),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Login,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Sign In",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Map quick toggle button
                        FilledTonalIconButton(
                            onClick = onOpenMap,
                            modifier = Modifier.testTag("explore_map_toggle_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "View Map",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search dishes, street eats, cafes...", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input")
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Active Waitlist Notice Card if active ticket exists
            if (currentWaitingTicket != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentWaitingTicket.status == TicketStatus.CALLING)
                                Color(0xFFFFEBEE) else Color(0xFFFFF3E0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onOpenTickets)
                            .testTag("active_ticket_banner")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (currentWaitingTicket.status == TicketStatus.CALLING)
                                            Color(0xFFD32F2F) else FoodieFlamePrimary
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (currentWaitingTicket.status == TicketStatus.CALLING)
                                        Icons.Default.NotificationsActive else Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentWaitingTicket.status == TicketStatus.CALLING)
                                        "Table Ready! Head inside now"
                                    else "You are in line at ${currentWaitingTicket.spotName}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF261D17)
                                )
                                Text(
                                    text = if (currentWaitingTicket.status == TicketStatus.CALLING)
                                        "Ticket ${currentWaitingTicket.ticketId} is being called"
                                    else "Position #${currentWaitingTicket.currentPosition} • ~${currentWaitingTicket.estimatedWaitMinutes} min wait",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF5E493C)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF5E493C)
                            )
                        }
                    }
                }
            }

            // 2. Category Filter Pills
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(FoodCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = { Text(category.displayName, fontSize = 13.sp) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("category_chip_${category.name}")
                        )
                    }
                }
            }

            // 3. Quick Toggles Row (Walk-in, Nearby, Top Rated)
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    FilterChip(
                        selected = filterWalkInOnly,
                        onClick = { filterWalkInOnly = !filterWalkInOnly },
                        label = { Text("⚡ Short Wait (<15m)", fontSize = 12.sp) },
                        leadingIcon = {
                            if (filterWalkInOnly) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        }
                    )
                    FilterChip(
                        selected = filterNearOnly,
                        onClick = { filterNearOnly = !filterNearOnly },
                        label = { Text("🚶 < 1.2 km Walk", fontSize = 12.sp) },
                        leadingIcon = {
                            if (filterNearOnly) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        }
                    )
                    FilterChip(
                        selected = filterTopRatedOnly,
                        onClick = { filterTopRatedOnly = !filterTopRatedOnly },
                        label = { Text("⭐ 4.85+ Stars", fontSize = 12.sp) },
                        leadingIcon = {
                            if (filterTopRatedOnly) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        }
                    )
                }
            }

            // 4. Live Food Competition Banner
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF261D18)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenArena)
                        .testTag("explore_battle_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = FoodieFlamePrimary,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "🔥 Live Battle: Golden Crackle Showdown",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Uncle Jin's Roast Pork vs Neon Tiger Bao",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Cast your vote • 1,160 votes cast • 3 days left • +50 XP",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFFDBCF)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FoodieAmber),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏆", fontSize = 22.sp)
                        }
                    }
                }
            }

            // 5. Food Crawl Discovery Banner
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenCrawls)
                        .testTag("food_crawl_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Curated Food Crawls",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Midnight Street Food Odyssey",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "3 iconic stops • 1.8 km walk • Crackling pork, birria & skewers",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.DirectionsWalk,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Local Culinary Spots (${filteredSpots.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Live Queue Updated",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 5. Spots Cards
            if (filteredSpots.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No culinary spots matched your filters",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Try clearing search or filters to see all spots",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(onClick = {
                            searchQuery = ""
                            selectedCategory = FoodCategory.ALL
                            filterWalkInOnly = false
                            filterNearOnly = false
                            filterTopRatedOnly = false
                        }) {
                            Text("Reset All Filters")
                        }
                    }
                }
            } else {
                items(filteredSpots, key = { it.id }) { spot ->
                    SpotCard(
                        spot = spot,
                        onClick = { onSpotClick(spot) },
                        onBookmarkToggle = { onBookmarkToggle(spot.id) }
                    )
                }
            }
        }
    }
}
