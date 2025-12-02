plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.locatour"
    compileSdk = 36

    defaultConfig {
        val apiKey: String = (project.findProperty("GRAPH_HOPPER_API_KEY") ?: "") as String
        buildConfigField("String", "GRAPH_HOPPER_API_KEY", "\"$apiKey\"")
        applicationId = "com.example.locatour"
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
}

dependencies {
    implementation("org.osmdroid:osmdroid-android:6.1.17")
    implementation("org.osmdroid:osmdroid-wms:6.1.17")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("io.github.muddz:styleabletoast:2.4.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}