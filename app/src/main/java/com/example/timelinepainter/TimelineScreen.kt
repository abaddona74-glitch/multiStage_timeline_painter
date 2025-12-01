package com.example.timelinepainter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@OptIn(ExperimentalTextApi::class)
@Composable
fun TimelineScreen() {
    val timelineState = rememberTimelineState()
    var showOverlay by remember { mutableStateOf(true) }

    // Constants
    val stages = Stage.values()
    val startTimeHour = 12
    val endTimeHour = 23
    val totalHours = endTimeHour - startTimeHour
    
    // Base Dimensions (Unzoomed)
    val baseHourHeight = 120f
    val headerHeight = 80f
    val timeColumnWidth = 60f // Space for time labels on the left

    // Colors (Dark Theme)
    val backgroundColor = Color(0xFF121212)
    val columnEvenColor = Color(0xFF1E1E1E)
    val columnOddColor = Color(0xFF121212)
    val gridLineColor = Color(0xFF333333)
    val textColor = Color(0xFFEEEEEE)
    val timeLabelColor = Color(0xFFAAAAAA)
    val headerBackgroundColor = Color(0xFF2C2C2C)

    LaunchedEffect(Unit) {
        delay(2500)
        showOverlay = false
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        val screenWidth = constraints.maxWidth.toFloat()
        val screenHeight = constraints.maxHeight.toFloat()

        // Calculate base column width to fit screen initially (minus time column)
        val baseColumnWidth = (screenWidth - timeColumnWidth) / stages.size

        // Current Dimensions based on Zoom
        val currentColumnWidth = baseColumnWidth * timelineState.zoom
        val currentHourHeight = baseHourHeight * timelineState.zoom
        
        // Total Content Size (Unzoomed for State calculation)
        val totalContentWidthUnzoomed = timeColumnWidth + (baseColumnWidth * stages.size)
        val totalContentHeightUnzoomed = headerHeight + (baseHourHeight * totalHours) + 100f // padding

        val textMeasurer = rememberTextMeasurer()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoomChange, _ ->
                        timelineState.transform(
                            panChange = pan,
                            zoomChange = zoomChange,
                            containerSize = Size(screenWidth, screenHeight),
                            contentSize = Size(totalContentWidthUnzoomed, totalContentHeightUnzoomed)
                        )
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                clipRect {
                    // We apply translation to the whole canvas
                    withTransform({ translate(left = timelineState.offsetX, top = timelineState.offsetY) }) {
                        
                        // 1. Draw Stage Columns (Backgrounds)
                        stages.forEachIndexed { index, _ ->
                            val x = timeColumnWidth + (index * currentColumnWidth)
                            val color = if (index % 2 == 0) columnEvenColor else columnOddColor
                            
                            drawRect(
                                color = color,
                                topLeft = Offset(x, 0f),
                                size = Size(currentColumnWidth, headerHeight + (totalHours * currentHourHeight) + 100f)
                            )
                            
                            // Vertical Divider
                            drawLine(
                                color = gridLineColor,
                                start = Offset(x, 0f),
                                end = Offset(x, headerHeight + (totalHours * currentHourHeight) + 100f),
                                strokeWidth = 1f
                            )
                        }

                        // 2. Draw Time Lines (Horizontal)
                        for (i in 0..totalHours) {
                            val y = headerHeight + (i * currentHourHeight)
                            drawLine(
                                color = gridLineColor,
                                start = Offset(0f, y),
                                end = Offset(timeColumnWidth + (stages.size * currentColumnWidth), y),
                                strokeWidth = 1f
                            )
                        }

                        // 3. Draw Events
                        sampleEvents.forEach { event ->
                            val stageIndex = stages.indexOf(event.stage)
                            if (stageIndex != -1) {
                                val startHourDiff = ChronoUnit.MINUTES.between(
                                    java.time.LocalTime.of(startTimeHour, 0),
                                    event.startTime
                                ) / 60f
                                val durationHours = ChronoUnit.MINUTES.between(event.startTime, event.endTime) / 60f
                                
                                val x = timeColumnWidth + (stageIndex * currentColumnWidth)
                                val y = headerHeight + (startHourDiff * currentHourHeight)
                                val width = currentColumnWidth
                                val height = durationHours * currentHourHeight

                                val padding = 6f * timelineState.zoom
                                val eventRectSize = Size(width - 2 * padding, height - 2 * padding)
                                val eventTopLeft = Offset(x + padding, y + padding)

                                // Event Background
                                drawRoundRect(
                                    color = event.color,
                                    topLeft = eventTopLeft,
                                    size = eventRectSize,
                                    cornerRadius = CornerRadius(8f * timelineState.zoom)
                                )

                                // Event Text (Clipped)
                                clipRect(
                                    left = eventTopLeft.x,
                                    top = eventTopLeft.y,
                                    right = eventTopLeft.x + eventRectSize.width,
                                    bottom = eventTopLeft.y + eventRectSize.height
                                ) {
                                    val artistText = textMeasurer.measure(
                                        text = event.artistName,
                                        style = TextStyle(
                                            fontSize = (14 * timelineState.zoom).sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    val timeText = textMeasurer.measure(
                                        text = "${event.startTime} - ${event.endTime}",
                                        style = TextStyle(
                                            fontSize = (10 * timelineState.zoom).sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    )

                                    drawText(
                                        artistText,
                                        topLeft = eventTopLeft + Offset(8f, 8f)
                                    )
                                    drawText(
                                        timeText,
                                        topLeft = eventTopLeft + Offset(8f, 8f + artistText.size.height)
                                    )
                                }
                            }
                        }
                    } // End of scrolled content

                    // 4. Sticky Headers (Stages) - Only translate X, keep Y fixed at top
                    // We need to redraw the header background over the scrolled content at the top
                    // But wait, if we scroll Y, the headers should scroll away? 
                    // Usually headers are sticky. Let's make them sticky.
                    
                    // To make sticky headers, we DON'T apply the Y translation to them.
                    // But we DO apply the X translation.
                    
                    // Draw Header Background
                    drawRect(
                        color = headerBackgroundColor,
                        topLeft = Offset(0f, 0f),
                        size = Size(screenWidth, headerHeight)
                    )

                    withTransform({ translate(left = timelineState.offsetX, top = 0f) }) {
                        stages.forEachIndexed { index, stage ->
                            val x = timeColumnWidth + (index * currentColumnWidth)
                            
                            // Header Text
                            val textResult = textMeasurer.measure(
                                text = stage.displayName,
                                style = TextStyle(
                                    fontSize = (18 * timelineState.zoom).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            )
                            
                            drawText(
                                textResult,
                                topLeft = Offset(
                                    x + (currentColumnWidth - textResult.size.width) / 2,
                                    (headerHeight - textResult.size.height) / 2
                                )
                            )
                        }
                    }

                    // 5. Sticky Time Column - Only translate Y, keep X fixed at left
                    // Draw Time Column Background
                    drawRect(
                        color = backgroundColor, // Or slightly different
                        topLeft = Offset(0f, headerHeight),
                        size = Size(timeColumnWidth, screenHeight)
                    )
                    
                    withTransform({ translate(left = 0f, top = timelineState.offsetY) }) {
                        for (i in 0..totalHours) {
                            val y = headerHeight + (i * currentHourHeight)
                            // Only draw if visible to save perf? Canvas handles culling mostly.
                            
                            val timeText = String.format("%02d:00", startTimeHour + i)
                            val textResult = textMeasurer.measure(
                                text = timeText,
                                style = TextStyle(
                                    fontSize = (12 * timelineState.zoom).sp,
                                    color = timeLabelColor,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            
                            drawText(
                                textResult,
                                topLeft = Offset(
                                    (timeColumnWidth - textResult.size.width) / 2,
                                    y - (textResult.size.height / 2)
                                )
                            )
                        }
                    }
                    
                    // 6. Corner Box (Top-Left intersection)
                    drawRect(
                        color = headerBackgroundColor,
                        topLeft = Offset(0f, 0f),
                        size = Size(timeColumnWidth, headerHeight)
                    )
                }
            }
        }

        // Zoom Indicator
        if (timelineState.zoom > 1.01f) {
            val zoomPercent = (timelineState.zoom * 100).roundToInt()
            val displayZoom = (zoomPercent / 10) * 10
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .background(Color(0xFF333333).copy(alpha = 0.9f), shape = MaterialTheme.shapes.medium)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Zoom: $displayZoom%",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Overlay Hint
        AnimatedVisibility(
            visible = showOverlay,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pinch to Zoom & Pan",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

