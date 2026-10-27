package mx.edu.personas.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import mx.edu.personas.BuildConfig
import mx.edu.personas.data.model.PersonDraft
import mx.edu.personas.data.model.PersonInsert
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

private const val PHOTO_BUCKET = "personas-fotos"

object PersonRepository {
    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
        ) {
            install(Postgrest)
            install(Storage)
        }
    }

    suspend fun save(person: PersonDraft, photoBytes: ByteArray) {
        require(BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_PUBLISHABLE_KEY.isNotBlank()) {
            "Configura la URL y la clave pública de Supabase en local.properties."
        }

        val filePath = "${java.util.UUID.randomUUID()}.jpg"
        // El bucket público permite formar la URL que se guardará en PostgreSQL sin exponer claves de Storage.
        val photoUrl = "${BuildConfig.SUPABASE_URL.trimEnd('/')}/storage/v1/object/public/$PHOTO_BUCKET/$filePath"

        // Primero se guarda la foto y luego PostgREST inserta su URL y los campos; id y fecha usan valores de PostgreSQL.
        try {
            // La ruta UUID es exclusiva; también se limpia si la respuesta de carga se pierde.
            supabase.storage.from(PHOTO_BUCKET).upload(filePath, photoBytes)
            val insert = PersonInsert(
                photoUrl = photoUrl,
                nombres = person.names,
                firstSurname = person.firstSurname,
                secondSurname = person.secondSurname,
                hobbies = person.hobbies,
                maritalStatus = person.maritalStatus,
                genero = person.gender,
                education = person.education,
                edad = person.age,
                birthDate = person.birthDate,
                preferredSeason = person.preferredSeason,
                latitud = person.latitude,
                longitud = person.longitude,
                altitud = person.altitude,
                localidad = person.locality,
                estado = person.isActive,
            )
            // La operación es suspendida y solo confirma el registro si la inserción devuelve una respuesta exitosa.
            supabase.from("personas").insert(insert)
        } catch (error: Exception) {
            // Si falla la carga o la inserción, se intenta borrar la ruta UUID recién reservada.
            withContext(NonCancellable + Dispatchers.IO) {
                runCatching { supabase.storage.from(PHOTO_BUCKET).delete(listOf(filePath)) }
            }
            throw error
        }
    }
}
