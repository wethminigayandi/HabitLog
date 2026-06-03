plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Remove compose plugin since we're using XML views
    // alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.habittracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.habittracker"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    // Remove compose features for pure XML approach
    // buildFeatures {
    //     compose = true
    // }
}

dependencies {
    // Material Components - Latest stable version
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.cardview:cardview:1.0.0")

    // Direct dependency declarations (not using libs catalog)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Add Navigation Component dependencies - THIS FIXES THE ERROR
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Add MPAndroidChart for charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}