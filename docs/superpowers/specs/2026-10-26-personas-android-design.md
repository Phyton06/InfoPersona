# Android personas app — design

Create a straightforward, single-module Android application for the university practice described in `task.md`. Reuse the approved scope: implement the Compose form and Supabase setup first; the records list, detail view, delivery documentation, and APK follow in later work.

## Structure

- Kotlin, Jetpack Compose, Material Design 3, and Noto Sans in one Android application module.
- Keep form state and validation in a small screen-level state holder; use a compact data model and repository for Supabase operations.
- Use Android activity-result APIs for selecting/taking a photo and runtime location permissions for automatic coordinates and reverse geocoding.
- Keep Supabase URL and public client key as local build configuration; never embed a service-role or secret key. Include SQL and setup instructions for a public photo bucket and explicitly scoped anonymous demo policies.
- Do not add authentication, enterprise architecture, tests, or unrequested screens. Do not claim live Supabase behavior until the user supplies real project credentials.

## First implementation scope

Scaffold the Android project and implement the registration form, required-field and age/date validation, photo and location capture, Supabase storage/insert integration, and setup documentation. Defer list/detail screens and APK export to later phases while preserving the requirements in `task.md`.

## Verification

Follow `task.md`: do not create or execute tests. Use Gradle compilation as the functional build check when SDK/dependencies permit. The required final deliverable remains `entregables/Practica_Formulario_Personas.apk`.
