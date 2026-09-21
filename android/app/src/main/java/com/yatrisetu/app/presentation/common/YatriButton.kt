package com.yatrisetu.app.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yatrisetu.app.presentation.theme.YatriColors
import com.yatrisetu.app.presentation.theme.YatriRadius

enum class YatriButtonVariant {
    PRIMARY,
    SECONDARY,
    CRITICAL
}

@Composable
fun YatriButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: YatriButtonVariant = YatriButtonVariant.PRIMARY,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(YatriRadius.Button)

    when (variant) {
        YatriButtonVariant.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier = modifier.height(48.dp),
                enabled = enabled,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = YatriColors.BrandAmberPrimary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                content = content
            )
        }
        YatriButtonVariant.SECONDARY -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.height(48.dp),
                enabled = enabled,
                shape = shape,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                content = content
            )
        }
        YatriButtonVariant.CRITICAL -> {
            Button(
                onClick = onClick,
                modifier = modifier.height(48.dp),
                enabled = enabled,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = YatriColors.SosRedPrimary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                content = content
            )
        }
    }
}
