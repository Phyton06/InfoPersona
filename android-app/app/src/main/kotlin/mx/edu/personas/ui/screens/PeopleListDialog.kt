package mx.edu.personas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import mx.edu.personas.data.model.PersonRecord
import mx.edu.personas.data.repository.PersonRepository
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val detailDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("es-MX"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleListDialog(onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    var people by remember { mutableStateOf<List<PersonRecord>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedPerson by remember { mutableStateOf<PersonRecord?>(null) }

    val reload: () -> Unit = {
        scope.launch {
            loading = true
            errorMessage = null
            try {
                people = PersonRepository.getAll()
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                errorMessage = error.message ?: "No se pudo consultar la lista."
            } finally {
                loading = false
            }
        }
    }
    // La vista se crea al abrirla, así que la consulta se refresca cada vez que se regresa a la lista.
    LaunchedEffect(Unit) { reload() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Personas registradas") },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Filled.Close, contentDescription = "Volver al formulario")
                            }
                        },
                        actions = {
                            IconButton(onClick = reload, enabled = !loading) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Actualizar lista")
                            }
                        },
                    )
                },
            ) { contentPadding ->
                when {
                    loading -> Box(
                        modifier = Modifier.fillMaxSize().padding(contentPadding),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator() }

                    errorMessage != null -> Column(
                        modifier = Modifier.fillMaxSize().padding(contentPadding).padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("No se pudo cargar la lista. ${errorMessage.orEmpty()}")
                        Button(onClick = reload, modifier = Modifier.padding(top = 16.dp)) { Text("Reintentar") }
                    }

                    people.isEmpty() -> Box(
                        modifier = Modifier.fillMaxSize().padding(contentPadding),
                        contentAlignment = Alignment.Center,
                    ) { Text("No hay personas registradas.") }

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(contentPadding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // LazyColumn crea solo las tarjetas visibles, incluso cuando hay muchos registros.
                        items(people, key = { it.id }) { person ->
                            PersonCard(person = person, onDetails = { selectedPerson = person })
                        }
                    }
                }
            }
        }
    }

    selectedPerson?.let { person ->
        PersonDetailsDialog(person = person, onDismiss = { selectedPerson = null })
    }
}

@Composable
private fun PersonCard(person: PersonRecord, onDetails: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Coil descarga la URL pública y recorta la imagen dentro de CircleShape sin bloquear la interfaz.
            AsyncImage(
                model = person.photoUrl,
                contentDescription = "Fotografía de ${person.nombres} ${person.firstSurname}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(72.dp).clip(CircleShape),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${person.nombres} ${person.firstSurname} ${person.secondSurname}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(if (person.estado) "Activo" else "Inactivo", style = MaterialTheme.typography.bodyMedium)
            }
            TextButton(onClick = onDetails) { Text("DETALLES") }
        }
    }
}

@Composable
private fun PersonDetailsDialog(person: PersonRecord, onDismiss: () -> Unit) {
    val registrationDate = formatRegistrationPart(person.registeredAt, "dd/MM/yyyy")
    val registrationTime = formatRegistrationPart(person.registeredAt, "HH:mm:ss")
    val birthDate = runCatching { LocalDate.parse(person.birthDate).format(detailDateFormatter) }
        .getOrDefault(person.birthDate)

    // El diálogo solo presenta los campos; no ofrece acciones que alteren el registro almacenado.
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalles de ${person.nombres}") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AsyncImage(
                    model = person.photoUrl,
                    contentDescription = "Fotografía de ${person.nombres} ${person.firstSurname}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(16.dp)),
                )
                DetailRow("ID", person.id)
                DetailRow("URL de la fotografía", person.photoUrl)
                DetailRow("Nombres", person.nombres)
                DetailRow("Primer apellido", person.firstSurname)
                DetailRow("Segundo apellido", person.secondSurname)
                DetailRow("Hobbies", person.hobbies.joinToString(", "))
                DetailRow("Estado civil", person.maritalStatus)
                DetailRow("Género", person.genero)
                DetailRow("Máximo grado de estudios", person.education)
                DetailRow("Edad", "${person.edad} años")
                DetailRow("Fecha de nacimiento", birthDate)
                DetailRow("Estación preferida", person.preferredSeason)
                DetailRow("Latitud", person.latitud.coordinateText())
                DetailRow("Longitud", person.longitud.coordinateText())
                DetailRow("Altitud sobre el nivel del mar", "${person.altitud.coordinateText()} m")
                DetailRow("Localidad", person.localidad)
                DetailRow("Fecha del registro", registrationDate)
                DetailRow("Hora del registro", registrationTime)
                DetailRow("Estado", if (person.estado) "Activo" else "Inactivo")
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun Double.coordinateText(): String = String.format(Locale.US, "%.6f", this)

private fun formatRegistrationPart(timestamp: String, pattern: String): String = runCatching {
    OffsetDateTime.parse(timestamp)
        .atZoneSameInstant(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern(pattern, Locale.forLanguageTag("es-MX")))
}.getOrDefault(timestamp)
