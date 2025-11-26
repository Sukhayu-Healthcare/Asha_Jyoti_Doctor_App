plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.ashajoyti_doctor_app"           // <- must match base folder
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ashajoyti_doctor_app"   // <- must match namespace
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

}

