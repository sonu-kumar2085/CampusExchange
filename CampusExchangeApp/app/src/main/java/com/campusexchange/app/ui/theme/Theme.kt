package com.campusexchange.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary              = Primary,
    onPrimary            = SurfaceWhite,
    primaryContainer     = SurfaceLight,
    onPrimaryContainer   = Primary,
    secondary            = Accent,
    onSecondary          = SurfaceWhite,
    secondaryContainer   = SurfaceLight,
    onSecondaryContainer = Primary,
    tertiary             = CoinGold,
    onTertiary           = SurfaceWhite,
    background           = Background,
    onBackground         = Primary,
    surface              = SurfaceWhite,
    onSurface            = Primary,
    surfaceVariant       = SurfaceLight,
    onSurfaceVariant     = Accent,
    outline              = Divider,
    error                = NegativeRed,
    onError              = SurfaceWhite
)

@Composable
fun CampusExchangeTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Background.toArgb()
            window.navigationBarColor = SurfaceWhite.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars    = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        shapes      = Shapes,
        content     = content
    )
}
