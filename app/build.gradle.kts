plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Needed for Data Binding annotation processor
    id("kotlin-kapt")
}

android {
    namespace = "com.example.hbbittracker"
    compileSdk = 36 // Using latest Android SDK

    defaultConfig {
        applicationId = "com.example.hbbittracker"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    // ✅ Enable both ViewBinding and DataBinding
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    // ----- Core AndroidX Libraries -----
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // ----- Navigation -----
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // ----- MPAndroidChart -----
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // ----- Gson (for JSON serialization in DataManager) -----
    implementation("com.google.code.gson:gson:2.10.1")

    // ----- Optional: Lifecycle + ViewModel (if not in toml) -----
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")

    // ----- Testing -----
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
