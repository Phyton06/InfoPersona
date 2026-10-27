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

- T1 — S1 — inline — complete — `4b0efcc`; `:app:assembleDebug` succeeded; initial direct push to remote `main` is `cec7fec`; runtime needs the user's Supabase public configuration.
- T2 — S2 — inline — in progress — commit pending.
- T3 — S3 — inline — pending — no commit.

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
