package com.yatrisetu.app.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yatrisetu.app.presentation.theme.YatriElevation
import com.yatrisetu.app.presentation.theme.YatriRadius
import com.yatrisetu.app.presentation.theme.YatriShapes

/**
 * YatriCard
 * Reusable elevated content card featuring soft elevation, subtle borders,
 * and Himalayan aesthetic styling.
 */
@Composable
fun YatriCard(
    modifier: Modifier = Modifier,
    shape: Shape = YatriShapes.medium,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = YatriElevation.Subtle,
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier
                        .clip(shape)
                        .clickable(onClick = onClick)
                } else Modifier
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = border
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
