# Personas Android practice

Objective: implement the university Android practice from `task.md` in small, explainable stages.
Branch: `feature/personas-android`. Delivery: staged local project; no remote operations.
Test runner: none; the user explicitly prohibited creating or running tests. Compile with Gradle when applicable.

## Specs

### S1 — Project and registration form

Source: `task.md`, sections 2–8 and 11–12. Use Kotlin, Jetpack Compose, Material Design 3, Noto Sans, and the specified palette. Implement the required personal fields, photo capture/selection, automatic location fields, runtime permissions, validation, and saving behavior using a simple structure. Preserve the user’s explicit constraint: “NO crear pruebas unitarias, pruebas de integración, pruebas instrumentadas ni archivos de test adicionales.” No authentication or unnecessary architecture.

Acceptance: app module builds; required fields and age/date consistency are implemented; Supabase upload/insert uses only public client configuration and never invents credentials; storage/database setup is documented. No tests are created or run.

### S2 — Registered people and details

Source: `task.md`, sections 9–10. Add the Supabase-backed alphabetical list and read-only complete details view, including image shapes and empty/loading states.

Acceptance: list is ordered by first surname and each details view displays all persisted fields. No tests are created or run.

### S3 — Delivery documentation and APK

Source: `task.md`, sections 13–16. Prepare Spanish academic documentation, compile a debug APK, and copy it to `entregables/Practica_Formulario_Personas.apk`.

Acceptance: docs accurately describe implemented behavior; APK exists and is nonempty, or the concrete build blocker is reported. No tests are created or run.

## Tasks

- T1 — S1 — inline — in progress — documentation baseline pending.
- T2 — S2 — inline — pending — no commit.
- T3 — S3 — inline — pending — no commit.

## Log

### L1

“lee el archivo task.md y realiza lo que se pide”

### L2

The user approved starting with a new Android project, implementing the form and Supabase setup first, and deferring list/details/docs/APK to later stages. The workspace initially contained only `task.md`; it is not a Git repository. Android CLI is installed and reports SDK `/home/pyton06/Android/Sdk`; `android create --list` provides an `empty-activity` Compose template. Do not create or run tests. Supabase URL and public key are not supplied and must not be fabricated.

### L3 — 2026-10-26

The user confirmed “hazlo” after reviewing the written design. Initialized a local Git repository and created branch `feature/personas-android`. CodeGraph initialization was attempted once but could not run because the upstream `codegraph` executable is unavailable; continue with bounded filesystem reads. Android CLI documentation confirms Compose `DatePicker` and Activity Result `PickVisualMedia` usage. Supabase Kotlin documentation confirms the Postgrest and Storage client APIs; use a local-only configuration source for the missing public URL/key. No tests were created or executed.
