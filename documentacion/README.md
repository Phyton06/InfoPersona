# Práctica: formulario de personas

## Objetivo

Aplicación académica Android para registrar personas mediante un formulario, almacenar fotografías y ubicación, y consultar los registros en Supabase.

> **Uso académico únicamente:** las políticas anónimas de demostración permiten a cualquier cliente público consultar los datos y subir o eliminar objetos del bucket. Utiliza solo datos ficticios; no almacenes información personal real.

## Tecnologías

- Kotlin 2.3, Jetpack Compose y Material Design 3.
- Noto Sans como fuente local; paleta Indigo 500/700 y Teal 500.
- Supabase Kotlin 3.8 para PostgreSQL y Storage; Coil 3.3 para las fotografías remotas.
- Android Gradle Plugin 9.0.1, Gradle Wrapper 9.1, JDK 17, `minSdk` 30 y `compileSdk` 36.

## Estructura principal

- `android-app/app/src/main/kotlin/mx/edu/personas/MainActivity.kt`: inicio de Compose y MaterialTheme.
- `ui/screens/PersonFormScreen.kt`: formulario, validaciones, selección de imagen y permisos.
- `ui/screens/PeopleListDialog.kt`: lista ordenada, actualización y detalles de solo consulta.
- `data/model/Person.kt`: modelos del formulario y de los datos serializados.
- `data/repository/PersonRepository.kt`: inserción, consulta ordenada, carga y limpieza de fotografías.
- `utils/LocationProvider.kt`: ubicación en primer plano, geocodificación y altitud MSL.
- `ui/theme/`: colores, tipografía Noto Sans y tema Material 3.
- `documentacion/supabase.sql`: tabla, bucket y políticas académicas.

## Preparar Supabase

1. Usa el proyecto Supabase asignado a la práctica y abre el SQL Editor. Si todavía no existe, crea uno en tu organización antes de continuar.
2. Revisa el script: elimina cinco tablas públicas heredadas antes de crear `personas`. En el proyecto usado para la práctica se comprobó que estaban vacías; antes de ejecutarlo en cualquier otro proyecto, verifica que no contengan datos. El script no elimina otros esquemas ni buckets.
3. Copia la URL y la clave **publishable** (o la clave pública `anon` compatible) en `android-app/local.properties`:

   ```properties
   supabase.url=https://TU-PROYECTO.supabase.co
   supabase.publishableKey=TU_CLAVE_PUBLICA
   ```

4. Conserva `local.properties` solo en tu equipo; está excluido de Git. No uses `service_role` ni claves secretas, y no las compartas en capturas o entregas.
5. Si faltan esos valores, el proyecto compila, pero las consultas y el guardado muestran un error de configuración hasta agregarlos.

La tabla almacena hobbies como JSONB. PostgreSQL genera el UUID y `fecha_registro` con `now()`; la aplicación no envía esos campos al insertar. La foto se convierte a JPEG, se carga con un nombre UUID y se guarda su URL pública. Si falla la carga o la inserción, la aplicación intenta borrar el objeto recién reservado.

## Pantallas y componentes

La barra inferior permite alternar entre **Registrar** y **Personas**. El formulario emplea campos de texto controlados, Checkbox para varios hobbies, menú desplegable para estado civil, iconos con etiquetas para género, RadioButton para estudios y estado, Slider entero de edad, DatePicker sin fechas futuras y FilterChip para estación. El Snackbar confirma el resultado. La edad calculada desde año, mes y día debe coincidir con el Slider.

La lista consulta Supabase cada vez que se abre, ordena por primer apellido ascendente y ofrece actualización manual. Cada tarjeta muestra fotografía, nombre y estado; `DETALLES` abre una vista de consulta que presenta todos los campos, incluida la fecha y hora de registro por separado.

## Fotografías y ubicación

Photo Picker limita el acceso a la foto elegida. La cámara escribe en una URI temporal de `FileProvider`. Coil muestra las URL públicas en Compose.

La ubicación se solicita en primer plano al pulsar el botón; no se solicita acceso en segundo plano. Se muestran latitud, longitud y localidad obtenida por geocodificación inversa. La altitud solo se guarda cuando Android 14 o posterior y el proveedor ofrecen altitud sobre el nivel medio del mar (MSL); no se usa la altitud elipsoidal del GPS como sustituto. Si faltan localidad o altitud válida, no se permite guardar.

## Compilar e instalar

Desde la raíz del repositorio:

```bash
cd android-app
./gradlew :app:assembleDebug
mkdir -p ../entregables
cp app/build/outputs/apk/debug/app-debug.apk ../entregables/Practica_Formulario_Personas.apk
```

El APK generado se encuentra en `entregables/Practica_Formulario_Personas.apk`. Para instalarlo en un dispositivo conectado:

```bash
adb install -r ../entregables/Practica_Formulario_Personas.apk
```

El APK Debug configurado se entrega en el repositorio. Incluye la URL y clave publishable configuradas localmente para el proyecto académico; nunca debe incluirse una clave `service_role`.

La aplicación no incluye autenticación, roles, edición, eliminación de personas ni panel administrativo. Por instrucción académica no se incluyen ni ejecutan pruebas automatizadas.
