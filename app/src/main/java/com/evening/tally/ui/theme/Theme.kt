package com.evening.tally.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.evening.tally.data.preference.LocalThemeIndex
import com.evening.tally.ui.theme.palette.LocalTonalPalettes
import com.evening.tally.ui.theme.palette.TonalPalettes
import com.evening.tally.ui.theme.palette.core.ProvideZcamViewingConditions
import com.evening.tally.ui.theme.palette.dynamic.extractTonalPalettesFromUserWallpaper
import com.evening.tally.ui.theme.palette.dynamicDarkColorScheme
import com.evening.tally.ui.theme.palette.dynamicLightColorScheme

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppTheme(
    useDarkTheme: Boolean,
    wallpaperPalettes: List<TonalPalettes> = extractTonalPalettesFromUserWallpaper(),
    content: @Composable () -> Unit,
) {
    val themeIndex = LocalThemeIndex.current

    val tonalPalettes = wallpaperPalettes[
        if (themeIndex >= wallpaperPalettes.size) {
            when {
                wallpaperPalettes.size == 5 -> 0
                wallpaperPalettes.size > 5 -> 5
                else -> 0
            }
        } else {
            themeIndex
        }
    ]


    ProvideZcamViewingConditions {
        CompositionLocalProvider(
            LocalTonalPalettes provides tonalPalettes.apply { Preparing() },
        ) {
            MaterialTheme(
                colorScheme = if (useDarkTheme) dynamicDarkColorScheme() else dynamicLightColorScheme(),
                typography = Typography,
                shapes = Shapes,
                content = content,
            )
        }
    }
}

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
