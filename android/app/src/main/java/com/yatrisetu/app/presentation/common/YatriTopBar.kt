package com.yatrisetu.app.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatrisetu.app.presentation.theme.YatriColors
import com.yatrisetu.app.presentation.theme.YatriTypographyTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YatriTopBar(
    title: String? = null,
    canNavigateBack: Boolean = false,
    onNavigateBack: (() -> Unit)? = null,
    showSosAction: Boolean = true,
    onSosClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        navigationIcon = {
            if (canNavigateBack && onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        title = {
            if (title != null) {
                Text(
                    text = title,
                    style = YatriTypographyTokens.TitleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                // Official Brand Lockup
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Yatri",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Setu",
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Normal,
                                fontSize = 21.sp,
                                color = YatriColors.BrandAmberSecondary
                            )
                        }
                        Text(
                            text = "HIMALAYAN FLOW INTELLIGENCE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        },
        actions = {
            if (showSosAction) {
                SosBeaconButton(
                    onClick = onSosClick,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    )
}
