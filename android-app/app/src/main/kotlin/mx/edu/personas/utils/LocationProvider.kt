package mx.edu.personas.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import mx.edu.personas.data.model.CapturedLocation
import java.util.Locale
import kotlin.coroutines.resume

object LocationProvider {
    suspend fun getCurrent(context: Context): CapturedLocation {
        val hasFinePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarsePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        check(hasFinePermission || hasCoarsePermission) {
            "Se requiere permiso de ubicación para completar estos campos."
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria().apply { accuracy = Criteria.ACCURACY_FINE }
        val provider = locationManager.getBestProvider(criteria, true)
            ?: error("Activa la ubicación del dispositivo y vuelve a intentarlo.")

        // Se obtiene una sola ubicación actual del proveedor activo; no se registran actualizaciones en segundo plano.
        // La corrutina permite cancelar la petición si la pantalla desaparece y limita la espera a treinta segundos.
        val location = withTimeoutOrNull(30_000) {
            suspendCancellableCoroutine<Location?> { continuation ->
                val cancellationSignal = CancellationSignal()
                continuation.invokeOnCancellation { cancellationSignal.cancel() }
                locationManager.getCurrentLocation(provider, cancellationSignal, context.mainExecutor) {
                    if (continuation.isActive) continuation.resume(it)
                }
            }
        } ?: error("La ubicación todavía no está disponible. Inténtalo nuevamente.")

        val locality = withContext(Dispatchers.IO) { findLocality(context, location) }
        // Android expone una altitud MSL explícita solo en API 34+ cuando el proveedor la informa.
        // La altitud GPS ordinaria es elipsoidal; se deja vacía para no confundirla con el nivel medio del mar.
        val mslAltitude = if (Build.VERSION.SDK_INT >= 34 && location.hasMslAltitude()) {
            location.mslAltitudeMeters
        } else {
            null
        }

        return CapturedLocation(location.latitude, location.longitude, mslAltitude, locality)
    }

    @Suppress("DEPRECATION")
    private fun findLocality(context: Context, location: Location): String {
        // Geocoder convierte coordenadas en una dirección; se intenta localidad y después referencias cercanas.
        check(Geocoder.isPresent()) { "La geocodificación no está disponible en este dispositivo." }
        val address = Geocoder(context, Locale("es", "MX"))
            .getFromLocation(location.latitude, location.longitude, 1)
            ?.firstOrNull()
        return listOfNotNull(
            address?.locality,
            address?.subLocality,
            address?.subAdminArea,
            address?.adminArea,
            address?.featureName,
        ).firstOrNull { it.isNotBlank() }
            ?: error("No se pudo determinar una localidad. Inténtalo nuevamente.")
    }
}
