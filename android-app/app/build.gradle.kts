import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
}

val supabaseProperties = Properties().apply {
  val propertiesFile = rootProject.file("local.properties")
  if (propertiesFile.exists()) {
    propertiesFile.inputStream().use { load(it) }
  }
}

fun buildConfigString(value: String): String =
  "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""

val supabaseUrl = supabaseProperties.getProperty("supabase.url", "")
val supabasePublishableKey = supabaseProperties.getProperty("supabase.publishableKey", "")

android {
  namespace = "mx.edu.personas"
  compileSdk = 36
  defaultConfig {
    applicationId = "mx.edu.personas"
    minSdk = 30
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
    buildConfigField("String", "SUPABASE_URL", buildConfigString(supabaseUrl))
    buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", buildConfigString(supabasePublishableKey))
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

kotlin {
  jvmToolchain(17)
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation("androidx.compose.material:material-icons-extended")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

  implementation(platform("io.github.jan-tennert.supabase:bom:3.8.0"))
  implementation("io.github.jan-tennert.supabase:postgrest-kt")
  implementation("io.github.jan-tennert.supabase:storage-kt")
  implementation("io.ktor:ktor-client-android:3.3.3")

  debugImplementation(libs.androidx.compose.ui.tooling)
}
