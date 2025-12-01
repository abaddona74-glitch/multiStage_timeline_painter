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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalTextApi::class)
@Composable
fun TimelineScreen() {
    // State for Zoom and Pan
    var zoom by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // State for Overlay Hint
    var showOverlay by remember { mutableStateOf(true) }

    // Constants
    val minZoom = 1f
    val maxZoom = 2.5f
    val stages = Stage.values()
    val startTimeHour = 12
    val endTimeHour = 23
    val totalHours = endTimeHour - startTimeHour

    // Hide overlay after 2.5 seconds
    LaunchedEffect(Unit) {
        delay(2500)
        showOverlay = false
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val screenWidth = constraints.maxWidth.toFloat()
        val screenHeight = constraints.maxHeight.toFloat()

        // Base dimensions
        val baseColumnWidth = screenWidth / stages.size
        // Let's say initially the total height fits the screen or is scrollable?
        // "In the initial state, the full timeline must fit the screen width."
        // It doesn't explicitly say it must fit the screen height, but usually timelines are tall.
        // Let's define a base hour height.
        val baseHourHeight = 100f // Arbitrary base height in pixels for 1 hour

        // Calculated dimensions based on zoom
        val currentColumnWidth = baseColumnWidth * zoom
        val currentHourHeight = baseHourHeight * zoom
        val totalContentWidth = currentColumnWidth * stages.size
        val totalContentHeight = currentHourHeight * totalHours + 100f // + header space

        // Text Measurer for drawing text on Canvas
        val textMeasurer = rememberTextMeasurer()

        // Gesture Detector
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoomChange, _ ->
                        val oldZoom = zoom
                        val newZoom = (zoom * zoomChange).coerceIn(minZoom, maxZoom)
                        zoom = newZoom

                        // Calculate bounds for scrolling
                        // Content can be larger than screen.
                        // maxOffset is 0 (aligned to top/left)
                        // minOffset is screenDimension - contentDimension (aligned to bottom/right)

                        val maxOffsetX = 0f
                        val minOffsetX = min(0f, screenWidth - totalContentWidth)
                        val maxOffsetY = 0f
                        val minOffsetY = min(0f, screenHeight - totalContentHeight)

                        // Adjust offset to keep focus or just simple pan
                        // Simple pan logic with bounds check:
                        offsetX = (offsetX + pan.x).coerceIn(minOffsetX, maxOffsetX)
                        offsetY = (offsetY + pan.y).coerceIn(minOffsetY, maxOffsetY)
                        
                        // If we zoomed, we might need to adjust offset to keep center?
                        // For simplicity in this challenge, we'll stick to simple pan + zoom clamping.
                        // But to prevent jumping when zooming out from a scrolled position:
                        if (newZoom != oldZoom) {
                             // Re-clamp in case zoom out made content smaller than screen or pulled edges in
                             val newMinOffsetX = min(0f, screenWidth - (baseColumnWidth * newZoom * stages.size))
                             val newMinOffsetY = min(0f, screenHeight - (baseHourHeight * newZoom * totalHours + 100f))
                             offsetX = offsetX.coerceIn(newMinOffsetX, 0f)
                             offsetY = offsetY.coerceIn(newMinOffsetY, 0f)
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                clipRect {
                    // Apply translation
                    withTransform({ translate(left = offsetX, top = offsetY) }) {
                        
                        // Draw Grid and Stages
                        stages.forEachIndexed { index, stage ->
                            val stageX = index * currentColumnWidth
                            
                            // Draw Column Background (alternating slightly for visibility?)
                            if (index % 2 == 1) {
                                drawRect(
                                    color = Color(0xFFF5F5F5),
                                    topLeft = Offset(stageX, 0f),
                                    size = Size(currentColumnWidth, totalContentHeight)
                                )
                            }

                            // Draw Stage Header
                            val headerHeight = 60f * zoom
                            drawRect(
                                color = Color.LightGray,
                                topLeft = Offset(stageX, 0f),
                                size = Size(currentColumnWidth, headerHeight)
                            )
                            
                            val textLayoutResult = textMeasurer.measure(
                                text = stage.displayName,
                                style = TextStyle(fontSize = (16 * zoom).sp, fontWeight = FontWeight.Bold)
                            )
                            drawText(
                                textLayoutResult,
                                topLeft = Offset(
                                    stageX + (currentColumnWidth - textLayoutResult.size.width) / 2,
                                    (headerHeight - textLayoutResult.size.height) / 2
                                )
                            )

                            // Draw Vertical Lines
                            drawLine(
                                color = Color.Gray,
                                start = Offset(stageX, 0f),
                                end = Offset(stageX, totalContentHeight),
                                strokeWidth = 1f
                            )
                        }
                        // Draw last vertical line
                        drawLine(
                            color = Color.Gray,
                            start = Offset(stages.size * currentColumnWidth, 0f),
                            end = Offset(stages.size * currentColumnWidth, totalContentHeight),
                            strokeWidth = 1f
                        )

                        // Draw Time Lines and Labels
                        val headerOffset = 60f * zoom
                        for (i in 0..totalHours) {
                            val y = headerOffset + i * currentHourHeight
                            
                            // Horizontal Line
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(0f, y),
                                end = Offset(totalContentWidth, y),
                                strokeWidth = 1f
                            )

                            // Time Label (drawn on the left or for each column? Let's draw on the left sticky-like or just at x=0)
                            // To make it visible, let's draw it at the very left of the content, 
                            // but since we scroll, maybe we should draw it in a separate layer? 
                            // For this "Canvas only" approach, we draw it here.
                            val timeText = String.format("%02d:00", startTimeHour + i)
                            val textResult = textMeasurer.measure(
                                text = timeText,
                                style = TextStyle(fontSize = (12 * zoom).sp, color = Color.DarkGray)
                            )
                            drawText(
                                textResult,
                                topLeft = Offset(10f, y - textResult.size.height / 2)
                            )
                        }

                        // Draw Events
                        sampleEvents.forEach { event ->
                            val stageIndex = stages.indexOf(event.stage)
                            if (stageIndex != -1) {
                                val startHourDiff = ChronoUnit.MINUTES.between(
                                    java.time.LocalTime.of(startTimeHour, 0),
                                    event.startTime
                                ) / 60f
                                val durationHours = ChronoUnit.MINUTES.between(event.startTime, event.endTime) / 60f
                                
                                val x = stageIndex * currentColumnWidth
                                val y = headerOffset + startHourDiff * currentHourHeight
                                val width = currentColumnWidth
                                val height = durationHours * currentHourHeight

                                // Draw Event Block
                                val padding = 4f * zoom
                                drawRect(
                                    color = event.color,
                                    topLeft = Offset(x + padding, y + padding),
                                    size = Size(width - 2 * padding, height - 2 * padding)
                                )

                                // Draw Artist Name
                                // Clip text to block
                                clipRect(
                                    left = x + padding,
                                    top = y + padding,
                                    right = x + width - padding,
                                    bottom = y + height - padding
                                ) {
                                    val artistText = textMeasurer.measure(
                                        text = event.artistName,
                                        style = TextStyle(fontSize = (14 * zoom).sp, fontWeight = FontWeight.Medium, color = Color.White)
                                    )
                                    drawText(
                                        artistText,
                                        topLeft = Offset(x + padding + 10f, y + padding + 10f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Zoom Indicator
        // "It updates in real time as the user zooms in or out, but values are shown only in 10% increments"
        if (zoom > 1.01f) { // Show only when zoomed in slightly? Or always when zooming? 
            // Requirement: "When zooming, a Zoom Indicator must appear"
            // Let's show it always if zoom > 1.0 or maybe just always for the demo.
            // "values are shown only in 10% increments" -> Round to nearest 10%
            val zoomPercent = (zoom * 100).roundToInt()
            val displayZoom = (zoomPercent / 10) * 10 // Round down/nearest to 10
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .background(Color.Black.copy(alpha = 0.7f), shape = MaterialTheme.shapes.medium)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Zoom: $displayZoom%",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Initial Overlay Hint
        AnimatedVisibility(
            visible = showOverlay,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)), // Darkened/Blurred effect simulation
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pinch to Zoom",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Helper extension for translate removed as it is part of DrawScope

