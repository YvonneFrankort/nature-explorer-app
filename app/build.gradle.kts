
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.example.naturegame"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.naturegame"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
//            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // --- Compose BOM FIRST ---
    implementation(platform(libs.compose.bom))

    // --- Compose ---
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.activity.compose)
    implementation(libs.compose.ui.tooling)

    // --- Coil (AsyncImage) ---
    implementation("io.coil-kt:coil-compose:2.6.0")

    // --- Lifecycle ---
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)

    // --- Navigation ---
    implementation(libs.navigation.compose)

    // --- CameraX ---
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation("androidx.camera:camera-extensions:1.3.4")

    // CameraX requires Guava for ListenableFuture
    implementation("com.google.guava:guava:31.1-android")

    // --- ML Kit (Image Labeling) ---
    implementation("com.google.mlkit:image-labeling:17.0.7")

    // --- Hilt ---
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // --- Room ---
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // --- Firebase ---
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    // --- OSMDroid ---
    implementation("org.osmdroid:osmdroid-android:6.1.16")

    // --- Permissions ---
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // --- Location ---
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // --- Profile (DataStore) ---
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // --- Splashscreen ---
    implementation(libs.androidx.core.splashscreen)
}
