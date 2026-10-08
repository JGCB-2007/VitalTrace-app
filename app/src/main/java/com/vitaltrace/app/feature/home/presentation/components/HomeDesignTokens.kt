package com.vitaltrace.app.feature.home.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

internal val HomeNavy: Color @Composable get() = MaterialTheme.colorScheme.primary
internal val HomeTeal: Color @Composable get() = MaterialTheme.colorScheme.secondary
internal val HomeMint: Color @Composable get() = MaterialTheme.colorScheme.tertiary
internal val HomeMintContainer: Color @Composable get() = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.22f)
internal val HomeWarmBackground: Color @Composable get() = MaterialTheme.colorScheme.background
internal val HomeSupportingText: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
internal val HomeInactive: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
internal val HomeReviewChip = Color(0xFF176486)
internal val HomeAlertBar = Color(0xFFE4A947)

val HomeBackground: Color @Composable
    get() = HomeWarmBackground
