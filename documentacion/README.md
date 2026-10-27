# Práctica: formulario de personas

## Objetivo

Aplicación académica Android para capturar información personal, una fotografía y datos de ubicación con Kotlin, Jetpack Compose y Material Design 3.

> La configuración anónima de Supabase de esta práctica es solo para una demostración con datos ficticios. No almacenes información personal real: las políticas permiten que cualquier cliente público consulte los registros y suba o elimine objetos del bucket.

## Tecnologías y estructura

- Kotlin, Jetpack Compose y Material Design 3.
- Noto Sans incluida como fuente local.
- El archivo Noto Sans se distribuye bajo SIL Open Font License 1.1.
- Cliente comunitario Supabase Kotlin para PostgreSQL y Storage.
- Coil 3 para cargar las fotografías almacenadas por URL en Compose.
- `android-app/app/src/main/kotlin/mx/edu/personas/ui/screens/PersonFormScreen.kt`: formulario y estado de pantalla.
- `android-app/app/src/main/kotlin/mx/edu/personas/data/repository/PersonRepository.kt`: carga de fotografía e inserción del registro.
- `android-app/app/src/main/kotlin/mx/edu/personas/ui/screens/PeopleListDialog.kt`: lista ordenada y detalles de consulta.
- `android-app/app/src/main/kotlin/mx/edu/personas/utils/LocationProvider.kt`: permiso, coordenadas, altitud MSL y geocodificación inversa.
- `documentacion/supabase.sql`: tabla, bucket y políticas de demostración.

## Configuración de Supabase

1. Crea un proyecto Supabase académico y abre el SQL Editor.
2. Ejecuta el contenido de `documentacion/supabase.sql`.
3. Copia la URL del proyecto y su clave **publishable** (o clave pública `anon` compatible) en `android-app/local.properties`:

   ```properties
   supabase.url=https://TU-PROYECTO.supabase.co
   supabase.publishableKey=TU_CLAVE_PUBLICA
   ```

4. Mantén ese archivo local: está excluido de Git. No uses `service_role`, claves secretas ni las compartas en capturas o entregas.
5. Abre `android-app/` en Android Studio y compila la variante Debug. Si no has configurado ambos valores, el proyecto aún compila, pero el botón de guardado indicará que falta la configuración.

La tabla `personas` almacena hobbies en JSONB y deja que PostgreSQL asigne el UUID y `fecha_registro = now()`. La aplicación comprime la fotografía a JPEG, la carga en `personas-fotos` y guarda su URL pública en la fila. Si falla la inserción, intenta eliminar la imagen recién cargada.

## Formulario

El formulario usa `TextField`, Checkbox, lista desplegable Material 3, selección de género con iconos y etiquetas visibles, RadioButton, Slider, DatePicker y FilterChip. La edad se compara con la fecha de nacimiento considerando año, mes y día. El selector impide fechas futuras. La segunda vista usa `LazyColumn`, muestra la foto y el estado, ordena por primer apellido y abre un diálogo de detalles de solo consulta.

La galería se abre con Photo Picker, que solo concede acceso al elemento elegido. La cámara recibe una URI temporal mediante `FileProvider`. Para ubicación se solicita permiso en primer plano al pulsar el botón; no se pide ubicación en segundo plano. La localidad se obtiene mediante geocodificación inversa. La altitud solo se muestra/guarda cuando Android 14 o posterior y el proveedor informan altitud sobre el nivel medio del mar (MSL); nunca se sustituye por la altitud elipsoidal del GPS.

## Restricciones académicas

No se incluyen autenticación, edición, eliminación de personas, paneles administrativos ni archivos o dependencias de pruebas. Los registros conservan sus campos capturados si falla el guardado para permitir reintentar.
