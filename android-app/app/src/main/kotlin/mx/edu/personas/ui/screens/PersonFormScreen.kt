package mx.edu.personas.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.personas.data.model.CapturedLocation
import mx.edu.personas.data.model.PersonDraft
import mx.edu.personas.data.repository.PersonRepository
import mx.edu.personas.utils.LocationProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import kotlin.math.roundToInt

private val maritalStatusOptions = listOf("Soltero/a", "Casado/a", "Divorciado/a", "Viudo/a", "Unión libre", "Separado/a")
private val educationOptions = listOf(
    "Sin estudios", "Primaria", "Secundaria", "Bachillerato",
    "Técnico Superior Universitario", "Licenciatura o Ingeniería", "Maestría", "Doctorado",
)
private val hobbyOptions = listOf("Leer", "Escuchar música", "Videojuegos", "Deportes", "Viajar")
private val seasonOptions = listOf("Primavera", "Verano", "Otoño", "Invierno")
private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "MX"))

private data class GenderOption(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val genderOptions = listOf(
    GenderOption("Masculino", Icons.Filled.Male),
    GenderOption("Femenino", Icons.Filled.Female),
    GenderOption("Otro", Icons.Filled.Person),
    GenderOption("Prefiero no decirlo", Icons.Filled.VisibilityOff),
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PersonFormScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var names by remember { mutableStateOf("") }
    var firstSurname by remember { mutableStateOf("") }
    var secondSurname by remember { mutableStateOf("") }
    var selectedHobbies by remember { mutableStateOf(setOf<String>()) }
    var maritalStatus by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(18f) }
    var birthDate by remember { mutableStateOf<LocalDate?>(null) }
    var preferredSeason by remember { mutableStateOf("") }
    var location by remember { mutableStateOf<CapturedLocation?>(null) }
    var locationMessage by remember { mutableStateOf("Aún no se obtiene la ubicación.") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoPreview by remember { mutableStateOf<Bitmap?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var isActive by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var maritalMenuExpanded by remember { mutableStateOf(false) }

    // Photo Picker entrega acceso solo a la imagen elegida; la app no solicita lectura completa de la galería.
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) photoUri = uri
    }
    // TakePicture escribe la imagen completa en una URI temporal de FileProvider, sin exponer rutas privadas del dispositivo.
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) photoUri = pendingCameraUri
        else pendingCameraUri = null
    }

    val loadLocation: () -> Unit = {
        scope.launch {
            isLoadingLocation = true
            locationMessage = "Obteniendo coordenadas y localidad…"
            try {
                location = LocationProvider.getCurrent(context)
                locationMessage = if (location?.altitudeMetersAboveSeaLevel == null) {
                    "Coordenadas obtenidas. Este dispositivo no proporciona altitud MSL válida; no se inventó un valor."
                } else {
                    "Ubicación obtenida correctamente."
                }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                location = null
                locationMessage = error.message ?: "No se pudo obtener la ubicación. Inténtalo nuevamente."
            } finally {
                isLoadingLocation = false
            }
        }
    }
    val locationPermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        if (results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            loadLocation()
        } else {
            locationMessage = "Permiso denegado. Se necesita ubicación para completar el registro."
        }
    }

    LaunchedEffect(photoUri) {
        photoPreview = photoUri?.let { loadPreview(context, it) }
    }

    // Scaffold aloja el Snackbar para informar éxito o error sin borrar los valores controlados del formulario.
    // Compose vuelve a dibujar las partes afectadas cuando cambia un valor de estado recordado.
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Registro de persona", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text("Completa todos los campos para guardar el registro.", style = MaterialTheme.typography.bodyMedium)

            SectionTitle("Fotografía")
            if (photoPreview != null) {
                Image(
                    bitmap = photoPreview!!.asImageBitmap(),
                    contentDescription = "Vista previa de la fotografía seleccionada",
                    contentScale = ContentScale.Crop,
                    // CircleShape recorta la vista sin modificar los bytes que después se suben.
                    modifier = Modifier.size(132.dp).align(Alignment.CenterHorizontally).clip(CircleShape),
                )
            } else {
                Box(
                    modifier = Modifier.size(132.dp).align(Alignment.CenterHorizontally).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Face, contentDescription = "Sin fotografía", modifier = Modifier.size(54.dp))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                    Text(" Galería")
                }
                OutlinedButton(
                    onClick = {
                        val captureFile = File(context.cacheDir, "captures/${UUID.randomUUID()}.jpg")
                        captureFile.parentFile?.mkdirs()
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", captureFile)
                        pendingCameraUri = uri
                        camera.launch(uri)
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                    Text(" Cámara")
                }
            }

            SectionTitle("Datos personales")
            // Los TextField son controlados por el estado Compose; el texto mostrado siempre coincide con el valor guardado.
            OutlinedTextField(names, { names = it }, label = { Text("Nombres") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(firstSurname, { firstSurname = it }, label = { Text("Primer apellido") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(secondSurname, { secondSurname = it }, label = { Text("Segundo apellido") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Text("Hobbies", style = MaterialTheme.typography.titleMedium)
            // Checkbox permite elegir varios hobbies; cada cambio actualiza el conjunto sin borrar las otras selecciones.
            hobbyOptions.forEach { hobby ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {
                        selectedHobbies = if (hobby in selectedHobbies) selectedHobbies - hobby else selectedHobbies + hobby
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(checked = hobby in selectedHobbies, onCheckedChange = { checked ->
                        selectedHobbies = if (checked) selectedHobbies + hobby else selectedHobbies - hobby
                    })
                    Text(hobby)
                }
            }

            Text("Estado civil", style = MaterialTheme.typography.titleMedium)
            // ExposedDropdownMenuBox vincula el campo de lectura y su menú; al elegir, se conserva una sola opción visible.
            ExposedDropdownMenuBox(
                expanded = maritalMenuExpanded,
                onExpandedChange = { maritalMenuExpanded = it },
            ) {
                OutlinedTextField(
                    value = maritalStatus,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecciona una opción") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = maritalMenuExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = maritalMenuExpanded,
                    onDismissRequest = { maritalMenuExpanded = false },
                ) {
                    maritalStatusOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { maritalStatus = option; maritalMenuExpanded = false },
                        )
                    }
                }
            }

            Text("Género", style = MaterialTheme.typography.titleMedium)
            // El icono señala la opción y el texto/semántica la nombra; así la selección no depende solo del color.
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                genderOptions.forEach { option ->
                    val selected = gender == option.label
                    OutlinedButton(
                        onClick = { gender = option.label },
                        modifier = Modifier.semantics { contentDescription = "${option.label}${if (selected) ", seleccionado" else ""}" },
                    ) {
                        Icon(option.icon, contentDescription = null)
                        Text(" ${option.label}")
                    }
                }
            }

            Text("Máximo grado de estudios", style = MaterialTheme.typography.titleMedium)
            // RadioButton hace exclusiva la elección; tocar una fila reemplaza la opción anterior en el estado.
            educationOptions.forEach { option ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(role = Role.RadioButton) { education = option },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = education == option, onClick = { education = option })
                    Text(option)
                }
            }

            Text("Edad: ${age.roundToInt()} años", style = MaterialTheme.typography.titleMedium)
            // Slider limita la edad a enteros entre cero y ciento veinte; el texto comunica el valor actual.
            Slider(value = age, onValueChange = { age = it.roundToInt().toFloat() }, valueRange = 0f..120f, steps = 119)

            // DatePicker entrega milisegundos UTC; se convierten a fecha sin desplazar el día y BirthDateSelectableDates bloquea fechas futuras.
            Text("Fecha de nacimiento", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = birthDate?.format(dateFormatter) ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Selecciona una fecha") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = "Abrir calendario de fecha de nacimiento")
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
            )
            val birthDateError = birthDate?.let { selectedDate ->
                val calculatedAge = Period.between(selectedDate, LocalDate.now()).years
                if (selectedDate.isAfter(LocalDate.now())) "La fecha de nacimiento no puede estar en el futuro."
                else if (calculatedAge != age.roundToInt()) {
                    "La edad calculada es $calculatedAge años; no coincide con la edad indicada (${age.roundToInt()})."
                } else null
            }
            if (birthDateError != null) Text(birthDateError, color = MaterialTheme.colorScheme.error)

            Text("Estación preferida", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // FilterChip refleja la elección exclusiva: comparar con preferredSeason deja solo una estación seleccionada.
            seasonOptions.forEach { season ->
                    FilterChip(
                        selected = preferredSeason == season,
                        onClick = { preferredSeason = season },
                        label = { Text(season) },
                    )
                }
            }

            SectionTitle("Ubicación automática")
            Text(locationMessage, style = MaterialTheme.typography.bodyMedium)
            // Se piden juntos fino y aproximado para respetar la elección de Android 12; solo al tocar el botón y sin segundo plano.
            OutlinedButton(
                onClick = {
                    val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) loadLocation()
                    else locationPermissions.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                },
                enabled = !isLoadingLocation,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isLoadingLocation) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Icon(Icons.Filled.LocationOn, contentDescription = null)
                Text(if (isLoadingLocation) " Obteniendo ubicación…" else " Reintentar ubicación")
            }
            ReadOnlyLocationField("Latitud", location?.latitude?.formatCoordinate())
            ReadOnlyLocationField("Longitud", location?.longitude?.formatCoordinate())
            ReadOnlyLocationField("Altitud sobre el nivel del mar", location?.altitudeMetersAboveSeaLevel?.formatCoordinate())
            ReadOnlyLocationField("Localidad", location?.locality)
            if (location?.altitudeMetersAboveSeaLevel == null) {
                Text(
                    "La altitud MSL solo se acepta cuando Android y el proveedor la informan; no se usa la altitud elipsoidal del GPS.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Text("Estado del registro", style = MaterialTheme.typography.titleMedium)
            listOf("Activo" to true, "Inactivo" to false).forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(role = Role.RadioButton) { isActive = value },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = isActive == value, onClick = { isActive = value })
                    Text(label)
                }
            }
            Text("La fecha y hora del registro se asignarán automáticamente al guardar.", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    val errorMessage = validateForm(
                        names, firstSurname, secondSurname, selectedHobbies, maritalStatus,
                        gender, education, birthDate, age.roundToInt(), preferredSeason, photoUri, location,
                    )
                    if (errorMessage != null) {
                        scope.launch { snackbarHostState.showSnackbar(errorMessage) }
                        return@Button
                    }
                    val selectedPhoto = photoUri ?: return@Button
                    val capturedLocation = location ?: return@Button
                    val selectedBirthDate = birthDate ?: return@Button
                    scope.launch {
                        isSaving = true
                        try {
                            // La carga de la foto y la inserción se ejecutan fuera del hilo de interfaz.
                            val photoBytes = compressPhoto(context, selectedPhoto)
                            val draft = PersonDraft(
                                names = names.trim(),
                                firstSurname = firstSurname.trim(),
                                secondSurname = secondSurname.trim(),
                                hobbies = selectedHobbies.toList(),
                                maritalStatus = maritalStatus,
                                gender = gender,
                                education = education,
                                age = age.roundToInt(),
                                birthDate = selectedBirthDate.toString(),
                                preferredSeason = preferredSeason,
                                latitude = capturedLocation.latitude,
                                longitude = capturedLocation.longitude,
                                altitude = requireNotNull(capturedLocation.altitudeMetersAboveSeaLevel),
                                locality = capturedLocation.locality,
                                isActive = isActive,
                            )
                            PersonRepository.save(draft, photoBytes)
                            snackbarHostState.showSnackbar("Registro guardado correctamente.")
                        } catch (error: Exception) {
                            if (error is CancellationException) throw error
                            snackbarHostState.showSnackbar(
                                "No se pudo guardar la información. Inténtalo nuevamente. ${error.message.orEmpty()}".trim(),
                            )
                        } finally {
                            isSaving = false
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Text(if (isSaving) " Guardando…" else "GUARDAR REGISTRO")
            }
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = birthDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
            selectableDates = BirthDateSelectableDates,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    birthDate = pickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } },
        ) {
            DatePicker(state = pickerState, showModeToggle = false)
        }
    }
}

