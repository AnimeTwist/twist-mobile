import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream


plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

val secretProperties = Properties()
if (System.getenv("CI") != "true") {
    val secretsFile = rootProject.file("secrets.properties")
    secretProperties.load(FileInputStream(secretsFile))
} else {
    secretProperties.setProperty("decrypt_key", System.getenv("DECRYPT_KEY"))
}


android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.3")
    defaultConfig {
        applicationId = "dev.smoketrees.twist"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 13
        versionName = "2.2.3-prod10"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        buildConfigField(
            "byte[]",
            "CRYPTO_KEY",
            "new byte[] {" + (secretProperties["decrypt_key"] as String).toByteArray()
                .joinToString()
                    + "}"
        )

        buildConfigField(
            "byte[]",
            "CDN_URL",
            "new byte[] {" + "https://twistcdn.bunny.sh".toByteArray()
                .joinToString()
                    + "}"
        )

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(mapOf("room.incremental" to "true"))
            }
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFile(file("proguard-rules.pro"))
            multiDexEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // For Kotlin projects
    kotlinOptions {
        jvmTarget = "1.8"
    }

    androidExtensions {
        isExperimental = true
    }

    buildFeatures {
        dataBinding = true
    }
}

kapt {
    useBuildCache = true
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.3.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.2.5")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")

    // Navigation component
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.0")

    // Paging support
    implementation("androidx.paging:paging-runtime-ktx:2.1.2")

    // Koin for DI
    implementation("org.koin:koin-android-viewmodel:2.0.1")

    // Networking stuff
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.squareup.retrofit2:retrofit:2.6.2")
    implementation("com.squareup.retrofit2:converter-gson:2.6.2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.2.2")

    // UI
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.recyclerview:recyclerview:1.2.0-alpha04")
    implementation("com.google.android.material:material:1.1.0")
    implementation("com.afollestad.material-dialogs:core:3.1.1")

    // apache commons
    implementation("org.apache.commons:commons-lang3:3.9")

    // Exoplayer - stream video
    implementation("com.google.android.exoplayer:exoplayer:2.11.7")
    implementation("com.google.android.exoplayer:extension-mediasession:2.11.7")

    // Spinkit - loading animations
    implementation("com.github.ybq:Android-SpinKit:1.4.0")

    // Room for database stuff
    implementation("androidx.room:room-runtime:2.2.5")
    kapt("androidx.room:room-compiler:2.2.5")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.2.5")

    // glide for image loading
    implementation("com.github.bumptech.glide:glide:4.11.0")
    kapt("com.github.bumptech.glide:compiler:4.11.0")

    // multidex
    implementation("com.android.support:multidex:1.0.3")

    // android system permissions
    implementation("com.github.quickpermissions:quickpermissions-kotlin:0.4.0")

    // html rendering
    implementation("org.sufficientlysecure:html-textview:3.9")

    // Fuzzy search
    implementation("me.xdrop:fuzzywuzzy:1.2.0")
}