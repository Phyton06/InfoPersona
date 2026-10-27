package mx.edu.personas.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Datos del formulario antes de que la carga de la foto produzca una URL pública. */
data class PersonDraft(
    val names: String,
    val firstSurname: String,
    val secondSurname: String,
    val hobbies: List<String>,
    val maritalStatus: String,
    val gender: String,
    val education: String,
    val age: Int,
    val birthDate: String,
    val preferredSeason: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val locality: String,
    val isActive: Boolean,
)

/** Datos que se envían a personas; el identificador y la fecha los genera PostgreSQL. */
@Serializable
data class PersonInsert(
    @SerialName("foto_url") val photoUrl: String,
    val nombres: String,
    @SerialName("primer_apellido") val firstSurname: String,
    @SerialName("segundo_apellido") val secondSurname: String,
    val hobbies: List<String>,
    @SerialName("estado_civil") val maritalStatus: String,
    val genero: String,
    @SerialName("grado_estudios") val education: String,
    val edad: Int,
    @SerialName("fecha_nacimiento") val birthDate: String,
    @SerialName("estacion_preferida") val preferredSeason: String,
    val latitud: Double,
    val longitud: Double,
    val altitud: Double,
    val localidad: String,
    val estado: Boolean,
)

/** Registro completo que se consulta para la lista y el diálogo de detalles. */
@Serializable
data class PersonRecord(
    val id: String,
    @SerialName("foto_url") val photoUrl: String,
    val nombres: String,
    @SerialName("primer_apellido") val firstSurname: String,
    @SerialName("segundo_apellido") val secondSurname: String,
    val hobbies: List<String>,
    @SerialName("estado_civil") val maritalStatus: String,
    val genero: String,
    @SerialName("grado_estudios") val education: String,
    val edad: Int,
    @SerialName("fecha_nacimiento") val birthDate: String,
    @SerialName("estacion_preferida") val preferredSeason: String,
    val latitud: Double,
    val longitud: Double,
    val altitud: Double,
    val localidad: String,
    @SerialName("fecha_registro") val registeredAt: String,
    val estado: Boolean,
)

data class CapturedLocation(
    val latitude: Double,
    val longitude: Double,
    val altitudeMetersAboveSeaLevel: Double?,
    val locality: String,
)
