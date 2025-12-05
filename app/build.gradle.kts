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
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

// Force specific AndroidX activity versions in case some dependency pulls 1.8.x transitively.
// This keeps the project compatible with compileSdk = 33 without upgrading AGP / SDK right now.
configurations.all {
    resolutionStrategy {
        force(
            "androidx.activity:activity:1.7.2",
            "androidx.activity:activity-ktx:1.7.2"
        )
    }
}

dependencies {
    // Material Components (bumped to a recent stable)
    implementation("com.google.android.material:material:1.10.0")

    // CircleImageView for circular avatar
    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // use activity libs pinned to 1.7.2 to match compileSdk 33
    implementation("androidx.activity:activity:1.7.2")
    implementation("androidx.activity:activity-ktx:1.7.2")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // -----------------------
    // Networking libraries
    // -----------------------

    // Retrofit + Gson converter
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp + logging interceptor
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Gson (optional but helpful)
    implementation("com.google.code.gson:gson:2.10.1")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    
    // WebRTC for video calling - using the Infobip version which is better maintained
    implementation("io.getstream:stream-webrtc-android:1.1.1")
}
