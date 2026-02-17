package com.ritesh.cashiro.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.core.view.WindowCompat
import com.ritesh.cashiro.data.preferences.AppFont
import com.ritesh.cashiro.data.preferences.ThemeStyle
import com.ritesh.cashiro.data.preferences.AccentColor


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CashiroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeStyle: ThemeStyle = ThemeStyle.DYNAMIC,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    isAmoledMode: Boolean = false,
    accentColor: AccentColor = AccentColor.BLUE,
    appFont: AppFont = AppFont.SYSTEM,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var colorScheme =
        when {
            themeStyle == ThemeStyle.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (darkTheme) dynamicDarkColorScheme(context)
                else dynamicLightColorScheme(context)
            }
            themeStyle == ThemeStyle.DEFAULT -> {
                if (darkTheme) getCustomDarkColorScheme(accentColor)
                else getCustomLightColorScheme(accentColor)
            }
            // Fallback
             dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (darkTheme) dynamicDarkColorScheme(context)
                else dynamicLightColorScheme(context)
            }
            darkTheme -> darkColorScheme()
            else -> lightColorScheme()
        }

    // Apply Amoled Black if enabled in Dark Mode
    if (darkTheme && isAmoledMode) {
        colorScheme = colorScheme.copy(
            background = Color.Black,
            surface = Color.Black,
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        SideEffect {
            // Enable edge-to-edge display
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Enforce transparent system bars for edge-to-edge on O+

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }

            // Control whether status bar icons should be dark or light
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    val fontFamily = when (appFont) {
        AppFont.SYSTEM -> FontFamily.Default
        AppFont.SN_PRO -> SNProFontFamily
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = getTypography(fontFamily = fontFamily),
        shapes = Shapes,
        content = content
    )
}


fun getCustomLightColorScheme(accent: AccentColor): ColorScheme {
    val primaryColor = when (accent) {
        AccentColor.ROSEWATER -> Latte_Rosewater
        AccentColor.FLAMINGO -> Latte_Flamingo
        AccentColor.PINK -> Latte_Pink
        AccentColor.MAUVE -> Latte_Mauve
        AccentColor.RED -> Latte_Red
        AccentColor.PEACH -> Latte_Peach
        AccentColor.YELLOW -> Latte_Yellow
        AccentColor.GREEN -> Latte_Green
        AccentColor.TEAL -> Latte_Teal
        AccentColor.SAPPHIRE -> Latte_Sapphire
        AccentColor.BLUE -> Latte_Blue
        AccentColor.LAVENDER -> Latte_Lavender
    }
    val secondaryColor = when (accent) {
        AccentColor.ROSEWATER -> Latte_Rosewater_secondary
        AccentColor.FLAMINGO -> Latte_Flamingo_secondary
        AccentColor.PINK -> Latte_Pink_secondary
        AccentColor.MAUVE -> Latte_Mauve_secondary
        AccentColor.RED -> Latte_Red_secondary
        AccentColor.PEACH -> Latte_Peach_secondary
        AccentColor.YELLOW -> Latte_Yellow_secondary
        AccentColor.GREEN -> Latte_Green_secondary
        AccentColor.TEAL -> Latte_Teal_secondary
        AccentColor.SAPPHIRE -> Latte_Sapphire_secondary
        AccentColor.BLUE -> Latte_Blue_secondary
        AccentColor.LAVENDER -> Latte_Lavender_secondary
    }

    val tertiaryColor = when (accent) {
        AccentColor.ROSEWATER -> Latte_Rosewater_tertiary
        AccentColor.FLAMINGO -> Latte_Flamingo_tertiary
        AccentColor.PINK -> Latte_Pink_tertiary
        AccentColor.MAUVE -> Latte_Mauve_tertiary
        AccentColor.RED -> Latte_Red_tertiary
        AccentColor.PEACH -> Latte_Peach_tertiary
        AccentColor.YELLOW -> Latte_Yellow_tertiary
        AccentColor.GREEN -> Latte_Green_tertiary
        AccentColor.TEAL -> Latte_Teal_tertiary
        AccentColor.SAPPHIRE -> Latte_Sapphire_tertiary
        AccentColor.BLUE -> Latte_Blue_tertiary
        AccentColor.LAVENDER -> Latte_Lavender_tertiary
    }

    return lightColorScheme(
        primary = primaryColor,
        onPrimary = Color(0xFFffffff),
        primaryContainer = primaryColor,
        onPrimaryContainer = Color(0xFFffffff),
        inversePrimary = Color(0xFF000000),
        secondary = secondaryColor,
        onSecondary = Color(0xFFffffff),
        secondaryContainer = secondaryColor,
        onSecondaryContainer = Color(0xFFffffff),
        tertiary = tertiaryColor,
        onTertiary = Color(0xFFffffff),
        tertiaryContainer = tertiaryColor,
        onTertiaryContainer = Color(0xFFffffff),
        background = Color(0xFFe2e2e9),
        onBackground = Color(0xFF1a1b20),
        surface = Color(0xFFE5E5EA),
        onSurface = Color(0xFF1a1b20),
        surfaceVariant = Color(0xFFc4c6d0),
        onSurfaceVariant = Color(0xFF44474f),
        inverseSurface = Color(0xFF2f3036),
        inverseOnSurface = Color(0xFFf0f0f7),
        error = Latte_Red,
        onError = Color(0xFFffffff),
        errorContainer = Latte_Red,
        onErrorContainer = Color(0xFFffffff),
        surfaceBright = Color(0xFFE8E9EC),
        surfaceDim = Color(0xFFd9d9e0),
        surfaceContainer = Color(0xFFf9f9ff),
        surfaceContainerHigh = Color(0xFFe8e7ee),
        surfaceContainerHighest = Color(0xFFe2e2e9),
        surfaceContainerLow = Color(0xFFffffff),
        surfaceContainerLowest = Color(0xFFf9f9ff)
    )
}

fun getCustomDarkColorScheme(accent: AccentColor): ColorScheme {
    val primaryColor = when (accent) {
        AccentColor.ROSEWATER -> Macchiato_Rosewater_dim
        AccentColor.FLAMINGO -> Macchiato_Flamingo_dim
        AccentColor.PINK -> Macchiato_Pink_dim
        AccentColor.MAUVE -> Macchiato_Mauve_dim
        AccentColor.RED -> Macchiato_Red_dim
        AccentColor.PEACH -> Macchiato_Peach_dim
        AccentColor.YELLOW -> Macchiato_Yellow_dim
        AccentColor.GREEN -> Macchiato_Green_dim
        AccentColor.TEAL -> Macchiato_Teal_dim
        AccentColor.SAPPHIRE -> Macchiato_Sapphire_dim
        AccentColor.BLUE -> Macchiato_Blue_dim
        AccentColor.LAVENDER -> Macchiato_Lavender_dim
    }

    val secondaryColor = when (accent) {
        AccentColor.ROSEWATER -> Macchiato_Rosewater_dim_secondary
        AccentColor.FLAMINGO -> Macchiato_Flamingo_dim_secondary
        AccentColor.PINK -> Macchiato_Pink_dim_secondary
        AccentColor.MAUVE -> Macchiato_Mauve_dim_secondary
        AccentColor.RED -> Macchiato_Red_dim_secondary
        AccentColor.PEACH -> Macchiato_Peach_dim_secondary
        AccentColor.YELLOW -> Macchiato_Yellow_dim_secondary
        AccentColor.GREEN -> Macchiato_Green_dim_secondary
        AccentColor.TEAL -> Macchiato_Teal_dim_secondary
        AccentColor.SAPPHIRE -> Macchiato_Sapphire_dim_secondary
        AccentColor.BLUE -> Macchiato_Blue_dim_secondary
        AccentColor.LAVENDER -> Macchiato_Lavender_dim_secondary
    }

    val tertiaryColor = when (accent) {
        AccentColor.ROSEWATER -> Macchiato_Rosewater_dim_tertiary
        AccentColor.FLAMINGO -> Macchiato_Flamingo_dim_tertiary
        AccentColor.PINK -> Macchiato_Pink_dim_tertiary
        AccentColor.MAUVE -> Macchiato_Mauve_dim_tertiary
        AccentColor.RED -> Macchiato_Red_dim_tertiary
        AccentColor.PEACH -> Macchiato_Peach_dim_tertiary
        AccentColor.YELLOW -> Macchiato_Yellow_dim_tertiary
        AccentColor.GREEN -> Macchiato_Green_dim_tertiary
        AccentColor.TEAL -> Macchiato_Teal_dim_tertiary
        AccentColor.SAPPHIRE -> Macchiato_Sapphire_dim_tertiary
        AccentColor.BLUE -> Macchiato_Blue_dim_tertiary
        AccentColor.LAVENDER -> Macchiato_Lavender_dim_tertiary
    }
    return darkColorScheme(
        primary = primaryColor,
        onPrimary = Color(0xFFffffff),
        primaryContainer = primaryColor,
        onPrimaryContainer = Color(0xFFffffff),
        inversePrimary = Color(0xFFffffff),
        secondary = secondaryColor,
        onSecondary = Color(0xFFffffff),
        secondaryContainer = secondaryColor,
        onSecondaryContainer = Color(0xFFffffff),
        tertiary = tertiaryColor,
        onTertiary = Color(0xFFffffff),
        tertiaryContainer = tertiaryColor,
        onTertiaryContainer = Color(0xFFffffff),
        background = Color(0xFF111318),
        onBackground = Color(0xFFe2e2e9),
        surface = Color(0xFF111318),
        onSurface = Color(0xFFe2e2e9),
        surfaceVariant = Color(0xFF1e1f25),
        onSurfaceVariant = Color(0xFFc4c6d0),
        inverseSurface = Color(0xFFe2e2e9),
        inverseOnSurface = Color(0xFF2f3036),
        error = Macchiato_Red_dim,
        onError = Color(0xFFffffff),
        errorContainer = Macchiato_Red_dim,
        onErrorContainer = Color(0xFFffffff),
        surfaceBright = Color(0xFF37393e),
        surfaceDim = Color(0xFF0c0e13),
        surfaceContainer = Color(0xFF1e1f25),
        surfaceContainerHigh = Color(0xFF282a2f),
        surfaceContainerHighest = Color(0xFF33353a),
        surfaceContainerLow = Color(0xFF1e1f25),
        surfaceContainerLowest = Color(0xFF1a1b20)
    )
}
