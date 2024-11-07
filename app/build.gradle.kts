plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.trainaut01"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.trainaut01"
        minSdk = 26
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
}



dependencies {
//    UI
    implementation("com.google.android.material:material:1.12.0")
    implementation ("com.squareup.picasso:picasso:2.8")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    //Firebase
    implementation ("com.google.android.gms:play-services-auth:20.6.0")
    implementation ("com.google.firebase:firebase-auth:22.0.0")
    implementation("com.google.firebase:firebase-firestore:24.9.0")
    implementation("com.google.firebase:firebase-database:20.1.0")
    implementation("com.google.firebase:firebase-analytics:21.3.0")
    implementation("com.google.firebase:firebase-messaging:23.1.2")
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation ("com.google.android.gms:play-services-base:17.6.0")


//    Dagger
    implementation("com.google.dagger:dagger:2.52")
    annotationProcessor ("com.google.dagger:dagger-compiler:2.52")

//    Picasso
    implementation("com.squareup.picasso:picasso:2.8")
//    Gson
    implementation ("com.google.code.gson:gson:2.8.9")

//    Lombok
    compileOnly("org.projectlombok:lombok:1.18.34")


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    apply(plugin = "com.google.gms.google-services")
}