package mx.edu.personas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    secondary = TealSecondary,
    background = LightBackground,
    surface = WhiteSurface,
    error = ErrorRed,
)

@Composable
fun FormularioDePersonasTheme(
    content: @Composable () -> Unit
) {
    // MaterialTheme distribuye los colores y estilos tipográficos de forma consistente a todos los componentes.
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
