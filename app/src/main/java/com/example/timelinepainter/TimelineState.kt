package com.example.timelinepainter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlin.math.max
import kotlin.math.min

class TimelineState(
    initialZoom: Float = 1f,
    initialOffsetX: Float = 0f,
    initialOffsetY: Float = 0f
) {
    var zoom by mutableFloatStateOf(initialZoom)
        private set
    var offsetX by mutableFloatStateOf(initialOffsetX)
        private set
    var offsetY by mutableFloatStateOf(initialOffsetY)
        private set

    val minZoom = 1f
    val maxZoom = 2.5f

    fun transform(panChange: androidx.compose.ui.geometry.Offset, zoomChange: Float, containerSize: androidx.compose.ui.geometry.Size, contentSize: androidx.compose.ui.geometry.Size) {
        val oldZoom = zoom
        val newZoom = (zoom * zoomChange).coerceIn(minZoom, maxZoom)
        
        // Calculate content dimensions with new zoom
        // Note: contentSize passed here should be the UNZOOMED size
        val currentContentWidth = contentSize.width * newZoom
        val currentContentHeight = contentSize.height * newZoom

        // Calculate bounds
        // We want to be able to scroll to the end of content
        // minOffset is usually negative (content larger than container)
        // maxOffset is 0 (content aligned to top-left)
        
        val minOffsetX = min(0f, containerSize.width - currentContentWidth)
        val maxOffsetX = 0f
        val minOffsetY = min(0f, containerSize.height - currentContentHeight)
        val maxOffsetY = 0f

        // Apply Zoom
        zoom = newZoom

        // Apply Pan with clamping
        // If we zoom, we might want to zoom towards a focal point, but for now simple center/pan logic
        var newOffsetX = offsetX + panChange.x
        var newOffsetY = offsetY + panChange.y

        // Adjust offset if zooming changed to keep relative position? 
        // For simplicity, we just clamp the new calculated offset.
        
        // If zooming out, we might need to pull the content back to fill the screen if it's smaller
        if (currentContentWidth < containerSize.width) {
            newOffsetX = 0f // Center or align start? Align start.
        }
        if (currentContentHeight < containerSize.height) {
            newOffsetY = 0f
        }

        offsetX = newOffsetX.coerceIn(minOffsetX, maxOffsetX)
        offsetY = newOffsetY.coerceIn(minOffsetY, maxOffsetY)
    }

    companion object {
        val Saver: Saver<TimelineState, *> = Saver(
            save = { listOf(it.zoom, it.offsetX, it.offsetY) },
            restore = { TimelineState(it[0] as Float, it[1] as Float, it[2] as Float) }
        )
    }
}

@Composable
fun rememberTimelineState(): TimelineState {
    return rememberSaveable(saver = TimelineState.Saver) {
        TimelineState()
    }
}
