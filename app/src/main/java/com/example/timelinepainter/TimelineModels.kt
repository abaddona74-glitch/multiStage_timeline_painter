package com.example.timelinepainter

import androidx.compose.ui.graphics.Color
import java.time.LocalTime

enum class Stage(val displayName: String) {
    Main("Main Stage"),
    Rock("Rock Stage"),
    Electro("Electro Stage")
}

data class FestivalEvent(
    val artistName: String,
    val stage: Stage,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val color: Color
)

val sampleEvents = listOf(
    FestivalEvent("DJ A", Stage.Electro, LocalTime.of(12, 0), LocalTime.of(13, 0), Color(0xFFE57373)),
    FestivalEvent("Band X", Stage.Main, LocalTime.of(13, 0), LocalTime.of(14, 30), Color(0xFF81C784)),
    FestivalEvent("RockZ", Stage.Rock, LocalTime.of(14, 0), LocalTime.of(15, 0), Color(0xFF64B5F6)),
    FestivalEvent("Ambient Line", Stage.Electro, LocalTime.of(15, 0), LocalTime.of(16, 30), Color(0xFFBA68C8)),
    FestivalEvent("Florence + The Machine", Stage.Main, LocalTime.of(16, 30), LocalTime.of(18, 0), Color(0xFFFFD54F)),
    FestivalEvent("The National", Stage.Rock, LocalTime.of(17, 0), LocalTime.of(18, 0), Color(0xFF4DB6AC)),
    FestivalEvent("Jamie xx", Stage.Electro, LocalTime.of(18, 0), LocalTime.of(19, 0), Color(0xFFA1887F)),
    FestivalEvent("Tame Impala", Stage.Main, LocalTime.of(19, 0), LocalTime.of(20, 30), Color(0xFF90A4AE)),
    FestivalEvent("Arctic Monkeys", Stage.Rock, LocalTime.of(20, 0), LocalTime.of(21, 30), Color(0xFFFF8A65)),
    FestivalEvent("Radiohead", Stage.Main, LocalTime.of(21, 30), LocalTime.of(23, 0), Color(0xFF7986CB))
)