private object BirthDateSelectableDates : androidx.compose.material3.SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        !Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate().isAfter(LocalDate.now())

    override fun isSelectableYear(year: Int): Boolean = year <= LocalDate.now().year
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
}

@Composable
private fun ReadOnlyLocationField(label: String, value: String?) {
    OutlinedTextField(
        value = value.orEmpty(),
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}

private fun Double.formatCoordinate(): String = String.format(Locale.US, "%.6f", this)

private fun validateForm(
    names: String,
    firstSurname: String,
    secondSurname: String,
    hobbies: Set<String>,
    maritalStatus: String,
    gender: String,
    education: String,
    birthDate: LocalDate?,
    age: Int,
    preferredSeason: String,
    photoUri: Uri?,
    location: CapturedLocation?,
): String? {
    if (names.isBlank() || firstSurname.isBlank() || secondSurname.isBlank()) return "Completa nombres y ambos apellidos."
    if (hobbies.isEmpty()) return "Selecciona al menos un hobby."
    if (maritalStatus.isBlank()) return "Selecciona el estado civil."
    if (gender.isBlank()) return "Selecciona el género."
    if (education.isBlank()) return "Selecciona el grado de estudios."
    if (birthDate == null) return "Selecciona la fecha de nacimiento."
    if (birthDate.isAfter(LocalDate.now())) return "La fecha de nacimiento no puede estar en el futuro."
    val actualAge = Period.between(birthDate, LocalDate.now()).years
    if (actualAge != age) return "La edad calculada es $actualAge años; no coincide con la edad indicada ($age)."
    if (preferredSeason.isBlank()) return "Selecciona la estación preferida."
    if (photoUri == null) return "Selecciona o toma una fotografía."
    if (location == null) return "Obtén la ubicación antes de guardar."
    if (location.altitudeMetersAboveSeaLevel == null) return "No hay una altitud MSL válida disponible en este dispositivo."
    if (location.locality.isBlank()) return "No se pudo determinar la localidad. Reintenta la ubicación."
    return null
}

private suspend fun loadPreview(context: Context, uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
    runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sampleSize = 1
        while (bounds.outWidth / sampleSize > 900 || bounds.outHeight / sampleSize > 900) sampleSize *= 2
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    }.getOrNull()
}

private suspend fun compressPhoto(context: Context, uri: Uri): ByteArray = withContext(Dispatchers.IO) {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        ?: error("No se pudo leer la fotografía seleccionada.")
    var sampleSize = 1
    while (bounds.outWidth / sampleSize > 1600 || bounds.outHeight / sampleSize > 1600) sampleSize *= 2
    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    val bitmap = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
        ?: error("No se pudo preparar la fotografía para subirla.")
    ByteArrayOutputStream().use { output ->
        try {
            check(bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)) {
                "No se pudo convertir la fotografía a JPEG."
            }
            output.toByteArray()
        } finally {
            bitmap.recycle()
        }
    }
}
