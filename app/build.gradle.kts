plugins {
    id("com.android.application")
    kotlin("android")
}

kotlin {
    // Use a Kotlin/Gradle JVM toolchain so Kotlin and Java compile to the same target
    jvmToolchain(17)
}

android {
    namespace = "com.example.ashajoyti_doctor_app"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.ashajoyti_doctor_app"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    // Make Java compile to Java 17 (match the Kotlin toolchain above)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Keep Kotlin jvmTarget in sync
    kotlinOptions {
        // For Kotlin DSL in Android module this block works; jvmToolchain already enforces toolchain,
        // but keep jvmTarget for clarity.
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
}
