package com.example.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

// Idle State CompositionLocals for NeoBrutalist animations
val LocalIdleState = staticCompositionLocalOf { false }
val LocalIdleBobbingOffset = staticCompositionLocalOf { 0.dp }
val LocalIdleWiggleRotation = staticCompositionLocalOf { 0f }
val LocalIdleShadowMultiplier = staticCompositionLocalOf { 1f }
