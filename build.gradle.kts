import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

// Load the properties file
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

// Extract the Stripe key
val stripeKey: String = localProperties.getProperty("stripeKey") ?: ""

android {
    namespace = "com.mowieinc.mowiekotlin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mowieinc.mowiekotlin"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "STRIPE_KEY", "\"${stripeKey}\"")

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation("com.google.android.material:material:1.9.0")


    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)
    implementation("com.google.firebase:firebase-database-ktx:20.3.3")

    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Compose BOM (Bill of Materials) for dependency version alignment
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))

    // Core Compose libraries
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Lifecycle support for Compose
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(libs.firebase.storage.ktx)
    implementation(libs.volley)
    implementation(libs.firebase.firestore.ktx)

    // Optional - UI Tooling for previews
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Optional - Icons
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // Coil for Jetpack Compose
    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("com.stripe:stripe-android:20.18.0")  // Use the latest version

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// Apply Google Services plugin
apply(plugin = "com.google.gms.google-services")