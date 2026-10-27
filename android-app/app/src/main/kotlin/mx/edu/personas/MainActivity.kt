package mx.edu.personas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import mx.edu.personas.ui.screens.PersonFormScreen
import mx.edu.personas.ui.theme.FormularioDePersonasTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      // Compose describe la pantalla con funciones y vuelve a dibujarla cuando cambia el estado observado.
      FormularioDePersonasTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background,
        ) {
          PersonFormScreen()
        }
      }
    }
  }
}
