package ee.ut.cs.orienteering.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = AccentDark,

    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = AccentDark,
    secondaryContainer = SecondaryDark.copy(alpha = 0.5f),

    outline = TextOnDark,

    onPrimary = OnPrimaryDark,
    onSecondary = TextOnDark,
    onSurface = TextOnDark,
    onBackground = TextOnDark,
    onSurfaceVariant = TextOnDark,
    onSecondaryContainer = TextOnDark,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    secondary = SecondaryLight,
    tertiary = AccentLight,

    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = AccentLight,
    secondaryContainer = SecondaryLight.copy(alpha = 0.4f),

    onPrimary = TextOnLight,
    onSecondary = TextOnLight,
    onSurface = TextOnLight,
    onSurfaceVariant = TextOnLight,
    onBackground = TextOnLight
)

/**
 * App-wide Material 3 theme wrapper for the Orienteering application.
 *
 * Behavior:
 * - Chooses between light and dark color schemes based on [darkTheme] or the system setting.
 * - Optionally enables dynamic colors on Android 12+ when [dynamicColor] is true.
 * - Applies the app's [Typography] and the resolved [colorScheme] to all nested composables.
 *
 * @param darkTheme Whether to use the dark color scheme. Defaults to the system setting.
 * @param dynamicColor Whether to enable Material You dynamic colors (Android 12+ only).
 * @param content The composable content that will be styled by this theme.
 */
@Composable
fun OrienteeringTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}