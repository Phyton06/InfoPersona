package mx.edu.personas.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import mx.edu.personas.R

// La familia declara Noto Sans para que los textos hereden la misma tipografía en todos los componentes.
private val NotoSans = FontFamily(
    Font(R.font.noto_sans, FontWeight.Normal),
    Font(R.font.noto_sans, FontWeight.Medium),
    Font(R.font.noto_sans, FontWeight.Bold),
)

val Typography = Typography(
    bodyLarge = TextStyle(fontFamily = NotoSans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = NotoSans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    titleLarge = TextStyle(fontFamily = NotoSans, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = NotoSans, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    labelLarge = TextStyle(fontFamily = NotoSans, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
)
