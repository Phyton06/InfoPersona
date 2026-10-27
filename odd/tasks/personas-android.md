# Personas Android practice

Objective: implement the university Android practice from `task.md` in small, explainable stages.
Branch: local `feature/personas-android`; remote target `main` (the user selected an initial direct push). Delivery: GitHub CLI; no PRs.
Delivery strategy: `exception-ok` because the user explicitly chose direct delivery to `main` instead of PR slicing for this academic project.
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

- T1 — S1 — inline — complete — `4b0efcc`; Debug build passes; live save requires a location provider with valid MSL altitude.
- T2 — S2 — inline — complete — `103f00f`; Debug build and live empty-list query pass; details with a record not exercised.
- T3 — S3 — inline — in progress — configured APK exported locally; final work-unit commit and emulator save/details proof pending.

## Log

### L1

“lee el archivo task.md y realiza lo que se pide”

### L2

The user approved starting with a new Android project, implementing the form and Supabase setup first, and deferring list/details/docs/APK to later stages. The workspace initially contained only `task.md`; it is not a Git repository. Android CLI is installed and reports SDK `/home/pyton06/Android/Sdk`; `android create --list` provides an `empty-activity` Compose template. Do not create or run tests. Supabase URL and public key are not supplied and must not be fabricated.

### L3 — 2026-10-26

The user confirmed “hazlo” after reviewing the written design. Initialized a local Git repository and created branch `feature/personas-android`. CodeGraph initialization was attempted once but could not run because the upstream `codegraph` executable is unavailable; continue with bounded filesystem reads. Android CLI documentation confirms Compose `DatePicker` and Activity Result `PickVisualMedia` usage. Supabase Kotlin documentation confirms the Postgrest and Storage client APIs; use a local-only configuration source for the missing public URL/key. No tests were created or executed.

### L4 — 2026-10-26

Scaffolded `android-app/` from the Compose template and implemented S1's registration form, photo/gallery capture, foreground location and geocoding, MSL-only altitude handling, Supabase Storage/Postgrest flow, SQL setup, and Spanish setup notes. Added a local Noto Sans font and removed template-generated test files and all test framework dependencies to honor the task. Global RDD status is off. `./gradlew --offline --no-daemon :app:assembleDebug` reached dependency resolution but could not compile because required public artifacts are absent from the local cache; no online dependency access was attempted. Independent static verification found a missing `verticalScroll` import and an upload cleanup gap; both were corrected and the exact findings rechecked as resolved. Kotlin compilation remains unverified. The Supabase URL and public key are still missing.

### L5 — 2026-10-26

