plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.example.campusapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.campusapp"
        minSdk = 31
        targetSdk = 35
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

    buildFeatures{

        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation ("com.airbnb.android:lottie:6.4.0")


    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)


    implementation ("androidx.navigation:navigation-fragment:2.7.7")
    implementation ("androidx.navigation:navigation-ui:2.7.7")


    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    implementation ("com.google.code.gson:gson:2.10.1")

    implementation ("com.google.firebase:firebase-database:20.3.0")
    implementation ("androidx.fragment:fragment-ktx:1.6.1")
    implementation ("com.google.firebase:firebase-analytics:21.5.0")
    implementation ("com.google.firebase:firebase-core:21.1.1")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("androidx.preference:preference:1.2.0")
    implementation ("androidx.appcompat:appcompat:1.7.0")
    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation ("com.google.firebase:firebase-auth:23.2.0")
    implementation ("com.google.firebase:firebase-firestore:25.1.4")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}