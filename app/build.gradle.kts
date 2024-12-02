plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // for database
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.pygmyhippo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pygmyhippo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        animationsDisabled = true
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
        dataBinding = true
    }
}


dependencies {
    implementation("com.github.kenglxn.QRGen:android:3.0.1") // QR generation
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.firestore)

    // qr code scanner
    implementation("com.google.mlkit:barcode-scanning:17.0.3")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.1")
    implementation("androidx.camera:camera-camera2:1.1.0")
    implementation("androidx.camera:camera-lifecycle:1.1.0")
    implementation("androidx.camera:camera-view:1.0.0-alpha31")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.3.3")

    // added for testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.1")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.3.0")
    androidTestImplementation("org.hamcrest:hamcrest-library:1.3")
    androidTestImplementation("org.hamcrest:hamcrest-core:1.3")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0") // this is for clicking in an arbitrary location
    // Added for database
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    debugImplementation("androidx.fragment:fragment-testing-manifest:1.8.5")
    implementation("com.google.firebase:firebase-storage:20.1.0")
    // For converting URL to drawable
    implementation("com.github.bumptech.glide:glide:4.15.1")
}