The user explicitly authorized public dependency downloads from Google Maven, Maven Central, and Gradle Plugin Portal. The first online `:app:assembleDebug` exposed API/import errors; inspection of the resolved Supabase 3.8.0 AAR and Android SDK 36 identified the causes (Storage's `storage` extension import and collection-based delete API, `ExposedDropdownMenu` as a box-scope member, and missing `Location?` type inference). Corrected these and `./gradlew --offline --no-daemon :app:assembleDebug` succeeded (38 tasks). Remaining SDK/Java deprecation warnings are non-fatal. Independent final review found missing Spanish explanations for implemented controls; added comments across Compose, theme, photo, location, Storage/Postgrest, Snackbar, and Shape flows; the focused recheck resolved that blocker. `git diff --check` passes; zero test sources/dependency declarations exist and no tests were run. Debug APK exists at `android-app/app/build/outputs/apk/debug/app-debug.apk` (73,507,408 bytes); it is not copied to `entregables/` because that is S3. Public Supabase URL/key remain unconfigured. The current implementation exceeds the 400-line delivery threshold; `ask-on-risk` requires a chain-strategy choice before the next commit.

### L6 — 2026-10-26

User's exact request: “este es el repo https://github.com/Phyton06/InfoPersona.git agregalos peres desde rama, haz todo por cli y mandalo a main”. This names a GitHub destination and requests CLI-based PR delivery toward `main`. Credential/session authorization is not yet established, and no remote repository or credential operation has been performed. PR creation also requires the approved issue number and the exact PR/merge flow to be confirmed.

### L7 — 2026-10-26

The user confirmed authorization to use the currently configured GitHub CLI session for the named repository and PR operations. Read-only CLI inspection found no remote branches/default branch and no issue matches for `"formulario de personas"` or `personas`; no remote writes were performed. The current request mentions PRs to `main`, but the target has no `main` branch, and the PR workflow requires an approved linked issue. Delivery is paused for the user's choice between an initial direct push to establish `main` (without a PR) or waiting for an approved issue and an established `main` branch to continue via PR.

### L8 — 2026-10-26

The user selected “1” from the two delivery choices, authorizing the initial direct push to initialize `main` without a PR. The remote repository was confirmed to have no branches; no issue was found in the project-specific searches. Use the previously authorized GitHub CLI session, keep local `local.properties` excluded, and do not create or run tests.

### L9 — 2026-10-26

Committed S1 as `4b0efcc` (`feat(personas): add Compose registration and Supabase setup`) on local branch `feature/personas-android`. The Debug build and static verification succeeded; no tests were created or run. The remote `main` has not yet been initialized; the next authorized operation is the user's selected initial direct push to `Phyton06/InfoPersona:main`. `task.md` and ignored `android-app/local.properties` were not included in the commit.

### L10 — 2026-10-26

Performed the user-selected direct initialization push with `git push origin HEAD:main`; GitHub accepted the new branch. `git ls-remote origin refs/heads/main` returned the exact local `HEAD` `cec7fec76ab791424f39d5213b39b536fad48877`. No PR was created. The only remaining untracked file is the original `task.md`; `android-app/local.properties` remains ignored. T1 is complete and work has started on T2.

### L11 — 2026-10-26

Implemented S2 with a Supabase query ordered by `primer_apellido`, a refreshable `LazyColumn` of Material cards, Coil-loaded shaped photos, active/inactive labels, and read-only details containing all fields with registration date/time split. Coil 3.6.3 was incompatible with SDK 36/AGP 9.0.1 (required API 37/AGP 9.1); version 3.3.0 is compatible. The decoder is a `PostgrestResult` member (`decodeList`), not a top-level import. `./gradlew --no-daemon :app:assembleDebug` succeeded (38 tasks); no tests were created or run. Independent S2 verification found the list/details requirements met by inspection; its demo-policy advisory is already explicit in SQL comments at lines 1–2 and README warnings. The updated debug APK is larger (116,508,645 bytes); it remains a Debug APK and S3 export is still pending.

### L12 — 2026-10-26

Committed S2 as `103f00f` (`feat(personas): add people list and details`). Build succeeded and the independent S2 review found the list/details requirements met by inspection; no tests were created or run. Supabase URL/key are still not configured, so live query behavior has not been exercised. The remote `main` still needs this work unit pushed; S3 documentation/APK export remains pending.

### L13 — 2026-10-26

Pushed the S2 work unit and tracker update to remote `main` (`103f00f` and `7e9acb3`). T3 is now in progress: complete Spanish README and academic report, check for a real Android device/emulator for screenshots, then rebuild and export the Debug APK. Supabase public URL/key remain unconfigured; no live backend claims or fake screenshots. No tests are created or run.

### L14 — 2026-10-26

Expanded `documentacion/README.md` and added `documentacion/PRACTICA.md` with the required report sections and explicit placeholders for every requested screenshot. `adb devices -l` returned no connected device; `android emulator list` was empty, so no screenshots were fabricated. The current Debug APK is about 116.5 MB and is not part of the Git push; the required copy to `entregables/Practica_Formulario_Personas.apk` remains pending. Supabase URL and public key are still absent, so ask the user to configure the ignored `android-app/local.properties` before producing the final configured APK.

### L15 — 2026-10-26

Committed and pushed the Spanish README/report as `7c5be17` to remote `main`. T3 remains open only for the configured final APK export; no live Supabase connection has been exercised. The Debug APK is about 116.5 MB, so it remains a local deliverable rather than a GitHub-tracked file. The original `task.md` remains untracked; local Supabase configuration remains ignored.

### L16 — 2026-10-26

The user asked for an emulator and noted Supabase MCP availability. Installed Android Emulator 37.2.12 and an Android 36 Google APIs image, created and started `medium_phone` using the local AVD directory (the ambient `ANDROID_AVD_HOME` initially pointed elsewhere). Installed/launched the app without debugger wait. Captured real emulator screenshots for the form, Photo Picker, camera, selection components, runtime location permission, and location fields under `documentacion/capturas/`. The emulator reported Mountain View coordinates and no MSL altitude; report labels these as virtual-device output, not physical-device GPS. Success Snackbar, populated list/details, and actual Supabase table/bucket screenshots remain unavailable because credentials/project are not configured. No Supabase MCP calls were made; project destination and authorization to apply the SQL migration are still needed.

### L17 — 2026-10-26

Committed and pushed the real emulator screenshots and report updates as `59caba8` to remote `main`. Supabase MCP has not been called: the project reference/URL and authorization to retrieve publishable configuration or apply `documentacion/supabase.sql` are not yet established. The app remains buildable but its Supabase URL/key are blank; final configured APK export is still pending.

### L18 — 2026-10-26

The user requested “crea un proyecto”. The available Supabase MCP operations do not include project creation, so no project or database resource was created. Creating a Supabase project requires an explicit organization, region, credential/session authorization, and a locally supplied database password; destination and cost-related settings are unresolved.

### L19 — 2026-10-26

Installed Supabase CLI 2.122.0 through the approved local tool-install request. The MCP still has no project-creation operation. No Supabase project, schema, or credentials were queried or changed. To create the project, the user must specify the organization and region, authorize the Supabase CLI session, and arrange a database password locally; possible billing must also be confirmed.

### L20 — 2026-10-26

The user authorized inspecting and applying `documentacion/supabase.sql` to the supplied project. The read-only MCP project URL check returned a different project from the supplied URL. Stopped before listing tables, reading keys/advisories, or applying SQL; no database/schema/data changes were made. Waiting for the MCP connection to target the supplied project.

### L21 — 2026-10-26

The user asked “cambialo” to switch the active Supabase MCP connection. The available MCP tools expose project URL retrieval but no operation to change the selected project or connection. No Supabase config, project, schema, or credentials were changed or re-read. The user must update the MCP connection to the supplied project URL and notify when ready.

### L22 — 2026-10-26

The user identified the MCP-connected project as the intended project, supplied its public client URL/key, and authorized replacement of the old test tables. The MCP URL matched. Pre-migration inspection found five legacy public tables (`drivers`, `passengers`, `ban_history`, `last_rides`, `coordenadas`), all with zero rows, and no Storage buckets. Applied migration `replace_legacy_test_tables_with_personas`: dropped only those old tables (without `CASCADE`), created `public.personas` with RLS, and created the public `personas-fotos` bucket and bucket-scoped policies. Post-migration inspection confirms `personas` exists with zero rows and the bucket is public with zero objects. Security advisor reports leaked-password protection disabled for Supabase Auth; this app has no authentication, so the setting was not changed. Local SQL/README now reflect the replacement; local properties still need configuration before the final APK.

### L23 — 2026-10-26

The user clarified that the currently MCP-connected project is the intended one and supplied its publishable URL/key. Mapped these values into ignored `android-app/local.properties` (not tracked). Rebuilt the configured Debug APK successfully; installed it in the emulator and the live Supabase list query returned the exact empty state `No hay personas registradas.` The migration and bucket are verified with zero rows/objects. Copied the 75,161,351-byte APK to `entregables/Practica_Formulario_Personas.apk`. No sample row was inserted: the emulator does not provide MSL altitude, so the form correctly blocks saving; success Snackbar and populated details remain unverified. No tests were created or run.
