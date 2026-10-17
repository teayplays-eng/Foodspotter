package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FoodCategory
import com.example.model.FoodSpot
import com.example.model.QueueLevel
import com.example.ui.theme.*

@Composable
fun InteractiveMapCanvas(
    spots: List<FoodSpot>,
    selectedSpot: FoodSpot?,
    onSpotSelected: (FoodSpot) -> Unit,
    onViewDetail: (FoodSpot) -> Unit,
    onJoinWaitlist: (FoodSpot) -> Unit,
    showHeatmap: Boolean = false,
    modifier: Modifier = Modifier
) {
    var zoomScale by remember { mutableFloatStateOf(1.0f) }

    Box(modifier = modifier.fillMaxSize()) {
        // Vector Map Drawing Surface
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(spots) {
                    detectTapGestures { tapOffset ->
                        val w = size.width
                        val h = size.height
                        // Check if tap hit any spot pin within 28.dp radius
                        val touchRadiusPx = 36.dp.toPx()
                        var closestSpot: FoodSpot? = null
                        var minDistance = Float.MAX_VALUE

                        for (spot in spots) {
                            val pinX = spot.coordinates.mapX * w
                            val pinY = spot.coordinates.mapY * h
                            val dx = tapOffset.x - pinX
                            val dy = tapOffset.y - pinY
                            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                            if (dist < touchRadiusPx && dist < minDistance) {
                                minDistance = dist
                                closestSpot = spot
                            }
                        }

                        if (closestSpot != null) {
                            onSpotSelected(closestSpot)
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // 1. Background City Map Surface
            drawRect(
                color = Color(0xFFF3EDE2),
                size = size
            )

            // 2. City Blocks / Neighborhood grids
            drawCityBlocks(width, height)

            // 3. Waterfront / River Channel
            drawRiverChannel(width, height)

            // 4. Parks & Culinary plazas
            drawFoodParks(width, height)

            // 5. Roads & Boulevards
            drawRoads(width, height)

            // 6. Heatmap glow if enabled
            if (showHeatmap) {
                drawQueueHeatmap(spots, width, height)
            }

            // 7. User location beacon
            val userX = 0.50f * width
            val userY = 0.52f * height
            drawCircle(
                color = Color(0x332196F3),
                radius = 28.dp.toPx(),
                center = Offset(userX, userY)
            )
            drawCircle(
                color = Color.White,
                radius = 9.dp.toPx(),
                center = Offset(userX, userY)
            )
            drawCircle(
                color = Color(0xFF1976D2),
                radius = 6.dp.toPx(),
                center = Offset(userX, userY)
            )

            // 8. Draw Spot Pins
            spots.forEach { spot ->
                val px = spot.coordinates.mapX * width
                val py = spot.coordinates.mapY * height
                val isSelected = selectedSpot?.id == spot.id

                val pinColor = when (spot.queueLevel) {
                    QueueLevel.NO_WAIT -> Color(0xFF2E7D32)
                    QueueLevel.FAST -> Color(0xFF43A047)
                    QueueLevel.MODERATE -> Color(0xFFEF6C00)
                    QueueLevel.BUSY -> Color(0xFFD32F2F)
                }

                // If selected, draw glowing outer target ring
                if (isSelected) {
                    drawCircle(
                        color = FoodieFlamePrimary.copy(alpha = 0.35f),
                        radius = 26.dp.toPx(),
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 16.dp.toPx(),
                        center = Offset(px, py)
                    )
                }

                // Pin Outer Circle with White border
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 14.dp.toPx() else 11.dp.toPx(),
                    center = Offset(px, py)
                )
                drawCircle(
                    color = pinColor,
                    radius = if (isSelected) 11.dp.toPx() else 8.dp.toPx(),
                    center = Offset(px, py)
                )

                // Small center core
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 4.dp.toPx() else 2.5f.dp.toPx(),
                    center = Offset(px, py)
                )
            }
        }

        // Selected Spot Floating Callout Card
        if (selectedSpot != null) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("map_spot_preview_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedSpot.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${selectedSpot.district} • ${selectedSpot.distanceKm} km away",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            QueueStatusChip(
                                queueLevel = selectedSpot.queueLevel,
                                estimatedWaitMinutes = selectedSpot.estimatedWaitMinutes,
                                partiesCount = selectedSpot.queuePartiesCount
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onViewDetail(selectedSpot) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("map_view_details_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("View Details", fontSize = 13.sp)
                            }

                            Button(
                                onClick = { onJoinWaitlist(selectedSpot) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("map_join_queue_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Queue,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Join Queue", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawCityBlocks(w: Float, h: Float) {
    val blockPaint = Color(0xFFE8E0D5)
    // Draw grid of city blocks
    for (i in 0..4) {
        for (j in 0..4) {
            drawRoundRect(
                color = blockPaint,
                topLeft = Offset(i * (w / 5) + 12f, j * (h / 5) + 12f),
                size = Size(w / 5 - 24f, h / 5 - 24f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
            )
        }
    }
}

private fun DrawScope.drawRiverChannel(w: Float, h: Float) {
    val riverColor = Color(0xFFB3E5FC)
    val path = Path().apply {
        moveTo(w * 0.75f, 0f)
        cubicTo(
            w * 0.70f, h * 0.3f,
            w * 0.95f, h * 0.6f,
            w * 0.80f, h
        )
        lineTo(w * 0.92f, h)
        cubicTo(
            w * 1.05f, h * 0.6f,
            w * 0.82f, h * 0.3f,
            w * 0.88f, 0f
        )
        close()
    }
    drawPath(path, color = riverColor, style = Fill)
}

private fun DrawScope.drawFoodParks(w: Float, h: Float) {
    val parkColor = Color(0xFFD7EED6)
    // Central Food Park
    drawRoundRect(
        color = parkColor,
        topLeft = Offset(w * 0.35f, h * 0.38f),
        size = Size(w * 0.28f, h * 0.16f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
    )
}

private fun DrawScope.drawRoads(w: Float, h: Float) {
    val roadColor = Color(0xFFFFFFFF)
    val strokeWidth = 14f

    // Major Avenues
    drawLine(roadColor, Offset(0f, h * 0.35f), Offset(w, h * 0.35f), strokeWidth = strokeWidth)
    drawLine(roadColor, Offset(0f, h * 0.68f), Offset(w, h * 0.68f), strokeWidth = strokeWidth)
    drawLine(roadColor, Offset(w * 0.32f, 0f), Offset(w * 0.32f, h), strokeWidth = strokeWidth)
    drawLine(roadColor, Offset(w * 0.65f, 0f), Offset(w * 0.65f, h), strokeWidth = strokeWidth)
}

private fun DrawScope.drawQueueHeatmap(spots: List<FoodSpot>, w: Float, h: Float) {
    spots.forEach { spot ->
        val px = spot.coordinates.mapX * w
        val py = spot.coordinates.mapY * h
        val glowColor = when (spot.queueLevel) {
            QueueLevel.BUSY -> Color(0x55D32F2F)
            QueueLevel.MODERATE -> Color(0x44EF6C00)
            else -> Color(0x3343A047)
        }
        drawCircle(
            color = glowColor,
            radius = (spot.estimatedWaitMinutes * 2f).coerceIn(30f, 90f),
            center = Offset(px, py)
        )
    }
}
