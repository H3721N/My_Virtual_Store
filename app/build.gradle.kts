plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.googleService)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

android {
    namespace = "com.gomez.herlin.mi_tiendita_virtual"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gomez.herlin.mi_tiendita_virtual"
        minSdk = 23
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.lottie) /*Animaciones  */
    implementation(libs.firebaseAuth) /* Autentificacion con firebase  */
    implementation(libs.firebaseDatabase)
    implementation(libs.firebase.database.ktx) /* Base de datos en tiempo real  */
    implementation(libs.imagePicker) /* Recortar imagen */
    implementation(libs.glide) /* Leer imagenes */
    implementation(libs.storage) /* Subir archivos multimedia */
    implementation(libs.authGoogle) /* Autentificacion con google */
    implementation(libs.ccp) // Selecciona codigo telefonico
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
}

kapt {
    correctErrorTypes = true
}
