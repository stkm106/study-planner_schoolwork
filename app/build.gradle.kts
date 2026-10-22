plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Plugin Firebase của bạn
}

android {
    namespace = "com.example.studyplanner"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.studyplanner"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)

    //Material Design & UI Components
    implementation("com.google.android.material:material:1.11.0")
    //MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    //Google ML Kit Text Recognition
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")
    //Firebase BoM & Cloud Firestore
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
}