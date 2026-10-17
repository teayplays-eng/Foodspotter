package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.model.FoodCategory
import com.example.model.FoodSpot
import com.example.ui.components.InteractiveMapCanvas
import com.example.ui.theme.FoodieFlamePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    spots: List<FoodSpot>,
    onSpotClick: (FoodSpot) -> Unit,
    onJoinWaitlist: (FoodSpot) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(FoodCategory.ALL) }
    var selectedSpot by remember { mutableStateOf<FoodSpot?>(spots.firstOrNull()) }
    var showHeatmap by remember { mutableStateOf(false) }

    val filteredSpots = remember(spots, selectedCategory) {
        if (selectedCategory == FoodCategory.ALL) spots
        else spots.filter { it.category == selectedCategory }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Map Canvas
        InteractiveMapCanvas(
            spots = filteredSpots,
            selectedSpot = selectedSpot,
            onSpotSelected = { selectedSpot = it },
            onViewDetail = onSpotClick,
            onJoinWaitlist = onJoinWaitlist,
            showHeatmap = showHeatmap,
            modifier = Modifier.fillMaxSize()
        )

        // Floating Top Overlay (Controls & Categories)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = FoodieFlamePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Culinary Radar Map",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Heatmap toggle button
                    FilterChip(
                        selected = showHeatmap,
                        onClick = { showHeatmap = !showHeatmap },
                        label = { Text(if (showHeatmap) "Heatmap ON" else "Wait Heatmap", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = null,
                                tint = if (showHeatmap) FoodieFlamePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("map_heatmap_toggle")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category selector bar
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(FoodCategory.values()) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedCategory = category }
                            .testTag("map_category_${category.name}")
                    ) {
                        Text(
                            text = category.displayName,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Floating Map Controls on right edge
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    // Recenter on user coordinates
                    selectedSpot = spots.minByOrNull { it.distanceKm }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("map_recenter_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Recenter",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
